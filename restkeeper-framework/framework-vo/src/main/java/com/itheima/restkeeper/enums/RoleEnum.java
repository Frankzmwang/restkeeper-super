package com.itheima.restkeeper.enums;

import com.itheima.restkeeper.basic.IBasicEnum;

/**
 * @ClassName RoleEnum.java
 * @Description 角色枚举
 */
public enum RoleEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    FAIL("1000","操作失败"),
    PAGE_FAIL("60005", "查询角色列表失败"),
    CREATE_FAIL("60007", "保存角色失败"),
    UPDATE_FAIL("60008", "修改角色失败"),
    DELETE_FAIL("60009", "删除角色失败"),
    INIT_ROLEID_OPTIONS_FAIL("60010", "初始化角色失败")
    ;

    private String code;
    private String msg;

    RoleEnum(String code, String msg) {
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
