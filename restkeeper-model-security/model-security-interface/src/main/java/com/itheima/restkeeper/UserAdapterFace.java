package com.itheima.restkeeper;

import com.itheima.restkeeper.req.ResourceVo;
import com.itheima.restkeeper.req.RoleVo;
import com.itheima.restkeeper.req.UserVo;

import java.util.List;

/**
 * @Description：用户权限适配服务接口定义
 */
public interface UserAdapterFace {

    /**
     * @Description 按用户名查找用户
     * @param username 登录名
     * @return
     */
    UserVo findUserByUsernameAndEnterpriseId(String username, Long enterpriseId);

    /**
     * @Description 查找用户所有角色
     * @param userId 用户Id
     * @return
     */
    List<RoleVo> findRoleByUserId(Long userId);

    /**
     * @Description 查询用户有资源
     * @param userId 用户Id
     * @return
     */
    List<ResourceVo> findResourceByUserId(Long userId);

}
