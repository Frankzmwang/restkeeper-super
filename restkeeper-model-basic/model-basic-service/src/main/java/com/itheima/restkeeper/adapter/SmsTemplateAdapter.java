package com.itheima.restkeeper.adapter;

import com.itheima.restkeeper.req.SmsTemplateVo;

/**
 * @ClassName SmsTemplateVoAdapter.java
 * @Description 模板适配器接口
 */
public interface SmsTemplateAdapter {

    /***
     * @description 申请模板
     * @param smsTemplateVo 模板信息
     * @return
     */
    SmsTemplateVo addSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception;

    /***
     * @description 删除模板
     * @param smsTemplateVo 模板信息
     * @return
     */
    Boolean deleteSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception;

    /***
     * @description 修改模板
     * @param smsTemplateVo 模板信息
     * @return
     */
    Boolean modifySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception;

    /***
     * @description 查询模板审核状态
     * @param smsTemplateVo 模板信息
     * @return
     */
    Boolean querySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception;


}
