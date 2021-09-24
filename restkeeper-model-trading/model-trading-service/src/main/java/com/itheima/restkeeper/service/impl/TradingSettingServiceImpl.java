package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.alipay.config.AlipayConfig;
import com.itheima.restkeeper.pojo.TradingSetting;
import com.itheima.restkeeper.mapper.TradingSettingMapper;
import com.itheima.restkeeper.req.TradingSettingVo;
import com.itheima.restkeeper.service.ITradingSettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description： 服务实现类
 */
@Service
public class TradingSettingServiceImpl extends ServiceImpl<TradingSettingMapper, TradingSetting> implements ITradingSettingService {

    @Autowired
    TradingSettingMapper tradingSettingMapper;

    @Autowired
    AlipayConfig alipayConfig;

    @Override
    public Page<TradingSetting> findTradingSettingVoPage(TradingSettingVo tradingSettingVo, int pageNum, int pageSize) {
        Page<TradingSetting> page = new Page<>(pageNum,pageSize);
        QueryWrapper<TradingSetting> queryWrapper = new QueryWrapper<>();
        if (!EmptyUtil.isNullOrEmpty(tradingSettingVo.getEnterpriseId())) {
            queryWrapper.lambda().likeRight(TradingSetting::getEnterpriseId,tradingSettingVo.getEnterpriseId());
        }
        if (!EmptyUtil.isNullOrEmpty(tradingSettingVo.getEnableFlag())) {
            queryWrapper.lambda().eq(TradingSetting::getEnableFlag,tradingSettingVo.getEnableFlag());
        }
        return page(page, queryWrapper);
    }

    @Override
    public TradingSetting createTradingSetting(TradingSettingVo tradingSettingVo) {
        TradingSetting tradingSetting = BeanConv.toBean(tradingSettingVo, TradingSetting.class);
        boolean flag = save(tradingSetting);
        if (flag){
            //创建配置信息
            alipayConfig.createOrUpdateConfigToRedis(tradingSetting);
            return tradingSetting;
        }
        return null;
    }

    @Override
    public Boolean updateTradingSetting(TradingSettingVo tradingSettingVo) {
        TradingSetting tradingSetting = BeanConv.toBean(tradingSettingVo, TradingSetting.class);
        boolean flag = updateById(tradingSetting);
        if (flag){
            //修改配置信息
            alipayConfig.createOrUpdateConfigToRedis(tradingSetting);
        }
        return flag;
    }

    @Override
    public Boolean deleteTradingSetting(String[] checkedIds) {
        List<String> ids = Arrays.asList(checkedIds);
        List<Long> idsLong = new ArrayList<>();
        ids.forEach(n -> {
            idsLong.add(Long.valueOf(n));
        });
        //移除缓存中配置
        List<TradingSetting> tradingSettings = listByIds(idsLong);
        tradingSettings.forEach(tradingSetting->{
            alipayConfig.removeConfigToRedis(tradingSetting);
        });
        return removeByIds(idsLong);
    }

    @Override
    public List<TradingSetting> findTradingSettingList() {
        LambdaQueryWrapper<TradingSetting> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(TradingSetting::getEnableFlag, SuperConstant.YES);
        return list(lambdaQueryWrapper);
    }
}
