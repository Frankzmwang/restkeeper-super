package com.itheima.restkeeper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.req.SmsTemplateVo;
import com.itheima.restkeeper.req.SmsTemplateVo;

/**
 * @ClassName SmsTemplateFace.java
 * @Description 模板dubbo接口
 */
public interface SmsTemplateFace {

    /**
     * @Description 签名列表
     * @param smsTemplateVo 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return Page<SmsTemplateVo>
     */
    Page<SmsTemplateVo> findSmsTemplateVoPage(SmsTemplateVo smsTemplateVo, int pageNum, int pageSize);


    /***
     * @description 申请模板
     * @param smsTemplate 模板信息
     * @return
     */
    SmsTemplateVo addSmsTemplate(SmsTemplateVo smsTemplate) throws Exception;

    /***
     * @description 删除模板
     * @param smsTemplate 模板信息
     * @return
     */
    Boolean deleteSmsTemplate(SmsTemplateVo smsTemplate) throws Exception;

    /***
     * @description 修改模板
     * @param smsTemplate 模板信息
     * @return
     */
    Boolean modifySmsTemplate(SmsTemplateVo smsTemplate) throws Exception;

    /***
     * @description 查询模板审核状态
     * @param smsTemplate 模板信息
     * @return
     */
    Boolean querySmsTemplate(SmsTemplateVo smsTemplate) throws Exception;


}
