package com.itheima.restkeeper.service;

import com.itheima.restkeeper.pojo.Trading;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description：交易订单表 服务类
 */
public interface ITradingService extends IService<Trading> {


    /***
     * @description 查询业务订单对应的交易单
     * @return
     * @return: com.itheima.restkeeper.pojo.Trading
     */
    Trading findTradingByProductOrderNo(Long productOrderNo);

    /***
     * @description 查询交易单号对应的交易单
     * @return
     * @return: com.itheima.restkeeper.pojo.Trading
     */
    Trading findTradingByProductTradingOrderNo(Long tradingOrderNo);

}
