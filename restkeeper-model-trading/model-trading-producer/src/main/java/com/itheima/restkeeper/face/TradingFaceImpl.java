package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.restkeeper.TradingFace;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.constant.TradingCacheConstant;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.adapter.ITradingAdapter;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.service.ITradingService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TradingFaceImpl.java
 * @Description 交易实现类
 */
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
    methods ={
        @Method(name = "doPay",retries = 0),
        @Method(name = "queryPay",retries = 2)
    })
@Slf4j
public class TradingFaceImpl implements TradingFace {

    @Autowired
    ITradingAdapter tradingAdapter;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ITradingService tradingService;

    @Override
    @GlobalTransactional
    public TradingVo doPay(TradingVo tradingVo) throws ProjectException {
        //1、校验交易单完整性
        Boolean flag = this.doPayCheckTradingVo(tradingVo);
        if (!flag){
            throw  new ProjectException(TradingEnum.CHECK_TRADING_FAIL);
        }
        //2、加锁操作
        String key = TradingCacheConstant.DO_PAY+tradingVo.getProductOrderNo();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(
                TradingCacheConstant.REDIS_WAIT_TIME,
                TradingCacheConstant.REDIS_LEASETIME,
                TimeUnit.SECONDS)){
                log.info("======>开始创建支付订单tradingVo：{}",tradingVo.toString());
                //3、支付路由适配
                TradingVo tradingVoResult = tradingAdapter.doPay(tradingVo);
                log.info("======>返回创建支付订单tradingVo：{}",tradingVo.toString());
                return tradingVoResult;
            }
        } catch (InterruptedException e) {
            log.warn("支付订单加锁失败tradingVo：{}",tradingVo.toString());
            throw new ProjectException(TradingEnum.TRYLOCK_TRADING_FAIL);
        }finally {
            lock.unlock();
        }
        log.error("支付订单失败tradingVo：{}",tradingVo.toString());
        throw new ProjectException(TradingEnum.PAYING_TRADING_FAIL);
    }

    /***
     * @description 交易单检查
     * @return
     * @return: java.lang.Boolean
     */
    private Boolean doPayCheckTradingVo(TradingVo tradingVo){
        Boolean flag =null;
        //订单为空
        if (EmptyUtil.isNullOrEmpty(tradingVo)) {
            flag = false;
        //订单号为空
        }else if (EmptyUtil.isNullOrEmpty(tradingVo.getProductOrderNo())){
            flag = false;
        //企业号为空
        }else if (EmptyUtil.isNullOrEmpty(tradingVo.getEnterpriseId())){
            flag = false;
        //交易金额为空
        }else if (EmptyUtil.isNullOrEmpty(tradingVo.getTradingAmount())){
            flag = false;
        //支付渠道为空
        }else if (EmptyUtil.isNullOrEmpty(tradingVo.getTradingChannel())){
            flag=false;
        }else {
            flag = true;
        }
        return flag;
    }

    @Override
    @GlobalTransactional
    public TradingVo queryPay(TradingVo tradingVo) throws ProjectException {
        //校验交易单完整性
        Boolean flag = this.queryPayCheckTradingVo(tradingVo);
        if (!flag){
            throw  new ProjectException(TradingEnum.CHECK_TRADING_FAIL);
        }
        log.info("======>开始查询支付结果：{}",tradingVo.toString());
        TradingVo tradingVoResult = tradingAdapter.queryPay(tradingVo);
        log.info("======>返回创建支付结果：{}",tradingVo.toString());
        return tradingVoResult;
    }

    private Boolean queryPayCheckTradingVo(TradingVo tradingVo){
        Boolean flag =null;
        //支付渠道为空
        if (EmptyUtil.isNullOrEmpty(tradingVo.getTradingChannel())){
            flag=false;
        }else {
            flag = true;
        }
        return flag;
    }


}
