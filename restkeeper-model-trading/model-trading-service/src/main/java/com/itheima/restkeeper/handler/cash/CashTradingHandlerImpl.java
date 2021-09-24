package com.itheima.restkeeper.handler.cash;

import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.ITradingHandler;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName cashTradingHandlerImpl.java
 * @Description 现金交易处理实现类
 */
@Service("cashTradingHandler")
@Slf4j
public class CashTradingHandlerImpl implements ITradingHandler {

    @Autowired
    ITradingService tradingService;

    @Override
    public TradingVo doPay(TradingVo tradingVo) throws ProjectException {
        //指定
        tradingVo.setResultCode(TradingEnum.TRADING_SUCCEED.getCode());
        tradingVo.setResultMsg(TradingEnum.TRADING_SUCCEED.getMsg());
        tradingVo.setTradingState(SuperConstant.YJS);
        tradingVo.setIsRefund(SuperConstant.NO);
        Trading trading = BeanConv.toBean(tradingVo, Trading.class);
        boolean flag = tradingService.saveOrUpdate(trading);
        if (!flag){
            throw new ProjectException(TradingEnum.SAVE_OR_UPDATE_FAIL);
        }
        return BeanConv.toBean(trading,TradingVo.class);
    }

    @Override
    public TradingVo queryPay(TradingVo tradingVo) throws ProjectException {
        Trading trading = tradingService.findTradingByProductTradingOrderNo(tradingVo.getTradingOrderNo());
        return BeanConv.toBean(trading,TradingVo.class);
    }
}
