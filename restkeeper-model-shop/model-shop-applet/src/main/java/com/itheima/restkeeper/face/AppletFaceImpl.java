package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.itheima.restkeeper.AppletFace;
import com.itheima.restkeeper.constant.AppletCacheConstant;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.OpenTableEnum;
import com.itheima.restkeeper.enums.OrderItemEnum;
import com.itheima.restkeeper.enums.RotaryTableEnum;
import com.itheima.restkeeper.enums.ShoppingCartEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.*;
import com.itheima.restkeeper.req.*;
import com.itheima.restkeeper.service.*;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.prometheus.client.Summary.build;

/**
 * @ClassName AppletFaceImpl.java
 * @Description 小程序H5实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}", timeout = 5000,
    methods = {
        @Method(name = "isOpen", retries = 2),
        @Method(name = "findAppletInfoVoByTableId", retries = 2),
        @Method(name = "openTable", retries = 0),
        @Method(name = "showOrderVoforTable", retries = 2),
        @Method(name = "handlerOrderVo", retries = 2),
        @Method(name = "reducePriceHandler", retries = 2),
        @Method(name = "findDishVoById", retries = 2),
        @Method(name = "opertionShoppingCart", retries = 0),
        @Method(name = "placeOrder", retries = 0),
        @Method(name = "rotaryTable", retries = 0),
        @Method(name = "clearShoppingCart",retries = 0)
    }
)
public class AppletFaceImpl implements AppletFace {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    IOrderService orderService;

    @Autowired
    ITableService tableService;

    @Autowired
    IOrderItemService orderItemService;

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IDishService dishService;

    @Autowired
    IDishFlavorService dishFlavorService;

    @Autowired
    IBrandService brandService;

    @Autowired
    IStoreService storeService;

    @Override
    public Boolean isOpen(Long tableId) {
        //1、查询桌台信息，是否为使用中
        Table table = tableService.getById(tableId);
        Boolean flagTableStatus = table.getTableStatus().equals(SuperConstant.USE);
        //2、是否已经有【待支付、支付中】订单存在
        OrderVo orderVoResult = orderService.findOrderByTableId(tableId);
        Boolean flagOrderVo = !EmptyUtil.isNullOrEmpty(orderVoResult);
        if (flagTableStatus||flagOrderVo){
            return true;
        }
        return false;
    }

    @Override
    public AppletInfoVo findAppletInfoVoByTableId(Long tableId) {
        //1、查询桌台信息
        Table table = tableService.getById(tableId);
        TableVo tableVo = BeanConv.toBean(table, TableVo.class);
        //2、查询门店
        Store store = storeService.getById(table.getStoreId());
        StoreVo storeVo = BeanConv.toBean(store, StoreVo.class);
        //3、查询品牌
        Brand brand = brandService.getById(store.getBrandId());
        BrandVo brandVo = BeanConv.toBean(brand, BrandVo.class);
        //4、查询分类
        List<Category> categorys = categoryService.findCategoryVoByStoreId(table.getStoreId());
        List<CategoryVo> categoryVoList = BeanConv.toBeanList(categorys, CategoryVo.class);
        //5、查询菜品
        List<Dish> dishs = dishService.findDishVoByStoreId(table.getStoreId());
        List<DishVo> dishVos = BeanConv.toBeanList(dishs, DishVo.class);
        //6、查询菜品口味
        dishVos.forEach(dishVo -> {
            List<DishFlavor> dishFlavors = dishFlavorService.findDishFlavorByDishId(dishVo.getId());
            List<DishFlavorVo> dishFlavorVos = BeanConv.toBeanList(dishFlavors, DishFlavorVo.class);
            dishVo.setDishFlavorVos(dishFlavorVos);
        });
        //7、构建返回对象
        AppletInfoVo appletInfoVo = AppletInfoVo.builder()
                .tableVo(tableVo)
                .storeVo(storeVo)
                .brandVo(brandVo)
                .categoryVos(categoryVoList)
                .dishVos(dishVos)
                .build();
        return appletInfoVo;
    }

    @Override
    @Transactional
    public OrderVo openTable(Long tableId,Integer personNumbers) throws ProjectException {
        //1、定义异常
        ProjectException projectException = null;
        //2、开台状态定义
        boolean flag = true;
        //3、锁定桌台，防止并发重复创建订单
        String key = AppletCacheConstant.OPEN_TABLE_LOCK+tableId;
        RLock lock = redissonClient.getLock(key);
        try {
            if(lock.tryLock(AppletCacheConstant.REDIS_WAIT_TIME,
                    AppletCacheConstant.REDIS_LEASETIME,
                    TimeUnit.SECONDS)){
                //4、幂等性：再次查询桌台订单情况
                OrderVo orderVoResult = orderService.findOrderByTableId(tableId);
                flag = EmptyUtil.isNullOrEmpty(orderVoResult);
                //5、未开台,为桌台创建当订单
                if (flag){
                    //5.1、查询桌台信息
                    Table table = tableService.getById(tableId);
                    //5.2、构建订单
                    Order order = Order.builder()
                            .tableId(tableId)
                            .tableName(table.getTableName())
                            .storeId(table.getStoreId())
                            .areaId(table.getAreaId())
                            .enterpriseId(table.getEnterpriseId())
                            .orderNo((Long) identifierGenerator.nextId(tableId))
                            .orderState(SuperConstant.DFK)
                            .isRefund(SuperConstant.NO)
                            .refund(new BigDecimal(0))
                            .discount(new BigDecimal(0))
                            .personNumbers(personNumbers)
                            .reduce(new BigDecimal(0))
                            .build();
                    orderService.save(order);
                    //6、修改桌台状态为使用中
                    TableVo tableVo = TableVo.builder()
                            .id(tableId)
                            .tableStatus(SuperConstant.USE).build();
                    tableService.updateTable(tableVo);
                }
            }
        } catch (InterruptedException e) {
            log.error("桌台：{}开桌创建订单失败：{}",ExceptionsUtil.getStackTraceAsString(e));
            projectException = new ProjectException(OpenTableEnum.TRY_LOCK_FAIL);
        }
        if (!EmptyUtil.isNullOrEmpty(projectException)){
            throw  projectException;
        }
        //7、订单处理：处理可核算订单项和购物车订单项，可调用桌台订单显示接口
        return showOrderVoforTable(tableId);
    }

    @Override
    public OrderVo showOrderVoforTable(Long tableId) throws ProjectException {
        OrderVo orderVoResult = orderService.findOrderByTableId(tableId);
        return handlerOrderVo(orderVoResult);
    }

    /***
     * @description 处理当前订单中订单项
     * 从DB中查询当前订单可核算订单项
     * 从redis查询当前订单购物车订单项
     * @param orderVo 订单信息
     * @return
     */
    @Override
    public OrderVo handlerOrderVo(OrderVo orderVo){
        if (!EmptyUtil.isNullOrEmpty(orderVo)) {
            //1、查询MySQL:可核算订单项
            List<OrderItem> orderItemList = orderItemService.findOrderItemByOrderNo(orderVo.getOrderNo());
            List<OrderItemVo> orderItemVoStatisticsList = BeanConv.toBeanList(orderItemList, OrderItemVo.class);
            //2、可核算订单项总金额
            BigDecimal reducePriceStatistics = new BigDecimal("0");
            //2.1、处理空可核算订单项
            if (EmptyUtil.isNullOrEmpty(orderItemVoStatisticsList)) {
                orderItemVoStatisticsList = new ArrayList<>();
            }else {
                //2.2、计算可核算订单项总金额
                reducePriceStatistics = reducePriceHandler(orderItemVoStatisticsList);
            }
            //3、查询redis:购物车订单项
            String key = AppletCacheConstant.ORDERITEMVO_STATISTICS + orderVo.getOrderNo();
            RMapCache<Long, OrderItemVo> orderItemVoRMap = redissonClient.getMapCache(key);
            List<OrderItemVo> orderItemVoTemporaryList = (List<OrderItemVo>) orderItemVoRMap.readAllValues();
            //3.1、计算购物车订单项总金额
            BigDecimal reducePriceTemporary=reducePriceHandler(orderItemVoTemporaryList);
            //4、构建订单信息
            orderVo.setOrderItemVoStatisticsList(orderItemVoStatisticsList);
            orderVo.setReducePriceStatistics(reducePriceStatistics);
            orderVo.setOrderItemVoTemporaryList(orderItemVoTemporaryList);
            orderVo.setReducePriceTemporary(reducePriceTemporary);
        }
        return orderVo;
    }

    @Override
    public BigDecimal reducePriceHandler(List<OrderItemVo> orderItemVos ){
        return orderItemVos.stream().map(orderItemVo -> {
            BigDecimal price = orderItemVo.getPrice();
            BigDecimal reducePrice = orderItemVo.getReducePrice();
            Long dishNum = orderItemVo.getDishNum();
            //如果有优惠价格以优惠价格计算
            if (EmptyUtil.isNullOrEmpty(reducePrice)) {
                return price.multiply(new BigDecimal(dishNum));
            } else {
                return reducePrice.multiply(new BigDecimal(dishNum));
            }
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public DishVo findDishVoById(Long dishId) {
        //查询菜品
        Dish dish = dishService.getById(dishId);
        //查询菜品口味
        List<DishFlavor> dishFlavors = dishFlavorService.findDishFlavorByDishId(dishId);
        DishVo dishVo = BeanConv.toBean(dish, DishVo.class);
        dishVo.setDishFlavorVos(BeanConv.toBeanList(dishFlavors,DishFlavorVo.class));
        return dishVo ;
    }

    @Override
    @Transactional
    public OrderVo opertionShoppingCart(Long dishId, Long orderNo,String dishFlavor, String opertionType) throws ProjectException {
        //1.1、获得菜品加锁
        String keyDish = AppletCacheConstant.REPERTORY_DISH_LOCK + dishId;
        RLock lockDish = redissonClient.getLock(keyDish);
        //1.2、获得订单加锁
        String keyOrder = AppletCacheConstant.ADD_TO_ORDERITEM_LOCK + orderNo;
        RLock lockOrder = redissonClient.getLock(keyOrder);
        //1.3、使用联锁
        RLock multiLock = redissonClient.getMultiLock(lockDish, lockOrder);
        ProjectException projectException = null;
        OrderVo orderVoResult = null;
        try {
            //2、是否获得到锁
            if (multiLock.tryLock(
                    AppletCacheConstant.REDIS_WAIT_TIME,
                    AppletCacheConstant.REDIS_LEASETIME,
                    TimeUnit.SECONDS)) {
                keyDish = AppletCacheConstant.REPERTORY_DISH + dishId;
                RAtomicLong atomicLong = redissonClient.getAtomicLong(keyDish);
                //3.1、添加到购物车订单项
                if (opertionType.equals(SuperConstant.OPERTION_TYPE_ADD)) {
                    this.addToShoppingCart(dishId, orderNo,dishFlavor, atomicLong);
                }
                //3.2、移除购物车订单项
                if (opertionType.equals(SuperConstant.OPERTION_TYPE_REMOVE)) {
                    this.removeToShoppingCart(dishId, orderNo, atomicLong);
                }
                //4、查询当前订单信息，并且处理订单项
                orderVoResult = orderService.findOrderByOrderNo(orderNo);
                return handlerOrderVo(orderVoResult);
            }
        } catch (InterruptedException e) {
            log.error("===编辑dishId：{}，orderNo：{}进入购物车加锁失败：{}",
                    dishId, orderNo, ExceptionsUtil.getStackTraceAsString(e));
            projectException = new ProjectException(OpenTableEnum.TRY_LOCK_FAIL);
        } finally {
            multiLock.unlock();
        }
        if (!EmptyUtil.isNullOrEmpty(projectException)) {
            throw projectException;
        }
        return orderVoResult;
    }

    /**
     * @param dishId     菜品ID
     * @param orderNo    订单编号
     * @param atomicLong 原子计数器
     * @return
     * @description 添加购物车订单项
     */
    private void addToShoppingCart(Long dishId, Long orderNo,String dishFlavor, RAtomicLong atomicLong) throws ProjectException {
        //1、如果库存够，redis减库存
        if (atomicLong.decrementAndGet() >= 0) {
            //2、mysql菜品表库存
            Boolean flag = dishService.updateDishNumber(-1L, dishId);
            if (!flag) {
                //减菜品库存失败，归还redis菜品库存
                atomicLong.incrementAndGet();
                throw new ProjectException(ShoppingCartEnum.UPDATE_DISHNUMBER_FAIL);
            }
            //3、查询redis缓存的购物车订单项
            String key = AppletCacheConstant.ORDERITEMVO_STATISTICS + orderNo;
            RMapCache<Long, OrderItemVo> orderItemVoRMap = redissonClient.getMapCache(key);
            OrderItemVo orderItemHandler = orderItemVoRMap.get(dishId);
            //4.1、如果以往购物车订单项中无则新增
            if (EmptyUtil.isNullOrEmpty(orderItemHandler)) {
                Dish dish = dishService.getById(dishId);
                OrderVo orderVo = orderService.findOrderByOrderNo(orderNo);
                OrderItemVo orderItemVo = OrderItemVo.builder()
                        .productOrderNo(orderNo)
                        .categoryId(dish.getCategoryId())
                        .dishId(dishId)
                        .dishName(dish.getDishName())
                        .dishFlavor(dishFlavor)
                        .dishNum(1L)
                        .price(dish.getPrice())
                        .reducePrice(dish.getReducePrice())
                        .build();
                //沿用订单中的分库键
                orderItemVo.setShardingId(orderVo.getShardingId());
                orderItemVoRMap.put(dishId, orderItemVo);
                //4.2、如果以往购物车订单项中有此菜品，则进行购物车订单项数量递增
            } else {
                orderItemHandler.setDishNum(orderItemHandler.getDishNum() + 1);
                orderItemVoRMap.put(dishId, orderItemHandler);
            }

        } else {
            //5、redis库存不足，虽然可以不处理，但建议还是做归还库存
            atomicLong.incrementAndGet();
            throw new ProjectException(ShoppingCartEnum.UNDERSTOCK);
        }
    }

    /**
     * @param dishId     菜品ID
     * @param orderNo    订单编号
     * @param atomicLong 原子计数器
     * @return
     * @description 移除购物车订单项
     */
    private void removeToShoppingCart(Long dishId, Long orderNo, RAtomicLong atomicLong) throws ProjectException {
        boolean flag = true;
        //1、菜品库存增加
        flag = dishService.updateDishNumber(1L, dishId);
        if (!flag) {
            throw new ProjectException(ShoppingCartEnum.UPDATE_DISHNUMBER_FAIL);
        }
        //2、查询redis缓存的购物车订单项
        String key = AppletCacheConstant.ORDERITEMVO_STATISTICS + orderNo;
        RMapCache<Long, OrderItemVo> orderItemVoRMap = redissonClient.getMapCache(key);
        OrderItemVo orderItemHandler = orderItemVoRMap.get(dishId);
        //3、购物车订单项存在
        if (!EmptyUtil.isNullOrEmpty(orderItemHandler)) {
            //3.1、购物车订单项的数量大于1，修改当前数量
            if (orderItemHandler.getDishNum().intValue() > 1) {
                orderItemHandler.setDishNum(orderItemHandler.getDishNum() - 1);
                orderItemVoRMap.put(dishId, orderItemHandler);
            } else {
                //3.2购物车订单项的数量等于1，则删除此订单项
                orderItemVoRMap.remove(dishId, orderItemHandler);
            }
            //4、redis菜品库存增加
            atomicLong.incrementAndGet();
        }
    }

    /***
     * @description 求交集:购物车订单项与核算订单项合并
     * @param flag 是否操作成功
     * @param orderItemVoStatisticsList 可核算订单项
     * @param orderItemVoTemporaryList 购物车订单项
     * @return 是否操作成功
     */
    private boolean intersection(boolean flag,
                                 List<OrderItemVo> orderItemVoStatisticsList,
                                 List<OrderItemVo> orderItemVoTemporaryList){
        //1、求交集
        List<OrderItemVo> listIntersection = new ArrayList<>();
        listIntersection.addAll(orderItemVoTemporaryList);
        listIntersection.retainAll(orderItemVoStatisticsList);
        if (!EmptyUtil.isNullOrEmpty(listIntersection)) {
            //2、获得核算订单项中需要增加的订单项
            List<OrderItemVo> orderItemVoHandler = orderItemVoStatisticsList
                    .stream().filter(n ->
                            listIntersection.contains(n)).collect(Collectors.toList());
            List<OrderItem> orderItemHandler = BeanConv
                    .toBeanList(orderItemVoHandler, OrderItem.class);
            //3、循环累加当前核算订单的菜品数量
            List<OrderItem> orderItems = new ArrayList<>();
            orderItemHandler.forEach(n -> {
                listIntersection.forEach(k -> {
                    if (n.getDishId().longValue() == k.getDishId().longValue()) {
                        OrderItem orderItem = OrderItem.builder()
                                .id(n.getId())
                                .dishNum(n.getDishNum() + k.getDishNum()).build();
                        orderItems.add(orderItem);
                    }
                });
            });
            //4、批量修改可结算订单项目
            flag = orderItemService.updateBatchById(orderItems);
        }
        return flag;
    }

    /***
     * @description 求差集:保存购物车订单项到可核算订单项
     * @param flag 是否操作成功
     * @param orderItemVoStatisticsList 可核算订单项
     * @param orderItemVoTemporaryList 购物车订单项
     * @return 是否操作成功
     */
    private Boolean difference(boolean flag,
                               List<OrderItemVo> orderItemVoStatisticsList,
                               List<OrderItemVo> orderItemVoTemporaryList){
        //1、求差集
        List<OrderItemVo> listDifference = new ArrayList<>();
        listDifference.addAll(orderItemVoTemporaryList);
        listDifference.removeAll(orderItemVoStatisticsList);
        //2、处理订单项
        if (!EmptyUtil.isNullOrEmpty(listDifference)) {
            List<OrderItem> orderItems = BeanConv
                    .toBeanList(listDifference, OrderItem.class);
            flag = orderItemService.saveBatch(orderItems);
        }
        return flag;
    }

    /***
     * @description 计算更新订单信息
     * @param orderNo   订单编号
     * @param orderVoResult 计算结果
     * @return
     * @return: java.lang.Boolean
     */
    private Boolean calculateOrderAmount(Long orderNo,OrderVo orderVoResult){
        //1、计算订单金额
        List<OrderItem> orderItemListResult = orderItemService
                .findOrderItemByOrderNo(orderNo);
        BigDecimal sumPrice = orderItemListResult.stream()
            .map(n -> {
                    BigDecimal price = n.getPrice();
                    BigDecimal reducePrice = n.getReducePrice();
                    Long dishNum = n.getDishNum();
                    //如果有优惠价格以优惠价格计算
                    if (EmptyUtil.isNullOrEmpty(reducePrice)) {
                        return price.multiply(new BigDecimal(dishNum));
                    } else {
                        return reducePrice.multiply(new BigDecimal(dishNum));
                    }
                }
            ).reduce(BigDecimal.ZERO, BigDecimal::add);
        //2、更新订单金额信息
        orderVoResult.setPayableAmountSum(sumPrice);
        OrderVo orderVoHandler = OrderVo.builder()
                .id(orderVoResult.getId())
                .payableAmountSum(sumPrice)
                .build();
        return orderService.updateById(BeanConv.toBean(orderVoHandler, Order.class));
    }

    @Override
    @Transactional
    public OrderVo placeOrder(Long orderNo) throws ProjectException {
        //1、锁定订单
        String key = AppletCacheConstant.ADD_TO_ORDERITEM_LOCK + orderNo;
        RLock lock = redissonClient.getLock(key);
        OrderVo orderVoResult = null;
        try {
            if (lock.tryLock(
                    AppletCacheConstant.REDIS_WAIT_TIME,
                    AppletCacheConstant.REDIS_LEASETIME,
                    TimeUnit.SECONDS)) {
                Boolean flag = true;
                orderVoResult = orderService.findOrderByOrderNo(orderNo);
                //2、查询可以核算订单项
                List<OrderItem> orderItemList = orderItemService.findOrderItemByOrderNo(orderVoResult.getOrderNo());
                List<OrderItemVo> orderItemVoStatisticsList = BeanConv.toBeanList(orderItemList, OrderItemVo.class);
                if (EmptyUtil.isNullOrEmpty(orderItemVoStatisticsList)) {
                    orderItemVoStatisticsList = new ArrayList<>();
                }
                //3、查询购物车订单项
                key = AppletCacheConstant.ORDERITEMVO_STATISTICS + orderNo;
                RMapCache<Long, OrderItemVo> orderItemVoRMap = redissonClient.getMapCache(key);
                List<OrderItemVo> orderItemVoTemporaryList = (List<OrderItemVo>)orderItemVoRMap.readAllValues();
                //4、购物车订单项不为空才合并
                if (!EmptyUtil.isNullOrEmpty(orderItemVoTemporaryList)) {
                    //5、求交集:购物车订单项与核算订单项合并
                    flag = this.intersection(flag,orderItemVoStatisticsList,orderItemVoTemporaryList);
                    if (!flag) {
                        throw new ProjectException(OrderItemEnum.UPDATE_ORDERITEM_FAIL);
                    }
                    //6、求差集:保存购物车订单项到可核算订单项
                    flag = this.difference(flag,orderItemVoStatisticsList,orderItemVoTemporaryList);
                    if (!flag) {
                        throw new ProjectException(OrderItemEnum.UPDATE_ORDERITEM_FAIL);
                    }
                    //7、计算更新订单信息
                    flag = this.calculateOrderAmount(orderNo,orderVoResult);
                    if (!flag) {
                        throw new ProjectException(OrderItemEnum.SAVE_ORDER_FAIL);
                    }
                    //8、清理redis购物车订单项目
                    orderItemVoTemporaryList.forEach(n->{
                        orderItemVoRMap.remove(n.getDishId());
                    });
                }
            }
        } catch (InterruptedException e) {
            log.error("合并订单出错：{}",ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(OrderItemEnum.LOCK_ORDER_FAIL);
        } finally {
            lock.unlock();
        }
        //9、再次查询订单
        return handlerOrderVo(orderVoResult);
    }

    @Override
    @Transactional
    public Boolean rotaryTable(Long sourceTableId, Long targetTableId, Long orderNo) throws ProjectException {
        Boolean flag = true;
        //1、锁定目标桌台
        String keyTargetTableId = AppletCacheConstant.OPEN_TABLE_LOCK + targetTableId;
        RLock lock = redissonClient.getLock(keyTargetTableId);
        ProjectException projectException = null;
        try {
            if (lock.tryLock(
                    AppletCacheConstant.REDIS_LEASETIME,
                    AppletCacheConstant.REDIS_WAIT_TIME,
                    TimeUnit.SECONDS)) {
                //2、查询目标桌台
                Table targetTable = tableService.getById(targetTableId);
                //2.1、桌台空闲
                if (SuperConstant.FREE.equals(targetTable.getTableStatus())) {
                    //3、订单关联新桌台
                    flag = orderService.rotaryTable(sourceTableId, targetTableId, orderNo);
                    if (flag) {
                        //4、修改桌台状态
                        tableService.updateTable(TableVo.builder()
                            .id(targetTableId)
                            .tableStatus(SuperConstant.USE)
                            .build());
                        tableService.updateTable(TableVo.builder()
                            .id(sourceTableId)
                            .tableStatus(SuperConstant.FREE)
                            .build());
                    } else {
                        projectException = new ProjectException(RotaryTableEnum.ROTARY_TABLE_FAIL);
                    }
                } else {
                    //2.2桌台非空闲
                    projectException = new ProjectException(RotaryTableEnum.ROTARY_TABLE_FAIL);
                }
            }
        } catch (InterruptedException e) {
            log.error("===转台加锁{}失败：{}", sourceTableId, ExceptionsUtil.getStackTraceAsString(e));
            projectException = new ProjectException(OpenTableEnum.TRY_LOCK_FAIL);
        } finally {
            lock.unlock();
        }
        if (!EmptyUtil.isNullOrEmpty(projectException)) {
            throw projectException;
        }
        return flag;
    }



    @Override
    public Boolean clearShoppingCart(Long orderNo) {
        String key = AppletCacheConstant.ORDERITEMVO_STATISTICS + orderNo;
        RMapCache<Long, OrderItemVo> orderItemVoRMap = redissonClient.getMapCache(key);
        //清理购物车归还库存
        if (!EmptyUtil.isNullOrEmpty(orderItemVoRMap)){
            List<OrderItemVo> orderItemVos = (List<OrderItemVo>) orderItemVoRMap.readAllValues();
            orderItemVos.forEach(n->{
                String keyDish = AppletCacheConstant.REPERTORY_DISH + n.getDishId();
                RAtomicLong atomicLong = redissonClient.getAtomicLong(keyDish);
                atomicLong.incrementAndGet();
            });
        }
        orderItemVoRMap.clear();
        return true;
    }


}
