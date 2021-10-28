package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.TradingSettingFace;
import com.itheima.restkeeper.enums.TradingSettingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.TradingSetting;
import com.itheima.restkeeper.req.TradingSettingVo;
import com.itheima.restkeeper.service.ITradingSettingService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName TradingSettingFaceImpl.java
 * @Description 企业配置
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}", timeout = 5000,
        methods = {
                @Method(name = "findTradingSettingVoPage", retries = 2),
                @Method(name = "createTradingSetting", retries = 0),
                @Method(name = "updateTradingSetting", retries = 0),
                @Method(name = "deleteTradingSetting", retries = 0),
                @Method(name = "saveOrUpdateTradingSetting", retries = 0),
                @Method(name = "findTradingSettingByEnterpriseId", retries = 2)

        })
public class TradingSettingFaceImpl implements TradingSettingFace {

    @Autowired
    ITradingSettingService tradingSettingService;

    @Override
    public Page<TradingSettingVo> findTradingSettingVoPage(TradingSettingVo enterpriseSettingVo,
                                                           int pageNum,
                                                           int pageSize)throws ProjectException{
        Page<TradingSetting> page = tradingSettingService.findTradingSettingVoPage(enterpriseSettingVo, pageNum, pageSize);
        Page<TradingSettingVo> pageVo = new Page<>();
        BeanConv.toBean(page, pageVo);
        //结果集转换
        List<TradingSetting> enterpriseList = page.getRecords();
        List<TradingSettingVo> enterpriseVoList = BeanConv.toBeanList(enterpriseList,TradingSettingVo.class);
        pageVo.setRecords(enterpriseVoList);
        return pageVo;
    }

    @Override
    public TradingSettingVo createTradingSetting(TradingSettingVo enterpriseSettingVo) throws ProjectException{
        return BeanConv.toBean(tradingSettingService.createTradingSetting(enterpriseSettingVo), TradingSettingVo.class);
    }

    @Override
    public Boolean updateTradingSetting(TradingSettingVo enterpriseSettingVo)throws ProjectException {
        return tradingSettingService.updateTradingSetting(enterpriseSettingVo);
    }

    @Override
    public Boolean deleteTradingSetting(String[] checkedIds)throws ProjectException {
        return tradingSettingService.deleteTradingSetting(checkedIds);
    }

    @Override
    public TradingSettingVo saveOrUpdateTradingSetting(TradingSettingVo tradingSettingVo)throws ProjectException {
        try {
            TradingSetting tradingSetting = BeanConv.toBean(tradingSettingVo, TradingSetting.class);
            tradingSettingService.saveOrUpdate(tradingSetting);
            return BeanConv.toBean(tradingSetting, TradingSettingVo.class);
        } catch (Exception e) {
            log.error("修改支付配置异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingSettingEnum.UPDATE_FAIL);
        }

    }

    @Override
    public TradingSettingVo findTradingSettingByEnterpriseId() throws ProjectException{
        try {
            TradingSetting tradingSetting = tradingSettingService.getOne(new QueryWrapper<>());
            return BeanConv.toBean(tradingSetting, TradingSettingVo.class);
        } catch (Exception e) {
            log.error("查询支付配置异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingSettingEnum.SELECT_FAIL);
        }
    }
}
