package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.DataDictFace;
import com.itheima.restkeeper.constant.DictCacheConstant;
import com.itheima.restkeeper.pojo.DataDict;
import com.itheima.restkeeper.req.DataDictVo;
import com.itheima.restkeeper.service.IDataDictService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DataDictFaceImpl.java
 * @Description 数字字典接口实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findDataDictVoPage",retries = 2),
                @Method(name = "saveDataDict",retries = 0),
                @Method(name = "updateDataDict",retries = 0),
                @Method(name = "findDataDictVoById",retries = 2),
                @Method(name = "updateByDataKey",retries = 0),
                @Method(name = "checkByDataKey",retries = 2),
                @Method(name = "findValueByDataKey",retries = 2),
                @Method(name = "findValueByDataKeys",retries = 2),
                @Method(name = "findDataDictVoByParentKey",retries = 2)
        })
public class DataDictFaceImpl implements DataDictFace {

    @Autowired
    IDataDictService dataDictService;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public Page<DataDictVo> findDataDictVoPage(DataDictVo dataDictVo,int pageNum, int pageSize) {
        Page<DataDict> page = dataDictService.findDataDictVoPage(dataDictVo, pageNum, pageSize);
        Page<DataDictVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<DataDict> routeList = page.getRecords();
        List<DataDictVo> routeVoList = BeanConv.toBeanList(routeList,DataDictVo.class);
        pageVo.setRecords(routeVoList);
        return pageVo;
    }

    @Override
    public DataDictVo saveDataDict(DataDictVo dataDictVo) {
        Boolean flag = false;
        String dataKey = dataDictVo.getDataKey();
        //添加锁
        RLock lock = redissonClient.getLock(DictCacheConstant.LOCK_PREFIX + dataKey);
        DataDict dataDict =null;
        try {
            if (lock.tryLock(
                    DictCacheConstant.REDIS_WAIT_TIME,
                    DictCacheConstant.REDIS_LEASETIME,
                    TimeUnit.MILLISECONDS)){
                flag = this.checkByDataKey(dataKey);
                if (flag){
                    //保存或修改信息
                    dataDict =BeanConv.toBean(dataDictVo,DataDict.class);
                    dataDictService.save(dataDict);
                }
            }
        } catch (InterruptedException ex) {
            log.warn("confirmPayment:确认提交失败：{}", ExceptionsUtil.getStackTraceAsString(ex));
        }finally {
            lock.unlock();
        }
        return BeanConv.toBean(dataDict,DataDictVo.class);
    }

    @Override
    public Boolean updateDataDict(DataDictVo dataDictVo) {
        Boolean flag = false;
        String dataKey = dataDictVo.getDataKey();
        //添加锁
        RLock lock = redissonClient.getLock(DictCacheConstant.LOCK_PREFIX + dataKey);
        DataDict dataDict =null;
        try {
            if (lock.tryLock(
                    DictCacheConstant.REDIS_WAIT_TIME,
                    DictCacheConstant.REDIS_LEASETIME,
                    TimeUnit.MILLISECONDS)){
                flag = this.checkByDataKey(dataKey);
                if (flag){
                    //保存或修改信息
                    dataDict =BeanConv.toBean(dataDictVo,DataDict.class);
                    flag = dataDictService.updateById(dataDict);
                }
            }
        } catch (InterruptedException ex) {
            log.warn("confirmPayment:修改提交失败：{}", ExceptionsUtil.getStackTraceAsString(ex));
        }finally {
            lock.unlock();
        }
        return flag;
    }

    @Override
    public DataDictVo findDataDictVoById(String dataDictId) {
        DataDict dataDict = dataDictService.getById(dataDictId);
        return BeanConv.toBean(dataDict, DataDictVo.class);
    }

    @Override
    public Boolean updateByDataKey(List<String> dataKeys,String enableFlag) {
        return dataDictService.updateByDataKey(dataKeys,enableFlag);
    }

    @Override
    public Boolean checkByDataKey(String dataKey) {
        return dataDictService.checkByDataKey(dataKey);
    }

    @Override
    public String findValueByDataKey(String dataKey) {
        return dataDictService.findValueByDataKey(dataKey);
    }

    @Override
    public List<DataDictVo> findValueByDataKeys(List<String> dataKeys) {
        List<DataDict> list = dataDictService.findValueByDataKeys(dataKeys);
        return BeanConv.toBeanList(list,DataDictVo.class);
    }

    @Override
    public List<DataDictVo> findDataDictVoByParentKey(String parentKey) {
        List<DataDict> list = dataDictService.findDataDictByParentKey(parentKey);
        return BeanConv.toBeanList(list,DataDictVo.class);
    }
}
