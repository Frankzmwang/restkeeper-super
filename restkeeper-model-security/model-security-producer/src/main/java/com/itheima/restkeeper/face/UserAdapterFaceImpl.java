package com.itheima.restkeeper.face;

import com.itheima.restkeeper.UserAdapterFace;
import com.itheima.restkeeper.pojo.Resource;
import com.itheima.restkeeper.pojo.Role;
import com.itheima.restkeeper.pojo.User;
import com.itheima.restkeeper.req.ResourceVo;
import com.itheima.restkeeper.req.RoleVo;
import com.itheima.restkeeper.req.UserVo;
import com.itheima.restkeeper.service.IUserAdapterService;
import com.itheima.restkeeper.utils.BeanConv;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName UserAdapterFaceImpl.java
 * @Description 用户权限适配服务接口定义实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",retries = 2,timeout = 5000)
public class UserAdapterFaceImpl implements UserAdapterFace {

    @Autowired
    IUserAdapterService userAdapterService;

    @Override
    public UserVo findUserByUsernameAndEnterpriseId(String username,Long enterpriseId) {
        User user = userAdapterService.findUserByUsernameAndEnterpriseId(username,enterpriseId);
        return BeanConv.toBean(user,UserVo.class);
    }

    @Override
    public List<RoleVo> findRoleByUserId(Long userId) {
        List<Role> roles = userAdapterService.findRoleByUserId(userId);
        return BeanConv.toBeanList(roles,RoleVo.class);
    }

    @Override
    public List<ResourceVo> findResourceByUserId(Long userId) {
        List<Resource> resources = userAdapterService.findResourceByUserId(userId);
        return BeanConv.toBeanList(resources,ResourceVo.class);
    }
}
