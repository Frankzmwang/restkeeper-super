package com.itheima.restkeeper.constant;

/**
 * @ClassName TradingCacheConstant.java
 * @Description 交易缓存维护
 */
public class TradingCacheConstant {

    //默认redis等待时间
    public static final int REDIS_WAIT_TIME = 5;

    //默认redis自动释放时间
    public static final int REDIS_LEASETIME = 4;

    //安全组前缀
    public static final String REDIS_GROUP = "trading:";

    //运营组
    public static final String DO_PAY = REDIS_GROUP+ "doPay:";

    //交易配置文件key
    public static final String ALI_PAY = REDIS_GROUP+ "aliPay:";
}
