package com.itheima.restkeeper.constant;

/**
 * @ClassName TardingConstant.java
 * @Description TODO
 */
public class TradingConstant {

    /***
     * 交易渠道:阿里支付
     */
    public static final String TRADING_CHANNEL_ALI_PAY = "ALI_PAY";

    /***
     * WAIT_BUYER_PAY【阿里订单状态：交易创建，等待买家付款】
     */
    public static final String ALI_WAIT_BUYER_PAY ="WAIT_BUYER_PAY";

    /***
     * TRADE_CLOSED【阿里订单状态：未付款交易超时关闭，或支付完成后全额退款】
     */
    public static final String ALI_TRADE_CLOSED ="TRADE_CLOSED";

    /***
     * TRADE_SUCCESS【阿里订单状态：交易支付成功】
     */
    public static final String ALI_TRADE_SUCCESS="TRADE_SUCCESS";

    /***
     * TRADE_FINISHED【阿里订单状态：交易结束，不可退款）
     */
    public static final String ALI_TRADE_FINISHED ="TRADE_FINISHED";

    /***
     * 交易渠道:微信支付
     */
    public static final String TRADING_CHANNEL_WE_CHAT_PAY = "WE_CHAT_PAY";


}
