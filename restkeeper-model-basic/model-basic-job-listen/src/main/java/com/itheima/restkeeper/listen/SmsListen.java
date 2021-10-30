package com.itheima.restkeeper.listen;

import com.alibaba.fastjson.JSONObject;
import com.itheima.restkeeper.adapter.SmsSendAdapter;
import com.itheima.restkeeper.pojo.LogBusiness;
import com.itheima.restkeeper.pojo.MqMessage;
import com.itheima.restkeeper.req.LogBusinessVo;
import com.itheima.restkeeper.req.SendMessageVo;
import com.itheima.restkeeper.service.ILogBusinessService;
import com.itheima.restkeeper.sink.LogSink;
import com.itheima.restkeeper.sink.SmsSink;
import com.itheima.restkeeper.utils.BeanConv;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName SmsListen.java
 * @Description 短信监听
 */
@Component
@Slf4j
public class SmsListen {

    @Autowired
    SmsSendAdapter smsSendAdapter;

    @StreamListener(SmsSink.SMS_INPUT)
    public void onMessage(@Payload MqMessage message,
                          @Header(AmqpHeaders.CHANNEL) Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws IOException {
        String jsonConten = message.getConten();
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        SendMessageVo sendMessageVo = JSONObject.parseObject(jsonConten, SendMessageVo.class);
        boolean flag = smsSendAdapter.SendSms(
                sendMessageVo.getTemplateNo(),
                sendMessageVo.getSginNo(),
                sendMessageVo.getLoadBalancerType(),
                sendMessageVo.getMobiles(),
                sendMessageVo.getTemplateParam());
        if (flag){
            channel.basicAck(deliveryTag,false);
        }
    }
}
