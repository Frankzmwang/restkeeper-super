package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.SmsChannelFace;
import com.itheima.restkeeper.enums.SmsChannelEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.req.SmsChannelVo;
import com.itheima.restkeeper.req.SmsChannelVo;
import com.itheima.restkeeper.service.ISmsChannelService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName SmsChannelFaceImpl.java
 * @Description 渠道dubbo接口实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
    methods ={
        @Method(name = "findSmsChannelVoPage",retries = 2),
        @Method(name = "createSmsChannel",retries = 0),
        @Method(name = "updateSmsChannel",retries = 0),
        @Method(name = "deleteSmsChannel",retries = 0),
        @Method(name = "findSmsChannelVoList",retries = 2)
    })
public class SmsChannelFaceImpl implements SmsChannelFace {

    @Autowired
    ISmsChannelService smsChannelService;


    @Override
    public Page<SmsChannelVo> findSmsChannelVoPage(SmsChannelVo smsChannelVo, int pageNum, int pageSize) {
        try {
            Page<SmsChannel> page = smsChannelService.findSmsChannelVoPage(smsChannelVo, pageNum, pageSize);
            Page<SmsChannelVo> pageVo = new Page<>();
            BeanConv.toBean(page,pageVo);
            //结果集转换
            List<SmsChannel> smsChannelList = page.getRecords();
            List<SmsChannelVo> smsChannelVoList = BeanConv.toBeanList(smsChannelList,SmsChannelVo.class);
            pageVo.setRecords(smsChannelVoList);
            return pageVo;
        } catch (Exception e) {
            log.error("查询通道列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsChannelEnum.PAGE_FAIL);
        }

    }

    @Override
    public SmsChannelVo createSmsChannel(SmsChannelVo smsChannelVo) {
        try {
            return BeanConv.toBean( smsChannelService.createSmsChannel(smsChannelVo), SmsChannelVo.class);
        } catch (Exception e) {
            log.error("保存通道异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsChannelEnum.CREATE_FAIL);
        }
    }

    @Override
    public Boolean updateSmsChannel(SmsChannelVo smsChannelVo) {
        try {
            return smsChannelService.updateSmsChannel(smsChannelVo);
        } catch (Exception e) {
            log.error("保存通道异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsChannelEnum.UPDATE_FAIL);
        }
    }

    @Override
    public Boolean deleteSmsChannel(String[] checkedIds) {
        try {
            return smsChannelService.deleteSmsChannel(checkedIds);
        } catch (Exception e) {
            log.error("删除通道异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsChannelEnum.DELETE_FAIL);
        }
    }

    @Override
    public SmsChannelVo findSmsChannelBySmsChannelId(Long smsChannelId) {
        try {
            SmsChannel smsChannel = smsChannelService.getById(smsChannelId);
            if (!EmptyUtil.isNullOrEmpty(smsChannel)){
                return BeanConv.toBean(smsChannel,SmsChannelVo.class);
            }
            return null;
        } catch (Exception e) {
            log.error("查找通道异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsChannelEnum.SELECT_FAIL);
        }
    }

    @Override
    public List<SmsChannelVo> findSmsChannelVoList() {
        try {
            return BeanConv.toBeanList(smsChannelService.findSmsChannelVoList(),SmsChannelVo.class);
        } catch (Exception e) {
            log.error("查找所有通道异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(SmsChannelEnum.SELECT_FAIL);
        }
    }
}
