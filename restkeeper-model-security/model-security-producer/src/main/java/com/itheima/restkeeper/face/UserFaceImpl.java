package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.UserFace;
import com.itheima.restkeeper.pojo.Role;
import com.itheima.restkeeper.pojo.User;
import com.itheima.restkeeper.req.UserVo;
import com.itheima.restkeeper.service.IUserAdapterService;
import com.itheima.restkeeper.service.IUserService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserFaceImpl.java
 * @Description TODO
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findUserVoPage",retries = 2),
                @Method(name = "createUser",retries = 0),
                @Method(name = "updateUser",retries = 0),
                @Method(name = "deleteUser",retries = 0),
                @Method(name = "updateUserEnableFlag",retries = 0)
        })
public class UserFaceImpl implements UserFace {

    @Autowired
    IUserService userService;

    @Autowired
    IUserAdapterService userAdapterService;

    @Override
    public Page<UserVo> findUserVoPage(UserVo userVo, int pageNum, int pageSize) {
        Page<User> page = userService.findUserVoPage(userVo, pageNum, pageSize);
        Page<UserVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<User> userList = page.getRecords();
        List<UserVo> userVoList = BeanConv.toBeanList(userList,UserVo.class);
        if (!EmptyUtil.isNullOrEmpty(userList)){
            userVoList.forEach(n->{
                List<Role> roles = userAdapterService.findRoleByUserId(n.getId());
                List<String> roleIdList = new ArrayList<>();
                for (Role role : roles) {
                    roleIdList.add(String.valueOf(role.getId()));
                }
                String[] roleIds = new String[roleIdList.size()];
                roleIdList.toArray(roleIds);
                n.setHasRoleIds(roleIds);
            });
        }
        pageVo.setRecords(userVoList);
        return pageVo;
    }

    @Override
    public UserVo createUser(UserVo userVo) {
       return BeanConv.toBean( userService.createUser(userVo),UserVo.class);
    }

    @Override
    public Boolean updateUser(UserVo userVo) {
        return userService.updateUser(userVo);
    }

    @Override
    public Boolean deleteUser(String[] checkedIds) {
        return userService.deleteUser(checkedIds);
    }

    @Override
    public UserVo findUserByUserId(Long userId) {
        User user = userService.getById(userId);
        if (!EmptyUtil.isNullOrEmpty(user)){
            return BeanConv.toBean(user,UserVo.class);
        }
        return null;
    }

    @Override
    public List<UserVo> findUserVoList() {
        return BeanConv.toBeanList(userService.findUserVoList(),UserVo.class);
    }

    @Override
    public Boolean updateUserEnableFlag(UserVo userVo) {
        return userService.updateUserEnableFlag(userVo);
    }


}
