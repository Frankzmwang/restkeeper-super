package com.itheima.restkeeper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.req.UserVo;

import java.util.List;

/**
 * @Description：用户接口
 */
public interface UserFace {

    /**
     * @Description 用户列表
     * @param userVo 查询条件
     * @return
     */
    Page<UserVo> findUserVoPage(UserVo userVo, int pageNum, int pageSize);

    /**
     * @Description 创建用户
     * @param userVo 对象信息
     * @return
     */
    UserVo createUser(UserVo userVo);

    /**
     * @Description 修改用户
     * @param userVo 对象信息
     * @return
     */
    Boolean updateUser(UserVo userVo);

    /**
     * @Description 删除用户
     * @param checkedIds 选择对象信息Id
     * @return
     */
    Boolean deleteUser(String[] checkedIds);

    /**
     * @Description 查找用户
     * @param userId 选择对象信息Id
     * @return
     */
    UserVo findUserByUserId(Long userId);

    /**
     * @Description 查找用户list
     * @return
     */
    List<UserVo> findUserVoList();

    /***
     * @description 启用禁用用户
     * @return
     * @return: java.lang.Boolean
     */
    Boolean updateUserEnableFlag(UserVo userVo);
}
