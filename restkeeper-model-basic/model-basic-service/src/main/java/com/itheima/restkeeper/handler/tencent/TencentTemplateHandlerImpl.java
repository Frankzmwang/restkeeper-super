package com.itheima.restkeeper.handler.tencent;

import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsTemplateHandler;
import com.itheima.restkeeper.pojo.SmsTemplate;
import com.itheima.restkeeper.req.SmsTemplateVo;
import com.itheima.restkeeper.service.ISmsTemplateService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TencentTemplateHandlerImpl.java
 * @Description 腾讯云模板处理器接口
 */
@Service("tencentTemplateHandler")
public class TencentTemplateHandlerImpl implements SmsTemplateHandler {

    @Lazy
    @Autowired
    SmsClient tencentSmsConfig;

    @Autowired
    ISmsTemplateService smsTemplateService;

    @Override
    public SmsTemplate addSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        // 实例化一个请求对象,每个接口都会对应一个request对象
        AddSmsTemplateRequest request = new AddSmsTemplateRequest();
        //模板名称
        request.setTemplateName(smsTemplateVo.getTemplateName());
        //模板内容
        request.setTemplateContent(smsTemplateVo.getContent());
        //短信类型，0表示普通短信, 1表示营销短信。
        request.setSmsType(Long.valueOf(smsTemplateVo.getTemplateType()));
        //是否国际/港澳台短信：
        //0：表示国内短信。
        //1：表示国际/港澳台短信。
        request.setInternational(Long.valueOf(smsTemplateVo.getInternational()));
        //模板备注，例如申请原因，使用场景等
        request.setRemark(smsTemplateVo.getRemark());
        // 返回的resp是一个AddSmsTemplateResponse的实例，与请求对象对应
        AddSmsTemplateResponse response = tencentSmsConfig.AddSmsTemplate(request);
        //受理状态
        String templateId = response.getAddTemplateStatus().getTemplateId();
        if (!EmptyUtil.isNullOrEmpty(templateId)) {
            //受理成功
            smsTemplateVo.setAcceptStatus(SuperConstant.YES);
            smsTemplateVo.setAcceptMsg("受理成功");
            //审核中
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsTemplateVo.setAuditMsg("审核中");
            //短信模板CODE。
            //您可以使用模板CODE通过
            smsTemplateVo.setTemplateCode(templateId);
            //QuerySmsTemplate接口或短信服务控制台查看模板申请状态和结果。
        } else {
            //受理失败
            smsTemplateVo.setAcceptStatus(SuperConstant.NO);
            smsTemplateVo.setAcceptMsg("受理失败");
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
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DeleteSmsTemplateRequest request = new DeleteSmsTemplateRequest();
        request.setTemplateId(Long.valueOf(smsTemplateVo.getTemplateCode()));
        // 返回的resp是一个DeleteSmsTemplateResponse的实例，与请求对象对应
        DeleteSmsTemplateResponse response = tencentSmsConfig.DeleteSmsTemplate(request);
        String deleteStatus = response.getDeleteTemplateStatus().getDeleteStatus();
        if ("return successfully!".equals(deleteStatus)) {
            return smsTemplateService.removeById(smsTemplateVo.getId());
        }
        return false;
    }

    @Override
    public Boolean modifySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        // 实例化一个请求对象,每个接口都会对应一个request对象
        ModifySmsTemplateRequest request = new ModifySmsTemplateRequest();
        //模板名称
        request.setTemplateName(smsTemplateVo.getTemplateName());
        //模板code
        request.setTemplateId(Long.valueOf(smsTemplateVo.getTemplateCode()));
        //模板内容
        request.setTemplateContent(smsTemplateVo.getContent());
        //短信类型，0表示普通短信, 1表示营销短信。
        request.setSmsType(Long.valueOf(smsTemplateVo.getTemplateType()));
        //是否国际/港澳台短信：
        //0：表示国内短信。
        //1：表示国际/港澳台短信。
        request.setInternational(Long.valueOf(smsTemplateVo.getInternational()));
        //模板备注，例如申请原因，使用场景等
        request.setRemark(smsTemplateVo.getRemark());
        // 返回的resp是一个ModifySmsTemplateResponse的实例，与请求对象对应
        ModifySmsTemplateResponse response = tencentSmsConfig.ModifySmsTemplate(request);
        Long templateId = response.getModifyTemplateStatus().getTemplateId();
        if (!EmptyUtil.isNullOrEmpty(templateId)) {
            //受理成功
            smsTemplateVo.setAcceptStatus(SuperConstant.YES);
            smsTemplateVo.setAcceptMsg("受理成功");
            //审核中
            smsTemplateVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsTemplateVo.setAuditMsg("审核中");
            smsTemplateVo.setTemplateCode(String.valueOf(templateId));
            //本地持久化
            return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
        }else {
            //受理失败
            smsTemplateVo.setAcceptStatus(SuperConstant.NO);
            smsTemplateVo.setAcceptMsg("受理失败");
            //重置审核状态
            smsTemplateVo.setAuditStatus(null);
            smsTemplateVo.setAuditMsg(null);
            smsTemplateVo.setTemplateCode(null);
            smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            return false;
        }
    }

    @Override
    public Boolean querySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DescribeSmsTemplateListRequest request = new DescribeSmsTemplateListRequest();
        //模板 ID 数组。 注：默认数组最大长度100。
        List<Long> templateCodes = new ArrayList<>();
        templateCodes.add(Long.valueOf(smsTemplateVo.getTemplateCode()));
        request.setTemplateIdSet(templateCodes.stream().toArray(Long[]::new));
        // 返回的resp是一个DescribeSmsTemplateListResponse的实例，与请求对象对应
        DescribeSmsTemplateListResponse response = tencentSmsConfig.DescribeSmsTemplateList(request);
        //处理结果
        DescribeTemplateListStatus[] describeTemplateStatusSet = response.getDescribeTemplateStatusSet();
        response.getDescribeTemplateStatusSet();
        if (describeTemplateStatusSet.length == 1) {
            DescribeTemplateListStatus describeTemplateListStatus = describeTemplateStatusSet[0];
            Long statusCode = describeTemplateListStatus.getStatusCode();
            //审核通过
            if (statusCode == 0) {
                smsTemplateVo.setAuditStatus(SuperConstant.STATUS_PASS_AUDIT);
                smsTemplateVo.setAuditMsg("审核通过");
                return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            //审核失败
            } else if (statusCode == -1) {
                smsTemplateVo.setAuditStatus(SuperConstant.STATUS_FAIL_AUDIT);
                smsTemplateVo.setAuditMsg(describeTemplateListStatus.getReviewReply());
                return smsTemplateService.updateById(BeanConv.toBean(smsTemplateVo, SmsTemplate.class));
            } else {
                return true;
            }
        }
        return false;
    }
}
