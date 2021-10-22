package com.itheima.restkeeper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.pojo.SmsTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.restkeeper.req.SmsTemplateVo;

import java.util.List;

/**
 * @Description：模板表 服务类
 */
public interface ISmsTemplateService extends IService<SmsTemplate> {

    /***
     * @description 查询相同模板code审核通过模板模板
     * @param templateNo 模板编号
     * @return
     */
    List<SmsTemplate> findSmsTemplateByTemplateNo(String templateNo);

    /**
     * @Description 模板列表
     * @param smsTemplateVo 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return Page<SmsSignVo>
     */
    Page<SmsTemplate> findSmsTemplateVoPage(SmsTemplateVo smsTemplateVo, int pageNum, int pageSize);
}
