package com.itheima.restkeeper.handler.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.constant.TradingCacheConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.ITradingHandler;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ShowApiService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description：支付宝交易处理实现类
 */
@Service("aliPayTradingHandler")
@Slf4j
public class AliPayTradingHandlerImpl implements ITradingHandler {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ITradingService tradingService;

    @Autowired
    ShowApiService showApiService;

    @Override
    public TradingVo doPay(TradingVo tradingVo) throws ProjectException {
        //1、从redis中获取当前企业支付config
        String key = TradingCacheConstant.ALI_PAY + tradingVo.getEnterpriseId();
        RBucket<Config> bucket = redissonClient.getBucket(key);
        Config config = bucket.get();
        //2、容器如果为空，抛出异常
        if (EmptyUtil.isNullOrEmpty(config)){
            throw  new ProjectException(TradingEnum.ENTERPRISEID_EMPT);
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
                //5.6、指定非退款交易
                tradingVo.setIsRefund(SuperConstant.NO);
                Trading trading = BeanConv.toBean(tradingVo, Trading.class);
                //5.7、重新保存信息
                boolean flag = tradingService.saveOrUpdate(trading);
                if (!flag){
                    throw new ProjectException(TradingEnum.SAVE_OR_UPDATE_FAIL);
                }
                //6、生成二维码，返回结果
                String imgUrl = showApiService.handlerQRcode(tradingVo.getPlaceOrderMsg());
                TradingVo tradingVoResult = BeanConv.toBean(trading, TradingVo.class);
                tradingVoResult.setImgUrl(imgUrl);
                return tradingVoResult;
            }else {
                throw new ProjectException(TradingEnum.FACE_TO_FACE_FAIL);
            }
        } catch (Exception e) {
            log.error("支付宝统一下单失败：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.FACE_TO_FACE_FAIL);
        }
    }

    @Override
    public TradingVo queryPay(TradingVo tradingVo) throws ProjectException {
        //1、从IOC容器中获取当前企业支付config
        String key = TradingCacheConstant.ALI_PAY + tradingVo.getEnterpriseId();
        RBucket<Config> bucket = redissonClient.getBucket(key);
        Config config = bucket.get();
        //2、容器如果为空，抛出异常
        if (EmptyUtil.isNullOrEmpty(config)){
            throw  new ProjectException(TradingEnum.ENTERPRISEID_EMPT);
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
                //当前交易状态：
                // WAIT_BUYER_PAY（交易创建，等待买家付款）、
                // TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                // TRADE_SUCCESS（交易支付成功）
                // TRADE_FINISHED（交易结束，不可退款）
                String tradeStatus = queryResponse.getTradeStatus();
                boolean flag = false;
                //5.1、未付款交易超时关闭，或支付完成后全额退款
                if (SuperConstant.ALI_TRADE_CLOSED.equals(tradeStatus)){
                    tradingVo.setTradingState(SuperConstant.QXDD);
                    flag= true;
                }
                //5.2、交易支付成功
                if (SuperConstant.ALI_TRADE_SUCCESS.equals(tradeStatus)||
                    SuperConstant.ALI_TRADE_FINISHED.equals(tradeStatus)){
                    tradingVo.setTradingState(SuperConstant.YJS);
                    flag= true;
                }
                //6、修改交易单状态
                if (flag){
                    tradingVo.setResultCode(queryResponse.getSubCode());
                    tradingVo.setResultMsg(queryResponse.getSubMsg());
                    tradingVo.setResultJson(JSONObject.toJSONString(queryResponse));
                    Trading trading = BeanConv.toBean(tradingVo, Trading.class);
                    Boolean flagSaveOrUpdate = tradingService.saveOrUpdate(trading);
                    if (!flagSaveOrUpdate){
                        throw new ProjectException(TradingEnum.SAVE_OR_UPDATE_FAIL);
                    }
                    //7、返回结果
                    return BeanConv.toBean(trading, TradingVo.class);
                }
            }else {
                throw new ProjectException(TradingEnum.QUERY_FACE_TO_FACE_FAIL);
            }
        } catch (Exception e) {
            log.error("查询支付宝统一下单失败：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.QUERY_FACE_TO_FACE_FAIL);
        }
        return null;
    }
}
