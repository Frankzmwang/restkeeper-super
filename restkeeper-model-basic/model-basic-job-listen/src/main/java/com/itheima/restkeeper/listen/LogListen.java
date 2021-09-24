package com.itheima.restkeeper.listen;

import com.alibaba.fastjson.JSONObject;
import com.itheima.restkeeper.pojo.LogBusiness;
import com.itheima.restkeeper.pojo.MqMessage;
import com.itheima.restkeeper.req.LogBusinessVo;
import com.itheima.restkeeper.service.ILogBusinessService;
import com.itheima.restkeeper.sink.LogSink;
import com.itheima.restkeeper.utils.BeanConv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @ClassName LogListen.java
 * @Description 日志监听
 */
@Slf4j
@Component
public class LogListen {

    @Autowired
    ILogBusinessService logBusinessService;

    @StreamListener(LogSink.LOG_INPUT)
    public void onMessage(@Payload MqMessage rocketMqMessage) {
        String jsonConten = rocketMqMessage.getConten();
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), rocketMqMessage);
        LogBusinessVo logBusinessVo= JSONObject.parseObject(jsonConten,LogBusinessVo.class);
        logBusinessService.save(BeanConv.toBean(logBusinessVo, LogBusiness.class));
    }
}
