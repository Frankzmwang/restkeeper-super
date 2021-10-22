package com.itheima.restkeeper.enums;

import com.itheima.restkeeper.basic.IBasicEnum;

/**
 * @ClassName SmsBlacklistEnum.java
 * @Description TODO
 */
public enum SmsBlacklistEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    FAIL("1000","操作失败"),
    PAGE_FAIL("48001", "查询通道列表失败"),
    CREATE_FAIL("48002", "保存通道失败"),
    UPDATE_FAIL("48003", "修改通道失败"),
    DELETE_FAIL("48004", "修改通道失败"),
    SELECT_FAIL("48005", "查询通道失败");

    private String code;
    private String msg;

    SmsBlacklistEnum(String code, String msg) {
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
