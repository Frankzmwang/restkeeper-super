package com.itheima.restkeeper.enums;

import com.itheima.restkeeper.basic.IBasicEnum;

/**
 * @ClassName BasicEnum.java
 * @Description 基础枚举
 */
public enum BasicEnum implements IBasicEnum {

    SUCCEED("200","操作成功"),
    SYSYTEM_FAIL("111000","系统运行异常")
    ;

    private String code;
    private String msg;

    BasicEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
