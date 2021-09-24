package com.itheima.restkeeper.enums;

import com.itheima.restkeeper.basic.IBasicEnum;

/**
 * @ClassName UserEnum.java
 * @Description TODO
 */
public enum UserEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    LOGOUT_SUCCEED("1004","退出成功"),
    FAIL("1000","操作失败"),
    PAGE_FAIL("70005", "查询用户列表失败"),
    CREATE_FAIL("70007", "保存用户失败"),
    UPDATE_FAIL("70008", "修改用户失败"),
    DELETE_FAIL("70009", "修改用户失败"),
    SELECT_USER_FAIL("70010", "查询用户失败"),
    SELECT_ROLE_FAIL("70011", "查询用户对应角色失败"),
    SELECT_RESOURCE_FAIL("70012", "查询用户对应资源失败"),
    SELECT_CURRENT_USER("70013", "查询当前用户失败"),
    SELECT_USER_LIST_FAIL("70014", "查询用户list失败"),
    ;

    private String code;
    private String msg;

    UserEnum(String code, String msg) {
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

