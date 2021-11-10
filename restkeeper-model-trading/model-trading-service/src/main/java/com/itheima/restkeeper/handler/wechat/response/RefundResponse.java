package com.itheima.restkeeper.handler.wechat.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName RefundResponse.java
 * @Description 退款返回
 */
@Data
@NoArgsConstructor
public class RefundResponse {

    private String funds_account;

    private String out_refund_no;

    private String out_trade_no;

    private String refund_id;

    //SUCCESS：退款成功
    //CLOSED：退款关闭
    //PROCESSING：退款处理中
    //ABNORMAL：退款异常
    private String status;

    private String transaction_id;

    private String user_received_account;

    //编码定义
    private String code;
}
