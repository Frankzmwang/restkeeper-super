package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.StoreFace;
import com.itheima.restkeeper.pojo.Store;
import com.itheima.restkeeper.req.StoreVo;
import com.itheima.restkeeper.service.IStoreService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName StoreFaceImpl.java
 * @Description 门店dubbbo服务实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findStoreVoPage",retries = 2),
                @Method(name = "createStore",retries = 0),
                @Method(name = "updateStore",retries = 0),
                @Method(name = "deleteStore",retries = 0)
        })
public class StoreFaceImpl implements StoreFace {

    @Autowired
    IStoreService storeService;

    @Override
    public Page<StoreVo> findStoreVoPage(StoreVo storeVo, int pageNum, int pageSize) {
        //构建分页对象
        Page<Store> page = storeService.findStoreVoPage(storeVo, pageNum, pageSize);
        Page<StoreVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Store> storeList = page.getRecords();
        List<StoreVo> storeVoList = BeanConv.toBeanList(storeList,StoreVo.class);
        pageVo.setRecords(storeVoList);
        return pageVo;
    }

    @Override
    public StoreVo createStore(StoreVo storeVo) {
        return BeanConv.toBean( storeService.createStore(storeVo), StoreVo.class);
    }

    @Override
    public Boolean updateStore(StoreVo storeVo) {
        return storeService.updateStore(storeVo);
    }

    @Override
    public Boolean deleteStore(String[] checkedIds) {
        return storeService.deleteStore(checkedIds);
    }

    @Override
    public StoreVo findStoreByStoreId(Long storeId) {
        Store store = storeService.getById(storeId);
        if (!EmptyUtil.isNullOrEmpty(store)){
            return BeanConv.toBean(store,StoreVo.class);
        }
        return null;
    }

    @Override
    public List<StoreVo> findStoreVoList() {
        return BeanConv.toBeanList(storeService.findStoreVoList(),StoreVo.class);
    }
}
