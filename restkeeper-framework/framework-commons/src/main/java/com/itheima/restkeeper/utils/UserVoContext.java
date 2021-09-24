package com.itheima.restkeeper.utils;

import com.alibaba.fastjson.JSON;
import com.itheima.restkeeper.req.UserVo;
import com.itheima.restkeeper.utils.EmptyUtil;

/**
 * @ClassName UserParameter.java
 * @Description 用于dubbot参数传递
 */
public class UserVoContext {

    // 创建线程局部变量，并初始化值
    private static ThreadLocal<String> userIdThreadLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return null;
        }
    };

    // 提供线程局部变量set方法
    public static void setUserVoString(String userVoString) {
        userIdThreadLocal.set(userVoString);
    }

    // 提供线程局部变量get方法
    public static String getUserVoString() {
        return userIdThreadLocal.get();
    }
}
