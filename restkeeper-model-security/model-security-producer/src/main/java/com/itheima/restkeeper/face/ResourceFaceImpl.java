package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.ResourceFace;
import com.itheima.restkeeper.pojo.Resource;
import com.itheima.restkeeper.req.ResourceVo;
import com.itheima.restkeeper.req.TreeVo;
import com.itheima.restkeeper.service.IResourceService;
import com.itheima.restkeeper.utils.BeanConv;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName ResourceFaceImpl.java
 * @Description 资源服务实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findResourceVoPage",retries = 2),
                @Method(name = "initResourceTreeVo",retries = 2),
                @Method(name = "createResource",retries = 0),
                @Method(name = "updateResource",retries = 0),
                @Method(name = "deleteResource",retries = 0)
        })
public class ResourceFaceImpl implements ResourceFace {

    @Autowired
    IResourceService resourceService;

    @Override
    public Page<ResourceVo> findResourceVoPage(ResourceVo resourceVo, int pageNum, int pageSize) {
        Page<Resource> page = resourceService.findResourceVoPage(resourceVo, pageNum, pageSize);
        Page<ResourceVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Resource> routeList = page.getRecords();
        List<ResourceVo> routeVoList = BeanConv.toBeanList(routeList,ResourceVo.class);
        pageVo.setRecords(routeVoList);
        return pageVo;
    }

    @Override
    public TreeVo initResourceTreeVo(String[] checkedIds) {
        return resourceService.initResourceTreeVo(checkedIds);
    }

    @Override
    public ResourceVo createResource(ResourceVo resourceVo) {
        return BeanConv.toBean(resourceService.createResource(resourceVo),ResourceVo.class);
    }

    @Override
    public Boolean updateResource(ResourceVo resourceVo) {
        return resourceService.updateResource(resourceVo);
    }

    @Override
    public Boolean deleteResource(String[] checkedIds) {
        return resourceService.deleteResource(checkedIds);
    }
}
