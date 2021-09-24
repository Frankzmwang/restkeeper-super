package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.DishFace;
import com.itheima.restkeeper.constant.AppletCacheConstant;
import com.itheima.restkeeper.enums.DishEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.Dish;
import com.itheima.restkeeper.pojo.DishFlavor;
import com.itheima.restkeeper.req.DishVo;
import com.itheima.restkeeper.service.IDishFlavorService;
import com.itheima.restkeeper.service.IDishService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DishFaceImpl.java
 * @Description 菜品接口实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findDishVoPage",retries = 2),
                @Method(name = "createDish",retries = 0),
                @Method(name = "updateDish",retries = 0),
                @Method(name = "deleteDish",retries = 0)
        })
public class DishFaceImpl implements DishFace {
    
    @Autowired
    IDishService dishService;

    @Autowired
    IDishFlavorService dishFlavorService;

    @Autowired
    RedissonClient redissonClient;


    @Override
    public Page<DishVo> findDishVoPage(DishVo dishVo, int pageNum, int pageSize) {
        Page<Dish> page = dishService.findDishVoPage(dishVo, pageNum, pageSize);
        Page<DishVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Dish> dishList = page.getRecords();
        List<DishVo> dishVoList = BeanConv.toBeanList(dishList,DishVo.class);
        if (!EmptyUtil.isNullOrEmpty(dishVoList)){
            dishVoList.forEach(n->{
                List<DishFlavor> dishFlavors = dishFlavorService.findDishFlavorByDishId(n.getId());
                List<String> dishFavorList = new ArrayList<>();
                for (DishFlavor dishFlavor : dishFlavors) {
                    dishFavorList.add(String.valueOf(dishFlavor.getDataKey()));
                }
                String[] dishFlavorDataKey = new String[dishFavorList.size()];
                dishFavorList.toArray(dishFlavorDataKey);
                n.setHasDishFlavor(dishFlavorDataKey);
            });
        }
        pageVo.setRecords(dishVoList);
        return pageVo;
    }

    @Override
    public DishVo createDish(DishVo dishVo) {
        DishVo dishVoResult = BeanConv.toBean(dishService.createDish(dishVo), DishVo.class);
        dishVoResult.setHasDishFlavor(dishVo.getHasDishFlavor());
        //构建初始化库存
        String key = AppletCacheConstant.REPERTORY_DISH+dishVoResult.getId();
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.set(dishVoResult.getDishNumber());
        return  dishVoResult;
    }

    @Override
    public Boolean updateDish(DishVo dishVo) throws ProjectException {
        Boolean flag = dishService.updateDish(dishVo);
        //菜品库存锁，前段小程序点餐或后台修改订单项都需要加锁
        String keyLock = AppletCacheConstant.REPERTORY_DISH_LOCK+dishVo.getId();
        RLock lock = redissonClient.getLock(keyLock);
        //添加可重入锁
        try {
            if (lock.tryLock(
                    AppletCacheConstant.REDIS_LEASETIME,
                    AppletCacheConstant.REDIS_WAIT_TIME,
                    TimeUnit.SECONDS)){
                //构建redis库存
                String key = AppletCacheConstant.REPERTORY_DISH+dishVo.getId();
                RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
                atomicLong.set(dishVo.getDishNumber());
                return flag;
            }
        } catch (InterruptedException e) {
            log.warn("修改菜品产生并发dishId:{}",dishVo.getId());
           throw new ProjectException(DishEnum.UPDATE_FAIL);
        }finally {
            lock.unlock();
        }
        return flag;
    }

    @Override
    public Boolean deleteDish(String[] checkedIds) {
        Boolean flag = dishService.deleteDish(checkedIds);
        for (String checkedId : checkedIds) {
            String key = AppletCacheConstant.REPERTORY_DISH+checkedId;
            RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
            atomicLong.delete();
        }
        return flag;
    }

    @Override
    public DishVo findDishByDishId(Long dishId) {
        Dish dish = dishService.getById(dishId);
        if (!EmptyUtil.isNullOrEmpty(dish)){
            return BeanConv.toBean(dish,DishVo.class);
        }
        return null;
    }

}
