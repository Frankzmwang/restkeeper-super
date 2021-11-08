package com.itheima.restkeeper.adapter.impl;

import com.itheima.restkeeper.handler.BeforePayHandler;
import com.itheima.restkeeper.adapter.NativePayAdapter;
import com.itheima.restkeeper.constant.TradingCacheConstant;
import com.itheima.restkeeper.constant.TradingConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.NativePayHandler;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.RefundRecordVo;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.RegisterBeanHandler;
import com.itheima.restkeeper.utils.ShowApiService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName NativePayAdapterImpl.java
 * @Description Native支付方式适配实现：商户生成二维码，用户扫描支付
 */
@Slf4j
@Component
public class NativePayAdapterImpl implements NativePayAdapter {

    @Autowired
    ITradingService tradingService;

    @Autowired
    ShowApiService showApiService;

    @Autowired
    RegisterBeanHandler registerBeanHandler;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    BeforePayHandler beforePayHandler;

    private static Map<String,String> nativePayHandlers =new HashMap<>();

    static {
        nativePayHandlers.put(TradingConstant.TRADING_CHANNEL_ALI_PAY,"aliNativePayHandler");
        nativePayHandlers.put(TradingConstant.TRADING_CHANNEL_WECHAT_PAY,"weChatNativePayHandler");
    }

    @Override
    public String queryQrCodeUrl(TradingVo tradingVo) throws ProjectException {
        Trading trading = tradingService.findTradByTradingOrderNo(tradingVo.getTradingOrderNo());
        //之前生成过则直接返回
        if (!EmptyUtil.isNullOrEmpty(trading.getQrCodeUrl())){
            return trading.getQrCodeUrl();
        }
        //之前未生成直接调用万维易源生成二维码图片，并保存
        String qrCodeUrl = showApiService.handlerQRcode(trading.getQrCodeUrl());
        trading.setQrCodeUrl(qrCodeUrl);
        tradingService.updateById(trading);
        return qrCodeUrl;
    }

    @Override
    public TradingVo createDownLineTrading(TradingVo tradingVo) throws ProjectException {
        //1、对交易订单加锁
        Long productOrderNo = tradingVo.getProductOrderNo();
        String key =TradingCacheConstant.CREATE_PAY + productOrderNo;
        RLock lock = redissonClient.getFairLock(key);
        try {
            boolean flag = lock.tryLock(TradingCacheConstant.REDIS_WAIT_TIME, TimeUnit.SECONDS);
            if (flag){
                //2、调用统一收单线下交易预创建
                String nativePayHandlerString = nativePayHandlers.get(tradingVo.getTradingChannel());
                NativePayHandler nativePayHandler = registerBeanHandler
                        .getBean(nativePayHandlerString, NativePayHandler.class);
                TradingVo downLineTrading = nativePayHandler.createDownLineTrading(tradingVo);
                //3、构建支付二维码
                String qrCodeUrl = this.queryQrCodeUrl(downLineTrading);
                downLineTrading.setQrCodeUrl(qrCodeUrl);
                return downLineTrading;
            }else {
                throw new ProjectException(TradingEnum.NATIVE_PAY_FAIL);
            }
        } catch (Exception e) {
            log.error("统一收单线下交易预创建异常:{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.NATIVE_PAY_FAIL);
        }
    }

    @Override
    public void queryDownLineTrading(TradingVo tradingVo) throws ProjectException {
        String nativePayHandlerString = nativePayHandlers.get(tradingVo.getTradingChannel());
        NativePayHandler nativePayHandler = registerBeanHandler
                .getBean(nativePayHandlerString, NativePayHandler.class);
        nativePayHandler.queryDownLineTrading(tradingVo);
    }

    @Override
    public TradingVo refundDownLineTrading(TradingVo tradingVo) throws ProjectException {
        //1、对交易订单加锁
        Long productOrderNo = tradingVo.getProductOrderNo();
        String key =TradingCacheConstant.REFUND_PAY + productOrderNo;
        RLock lock = redissonClient.getFairLock(key);
        try {
            boolean flag = lock.tryLock(TradingCacheConstant.REDIS_WAIT_TIME, TimeUnit.SECONDS);
            if (flag){
                //2、统一收单交易退款接口
                String nativePayHandlerString = nativePayHandlers.get(tradingVo.getTradingChannel());
                NativePayHandler nativePayHandler = registerBeanHandler
                        .getBean(nativePayHandlerString, NativePayHandler.class);
                return nativePayHandler.refundDownLineTrading(tradingVo);
            }else {
                throw new ProjectException(TradingEnum.NATIVE_REFUND_FAIL);
            }
        } catch (Exception e) {
            log.error("统一收单交易退款接口异常:{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.NATIVE_REFUND_FAIL);
        }
    }

    @Override
    public void queryRefundDownLineTrading(RefundRecordVo refundRecordVo) throws ProjectException {
        String nativePayHandlerString = nativePayHandlers.get(refundRecordVo.getTradingChannel());
        NativePayHandler nativePayHandler = registerBeanHandler
                .getBean(nativePayHandlerString, NativePayHandler.class);
        nativePayHandler.queryRefundDownLineTrading(refundRecordVo);
    }
}
