package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.RoleFace;
import com.itheima.restkeeper.pojo.Role;
import com.itheima.restkeeper.req.RoleVo;
import com.itheima.restkeeper.service.IRoleResourceService;
import com.itheima.restkeeper.service.IRoleService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName RoleFaceImpl.java
 * @Description 角色服务实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findRoleVoPage",retries = 2),
                @Method(name = "createRole",retries = 0),
                @Method(name = "updateRole",retries = 0),
                @Method(name = "deleteRole",retries = 0),
                @Method(name = "initRoleIdOptions",retries = 2)
        })
public class RoleFaceImpl implements RoleFace {

    @Autowired
    IRoleService roleService;

    @Autowired
    IRoleResourceService roleResourceService;

    @Override
    public Page<RoleVo> findRoleVoPage(RoleVo roleVo, int pageNum, int pageSize) {
        Page<Role> page = roleService.findRoleVoPage(roleVo, pageNum, pageSize);
        Page<RoleVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Role> roleList = page.getRecords();
        List<RoleVo> roleVoList = BeanConv.toBeanList(roleList,RoleVo.class);
        if (!EmptyUtil.isNullOrEmpty(roleVoList)){
            roleVoList.forEach(n->{
                List<String> resourceIdList = roleResourceService.findResourceIdByRoleId(n.getId());
                String[] resourceIds = new String[resourceIdList.size()];
                resourceIdList.toArray(resourceIds);
                n.setHasResourceIds(resourceIds);
            });
        }
        
        pageVo.setRecords(roleVoList);
        return pageVo;
    }

    @Override
    public RoleVo createRole(RoleVo roleVo) {
        return BeanConv.toBean(roleService.createRole(roleVo),RoleVo.class);
    }

    @Override
    public Boolean updateRole(RoleVo roleVo) {
        return roleService.updateRole(roleVo);
    }

    @Override
    public Boolean deleteRole(String[] checkedIds) {
        return roleService.deleteRole(checkedIds);
    }

    @Override
    public List<RoleVo> initRoleIdOptions() {
        List<Role> roles = roleService.initRoleIdOptions();
        return BeanConv.toBeanList(roles,RoleVo.class);
    }
}
