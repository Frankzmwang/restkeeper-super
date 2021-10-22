package com.itheima.restkeeper.handler.baidu;

import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.model.v3.*;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsTemplateHandler;
import com.itheima.restkeeper.pojo.SmsTemplate;
import com.itheima.restkeeper.req.SmsTemplateVo;
import com.itheima.restkeeper.service.ISmsSignService;
import com.itheima.restkeeper.service.ISmsTemplateService;
import com.itheima.restkeeper.utils.BeanConv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName BaiduTemplateHandlerImpl.java
 * @Description 百度模板审核
 */
@Service("baiduTemplateHandler")
public class BaiduTemplateHandlerImpl implements SmsTemplateHandler {

    @Autowired
    SmsClient baiduSmsConfig;

    @Autowired
    ISmsTemplateService smsTemplateService;

    @Override
    public SmsTemplate addSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        CreateTemplateRequest request = new CreateTemplateRequest()
            //模板名称
            .withName(smsTemplateVo.getTemplateName())
            //模板内容
            .withContent(smsTemplateVo.getContent())
            //短信类型
            .withSmsType(smsTemplateVo.getTemplateType())
            //适用国家类型
            //DOMESTIC：国内
            //INTERNATIONAL：国际/港澳台
            //GLOBAL：全球
            .withCountryType(smsTemplateVo.getInternational())
            //模板描述
            .withDescription(smsTemplateVo.getRemark());
        CreateTemplateResponse response = baiduSmsConfig.createTemplate(request);
        String status = response.getStatus();
        if ("SUBMITTED".equals(status)) {
            //受理成功
            smsTemplateVo.setAcceptStatus(SuperConstant.YES);
            smsTemplateVo.setAcceptMsg("受理成功");
            //审核中
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsTemplateVo.setAuditMsg("审核中");
            smsTemplateVo.setTemplateCode(response.getTemplateId());

        } else {
            smsTemplateVo.setAcceptStatus(SuperConstant.NO);
            smsTemplateVo.setAcceptMsg(response.getStatus());
        }
        //本地持久化
        SmsTemplate smsTemplate = BeanConv.toBean(smsTemplateVo, SmsTemplate.class);
        boolean flag = smsTemplateService.save(smsTemplate);
        if (flag){
            return smsTemplate;
        }
        return null;
    }

    @Override
    public Boolean deleteSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        DeleteTemplateRequest request =new DeleteTemplateRequest()
            .withTemplateId(smsTemplateVo.getTemplateCode());
        baiduSmsConfig.deleteTemplate(request);
        return smsTemplateService.removeById(smsTemplateVo.getId());
    }

    @Override
    public Boolean modifySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        ModifyTemplateRequest request = new ModifyTemplateRequest()
            //模板id
            .withTemplateId(smsTemplateVo.getTemplateCode())
            //模板名称
            .withName(smsTemplateVo.getTemplateName())
            //模板内容
            .withContent(smsTemplateVo.getContent())
            //短信类型
            .withSmsType(smsTemplateVo.getTemplateType())
            //适用国家类型
            //DOMESTIC：国内
            //INTERNATIONAL：国际/港澳台
            //GLOBAL：全球
            .withCountryType(smsTemplateVo.getInternational())
            //模板描述
            .withDescription(smsTemplateVo.getRemark());
        baiduSmsConfig.modifyTemplate(request);
        //受理成功
        smsTemplateVo.setAcceptStatus(SuperConstant.YES);
        smsTemplateVo.setAcceptMsg("受理成功");
        //审核中
        smsTemplateVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
        smsTemplateVo.setAuditMsg("审核中");
        //本地持久化
        return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
    }

    @Override
    public Boolean querySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        GetTemplateRequest request = new GetTemplateRequest()
                .withTemplateId(smsTemplateVo.getTemplateCode());
        GetTemplateResponse response = baiduSmsConfig.getTemplate(request);
        //处理返回结果
        String status = response.getStatus();
        //审核通过
        if ("READY".equals(status)) {
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_PASS_AUDIT);
            smsTemplateVo.setAuditMsg("审核通过");
            return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            //审核失败
        } else if ("REJECTED".equals(status) || "ABORTED".equals(status)) {
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_FAIL_AUDIT);
            smsTemplateVo.setAuditMsg(response.getReview());
            return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
        } else {
            return true;
        }
    }
}
