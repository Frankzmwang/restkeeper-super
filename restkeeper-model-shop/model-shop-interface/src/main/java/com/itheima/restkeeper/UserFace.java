package com.itheima.restkeeper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.req.UserVo;

import java.util.List;

/**
 * @Description：用户dubbo接口
 */
public interface UserFace {

    /**
     * @param userVo   查询条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return Page<UserVo>
     * @Description 用户列表
     */
    Page<UserVo> findUserVoPage(UserVo userVo, int pageNum, int pageSize);

    /**
     * @param userVo 对象信息
     * @return UserVo
     * @Description 创建用户
     */
    UserVo createUser(UserVo userVo);

    /**
     * @param userVo 对象信息
     * @return Boolean
     * @Description 修改用户
     */
    Boolean updateUser(UserVo userVo);

    /**
     * @param checkedIds 选择对象信息Id
     * @return Boolean
     * @Description 删除用户
     */
    Boolean deleteUser(String[] checkedIds);


    /**
     * @param userId 选择对象信息Id
     * @return
     * @Description 查找用户
     */
    UserVo findUserByUserId(Long userId);

    /**
     * @return List<UserVo>
     * @Description 查找用户list
     */
    List<UserVo> findUserVoList();
}
