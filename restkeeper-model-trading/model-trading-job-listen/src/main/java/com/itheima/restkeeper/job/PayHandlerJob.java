package com.itheima.restkeeper.job;

import com.itheima.restkeeper.OrderFace;
import com.itheima.restkeeper.TradingFace;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.req.OrderVo;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.transaction.Propagation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description：近期订单处理
 */
@Component
public class PayHandlerJob {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    TradingFace tradingFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    OrderFace orderFace;

    /***
     * @description 支付宝支付状态同步
     * @param param
     * @return
     */
    @XxlJob(value = "payHandlerJob")
    @GlobalTransactional
    public ReturnT<String> execute(String param) {
        //查询所有支付中的订单
        List<OrderVo> orderVoList = orderFace.findOrderVoPaying();
        for (OrderVo orderVo : orderVoList) {
            this.synchTradingState(orderVo);
        }
        ReturnT.SUCCESS.setMsg("执行-支付同步-成功");
        return ReturnT.SUCCESS;

    }

    public void synchTradingState(OrderVo orderVo){
        //构建查询条件
        TradingVo tradingVo = TradingVo.builder()
                .tradingChannel(orderVo.getTradingChannel())
                .enterpriseId(orderVo.getEnterpriseId())
                .productOrderNo(orderVo.getOrderNo())
                .build();
        //交易单状态查询
        TradingVo tradingVoResult = tradingFace.queryPay(tradingVo);
        //订单状态同步交易单状态
        if (!EmptyUtil.isNullOrEmpty(tradingVoResult)){
            Long productOrderNo = tradingVoResult.getProductOrderNo();
            String tradingState = tradingVoResult.getTradingState();
            orderFace.synchTradingState(productOrderNo,tradingState);
        }
    }

}
