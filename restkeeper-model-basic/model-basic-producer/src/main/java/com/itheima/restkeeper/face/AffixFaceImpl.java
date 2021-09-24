package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.enums.AffixEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.Affix;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.UploadMultipartFile;
import com.itheima.restkeeper.service.IAffixService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName AffixFaceImpl.java
 * @Description 附件接口实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
    methods ={
        @Method(name = "upLoad",retries = 0),
        @Method(name = "bindBusinessId",retries = 0),
        @Method(name = "findAffixVoByBusinessId",retries = 2),
        @Method(name = "deleteAffixVoByBusinessId",retries = 0),
        @Method(name = "findAffixVoPage",retries = 2),
        @Method(name = "deleteAffix",retries = 0)
    })
public class AffixFaceImpl implements AffixFace {

    @Autowired
    IAffixService affixService;

    @Override
    public AffixVo upLoad(UploadMultipartFile multipartFile, AffixVo affixVo)  {
        try {
            Affix affix = affixService.upLoad(multipartFile, affixVo);
            return BeanConv.toBean(affix,AffixVo.class);
        } catch (Exception e) {
            log.error("文件上传异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(AffixEnum.UPLOAD_FAIL);
        }
    }

    @Override
    public AffixVo bindBusinessId(AffixVo affixVo) {
        try {
            return BeanConv.toBean(affixService.bindBusinessId(affixVo),AffixVo.class);
        } catch (Exception e) {
            log.error("绑定业务：{}异常：{}", affixVo.toString(),ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(AffixEnum.UPLOAD_FAIL);
        }
    }

    @Override
    public List<AffixVo> findAffixVoByBusinessId(Long businessId) {
        try {
            List<Affix> affixes = affixService.findAffixVoByBusinessId(businessId);
            return BeanConv.toBeanList(affixes,AffixVo.class);
        }catch (Exception e){
            log.error("查询业务对应附件：{}异常：{}", businessId,ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(AffixEnum.SELECT_AFFIX_BUSINESSID_FAIL);
        }
    }

    @Override
    public Boolean deleteAffixVoByBusinessId(Long businessId) {
        try {
            return affixService.deleteAffixVoByBusinessId(businessId);
        }catch (Exception e){
            log.error("删除业务对应附件：{}失败",businessId);
            throw new ProjectException(AffixEnum.DELETE_AFFIX_BUSINESSID_FAIL);
        }
    }

    @Override
    public Page<AffixVo> findAffixVoPage(AffixVo affixVo, int pageNum, int pageSize) {
        try {
            Page<Affix> page = affixService.findAffixVoPage(affixVo, pageNum, pageSize);
            Page<AffixVo> pageVo = new Page<>();
            BeanConv.toBean(page,pageVo);
            //结果集转换
            List<Affix> routeList = page.getRecords();
            List<AffixVo> routeVoList = BeanConv.toBeanList(routeList,AffixVo.class);
            pageVo.setRecords(routeVoList);
            return pageVo;
        }catch (Exception e){
            log.error("查询附件列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(AffixEnum.PAGE_FAIL);
        }
    }

    @Override
    public Boolean deleteAffix(String[] checkedIds) {
        try {
            return affixService.deleteAffix(checkedIds);
        }catch (Exception e){
            log.error("删除业务对应附件：{}",ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(AffixEnum.DELETE_AFFIX_FAIL);
        }
    }
}
