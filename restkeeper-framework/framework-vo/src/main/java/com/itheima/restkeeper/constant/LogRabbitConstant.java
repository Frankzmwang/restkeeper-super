package com.itheima.restkeeper.constant;

/**
 * @ClassName LogRabbitConstant.java
 * @Description 日志队列定义
 */
public class LogRabbitConstant {

    //日志交换机
    public static final String LOG_EXCHANGE_NAME = "log.exchange.direct.ttl";

    //日志死信队列
    public static final String LOG_QUEUE_NAME = "log.queue";

    //日志routing
    public static final String LOG_ROUTING_KEY = "log.message";

}
