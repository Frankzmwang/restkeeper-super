package com.itheima.restkeeper.handler;

import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.TradingVo;

/**
 * @ClassName ITradingHandler.java
 * @Description 支付处理接口
 */
public interface ITradingHandler {

    /***
     * @description 支付业务接口
     * @param tradingVo
     * @return: com.itheima.springcloud.req.TradingVo
     */
    TradingVo doPay(TradingVo tradingVo) throws ProjectException;

    /***
     * @description 扫码支付结果查询接口
     * @param tradingVo
     * @return: com.itheima.springcloud.req.TradingVo
     */
    TradingVo queryPay(TradingVo tradingVo) throws ProjectException;
}
