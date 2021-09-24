package com.itheima.restkeeper.web;

import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.AppletFace;
import com.itheima.restkeeper.DataDictFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.BrandEnum;
import com.itheima.restkeeper.enums.DishEnum;
import com.itheima.restkeeper.enums.OrderEnum;
import com.itheima.restkeeper.enums.TableEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.*;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName AppletController.java
 * @Description 应用接口
 */
@RestController
@RequestMapping("applet")
@Slf4j
@Api(tags = "小程序controller")
public class AppletController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    AppletFace appletFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    AffixFace affixFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    DataDictFace dataDictFace;

    @GetMapping("is-open/{tableId}")
    @ApiOperation(value = "查询是否开桌",notes = "是否开桌,已开台：进入继续点餐流程,未开台：进入开台流程")
    @ApiImplicitParam(paramType = "path",name = "tableId",value = "桌台",dataType = "Long")
    public ResponseWrap<Boolean> isOpen(@PathVariable("tableId") Long tableId) throws ProjectException {
        try {
            Boolean isOpen = appletFace.isOpen(tableId);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,isOpen);
        }catch (Exception e){
            log.error("查询桌台信息异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            return ResponseWrapBuild.build(TableEnum.SELECT_TABLE_FAIL,null);
        }
    }

    @GetMapping("table-appletInfo/{tableId}")
    @ApiOperation(value = "查询桌台相关主体信息",notes = "查询桌台相关主体信息:品牌、门店、菜品、口味、分类等")
    @ApiImplicitParam(paramType = "path",name = "tableId",value = "桌台Id",dataType = "Long")
    public ResponseWrap<AppletInfoVo> findAppletInfoVoByTableId(@PathVariable("tableId") Long tableId)
           throws ProjectException {
        try {
            //1、查询订单情况，注意：品牌图片信息、菜品图片口味信息需要调用通用服务获得
            AppletInfoVo appletInfoVo = appletFace.findAppletInfoVoByTableId(tableId);
            //2、处理品牌图片
            BrandVo brandVo = appletInfoVo.getBrandVo();
            List<AffixVo> affixVoListBrand = affixFace.findAffixVoByBusinessId(brandVo.getId());
            brandVo.setAffixVo(affixVoListBrand.get(0));
            appletInfoVo.setBrandVo(brandVo);
            //3、处理菜品图片及口味
            List<DishVo> dishVos = appletInfoVo.getDishVos();
            dishVos.forEach(dishVo->{
                List<DishFlavorVo> dishFlavorVos = dishVo.getDishFlavorVos();
                //3.1、构建数字字典dataKeys
                List<String> dataKeys = dishFlavorVos.stream()
                        .map(DishFlavorVo::getDataKey).collect(Collectors.toList());
                //3.2、RPC查询数字字典口味信息
                List<DataDictVo> valueByDataKeys = dataDictFace.findValueByDataKeys(dataKeys);
                dishVo.setDataDictVos(valueByDataKeys);
                //3.3、RPC查询附件信息
                List<AffixVo> affixVoListDish = affixFace.findAffixVoByBusinessId(dishVo.getId());
                dishVo.setAffixVo(affixVoListDish.get(0));
            });
            //4、包装返回
            return ResponseWrapBuild.build(TableEnum.SUCCEED,appletInfoVo);
        } catch (Exception e) {
            log.error("查询桌台相关主体信息异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.SELECT_TABLE_FAIL);
        }
    }


    @PostMapping("open-table/{tableId}/{personNumbers}")
    @ApiOperation(value = "开桌操作",notes = "未开桌：选择人数创建订单")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path",name = "tableId",value = "桌台",dataType = "Long"),
        @ApiImplicitParam(paramType = "path",name = "personNumbers",value = "就餐人数",dataType = "Integer"),
    })
    public ResponseWrap<OrderVo> openTable(@PathVariable("tableId") Long tableId,
                                           @PathVariable("personNumbers") Integer personNumbers)
                                           throws ProjectException {
        try {
            OrderVo orderVoResult = appletFace.openTable(tableId,personNumbers);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,orderVoResult);
        }catch (Exception e){
            log.error("开桌操作异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.OPEN_TABLE_FAIL);
        }
    }

    @PostMapping("show-ordervo-table/{tableId}")
    @ApiOperation(value = "查询桌台订单信息",notes = "已开桌：查询当前桌台订单信息【包括可核算订单项和购物车订单项】")
    @ApiImplicitParam(paramType = "path",name = "tableId",value = "桌台",dataType = "Long")
    public ResponseWrap<OrderVo> showOrderVoforTable(@PathVariable("tableId") Long tableId) throws ProjectException {
        try {
            OrderVo orderVoResult = appletFace.showOrderVoforTable(tableId);
            if (!EmptyUtil.isNullOrEmpty(orderVoResult)){
                //订单项目[可核算订单项目]附件
                List<OrderItemVo> orderItemVoStatisticsList = orderVoResult.getOrderItemVoStatisticsList();
                orderItemVoStatisticsList.forEach(n->{
                    List<AffixVo> affixVoListDish = affixFace.findAffixVoByBusinessId(n.getDishId());
                    n.setAffixVo(affixVoListDish.get(0));
                });
                //订单项目[购物车中订单项目]附件
                List<OrderItemVo> orderItemVoTemporaryList = orderVoResult.getOrderItemVoTemporaryList();
                orderItemVoTemporaryList.forEach(n->{
                    List<AffixVo> affixVoListDish = affixFace.findAffixVoByBusinessId(n.getDishId());
                    n.setAffixVo(affixVoListDish.get(0));
                });
            }
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,orderVoResult);
        }catch (Exception e){
            log.error("查询桌台订单信息异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(OrderEnum.SELECT_TABLE_ORDER_FAIL);
        }

    }

    @PostMapping("dish-details/{dishId}")
    @ApiOperation(value = "查询菜品详情",notes = "显示菜品详情,包括口味")
    @ApiImplicitParam(paramType = "path",name = "dishId",value = "菜品Id",dataType = "菜品Id")
    public ResponseWrap<DishVo> findDishVoById(@PathVariable("dishId") Long dishId) throws ProjectException {
        try {
            //1、查询菜品信息，注意：菜品图片口味信息需要调用通用服务获得
            DishVo dishVo = appletFace.findDishVoById(dishId);
            //2、处理菜品口味
            List<DishFlavorVo> dishFlavorVos = dishVo.getDishFlavorVos();
            List<String> DataKeys = dishFlavorVos.stream()
                    .map(DishFlavorVo::getDataKey).collect(Collectors.toList());
            List<DataDictVo> valueByDataKeys = dataDictFace.findValueByDataKeys(DataKeys);
            dishVo.setDataDictVos(valueByDataKeys);
            //3、处理菜品图片
            List<AffixVo> affixVoListDish = affixFace.findAffixVoByBusinessId(dishVo.getId());
            dishVo.setAffixVo(affixVoListDish.get(0));
            //4、封装返回结果
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,dishVo);
        }catch (Exception e){
            log.error("查询菜品详情异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.SELECT_DISH_FAIL);
        }
    }

    @PostMapping("opertion-shopping-cart/{dishId}/{orderNo}/{dishFlavor}/{opertionType}")
    @ApiOperation(value = "操作购物车",notes = "每次点击添加或减少1，则添加或减少购物车订单项1，同时增减库存1")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path",name = "dishId",value = "菜品ID",dataType = "Long"),
        @ApiImplicitParam(paramType = "path",name = "orderNo",value = "订单编号",dataType = "Long"),
        @ApiImplicitParam(paramType = "path",name = "dishFlavor",value = "菜品口味",dataType = "String"),
        @ApiImplicitParam(paramType = "path",name = "opertionType",value = "操作动作",dataType = "String")
    })
    public ResponseWrap<OrderVo> opertionShoppingCart(@PathVariable("dishId") Long dishId,
                                                      @PathVariable("orderNo") Long orderNo,
                                                      @PathVariable("dishFlavor") String dishFlavor,
                                                      @PathVariable("opertionType") String opertionType)
                                                      throws ProjectException {
        try {
            OrderVo orderVoResult = appletFace.opertionShoppingCart(dishId,orderNo,dishFlavor,opertionType);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,orderVoResult);
        }catch (Exception e){
            log.error("操作购物车详情异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(OrderEnum.OPERTION_SHOPPING_CART_FAIL);
        }
    }

    @PostMapping("clear-shopping-cart/{orderNo}")
    @ApiOperation(value = "清理购物车",notes = "清理购物车")
    @ApiImplicitParam(paramType = "path",name = "orderNo",value = "订单编号",dataType = "Long")
    public ResponseWrap<Boolean> clearShoppingCart(@PathVariable("orderNo") Long orderNo)
            throws ProjectException {
        try {
            Boolean flag = appletFace.clearShoppingCart(orderNo);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,flag);
        }catch (Exception e){
            log.error("操作购物车详情异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(OrderEnum.CLEAR_SHOPPING_CART_FAIL);
        }
    }

    @PostMapping("placeOrder/{orderNo}")
    @ApiOperation(value = "下单操作",notes = "添加购物车订单项到可结算订单项")
    @ApiImplicitParam(paramType = "path",name = "orderNo",value = "订单编号",dataType = "Long")
    public ResponseWrap<OrderVo> addToOrderItem(@PathVariable("orderNo") Long orderNo) throws ProjectException {
        try {
            OrderVo orderVoResult = appletFace.placeOrder(orderNo);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,orderVoResult);
        }catch (Exception e){
            log.error("下单操作异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(OrderEnum.PLACE_ORDER_FAIL);
        }
    }

    @PostMapping("rotary-table/{sourceTableId}/{targetTableId}/{orderNo}")
    @ApiOperation(value = "转台",notes = "转台")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path",name = "sourceTableId",value = "源桌台",dataType = "Long"),
        @ApiImplicitParam(paramType = "path",name = "targetTableId",value = "目标桌台",dataType = "Long"),
        @ApiImplicitParam(paramType = "path",name = "orderNo",value = "订单编号",dataType = "Long")
    })
    public ResponseWrap<Boolean> rotaryTable(@PathVariable("sourceTableId") Long sourceTableId,
                                             @PathVariable("targetTableId") Long targetTableId,
                                             @PathVariable("orderNo") Long orderNo) throws ProjectException {
        try {
            Boolean flag = appletFace.rotaryTable(sourceTableId,targetTableId,orderNo);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,flag);
        }catch (Exception e){
            log.error("操作购物车详情异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.ROTARY_TABLE_FAIL);
        }
    }

}
