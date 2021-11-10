package com.itheima.restkeeper.handler.wechat.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PreCreateResponse.java
 * @Description TODO
 */
@Data
@NoArgsConstructor
public class PreCreateResponse {

    //二维码请求地址
    private String code_url;

    //编码定义
    private String code;
}
