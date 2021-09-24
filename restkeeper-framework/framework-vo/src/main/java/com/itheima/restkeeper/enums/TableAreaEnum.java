package com.itheima.restkeeper.enums;

import com.itheima.restkeeper.basic.IBasicEnum;

/**
 * @ClassName TableAreaEnum.java
 * @Description TODO
 */
public enum TableAreaEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    FAIL("1000","操作失败"),
    PAGE_FAIL("15005", "查询区域列表失败"),
    CREATE_FAIL("15007", "保存区域失败"),
    UPDATE_FAIL("15008", "修改区域失败"),
    DELETE_FAIL("15009", "修改区域失败"),
    SELECT_AREA_FAIL("15010", "查询区域失败"),
    SELECT_AREA_LIST_FAIL("15010", "查询区域list失败")
    ;

    private String code;
    private String msg;

    TableAreaEnum(String code, String msg) {
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

