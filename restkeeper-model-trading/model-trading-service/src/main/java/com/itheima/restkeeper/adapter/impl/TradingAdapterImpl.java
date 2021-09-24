package com.itheima.restkeeper.adapter.impl;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.itheima.restkeeper.adapter.ITradingAdapter;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.handler.ITradingHandler;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ShowApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ITradingAdapterHandlerImpl.java
 * @Description 交易接口适配器实现
 */
@Slf4j
@Service
public class TradingAdapterImpl implements ITradingAdapter {

    @Autowired
    ITradingHandler aliPayTradingHandler;

    @Autowired
    ITradingHandler cashTradingHandler;

    @Autowired
    ITradingHandler freeChargeHandler;

    @Autowired
    ITradingHandler refundHandler;

    @Autowired
    ShowApiService showApiService;

    @Autowired
    ITradingService tradingService;

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Override
    public TradingVo doPay(TradingVo tradingVo) throws ProjectException {
        //1、查询业务交易单
        Trading trading = tradingService.findTradingByProductOrderNo(tradingVo.getProductOrderNo());
        //2、查看二维码：交易单不为空，且交易状态为待支付,且是线上支付
        if (!EmptyUtil.isNullOrEmpty(trading)
                &&trading.getTradingState().equals(SuperConstant.FKZ)
                &&trading.getTradingChannel().equals(SuperConstant.TRADING_CHANNEL_ALIPAY)){
            TradingVo tradingVoResult = BeanConv.toBean(trading,TradingVo.class);
            String imgUrl = showApiService.handlerQRcode(tradingVoResult.getPlaceOrderMsg());
            tradingVoResult.setImgUrl(imgUrl);
            return tradingVoResult;
        }
        //3、幂等性处理
        tradingVo = this.idempotenceTradingVo(trading,tradingVo);
        //4、交易路由选择
        return this.routeTrading(tradingVo);
    }

    /***
     * @description 幂等性处理
     * @param trading
     * @return
     */
    private TradingVo idempotenceTradingVo(Trading trading,TradingVo tradingVo){
        //3.1、退单操作：交易单不为空，交易状态为成功，渠道为退款操作
        if (!EmptyUtil.isNullOrEmpty(trading)
                &&trading.getTradingState().equals(SuperConstant.YJS)
                &&SuperConstant.TRADING_CHANNEL_REFUND.equals(tradingVo.getTradingChannel())){
            tradingVo.setId(trading.getId());
            log.info("退款支付：{}",tradingVo.toString());
        //3.2、重新支付：交易单不为空，且交易状态为关闭，说明交易超时，废弃当前的交易号
        }else if (!EmptyUtil.isNullOrEmpty(trading)
                &&trading.getTradingState().equals(SuperConstant.QXDD)){
            tradingVo.setId(trading.getId());
            tradingVo.setTradingOrderNo((Long) identifierGenerator.nextId(tradingVo));
        //3.3、重复支付:如果交易单不为空，且交易状态为成功，则直接抛出异常，告知交易单已完成
        }else if (!EmptyUtil.isNullOrEmpty(trading)
                &&trading.getTradingState().equals(SuperConstant.YJS)){
            throw  new ProjectException(TradingEnum.TRADING_STATE_SUCCEED);
        //3.4、首次支付
        }else {
            tradingVo.setTradingOrderNo((Long) identifierGenerator.nextId(tradingVo));
            log.info("首次支付：{}",tradingVo.toString());
        }
        return tradingVo;
    }

    /***
     * @description 交易路由选择
     *
     * @param tradingVo
     * @return
     */
    private TradingVo routeTrading(TradingVo tradingVo){
        //4.1、支付宝
        if (SuperConstant.TRADING_CHANNEL_ALIPAY.equals(tradingVo.getTradingChannel())){
            return aliPayTradingHandler.doPay(tradingVo);
        //4.2、现金
        }else if(SuperConstant.TRADING_CHANNEL_CASHPAY.equals(tradingVo.getTradingChannel())){
            return cashTradingHandler.doPay(tradingVo);
        //4.3、免单
        }else if(SuperConstant.TRADING_CHANNEL_FREE_CHARGE.equals(tradingVo.getTradingChannel())){
            return freeChargeHandler.doPay(tradingVo);
        //4.4、退款
        }else if(SuperConstant.TRADING_CHANNEL_REFUND.equals(tradingVo.getTradingChannel())){
            return refundHandler.doPay(tradingVo);
        }else {
            return null;
        }
    }

    @Override
    public TradingVo queryPay(TradingVo tradingVo) throws ProjectException {
        //1、查询业务交易单
        Trading trading = tradingService.findTradingByProductOrderNo(tradingVo.getProductOrderNo());
        TradingVo tradingVoRestult = BeanConv.toBean(trading,TradingVo.class);
        //支付宝
        if (SuperConstant.TRADING_CHANNEL_ALIPAY.equals(tradingVo.getTradingChannel())){
            return aliPayTradingHandler.queryPay(tradingVoRestult);
        }
        //现金
        else if(SuperConstant.TRADING_CHANNEL_CASHPAY.equals(tradingVo.getTradingChannel())){
            return cashTradingHandler.queryPay(tradingVoRestult);
        }
        //免单
        else if(SuperConstant.TRADING_CHANNEL_FREE_CHARGE.equals(tradingVo.getTradingChannel())){
            return freeChargeHandler.queryPay(tradingVoRestult);
        }else {
            return null;
        }
    }


}
