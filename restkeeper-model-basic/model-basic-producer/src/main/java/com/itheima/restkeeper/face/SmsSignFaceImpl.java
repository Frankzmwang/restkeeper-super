package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.SmsSignFace;
import com.itheima.restkeeper.adapter.SmsSignAdapter;
import com.itheima.restkeeper.enums.SmsSignEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.SmsSign;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.service.ISmsSignService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName SmsSignFaceImpl.java
 * @Description TODO
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
    methods ={
        @Method(name = "findSmsSignVoPage",retries = 2),
        @Method(name = "addSmsSign",retries = 0),
        @Method(name = "deleteSmsSign",retries = 0),
        @Method(name = "modifySmsSign",retries = 0),
        @Method(name = "querySmsSign",retries = 2)
    })
public class SmsSignFaceImpl implements SmsSignFace {

    @Autowired
    SmsSignAdapter smsSignAdapter;

    @Autowired
    ISmsSignService smsSignService;

    @Override
    public Page<SmsSignVo> findSmsSignVoPage(SmsSignVo smsSignVo, int pageNum, int pageSize) {
        try {
            //查询Page<SmsSignVo>图片分页
            Page<SmsSign> page = smsSignService.findSmsSignVoPage(smsSignVo, pageNum, pageSize);
            //转化Page<SmsSignVo>为Page<SmsSignVo>
            Page<SmsSignVo> pageVo = new Page<>();
            BeanConv.toBean(page,pageVo);
            //转换List<SmsSignVo>为 List<SmsSignVo>
            List<SmsSign> smsSignVoList = page.getRecords();
            List<SmsSignVo> smsSignVoVoList = BeanConv.toBeanList(smsSignVoList,SmsSignVo.class);
            pageVo.setRecords(smsSignVoVoList);
            //返回结果
            return pageVo;
        } catch (Exception e) {
            log.error("查询签名列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsSignEnum.PAGE_FAIL);
        }
    }

    @Override
    public SmsSignVo addSmsSign(SmsSignVo smsSignVo) throws Exception {
        try {
            return smsSignAdapter.addSmsSign(smsSignVo);
        } catch (Exception e) {
            log.error("添加签名异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsSignEnum.CREATE_FAIL);
        }
    }

    @Override
    public Boolean deleteSmsSign(SmsSignVo smsSignVo) throws Exception {
        try {
            return smsSignAdapter.deleteSmsSign(smsSignVo);
        } catch (Exception e) {
            log.error("删除签名异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsSignEnum.DELETE_FAIL);
        }
    }

    @Override
    public Boolean modifySmsSign(SmsSignVo smsSignVo) throws Exception {
        try {
            return smsSignAdapter.modifySmsSign(smsSignVo);
        } catch (Exception e) {
            log.error("修改签名异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsSignEnum.UPDATE_FAIL);
        }
    }

    @Override
    public Boolean querySmsSign(SmsSignVo smsSignVo) throws Exception {
        try {
            return smsSignAdapter.querySmsSign(smsSignVo);
        } catch (Exception e) {
            log.error("查询签名状态异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsSignEnum.SELECT_FAIL);
        }
    }
}
