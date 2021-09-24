package com.itheima.restkeeper.handler.refund;

import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.ITradingHandler;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName RefundHandlerImpl.java
 * @Description 退款交易处理
 */
@Service("refundHandler")
public class RefundHandlerImpl implements ITradingHandler {

    @Autowired
    ITradingService tradingService;

    @Override
    public TradingVo doPay(TradingVo tradingVo) throws ProjectException {
        tradingVo.setTradingState(SuperConstant.YJS);
        //指定此订单为退款交易单
        tradingVo.setIsRefund(SuperConstant.YES);
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
