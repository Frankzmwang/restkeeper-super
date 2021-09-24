package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.TableFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.TableEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.TableVo;
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
 * @ClassName TableController.java
 * @Description 桌台Controller
 */
@RestController
@RequestMapping("table")
@Slf4j
@Api(tags = "桌台controller")
public class TableController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    TableFace tableFace;

    /**
     * @Description 桌台列表
     * @param tableVo 查询条件
     * @return
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询桌台list",notes = "查询桌台list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableVo",value = "桌台查询对象",dataType = "TableVo"),
            @ApiImplicitParam(paramType = "path",name = "pageNum",value = "页码",dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页条数",dataType = "Integer")
    })
    public ResponseWrap<Page<TableVo>> findTableVoPage(
            @RequestBody TableVo tableVo,
            @PathVariable("pageNum") int pageNum,
            @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<TableVo> tableVoList = tableFace.findTableVoPage(tableVo,pageNum,pageSize);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,tableVoList);
        } catch (Exception e) {
            log.error("查询桌台列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.SELECT_TABLE_LIST_FAIL);
        }
    }

    /**
     * @Description 注册桌台
     * @param tableVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "注册桌台",notes = "注册桌台")
    @ApiImplicitParam(name = "tableVo",value = "桌台对象",required = true,dataType = "TableVo")
    ResponseWrap<TableVo> createTable(@RequestBody TableVo tableVo) throws ProjectException {
        try {
            TableVo tableVoResult = tableFace.createTable(tableVo);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,tableVoResult);
        } catch (Exception e) {
            log.error("保存桌台异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.CREATE_FAIL);
        }
    }

    /**
     * @Description 修改桌台
     * @param tableVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "修改桌台",notes = "修改桌台")
    @ApiImplicitParam(name = "tableVo",value = "桌台对象",required = true,dataType = "TableVo")
    ResponseWrap<Boolean> updateTable(@RequestBody TableVo tableVo) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(tableVo.getId())){
            throw new ProjectException(TableEnum.UPDATE_FAIL);
        }
        try {
            Boolean flag = tableFace.updateTable(tableVo);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("保存桌台异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 查找桌台
     * @param tableId 桌台id
     * @return
     */
    @GetMapping("{tableId}")
    @ApiOperation(value = "查找桌台",notes = "查找桌台")
    @ApiImplicitParam(paramType = "path",name = "tableId",value = "桌台Id",dataType = "Long")
    ResponseWrap<TableVo> findTableByTableId(@PathVariable("tableId") Long tableId) throws ProjectException {

        if (EmptyUtil.isNullOrEmpty(tableId)){
            throw new ProjectException(TableEnum.SELECT_TABLE_FAIL);
        }
        try {
            TableVo tableVo = tableFace.findTableByTableId(tableId);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,tableVo);
        } catch (Exception e) {
            log.error("查找桌台所有桌台异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.SELECT_TABLE_FAIL);
        }
    }

    /**
     * @Description 删除桌台
     * @param tableVo 查询对象
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除桌台",notes = "删除桌台")
    @ApiImplicitParam(name = "tableVo",value = "桌台查询对象",required = true,dataType = "TableVo")
    ResponseWrap<Boolean> deleteTable(@RequestBody TableVo tableVo ) throws ProjectException {
        String[] checkedIds = tableVo.getCheckedIds();
        if (EmptyUtil.isNullOrEmpty(checkedIds)){
            throw new ProjectException(TableEnum.DELETE_FAIL);
        }
        try {
            Boolean flag = tableFace.deleteTable(checkedIds);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("删除桌台异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.DELETE_FAIL);
        }
    }

    /**
     * @Description 查找桌台
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "查找桌台列表",notes = "查找桌台列表")
    ResponseWrap<List<TableVo>> findTableVoList() throws ProjectException {
        try {
            List<TableVo> list = tableFace.findTableVoList();
            return ResponseWrapBuild.build(TableEnum.SUCCEED,list);
        } catch (Exception e) {
            log.error("查找桌台所有桌台异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.SELECT_TABLE_LIST_FAIL);
        }
    }

    @PostMapping("update-table-enableFlag")
    @ApiOperation(value = "修改桌台有效状态",notes = "修改桌台有效状态")
    ResponseWrap<Boolean> updateTableEnableFlag(@RequestBody TableVo tableVo) throws ProjectException {
        try {
            Boolean flag = tableFace.updateTable(tableVo);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改桌台有效状态：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.UPDATE_FAIL);
        }
    }

    @PostMapping("update-table-tableStatus")
    @ApiOperation(value = "修改桌台状态",notes = "修改桌台状态")
    ResponseWrap<Boolean> updateTableStatus(@RequestBody TableVo tableVo) throws ProjectException {
        try {
            Boolean flag = tableFace.updateTable(tableVo);
            return ResponseWrapBuild.build(TableEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改桌台有效状态：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TableEnum.UPDATE_FAIL);
        }
    }
}
