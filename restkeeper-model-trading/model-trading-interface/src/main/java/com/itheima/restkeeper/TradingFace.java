package com.itheima.restkeeper;

import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.OrderVo;
import com.itheima.restkeeper.req.TradingVo;

import java.util.List;

/**
 * @ClassName TradingFace.java
 * @Description 交易dubbo接口定义
 */
public interface TradingFace {

    /***
     * @description 支付业务接口
     * @param tradingVo 交易订单
     * @return: com.itheima.restkeeper.req.TradingVo
     */
    TradingVo doPay(TradingVo tradingVo) throws ProjectException;

    /***
     * @description 查询交易结果
     * @param tradingVo 交易订单
     * @return: com.itheima.restkeeper.req.TradingVo
     */
    TradingVo queryPay(TradingVo tradingVo) throws ProjectException;


}
