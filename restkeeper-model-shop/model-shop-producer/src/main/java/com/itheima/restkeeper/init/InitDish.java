package com.itheima.restkeeper.init;

import com.itheima.restkeeper.constant.AppletCacheConstant;
import com.itheima.restkeeper.pojo.Dish;
import com.itheima.restkeeper.service.IDishService;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @ClassName InitDish.java
 * @Description 初始化菜品库存
 */
@Component
public class InitDish {

    @Autowired
    IDishService dishService;

    @Autowired
    RedissonClient redissonClient;

    @PostConstruct
    public void initDishNumber(){
        //查询所有有效的且起售状态的菜品
        List<Dish> dishList = dishService.findDishVos();
        for (Dish dish : dishList) {
            //构建初始化库存
            String key = AppletCacheConstant.REPERTORY_DISH+dish.getId();
            RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
            atomicLong.set(dish.getDishNumber());
        }
    }
}
