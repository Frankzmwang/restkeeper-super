package com.itheima.restkeeper.handler.cash;

import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.constant.TradingConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.BeforePayHandler;
import com.itheima.restkeeper.handler.CashPayHandler;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName CashPayHandlerImpl.java
 * @Description 现金结算
 */
@Component("cashPayHandler")
public class CashPayHandlerImpl implements CashPayHandler {

    @Autowired
    BeforePayHandler beforePayHandler;

    @Autowired
    ITradingService tradingService;

    @Override
    public TradingVo createCachTrading(TradingVo tradingVo) throws ProjectException {
        //1.1、交易前置处理：检测交易单参数
        Boolean flag = beforePayHandler.checkeCreateDownLineTrading(tradingVo);
        if (!flag){
            throw new ProjectException(TradingEnum.CASH_PAY_FAIL);
        }
        //1.2、交易前置处理：幂等性处理
        TradingVo tradingVoResult = beforePayHandler.idempotentCreateDownLineTrading(tradingVo);
        //2、指定订单状态
        tradingVo.setResultCode(TradingConstant.YJS);
        tradingVo.setResultMsg(TradingConstant.YJS);
        tradingVo.setTradingState(TradingConstant.YJS);
        Trading trading = BeanConv.toBean(tradingVo, Trading.class);
        flag = tradingService.saveOrUpdate(trading);
        if (!flag){
            throw new ProjectException(TradingEnum.SAVE_OR_UPDATE_FAIL);
        }
        return BeanConv.toBean(trading,TradingVo.class);
    }

}
