package com.itheima.restkeeper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.pojo.TradingSetting;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.restkeeper.req.TradingSettingVo;

import java.util.List;

/**
 * @Description： 服务类
 */
public interface ITradingSettingService extends IService<TradingSetting> {

    /**
     * @Description 企业配置列表
     * @param enterpriseSettingVo 查询条件
     * @return
     */
    Page<TradingSetting> findTradingSettingVoPage(TradingSettingVo enterpriseSettingVo, int pageNum, int pageSize);

    /**
     * @Description 创建企业配置
     * @param enterpriseSettingVo 对象信息
     * @return
     */
    TradingSetting createTradingSetting(TradingSettingVo enterpriseSettingVo);

    /**
     * @Description 修改企业配置
     * @param enterpriseSettingVo 对象信息
     * @return
     */
    Boolean updateTradingSetting(TradingSettingVo enterpriseSettingVo);

    /**
     * @Description 删除企业配置
     * @param checkedIds 选择IDS
     * @return
     */
    Boolean deleteTradingSetting(String[] checkedIds);

    /***
     * @description 查询所有有效的交易配置
     * @return
     */
    List<TradingSetting> findTradingSettingList();

}
