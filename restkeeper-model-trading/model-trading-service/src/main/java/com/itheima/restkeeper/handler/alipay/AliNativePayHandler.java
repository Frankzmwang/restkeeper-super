package com.itheima.restkeeper.handler.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.constant.TradingCacheConstant;
import com.itheima.restkeeper.constant.TradingConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.NativePayHandler;
import com.itheima.restkeeper.handler.alipay.config.AlipayConfig;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ShowApiService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName AliNativePayHandler.java
 * @Description 支付宝Native支付方式：商户生成二维码，用户扫描支付
 */
@Slf4j
@Component
public class AliNativePayHandler implements NativePayHandler {

    @Autowired
    AlipayConfig alipayConfig;

    @Autowired
    ITradingService tradingService;

    @Override
    public TradingVo createDownLineTrading(TradingVo tradingVo) throws ProjectException {
        //1、获得支付宝配置文件
        Config config = alipayConfig.queryConfig(tradingVo.getTradingChannel(),
                tradingVo.getEnterpriseId());
        //2、容器如果为空，抛出异常
        if (EmptyUtil.isNullOrEmpty(config)){
            throw  new ProjectException(TradingEnum.CONFIG_EMPT);
        }
        //3、Factory使用配置
        Factory.setOptions(config);
        try {
            //4、调用支付宝API面对面支付【1】：支付的备注
            AlipayTradePrecreateResponse precreateResponse = Factory
                .Payment
                .FaceToFace()
                .preCreate(tradingVo.getMemo(),
                    String.valueOf(tradingVo.getTradingOrderNo()),
                    String.valueOf(tradingVo.getTradingAmount()));
            //5、受理结果【只表示请求是否成功，而不是支付是否成功】
            boolean isSuccess = ResponseChecker.success(precreateResponse);
            //5.1、受理成功：修改交易单
            if (isSuccess){
                String subCode = precreateResponse.getCode();
                String subMsg = precreateResponse.getQrCode();
                //5.2、指定统一下单code
                tradingVo.setPlaceOrderCode(subCode);
                //5.3、指定统一下单返回信息
                tradingVo.setPlaceOrderMsg(subMsg);
                //5.4、指定统一下单json字符串
                tradingVo.setPlaceOrderJson(JSONObject.toJSONString(precreateResponse));
                //5.5、指定交易状态
                tradingVo.setTradingState(SuperConstant.FKZ);
                //5.7、重新保存信息
                Trading trading = BeanConv.toBean(tradingVo, Trading.class);
                boolean flag = tradingService.saveOrUpdate(trading);
                if (!flag){
                    throw new ProjectException(TradingEnum.SAVE_OR_UPDATE_FAIL);
                }
                //6、返回结果
                return BeanConv.toBean(trading, TradingVo.class);
            }else {
                throw new ProjectException(TradingEnum.FACE_TO_FACE_FAIL);
            }
        } catch (Exception e) {
            log.error("支付宝统一下单创建失败：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.FACE_TO_FACE_FAIL);
        }
    }

    @Override
    public void queryDownLineTrading(TradingVo tradingVo) throws ProjectException {
        //1、获得支付宝配置文件
        Config config = alipayConfig.queryConfig(tradingVo.getTradingChannel(),
                tradingVo.getEnterpriseId());
        //2、容器如果为空，抛出异常
        if (EmptyUtil.isNullOrEmpty(config)){
            throw  new ProjectException(TradingEnum.CONFIG_EMPT);
        }
        //3、使用配置
        Factory.setOptions(config);
        try {
            //4、调用支付宝API：通用查询支付情况
            AlipayTradeQueryResponse queryResponse = Factory
                .Payment
                .Common()
                .query(String.valueOf(tradingVo.getTradingOrderNo()));
            boolean success = ResponseChecker.success(queryResponse);
            //5、响应成功，分析交易状态
            if (success){
                String tradeStatus = queryResponse.getTradeStatus();
                Boolean flag = null;
                //5.1、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                if (TradingConstant.ALI_TRADE_CLOSED.equals(tradeStatus)){
                    tradingVo.setTradingState(SuperConstant.QXDD);
                    flag= true;
                //5.2、TRADE_SUCCESS（交易支付成功）TRADE_FINISHED（交易结束，不可退款）
                }else if (TradingConstant.ALI_TRADE_SUCCESS.equals(tradeStatus)||
                    TradingConstant.ALI_TRADE_FINISHED.equals(tradeStatus)){
                    tradingVo.setTradingState(SuperConstant.YJS);
                    flag= true;
                //5.3、当前交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）不处理
                }else {
                    flag = false;
                }
                //6、修改交易单状态
                if (flag){
                    tradingVo.setResultCode(queryResponse.getSubCode());
                    tradingVo.setResultMsg(queryResponse.getSubMsg());
                    tradingVo.setResultJson(JSONObject.toJSONString(queryResponse));
                    Trading trading = BeanConv.toBean(tradingVo, Trading.class);
                    tradingService.saveOrUpdate(trading);
                }
            }
        } catch (Exception e) {
            log.warn("查询支付宝统一下单失败：{}", ExceptionsUtil.getStackTraceAsString(e));
        }
    }

    @Override
    public TradingVo refundDownLineTrading(TradingVo tradingVo) throws ProjectException {
        //1、获得支付宝配置文件
        Config config = alipayConfig.queryConfig(tradingVo.getTradingChannel(),
                tradingVo.getEnterpriseId());
        //2、容器如果为空，抛出异常
        if (EmptyUtil.isNullOrEmpty(config)){
            throw  new ProjectException(TradingEnum.CONFIG_EMPT);
        }
        //3、使用配置
        Factory.setOptions(config);
        try {
            AlipayTradeRefundResponse refundResponse = Factory.Payment
                .Common()
                .refund(String.valueOf(tradingVo.getTradingOrderNo()),
                        String.valueOf(tradingVo.getRefund()));
            boolean success = ResponseChecker.success(refundResponse);
            if (success){
                //4、指定此订单为退款交易单
                tradingVo.setIsRefund(SuperConstant.YES);
                //5、保存交易单信息
                Trading trading = BeanConv.toBean(tradingVo, Trading.class);
                boolean flag = tradingService.saveOrUpdate(trading);
                //6、保存退款单信息
            }else {
                throw new ProjectException(TradingEnum.REFUND_FAIL);
            }
        } catch (Exception e) {
            log.error("支付宝统一下单退款失败：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.REFUND_FAIL);
        }
        return null;
    }

    @Override
    public TradingVo QueryRefundDownLineTrading(TradingVo tradingVo) throws ProjectException {
        return null;
    }
}
