package com.itheima.restkeeper.web;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.OrderFace;
import com.itheima.restkeeper.TableFace;
import com.itheima.restkeeper.TradingFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.BrandEnum;
import com.itheima.restkeeper.enums.OrderEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.*;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import com.itheima.restkeeper.utils.UserVoContext;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderController.java
 * @Description TODO
 */
@RestController
@RequestMapping("order")
@Slf4j
@Api(tags = "订单信息controller")
public class OrderController {

    @DubboReference(version = "${dubbo.application.version}", check = false)
    OrderFace orderFace;

    @DubboReference(version = "${dubbo.application.version}", check = false)
    TradingFace tradingFace;

    @DubboReference(version = "${dubbo.application.version}", check = false)
    TableFace tableFace;

    /**
     * @param orderVo 查询条件
     * @return
     * @Description 订单列表
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单list", notes = "查询订单list")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderVo", value = "订单查询对象", required = false, dataType = "OrderVo"),
        @ApiImplicitParam(paramType = "path", name = "pageNum", value = "页码", example = "1", dataType = "Integer"),
        @ApiImplicitParam(paramType = "path", name = "pageSize", value = "每页条数", example = "10", dataType = "Integer")
    })
    public ResponseWrap<Page<OrderVo>> findOrderVoPage(
        @RequestBody OrderVo orderVo,
        @PathVariable("pageNum") int pageNum,
        @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<OrderVo> orderVoList = orderFace.findOrderVoPage(orderVo, pageNum, pageSize);
            return ResponseWrapBuild.build(OrderEnum.SUCCEED, orderVoList);
        } catch (Exception e) {
            log.error("查询订单列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(OrderEnum.PAGE_FAIL);
        }
    }

    @PostMapping("opertion-to-orderItem/{dishId}/{orderNo}/{opertionType}")
    @ApiOperation(value = "操作订单菜品数量",notes = "操作订单菜品数量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",name = "dishId",value = "菜品ID",dataType = "Long"),
            @ApiImplicitParam(paramType = "path",name = "orderNo",value = "订单编号",dataType = "Long"),
            @ApiImplicitParam(paramType = "path",name = "opertionType",value = "操作动作",example = "ADD",dataType = "String")
    })
    public ResponseWrap<OrderVo> opertionToOrderItem(
            @PathVariable("dishId") Long dishId,
            @PathVariable("orderNo") Long orderNo,
            @PathVariable("opertionType") String opertionType) throws ProjectException {
        OrderVo orderVo = orderFace.opertionToOrderItem(dishId,orderNo,opertionType);
        return ResponseWrapBuild.build(BrandEnum.SUCCEED,orderVo);
    }

    @PostMapping("handleTrading")
    @ApiOperation(value = "订单结算",notes = "订单结算")
    @ApiImplicitParam(name = "orderVo",value = "订单信息",dataType = "OrderVo")
    @GlobalTransactional
    public ResponseWrap<TradingVo> handleTrading(@RequestBody OrderVo orderVo){
        //1、获得结算人信息
        String userVoString = UserVoContext.getUserVoString();
        UserVo userVo = JSONObject.parseObject(userVoString, UserVo.class);
        orderVo.setCashierId(userVo.getId());
        orderVo.setCashierName(userVo.getUsername());
        //2、根据订单生成交易单
        TradingVo tradingVo = orderFace.handleTrading(orderVo);
        if (EmptyUtil.isNullOrEmpty(tradingVo)){
            throw new ProjectException(OrderEnum.FAIL);
        }
        //3、调用支付RPC接口，进行支付
        TradingVo tradingVoResult = tradingFace.doPay(tradingVo);
        //4、结算后桌台状态修改：开桌-->空闲
        Boolean flag = true;
        if (EmptyUtil.isNullOrEmpty(tradingVoResult)){
            throw new ProjectException(OrderEnum.FAIL);
        }else {
            TableVo tableVo = TableVo.builder()
                    .id(orderVo.getTableId())
                    .tableStatus(SuperConstant.FREE).build();
            flag = tableFace.updateTable(tableVo);
            if (!flag){
                throw new ProjectException(OrderEnum.FAIL);
            }
        }
        return ResponseWrapBuild.build(BrandEnum.SUCCEED,tradingVoResult);
    }

    @PostMapping("handle-trading-refund")
    @ApiOperation(value = "订单退款",notes = "订单退款")
    @ApiImplicitParam(name = "orderVo",value = "订单信息",dataType = "OrderVo")
    @GlobalTransactional
    public ResponseWrap<Boolean> handleTradingRefund(@RequestBody OrderVo orderVo){
        //1、获得当前订单结算人信息
        String userVoString = UserVoContext.getUserVoString();
        UserVo userVo = JSONObject.parseObject(userVoString, UserVo.class);
        //2、退款收款人不为同一人，退款操作拒绝
        if (orderVo.getTradingChannel().equals(SuperConstant.TRADING_CHANNEL_REFUND)
                &&orderVo.getCashierId().longValue()!=userVo.getId().longValue()){
            throw new ProjectException(OrderEnum.REFUND_FAIL);
        }
        //3、获取当前交易单信息
        OrderVo orderVoBefore = orderFace.findOrderVoPaid(orderVo.getOrderNo());
        //4、当前交易单信息，退款操作拒绝
        if (!SuperConstant.YJS.equals(orderVoBefore.getOrderState())){
            throw new ProjectException(OrderEnum.REFUND_FAIL);
        }
        orderVo.setCashierId(userVo.getId());
        orderVo.setCashierName(userVo.getUsername());
        //5、根据订单生成交易单
        TradingVo tradingVo = orderFace.handleTrading(orderVo);
        if (EmptyUtil.isNullOrEmpty(tradingVo)){
            throw new ProjectException(OrderEnum.FAIL);
        }
        //6、执行退款交易
        TradingVo tradingVoResult = tradingFace.doPay(tradingVo);
        boolean flag = true;
        if (EmptyUtil.isNullOrEmpty(tradingVoResult)){
            throw new ProjectException(OrderEnum.FAIL);
        }
        return ResponseWrapBuild.build(BrandEnum.SUCCEED,flag);
    }

}
