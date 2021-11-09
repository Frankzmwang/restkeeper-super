package com.itheima.restkeeper.handler.wechat;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.BeforePayHandler;
import com.itheima.restkeeper.handler.NativePayHandler;
import com.itheima.restkeeper.handler.alipay.config.AlipayConfig;
import com.itheima.restkeeper.handler.wechat.client.WechatPayClient;
import com.itheima.restkeeper.handler.wechat.config.WechatPayConfig;
import com.itheima.restkeeper.req.RefundRecordVo;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.IRefundRecordService;
import com.itheima.restkeeper.service.ITradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName WechatNativePayHandler.java
 * @Description TODO
 */
@Component
public class WechatNativePayHandler implements NativePayHandler {

    @Autowired
    WechatPayConfig wechatPayConfig;

    @Autowired
    ITradingService tradingService;

    @Autowired
    IRefundRecordService refundRecordService;

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Autowired
    BeforePayHandler aliBeforePayHandler;

    @Override
    public TradingVo createDownLineTrading(TradingVo tradingVo) throws ProjectException {
        WechatPayClient wechatPayClient = wechatPayConfig.queryConfig(tradingVo.getEnterpriseId());
        wechatPayClient.preCreate(
                String.valueOf(tradingVo.getTradingOrderNo()),
                String.valueOf(tradingVo.getTradingAmount()),
                tradingVo.getMemo());
        return tradingVo;
    }

    @Override
    public void queryDownLineTrading(TradingVo tradingVo) throws ProjectException {

    }

    @Override
    public TradingVo refundDownLineTrading(TradingVo tradingVo) throws ProjectException {
        return null;
    }

    @Override
    public void queryRefundDownLineTrading(RefundRecordVo refundRecordVo) throws ProjectException {

    }
}
