package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.BrandFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.BrandEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.BrandVo;
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
 * @ClassName BrandController.java
 * @Description 品牌Controller
 */
@RestController
@RequestMapping("brand")
@Slf4j
@Api(tags = "品牌controller")
public class BrandController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    BrandFace brandFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    AffixFace affixFace;

    /**
     * @Description 品牌列表
     * @param brandVo 查询条件
     * @return
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询品牌分页",notes = "查询品牌分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandVo",value = "品牌查询对象",required = true,dataType = "BrandVo"),
            @ApiImplicitParam(paramType = "path",name = "pageNum",value = "页码",dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页条数",dataType = "Integer")
    })
    public ResponseWrap<Page<BrandVo>> findBrandVoPage(
            @RequestBody BrandVo brandVo,
            @PathVariable("pageNum") int pageNum,
            @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<BrandVo> brandVoPage = brandFace.findBrandVoPage(brandVo, pageNum, pageSize);
            //处理附件
            if (!EmptyUtil.isNullOrEmpty(brandVoPage)&&
                    !EmptyUtil.isNullOrEmpty(brandVoPage.getRecords())){
                List<BrandVo> brandVoList = brandVoPage.getRecords();
                brandVoList.forEach(n->{
                    List<AffixVo> affixVoList = affixFace.findAffixVoByBusinessId(n.getId());
                    if (!EmptyUtil.isNullOrEmpty(affixVoList)){
                        n.setAffixVo(affixVoList.get(0));
                    }
                });
                brandVoPage.setRecords(brandVoList);
            }
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,brandVoPage);
        } catch (Exception e) {
            log.error("查询品牌列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.PAGE_FAIL);
        }
    }

    /**
     * @Description 查询品牌下拉框
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "查询品牌下拉框",notes = "查询品牌下拉框")
    public ResponseWrap<List<BrandVo>> findBrandVoList() throws ProjectException {
        try {
            List<BrandVo> brandVoPage = brandFace.findBrandVoList();
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,brandVoPage);
        } catch (Exception e) {
            log.error("查询品牌列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.PAGE_FAIL);
        }
    }

    /**
     * @Description 注册品牌
     * @param brandVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "注册品牌",notes = "注册品牌")
    @ApiImplicitParam(name = "brandVo",value = "品牌对象",required = true,dataType = "BrandVo")
    ResponseWrap<BrandVo> createBrand(@RequestBody BrandVo brandVo) throws ProjectException {
        try {
            BrandVo brandVoResult = brandFace.createBrand(brandVo);
            //绑定附件
            if (!EmptyUtil.isNullOrEmpty(brandVoResult)){
                affixFace.bindBusinessId(
                        AffixVo.builder()
                                .businessId(brandVoResult.getId())
                                .id(brandVo.getAffixVo().getId())
                                .build());
            }
            brandVoResult.setAffixVo(AffixVo.builder()
                    .pathUrl(brandVo.getAffixVo().getPathUrl())
                    .businessId(brandVoResult.getId())
                    .id(brandVo.getAffixVo().getId()).build());
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,brandVoResult);
        } catch (Exception e) {
            log.error("保存品牌异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.CREATE_FAIL);
        }
    }

    /**
     * @Description 修改品牌
     * @param brandVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "修改品牌",notes = "修改品牌")
    @ApiImplicitParam(name = "brandVo",value = "品牌对象",required = true,dataType = "BrandVo")
    ResponseWrap<Boolean> updateBrand(@RequestBody BrandVo brandVo) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(brandVo.getId())){
            throw new ProjectException(BrandEnum.UPDATE_FAIL);
        }
        try {
            Boolean flag = brandFace.updateBrand(brandVo);
            if (flag){
                List<AffixVo> affixVoList = affixFace.findAffixVoByBusinessId(brandVo.getId());
                List<Long> affixIds = affixVoList.stream().map(AffixVo::getId).collect(Collectors.toList());
                if (!affixIds.contains(brandVo.getAffixVo().getId())){
                    //删除图片
                    flag = affixFace.deleteAffixVoByBusinessId(brandVo.getId());
                    //绑定新图片
                    affixFace.bindBusinessId(AffixVo.builder()
                            .businessId(brandVo.getId())
                            .id(brandVo.getAffixVo().getId())
                            .build());
                }
            }
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("保存品牌异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 删除品牌
     * @param brandVo 查询对象
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除品牌",notes = "删除品牌")
    @ApiImplicitParam(name = "brandVo",value = "品牌查询对象",required = true,dataType = "BrandVo")
    ResponseWrap<Boolean> deleteBrand(@RequestBody BrandVo brandVo ) throws ProjectException {
        String[] checkedIds = brandVo.getCheckedIds();
        if (EmptyUtil.isNullOrEmpty(checkedIds)){
            throw new ProjectException(BrandEnum.DELETE_FAIL);
        }
        try {
            Boolean flag = brandFace.deleteBrand(checkedIds);
            //删除图片
            for (String checkedId : checkedIds) {
                affixFace.deleteAffixVoByBusinessId(Long.valueOf(checkedId));
            }
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("删除品牌异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.DELETE_FAIL);
        }
    }

    /**
     * @Description 查找品牌
     * @param brandId 登录名
     * @return
     */
    @GetMapping("{brandId}")
    @ApiOperation(value = "查找品牌",notes = "查找品牌")
    @ApiImplicitParam(paramType = "path",name = "brandId",value = "品牌Id",dataType = "Long")
    ResponseWrap<BrandVo> findBrandByBrandId(@PathVariable("brandId") Long brandId) throws ProjectException {

        if (EmptyUtil.isNullOrEmpty(brandId)){
            throw new ProjectException(BrandEnum.SELECT_BRAND_FAIL);
        }
        try {
            BrandVo brandVo = brandFace.findBrandByBrandId(brandId);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,brandVo);
        } catch (Exception e) {
            log.error("查找品牌所有品牌异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.SELECT_BRAND_FAIL);
        }
    }

    @PostMapping("update-brand-enableFlag")
    @ApiOperation(value = "修改品牌状态",notes = "修改品牌状态")
    @ApiImplicitParam(name = "brandVo",value = "品牌查询对象",required = true,dataType = "BrandVo")
    ResponseWrap<Boolean> updateBrandEnableFlag(@RequestBody BrandVo brandVo) throws ProjectException {
        try {
            Boolean flag = brandFace.updateBrand(brandVo);
            return ResponseWrapBuild.build(BrandEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("修改品牌状态：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(BrandEnum.UPDATE_FAIL);
        }
    }

}
