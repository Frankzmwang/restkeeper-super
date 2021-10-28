package com.itheima.restkeeper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.TradingSettingVo;

/**
 * @ClassName ITradingSettingSettingFace.java
 * @Description 企业配置信息
 */
public interface TradingSettingFace {

    /**
     * @Description 交易配置列表
     * @param TradingSettingVo 查询条件
     * @return
     */
    Page<TradingSettingVo> findTradingSettingVoPage(TradingSettingVo TradingSettingVo,
                                                    int pageNum,
                                                    int pageSize)throws ProjectException;

    /**
     * @Description 创建交易配置
     * @param TradingSettingVo 对象信息
     * @return
     */
    TradingSettingVo createTradingSetting(TradingSettingVo TradingSettingVo)throws ProjectException;

    /**
     * @Description 修改交易配置
     * @param TradingSettingVo 对象信息
     * @return
     */
    Boolean updateTradingSetting(TradingSettingVo TradingSettingVo)throws ProjectException;

    /**
     * @Description 删除交易配置
     * @param checkedIds 选择IDS
     * @return
     */
    Boolean deleteTradingSetting(String[] checkedIds)throws ProjectException;

    /***
     * @description 保存或修改交易配置
     * @return 
     * @return: java.lang.Boolean
     */
    TradingSettingVo saveOrUpdateTradingSetting(TradingSettingVo tradingSettingVo)throws ProjectException;

    /***
     * @description 查询当前企业的交易配置
     * @return
     * @return: java.lang.Boolean
     */
    TradingSettingVo findTradingSettingByEnterpriseId()throws ProjectException;
}
