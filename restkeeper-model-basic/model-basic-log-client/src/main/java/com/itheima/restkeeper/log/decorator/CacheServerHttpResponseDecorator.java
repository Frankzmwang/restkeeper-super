package com.itheima.restkeeper.log.decorator;

import com.alibaba.fastjson.JSONObject;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.pojo.MqMessage;
import com.itheima.restkeeper.req.LogBusinessVo;
import com.itheima.restkeeper.source.LogSource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName CacheServerHttpResponseDecorator.java
 * @Description TODO
 */
@Slf4j
public class CacheServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    LogSource logSource;

    ServerWebExchange exchange;

    private Long messageId;

    private String sender;

    private byte[] bytes;

    public CacheServerHttpResponseDecorator(ServerWebExchange exchange,
                                            LogSource logSource,
                                            Long messageId,
                                            String sender) {
        super(exchange.getResponse());
        this.logSource = logSource;
        this.exchange = exchange;
        this.messageId = messageId;
        this.sender =sender;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        AtomicReference<String> bodyRef = new AtomicReference<>();
        if (body instanceof Flux) {
            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
            return super.writeWith(fluxBody.buffer().map(dataBuffer -> {
                DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                DataBuffer join = dataBufferFactory.join(dataBuffer);
                byte[] content = new byte[join.readableByteCount()];
                join.read(content);
                // ???????????????
                DataBufferUtils.release(join);
                String result = new String(content, Charset.forName("UTF-8"));
                ResponseWrap responseWrap = JSONObject.parseObject(result,ResponseWrap.class);
                this.trace(exchange.getRequest(),responseWrap);
                byte[] uppedContent = result.getBytes();
                return getDelegate().bufferFactory().wrap(uppedContent);
            }));
        }
        return super.writeWith(body);
    }

    private void trace(ServerHttpRequest request, ResponseWrap responseWrap) {
        //??????IP
        String hostAddress = request.getRemoteAddress().getAddress().getHostAddress();
        //??????host
        String host = request.getURI().getHost();
        //????????????
        String requestUri = request.getURI().getPath();
        //????????????
        String method = request.getMethodValue().toUpperCase();
        //??????id
        String requestId = request.getId();
        //?????????
        String requesBody = request.getHeaders().getFirst("requesBody");

        LogBusinessVo logBusinessVo = LogBusinessVo.builder()
                .requestId(requestId)
                .host(host)
                .hostAddress(hostAddress)
                .requestUri(requestUri)
                .requesBody(requesBody)
                .requestMethod(method)
                .responseBody(JSONObject.toJSONString(responseWrap.getDatas()))
                .responseCode(responseWrap.getCode())
                .responseMsg(responseWrap.getMsg())
                .userId(responseWrap.getUserId())
                .userName(responseWrap.getUserName())
                .build();
        String logBusinessVoJsonString = JSONObject.toJSONString(logBusinessVo);
        log.info("================logBusinessVoJsonString:{}",logBusinessVoJsonString);
        //??????????????????
        MqMessage mqMessage = MqMessage.builder()
                .id(messageId)
                .title("log-message")
                .content(logBusinessVoJsonString)
                .messageType("log-request")
                .produceTime(Timestamp.valueOf(LocalDateTime.now()))
                .sender(sender)
                .build();
        Message<MqMessage> message = MessageBuilder.withPayload(mqMessage).setHeader("type", "log-key").build();
        boolean flag = logSource.logOutput().send(message);
        log.info("?????????{}?????????{}",mqMessage.toString(),flag);
    }
}
