package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.EnterpriseFace;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.init.InitEnterpriseWebSiteInfo;
import com.itheima.restkeeper.pojo.Enterprise;
import com.itheima.restkeeper.req.EnterpriseVo;
import com.itheima.restkeeper.service.IEnterpriseService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName EnterpriseFaceImpl.java
 * @Description 企业服务
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findEnterpriseVoPage",retries = 2),
                @Method(name = "createEnterprise",retries = 0),
                @Method(name = "updateEnterprise",retries = 0),
                @Method(name = "deleteEnterprise",retries = 0)
        })
public class EnterpriseFaceImpl implements EnterpriseFace {

    @Autowired
    IEnterpriseService EnterpriseService;

    @Autowired
    InitEnterpriseWebSiteInfo initEnterpriseWebSiteInfo;

    @Override
    public Page<EnterpriseVo> findEnterpriseVoPage(EnterpriseVo enterpriseVo, int pageNum, int pageSize) {
        Page<Enterprise> page = EnterpriseService.findEnterpriseVoPage(enterpriseVo, pageNum, pageSize);
        Page<EnterpriseVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Enterprise> enterpriseList = page.getRecords();
        List<EnterpriseVo> enterpriseVoList = BeanConv.toBeanList(enterpriseList,EnterpriseVo.class);
        pageVo.setRecords(enterpriseVoList);
        return pageVo;
    }

    @Override
    public EnterpriseVo createEnterprise(EnterpriseVo eterperiseVo) {
        Enterprise enterpriseResult = EnterpriseService.createEnterprise(eterperiseVo);
        //同步缓存
        if (!EmptyUtil.isNullOrEmpty(enterpriseResult)){
            initEnterpriseWebSiteInfo.addWebSiteforRedis(eterperiseVo.getWebSite(),eterperiseVo);
        }
        return BeanConv.toBean(enterpriseResult,EnterpriseVo.class);
    }

    @Override
    public Boolean updateEnterprise(EnterpriseVo enterpriseVo) {
        Boolean flag = EnterpriseService.updateEnterprise(enterpriseVo);
        //同步缓存
        if (flag){
            if (enterpriseVo.getEnableFlag().equals(SuperConstant.YES)){
                initEnterpriseWebSiteInfo.updataWebSiteforRedis(enterpriseVo.getWebSite(),enterpriseVo);
            }else {
                initEnterpriseWebSiteInfo.deleteWebSiteforRedis(enterpriseVo.getWebSite(),enterpriseVo);
            }

        }
        return flag;
    }

    @Override
    public Boolean deleteEnterprise(String[] checkedIds) {
        //同步缓存
        for (String checkedId : checkedIds) {
            Enterprise enterprise = EnterpriseService.getById(checkedId);
            EnterpriseVo enterpriseVo = BeanConv.toBean(enterprise, EnterpriseVo.class);
            initEnterpriseWebSiteInfo.deleteWebSiteforRedis(enterprise.getWebSite(),enterpriseVo);
        }
        Boolean flag =  EnterpriseService.deleteEnterprise(checkedIds);
        return flag;
    }

    @Override
    public List<EnterpriseVo> initEnterpriseIdOptions() {
        return BeanConv.toBeanList(EnterpriseService.initEnterpriseIdOptions(),EnterpriseVo.class);
    }


}
