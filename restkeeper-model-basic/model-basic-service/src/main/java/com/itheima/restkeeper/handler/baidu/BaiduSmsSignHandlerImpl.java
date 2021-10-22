package com.itheima.restkeeper.handler.baidu;

import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.model.v3.*;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsSignHandler;
import com.itheima.restkeeper.pojo.SmsSign;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.service.ISmsSignService;
import com.itheima.restkeeper.utils.BeanConv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName BaiduSmsSignHandlerImpl.java
 * @Description 百度签名审核
 */
@Service("baiduSmsSignHandler")
public class BaiduSmsSignHandlerImpl implements SmsSignHandler {

    @Autowired
    SmsClient baiduSmsConfig;

    @Autowired
    ISmsSignService smsSignService;

    @Override
    public SmsSign addSmsSign(SmsSignVo smsSignVo) throws Exception {
        CreateSignatureRequest request = new CreateSignatureRequest()
            //签名内容
            .withContent(smsSignVo.getSignName())
            //签名类型。
            //Enterprise：企业
            //MobileApp：移动应用名称
            //Web：工信部备案的网站名称
            //WeChatPublic：微信公众号名称
            //Brand：商标名称
            //Else：其他
            .withContentType(smsSignVo.getSignType())
            //签名适用的国家类型
            //DOMESTIC：国内
            //INTERNATIONAL：国际/港澳台
            //GLOBAL：全球均适用
            //默认为DOMESTIC
            .withCountryType(smsSignVo.getInternational())
            //对于签名的描述
            .withDescription(smsSignVo.getRemark())
            //签名的证明文件经过base64编码后的字符串。文件大小不超过2MB。
            .withSignatureFileBase64(smsSignVo.getProofImage())
            //签名证明文件的格式，目前支持JPG、PNG、JPEG三种格式
            .withSignatureFileFormat(smsSignVo.getProofType());
        CreateSignatureResponse response = baiduSmsConfig.createSignature(request);
        String status = response.getStatus();
        if ("SUBMITTED".equals(status)) {
            //受理成功
            smsSignVo.setAcceptStatus(SuperConstant.YES);
            smsSignVo.setAcceptMsg("受理成功");
            //审核中
            smsSignVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsSignVo.setAuditMsg("审核中");
            smsSignVo.setSignCode(response.getSignatureId());

        } else {
            smsSignVo.setAcceptStatus(SuperConstant.NO);
            smsSignVo.setAcceptMsg(response.getStatus());
        }
        //本地持久化
        SmsSign smsSign = BeanConv.toBean(smsSignVo, SmsSign.class);
        boolean flag = smsSignService.save(smsSign);
        if (flag){
            return smsSign;
        }
        return null;
    }

    @Override
    public Boolean deleteSmsSign(SmsSignVo smsSignVo) throws Exception {
        DeleteSignatureRequest request = new DeleteSignatureRequest()
            //短信模板唯一识别码
            .withSignatureId(smsSignVo.getSignCode());
        baiduSmsConfig.deleteSignature(request);
        return smsSignService.removeById(smsSignVo.getId());
    }

    @Override
    public Boolean modifySmsSign(SmsSignVo smsSignVo) throws Exception {
        ModifySignatureRequest request = new ModifySignatureRequest()
            .withSignatureId(smsSignVo.getSignCode())
            //签名内容
            .withContent(smsSignVo.getSignName())
            //签名类型。
            //Enterprise：企业
            //MobileApp：移动应用名称
            //Web：工信部备案的网站名称
            //WeChatPublic：微信公众号名称
            //Brand：商标名称
            //Else：其他
            .withContentType(smsSignVo.getSignType())
            //签名适用的国家类型
            //DOMESTIC：国内
            //INTERNATIONAL：国际/港澳台
            //GLOBAL：全球均适用
            //默认为DOMESTIC
            .withCountryType(smsSignVo.getInternational())
            //对于签名的描述
            .withDescription(smsSignVo.getRemark())
            //签名的证明文件经过base64编码后的字符串。文件大小不超过2MB。
            .withSignatureFileBase64(smsSignVo.getProofImage())
            //签名证明文件的格式，目前支持JPG、PNG、JPEG三种格式
            .withSignatureFileFmt(smsSignVo.getProofType());
        baiduSmsConfig.modifySignature(request);
        //受理成功
        smsSignVo.setAcceptStatus(SuperConstant.YES);
        smsSignVo.setAcceptMsg("受理成功");
        //审核中
        smsSignVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
        smsSignVo.setAuditMsg("审核中");
        //本地持久化
        return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
    }

    @Override
    public Boolean querySmsSign(SmsSignVo smsSignVo) throws Exception {
        GetSignatureRequest request = new GetSignatureRequest();
        //查询
        request.setSignatureId(smsSignVo.getSignCode());
        GetSignatureResponse response = baiduSmsConfig.getSignature(request);
        //处理返回结果
        String status = response.getStatus();
        //审核通过
        if ("READY".equals(status)) {
            smsSignVo.setAuditStatus(SuperConstant.STATUS_PASS_AUDIT);
            smsSignVo.setAuditMsg("审核通过");
            return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
            //审核失败
        } else if ("REJECTED".equals(status) || "ABORTED".equals(status)) {
            smsSignVo.setAuditStatus(SuperConstant.STATUS_FAIL_AUDIT);
            smsSignVo.setAuditMsg(response.getReview());
            return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
        } else {
            return true;
        }
    }
}
