package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.StoreFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.StoreEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.StoreVo;
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

import java.util.List;

/**
 * @ClassName StoreController.java
 * @Description 门店Controller
 */
@RestController
@RequestMapping("store")
@Slf4j
@Api(tags = "门店controller")
public class StoreController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    StoreFace storeFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    AffixFace affixFace;

    /**
     * @Description 门店列表
     * @param storeVo 查询条件
     * @return
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询门店分页",notes = "查询门店分页")
    @ApiImplicitParams({
       @ApiImplicitParam(name = "storeVo",value = "门店查询对象",required = true,dataType = "StoreVo"),
       @ApiImplicitParam(paramType = "path",name = "pageNum",value = "页码",dataType = "Integer"),
       @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页条数",dataType = "Integer")
    })
    public ResponseWrap<Page<StoreVo>> findStoreVoPage(
            @RequestBody StoreVo storeVo,
            @PathVariable("pageNum") int pageNum,
            @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<StoreVo> storeVoPage = storeFace.findStoreVoPage(storeVo, pageNum, pageSize);
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,storeVoPage);
        } catch (Exception e) {
            log.error("查询门店列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.PAGE_FAIL);
        }
    }

    /**
     * @Description 注册门店
     * @param storeVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "注册门店",notes = "注册门店")
    @ApiImplicitParam(name = "storeVo",value = "门店对象",required = true,dataType = "StoreVo")
    ResponseWrap<StoreVo> createStore(@RequestBody StoreVo storeVo) throws ProjectException {
        try {
            StoreVo storeVoResult = storeFace.createStore(storeVo);
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,storeVoResult);
        } catch (Exception e) {
            log.error("保存门店异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.CREATE_FAIL);
        }
    }

    /**
     * @Description 修改门店
     * @param storeVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "修改门店",notes = "修改门店")
    @ApiImplicitParam(name = "storeVo",value = "门店对象",required = true,dataType = "StoreVo")
    ResponseWrap<Boolean> updateStore(@RequestBody StoreVo storeVo) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(storeVo.getId())){
            throw new ProjectException(StoreEnum.UPDATE_FAIL);
        }
        try {
            Boolean flag = storeFace.updateStore(storeVo);
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("保存门店异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 删除门店
     * @param storeVo 查询对象
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除门店",notes = "删除门店")
    @ApiImplicitParam(name = "storeVo",value = "门店查询对象",required = true,dataType = "StoreVo")
    ResponseWrap<Boolean> deleteStore(@RequestBody StoreVo storeVo ) throws ProjectException {
        String[] checkedIds = storeVo.getCheckedIds();
        if (EmptyUtil.isNullOrEmpty(checkedIds)){
            throw new ProjectException(StoreEnum.DELETE_FAIL);
        }
        try {
            Boolean flag = storeFace.deleteStore(checkedIds);
            //删除图片
            for (String checkedId : checkedIds) {
                affixFace.deleteAffixVoByBusinessId(Long.valueOf(checkedId));
            }
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("删除门店异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.DELETE_FAIL);
        }
    }

    /**
     * @Description 查找门店
     * @param storeId 门店id
     * @return
     */
    @GetMapping("{storeId}")
    @ApiOperation(value = "查找门店",notes = "查找门店")
    @ApiImplicitParam(paramType = "path",name = "storeId",value = "门店Id",dataType = "Long")
    ResponseWrap<StoreVo> findStoreByStoreId(@PathVariable("storeId") Long storeId) throws ProjectException {

        if (EmptyUtil.isNullOrEmpty(storeId)){
            throw new ProjectException(StoreEnum.SELECT_STORE_FAIL);
        }
        try {
            StoreVo storeVo = storeFace.findStoreByStoreId(storeId);
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,storeVo);
        } catch (Exception e) {
            log.error("查找门店所有门店异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.SELECT_STORE_FAIL);
        }
    }

    /**
     * @Description 查找门店
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "查找门店列表",notes = "查找门店列表")
    ResponseWrap<List<StoreVo>> findStoreVoList() throws ProjectException {
        try {
            List<StoreVo> list = storeFace.findStoreVoList();
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,list);
        } catch (Exception e) {
            log.error("查找门店所有门店异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.SELECT_STORE_LIST_FAIL);
        }
    }

    @PostMapping("update-store-enableFlag")
    @ApiOperation(value = "修改门店状态",notes = "修改门店状态")
    ResponseWrap<Boolean> updateStoreEnableFlag(@RequestBody StoreVo storeVo) throws ProjectException {
        try {
            Boolean flag = storeFace.updateStore(storeVo);
            return ResponseWrapBuild.build(StoreEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改门店状态：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(StoreEnum.UPDATE_FAIL);
        }
    }

}
