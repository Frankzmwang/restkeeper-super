package com.itheima.restkeeper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.req.StoreVo;

import java.util.List;

/**
 * @ClassName StoreFace.java
 * @Description 门店dubbo服务
 */
public interface StoreFace {

    /**
     * @Description 品牌列表
     * @param storeVo 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return Page<BrandVo>
     */
    Page<StoreVo> findStoreVoPage(StoreVo storeVo, int pageNum, int pageSize);

    /**
     * @Description 创建门店
     * @param storeVo 对象信息
     * @return StoreVo
     */
    StoreVo createStore(StoreVo storeVo);

    /**
     * @Description 修改门店
     * @param storeVo 对象信息
     * @return Boolean
     */
    Boolean updateStore(StoreVo storeVo);

    /**
     * @Description 删除门店
     * @param checkedIds 选择对象信息Id
     * @return Boolean
     */
    Boolean deleteStore(String[] checkedIds);


    /**
     * @Description 查找门店
     * @param storeId 选择对象信息Id
     * @return StoreVo
     */
    StoreVo findStoreByStoreId(Long storeId);

    /***
     * @description 查询门店下拉框
     * @return: List<StoreVo>
     */
    List<StoreVo> findStoreVoList();
}
