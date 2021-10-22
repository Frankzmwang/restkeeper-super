package com.itheima.restkeeper.handler.aliyun;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsTemplateHandler;
import com.itheima.restkeeper.pojo.SmsTemplate;
import com.itheima.restkeeper.req.SmsTemplateVo;
import com.itheima.restkeeper.service.ISmsTemplateService;
import com.itheima.restkeeper.utils.BeanConv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @ClassName SmsTemplateAdapter.java
 * @Description 阿里云模板处理器接口
 */
@Service("aliyunTemplateHandler")
public class AliyunTemplateHandlerImpl implements SmsTemplateHandler {

    @Lazy
    @Autowired
    Client aliyunSmsConfig;

    @Autowired
    ISmsTemplateService smsTemplateService;

    @Override
    public SmsTemplate addSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        AddSmsTemplateRequest request = new AddSmsTemplateRequest();
        //短信类型。取值：
        //0：验证码。
        //1：短信通知。
        //2：推广短信。
        //3：国际/港澳台消息。
        request.setTemplateType(Integer.valueOf(smsTemplateVo.getTemplateType()));
        request.setTemplateName(smsTemplateVo.getTemplateName());
        request.setTemplateContent(smsTemplateVo.getContent());
        request.setRemark(smsTemplateVo.getRemark());
        AddSmsTemplateResponse response = aliyunSmsConfig.addSmsTemplate(request);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            //受理成功
            smsTemplateVo.setAcceptStatus(SuperConstant.YES);
            smsTemplateVo.setAcceptMsg("受理成功");
            //审核中
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsTemplateVo.setAuditMsg("审核中");
            //短信模板CODE。
            //您可以使用模板CODE通过
            //QuerySmsTemplate接口或短信服务控制台查看模板申请状态和结果。
            smsTemplateVo.setTemplateCode(response.getBody().getTemplateCode());
        }else {
            //受理失败
            smsTemplateVo.setAcceptStatus(SuperConstant.NO);
            smsTemplateVo.setAcceptMsg( response.getBody().getMessage());
        }
        SmsTemplate smsTemplate = BeanConv.toBean(smsTemplateVo, SmsTemplate.class);
        boolean flag = smsTemplateService.save(smsTemplate);
        if (flag){
            return smsTemplate;
        }
        return null;
    }

    @Override
    public Boolean deleteSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        DeleteSmsTemplateRequest request = new DeleteSmsTemplateRequest();
        request.setTemplateCode(smsTemplateVo.getTemplateCode());
        DeleteSmsTemplateResponse response = aliyunSmsConfig.deleteSmsTemplate(request);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            return smsTemplateService.removeById(smsTemplateVo.getId());
        }else {
            return false;
        }
    }

    @Override
    public Boolean modifySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        ModifySmsTemplateRequest request = new ModifySmsTemplateRequest();
        //短信类型。取值：
        //0：验证码。
        //1：短信通知。
        //2：推广短信。
        //3：国际/港澳台消息。
        request.setTemplateType(Integer.valueOf(smsTemplateVo.getTemplateType()));
        request.setTemplateName(smsTemplateVo.getTemplateName());
        request.setTemplateCode(smsTemplateVo.getTemplateCode());
        request.setTemplateContent(smsTemplateVo.getContent());
        request.setRemark(smsTemplateVo.getRemark());
        ModifySmsTemplateResponse response = aliyunSmsConfig.modifySmsTemplate(request);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            //受理成功
            smsTemplateVo.setAcceptStatus(SuperConstant.YES);
            smsTemplateVo.setAcceptMsg("受理成功");
            //审核中
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsTemplateVo.setAuditMsg("审核中");
            //短信模板CODE。
            //您可以使用模板CODE通过
            //QuerySmsTemplate接口或短信服务控制台查看模板申请状态和结果。
            smsTemplateVo.setTemplateCode(response.getBody().getTemplateCode());
            return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
        }else {
            //受理失败
            smsTemplateVo.setAcceptStatus(SuperConstant.NO);
            smsTemplateVo.setAcceptMsg( response.getBody().getMessage());
            smsTemplateVo.setAuditStatus(null);
            smsTemplateVo.setAuditMsg(null);
            smsTemplateVo.setTemplateCode(null);
            smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            return false;
        }
    }

    @Override
    public Boolean querySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        QuerySmsTemplateRequest request = new QuerySmsTemplateRequest();
        QuerySmsTemplateResponse response = aliyunSmsConfig.querySmsTemplate(request);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            Integer templateStatus =response.getBody().getTemplateStatus();
            //审核通过
            if (templateStatus == 1) {
                smsTemplateVo.setAuditStatus(SuperConstant.STATUS_PASS_AUDIT);
                smsTemplateVo.setAuditMsg("审核通过");
                return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            //审核失败
            } else if (templateStatus == 2) {
                smsTemplateVo.setAuditStatus(SuperConstant.STATUS_FAIL_AUDIT);
                smsTemplateVo.setAuditMsg(response.getBody().getReason());
                return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            } else {
                return true;
            }
        }else {
            return false;
        }
    }
}
