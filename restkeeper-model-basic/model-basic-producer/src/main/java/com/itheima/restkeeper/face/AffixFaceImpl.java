package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.pojo.Affix;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.UploadMultipartFile;
import com.itheima.restkeeper.service.IAffixService;
import com.itheima.restkeeper.utils.BeanConv;
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
    public AffixVo upLoad(UploadMultipartFile multipartFile, AffixVo affixVo) throws IOException {
        Affix affix = affixService.upLoad(multipartFile, affixVo);
        return BeanConv.toBean(affix,AffixVo.class);
    }

    @Override
    public AffixVo bindBusinessId(AffixVo affixVo) {
        return BeanConv.toBean(affixService.bindBusinessId(affixVo),AffixVo.class);
    }

    @Override
    public List<AffixVo> findAffixVoByBusinessId(Long businessId) {
        List<Affix> affixes = affixService.findAffixVoByBusinessId(businessId);
        return BeanConv.toBeanList(affixes,AffixVo.class);
    }

    @Override
    public Boolean deleteAffixVoByBusinessId(Long businessId) {
        return affixService.deleteAffixVoByBusinessId(businessId);
    }

    @Override
    public Page<AffixVo> findAffixVoPage(AffixVo affixVo, int pageNum, int pageSize) {
        Page<Affix> page = affixService.findAffixVoPage(affixVo, pageNum, pageSize);
        Page<AffixVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Affix> routeList = page.getRecords();
        List<AffixVo> routeVoList = BeanConv.toBeanList(routeList,AffixVo.class);
        pageVo.setRecords(routeVoList);
        return pageVo;
    }

    @Override
    public Boolean deleteAffix(String[] checkedIds) {
        return affixService.deleteAffix(checkedIds);
    }
}
