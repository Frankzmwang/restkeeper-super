package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.DishFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.DishEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.DishVo;
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
import java.util.stream.Collectors;

/**
 * @ClassName DishController.java
 * @Description 菜品Controller
 */
@RestController
@RequestMapping("dish")
@Slf4j
@Api(tags = "菜品controller")
public class DishController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    DishFace dishFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    AffixFace affixFace;

    /**
     * @Description 菜品列表
     * @param dishVo 查询条件
     * @return
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询菜品分页",notes = "查询菜品分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dishVo",value = "菜品查询对象",dataType = "DishVo"),
        @ApiImplicitParam(paramType = "path",name = "pageNum",value = "页码",dataType = "Integer"),
        @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页条数",dataType = "Integer")
    })
    public ResponseWrap<Page<DishVo>> findDishVoPage(
            @RequestBody DishVo dishVo,
            @PathVariable("pageNum") int pageNum,
            @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<DishVo> dishVoPage = dishFace.findDishVoPage(dishVo, pageNum, pageSize);
            //处理附件
            if (!EmptyUtil.isNullOrEmpty(dishVoPage)&&!EmptyUtil.isNullOrEmpty(dishVoPage.getRecords())){
                List<DishVo> brandVoList = dishVoPage.getRecords();
                brandVoList.forEach(n->{
                    List<AffixVo> affixVoList = affixFace.findAffixVoByBusinessId(n.getId());
                    if (!EmptyUtil.isNullOrEmpty(affixVoList)){
                        n.setAffixVo(affixVoList.get(0));
                    }
                });
                dishVoPage.setRecords(brandVoList);
            }
            return ResponseWrapBuild.build(DishEnum.SUCCEED,dishVoPage);
        } catch (Exception e) {
            log.error("查询菜品列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.PAGE_FAIL);
        }
    }

    /**
     * @Description 添加菜品
     * @param dishVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "添加菜品",notes = "添加菜品")
    @ApiImplicitParam(name = "dishVo",value = "菜品对象",required = true,dataType = "DishVo")
    ResponseWrap<DishVo> createDish(@RequestBody DishVo dishVo) throws ProjectException {
        try {
            DishVo dishVoResult = dishFace.createDish(dishVo);
            //绑定附件
            if (!EmptyUtil.isNullOrEmpty(dishVoResult)){
                affixFace.bindBusinessId(AffixVo.builder()
                        .businessId(dishVoResult.getId())
                        .id(dishVo.getAffixVo().getId())
                        .build());
            }
            dishVoResult.setAffixVo(AffixVo.builder()
                    .pathUrl(dishVo.getAffixVo().getPathUrl())
                    .businessId(dishVoResult.getId())
                    .id(dishVo.getAffixVo().getId()).build());
            return ResponseWrapBuild.build(DishEnum.SUCCEED,dishVoResult);
        } catch (Exception e) {
            log.error("保存菜品异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.CREATE_FAIL);
        }
    }

    /**
     * @Description 修改菜品
     * @param dishVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "修改菜品",notes = "修改菜品")
    @ApiImplicitParam(name = "dishVo",value = "菜品对象",required = true,dataType = "DishVo")
    ResponseWrap<Boolean> updateDish(@RequestBody DishVo dishVo) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(dishVo.getId())){
            throw new ProjectException(DishEnum.UPDATE_FAIL);
        }
        try {
            Boolean flag = dishFace.updateDish(dishVo);
            if (flag){
                List<AffixVo> affixVoList = affixFace.findAffixVoByBusinessId(dishVo.getId());
                List<Long> affixIds = affixVoList.stream()
                        .map(AffixVo::getId).collect(Collectors.toList());
                if (!affixIds.contains(dishVo.getAffixVo().getId())){
                    //删除图片
                    flag = affixFace.deleteAffixVoByBusinessId(dishVo.getId());
                    //绑定新图片
                    affixFace.bindBusinessId(AffixVo.builder()
                            .businessId(dishVo.getId())
                            .id(dishVo.getAffixVo().getId())
                            .build());
                }
            }
            return ResponseWrapBuild.build(DishEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("保存菜品异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 删除菜品
     * @param dishVo 查询对象
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除菜品",notes = "删除菜品")
    @ApiImplicitParam(name = "dishVo",value = "菜品查询对象",required = true,dataType = "DishVo")
    ResponseWrap<Boolean> deleteDish(@RequestBody DishVo dishVo ) throws ProjectException {
        String[] checkedIds = dishVo.getCheckedIds();
        if (EmptyUtil.isNullOrEmpty(checkedIds)){
            throw new ProjectException(DishEnum.DELETE_FAIL);
        }
        try {
            Boolean flag = dishFace.deleteDish(checkedIds);
            //删除图片
            for (String checkedId : checkedIds) {
                affixFace.deleteAffixVoByBusinessId(Long.valueOf(checkedId));
            }
            return ResponseWrapBuild.build(DishEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("删除菜品异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.DELETE_FAIL);
        }
    }

    /**
     * @Description 查找菜品
     * @param dishId 菜品id
     * @return
     */
    @GetMapping("{dishId}")
    @ApiOperation(value = "查找菜品",notes = "查找菜品")
    @ApiImplicitParam(paramType = "path",name = "dishId",value = "菜品Id",dataType = "Long")
    ResponseWrap<DishVo> findDishByDishId(@PathVariable("dishId") Long dishId) throws ProjectException {

        if (EmptyUtil.isNullOrEmpty(dishId)){
            throw new ProjectException(DishEnum.SELECT_DISH_FAIL);
        }
        try {
            DishVo dishVo = dishFace.findDishByDishId(dishId);
            return ResponseWrapBuild.build(DishEnum.SUCCEED,dishVo);
        } catch (Exception e) {
            log.error("查找菜品所有菜品异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.SELECT_DISH_FAIL);
        }
    }

    @PostMapping("update-dish-enableFlag")
    @ApiOperation(value = "修改菜品有效状态",notes = "修改菜品有效状态")
    ResponseWrap<Boolean> updateDishEnableFlag(@RequestBody DishVo dishVo) throws ProjectException {
        try {
            Boolean flag = dishFace.updateDish(dishVo);
            return ResponseWrapBuild.build(DishEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改菜品有效状态：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.UPDATE_FAIL);
        }
    }

    @PostMapping("update-dish-dishStatus")
    @ApiOperation(value = "修改菜品状态",notes = "修改菜品状态")
    ResponseWrap<Boolean> updateDishDishStatus(@RequestBody DishVo dishVo) throws ProjectException {
        try {
            Boolean flag = dishFace.updateDish(dishVo);
            return ResponseWrapBuild.build(DishEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改菜品有效状态：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(DishEnum.UPDATE_FAIL);
        }
    }

}
