package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.restkeeper.pojo.Trading;
import com.itheima.restkeeper.mapper.TradingMapper;
import com.itheima.restkeeper.service.ITradingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description：交易订单表 服务实现类
 */
@Service
public class TradingServiceImpl extends ServiceImpl<TradingMapper, Trading> implements ITradingService {

    @Override
    public Trading findTradingByProductOrderNo(Long productOrderNo) {
        LambdaQueryWrapper<Trading> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Trading::getProductOrderNo,productOrderNo);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public Trading findTradingByProductTradingOrderNo(Long tradingOrderNo) {
        LambdaQueryWrapper<Trading> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Trading::getTradingOrderNo,tradingOrderNo);
        return getOne(lambdaQueryWrapper);
    }
}
