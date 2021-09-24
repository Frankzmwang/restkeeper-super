package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.ResourceFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.ResourceEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.ResourceVo;
import com.itheima.restkeeper.req.TreeVo;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ResourceController.java
 * @Description 资源controller
 */
@RestController
@RequestMapping("resource")
@Api(tags = "资源controller")
@Slf4j
public class ResourceController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    ResourceFace resourceFace;

    /**
     * @Description 资源列表
     * @param resourceVo 查询条件
     * @return
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询资源分页",notes = "查询资源分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceVo",value = "资源查询对象",required = false,dataType = "ResourceVo"),
            @ApiImplicitParam(paramType = "path",name = "pageNum",value = "页码",example = "1",dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页条数",example = "10",dataType = "Integer")
    })
    public ResponseWrap<Page<ResourceVo>> findResourceVoPage(
            @RequestBody ResourceVo resourceVo,
            @PathVariable("pageNum") int pageNum,
            @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<ResourceVo> resourceVoPage = resourceFace.findResourceVoPage(resourceVo, pageNum, pageSize);
            return ResponseWrapBuild.build(ResourceEnum.SUCCEED,resourceVoPage);
        } catch (Exception e) {
            log.error("查询资源列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(ResourceEnum.PAGE_FAIL);
        }
    }

    /**
     * @Description 资源树
     * @param resourceVo 资源对象
     * @return
     */
    @PostMapping("tree")
    @ApiOperation(value = "查询资源树",notes = "查询资源树")
    @ApiImplicitParam(name = "resourceVo",value = "资源查询对象",required = false,dataType = "ResourceVo")
    public ResponseWrap<TreeVo> initResourceTreeVo(@RequestBody ResourceVo resourceVo)throws ProjectException {
        try {
            TreeVo treeVo = resourceFace.initResourceTreeVo(resourceVo.getCheckedIds());
            return ResponseWrapBuild.build(ResourceEnum.SUCCEED,treeVo);
        } catch (Exception e) {
            log.error("查询资源数异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(ResourceEnum.INIT_TREE_FAIL);
        }
    }

    /**
     * @Description 创建资源
     * @param resourceVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "保存资源",notes = "保存资源")
    @ApiImplicitParam(name = "resourceVo",value = "资源对象",required = true,dataType = "ResourceVo")
    ResponseWrap<ResourceVo> createResource(@RequestBody ResourceVo resourceVo) throws ProjectException {
        try {
            ResourceVo resourceVoResult = resourceFace.createResource(resourceVo);
            return ResponseWrapBuild.build(ResourceEnum.SUCCEED,resourceVoResult);
        } catch (Exception e) {
            log.error("保存资源异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(ResourceEnum.CREATE_FAIL);
        }
    }

    /**
     * @Description 修改资源
     * @param resourceVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "修改资源",notes = "修改资源")
    @ApiImplicitParam(name = "resourceVo",value = "资源对象",required = true,dataType = "ResourceVo")
    ResponseWrap<Boolean> updateResource(@RequestBody ResourceVo resourceVo) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(resourceVo.getId())){
            throw new ProjectException(ResourceEnum.UPDATE_FAIL);
        }
        try {
            Boolean flag = resourceFace.updateResource(resourceVo);
            return ResponseWrapBuild.build(ResourceEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("保存资源异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(ResourceEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 删除资源
     * @param resourceVo 查询对象
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除资源",notes = "删除资源")
    @ApiImplicitParam(name = "resourceVo",value = "资源查询对象",required = true,dataType = "ResourceVo")
    ResponseWrap<Boolean> deleteResource(@RequestBody ResourceVo resourceVo )throws ProjectException {
        String[] checkedIds = resourceVo.getCheckedIds();
        if (EmptyUtil.isNullOrEmpty(checkedIds)){
            throw new ProjectException(ResourceEnum.DELETE_FAIL);
        }
        try {
            Boolean flag = resourceFace.deleteResource(checkedIds);
            return ResponseWrapBuild.build(ResourceEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("删除资源异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(ResourceEnum.DELETE_FAIL);
        }
    }

    @PostMapping("update-resource-enableFlag")
    @ApiOperation(value = "修改资源状态",notes = "修改资源状态")
    @ApiImplicitParam(name = "resourceVo",value = "资源查询对象",required = false,dataType = "ResourceVo")
    ResponseWrap<Boolean> updateResourceEnableFlag(@RequestBody ResourceVo resourceVo) throws ProjectException {
        try {
            Boolean flag = resourceFace.updateResource(resourceVo);
            return ResponseWrapBuild.build(ResourceEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改资源状态异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(ResourceEnum.UPDATE_FAIL);
        }
    }

}
