package com.itheima.restkeeper.handler.aliyun;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsSignHandler;
import com.itheima.restkeeper.pojo.SmsSign;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.service.ISmsSignService;
import com.itheima.restkeeper.utils.BeanConv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SmsSignAdapter.java
 * @Description 阿里云签名处理器接口
 */
@Service("aliyunSmsSignHandler")
public class AliyunSmsSignHandlerImpl implements SmsSignHandler {

    @Autowired
    Client aliyunSmsConfig;

    @Autowired
    ISmsSignService smsSignService;

    @Override
    public SmsSign addSmsSign(SmsSignVo smsSignVo) throws Exception {
        AddSmsSignRequest addSmsSignRequest = new AddSmsSignRequest();
        //签名名称
        addSmsSignRequest.setSignName(smsSignVo.getSignName());
        //签名来源。取值：
        //0：企事业单位的全称或简称。
        //1：工信部备案网站的全称或简称。
        //2：App应用的全称或简称。
        //3：公众号或小程序的全称或简称。
        //4：电商平台店铺名的全称或简称。
        //5：商标名的全称或简称。
        addSmsSignRequest.setSignSource(Integer.valueOf(smsSignVo.getSignType()));
        //短信签名申请说明。请在申请说明中详细描述您的业务使用场景，
        //申请工信部备案网站的全称或简称请在此处填写域名，长度不超过200个字符。
        addSmsSignRequest.setRemark(smsSignVo.getRemark());
        //附加信息
        List<AddSmsSignRequest.AddSmsSignRequestSignFileList> signFileList = addSmsSignRequest.getSignFileList();
        AddSmsSignRequest.AddSmsSignRequestSignFileList fileList = new AddSmsSignRequest.AddSmsSignRequestSignFileList();
        fileList.setFileContents(smsSignVo.getProofImage());
        fileList.setFileSuffix(smsSignVo.getProofType());
        signFileList.add(fileList);
        addSmsSignRequest.setSignFileList(signFileList);
        // 复制代码运行请自行打印 API 的返回值
        AddSmsSignResponse response = aliyunSmsConfig.addSmsSign(addSmsSignRequest);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            //受理成功
            smsSignVo.setAcceptStatus(SuperConstant.YES);
            smsSignVo.setAcceptMsg("受理成功");
            //审核中
            smsSignVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsSignVo.setAuditMsg("审核中");
            smsSignVo.setSignCode(smsSignVo.getSignName());
        }else {
            smsSignVo.setAcceptStatus(SuperConstant.NO);
            smsSignVo.setAcceptMsg(response.getBody().getMessage());
        }
        SmsSign smsSign = BeanConv.toBean(smsSignVo, SmsSign.class);
        boolean flag = smsSignService.save(smsSign);
        if (flag){
            return smsSign;
        }
        return null;
    }

    @Override
    public Boolean deleteSmsSign(SmsSignVo smsSignVo) throws Exception {
        DeleteSmsSignRequest deleteSmsSignRequest = new DeleteSmsSignRequest();;
        deleteSmsSignRequest.setSignName(smsSignVo.getSignName());
        DeleteSmsSignResponse response = aliyunSmsConfig.deleteSmsSign(deleteSmsSignRequest);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            return smsSignService.removeById(smsSignVo.getId());
        }else {
            return false;
        }
    }

    @Override
    public Boolean modifySmsSign(SmsSignVo smsSignVo) throws Exception {
        ModifySmsSignRequest modifySmsSignRequest = new ModifySmsSignRequest();
        //签名名称
        modifySmsSignRequest.setSignName(smsSignVo.getSignName());
        //签名来源。取值：
        //0：企事业单位的全称或简称。
        //1：工信部备案网站的全称或简称。
        //2：App应用的全称或简称。
        //3：公众号或小程序的全称或简称。
        //4：电商平台店铺名的全称或简称。
        //5：商标名的全称或简称。
        modifySmsSignRequest.setSignSource(Integer.valueOf(smsSignVo.getSignType()));
        //短信签名申请说明。请在申请说明中详细描述您的业务使用场景，
        //申请工信部备案网站的全称或简称请在此处填写域名，长度不超过200个字符。
        modifySmsSignRequest.setRemark(smsSignVo.getRemark());
        //附加信息
        List<ModifySmsSignRequest.ModifySmsSignRequestSignFileList> signFileList = modifySmsSignRequest.getSignFileList();
        ModifySmsSignRequest.ModifySmsSignRequestSignFileList fileList = new ModifySmsSignRequest.ModifySmsSignRequestSignFileList();
        fileList.setFileContents(smsSignVo.getProofImage());
        fileList.setFileSuffix(smsSignVo.getProofType());
        signFileList.add(fileList);
        modifySmsSignRequest.setSignFileList(signFileList);
        ModifySmsSignResponse response = aliyunSmsConfig.modifySmsSign(modifySmsSignRequest);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            //受理成功
            smsSignVo.setAcceptStatus(SuperConstant.YES);
            smsSignVo.setAcceptMsg("受理成功");
            //审核中
            smsSignVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsSignVo.setAuditMsg("审核中");
            smsSignVo.setSignCode(smsSignVo.getSignName());
            return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
        }else {
            //受理失败
            smsSignVo.setAcceptStatus(SuperConstant.NO);
            smsSignVo.setAcceptMsg(response.getBody().getMessage());
            //重置审核状态
            smsSignVo.setAuditStatus(null);
            smsSignVo.setAuditMsg(null);
            smsSignVo.setSignCode(null);
            smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
            return false;
        }
    }

    @Override
    public Boolean querySmsSign(SmsSignVo smsSignVo) throws Exception {
        QuerySmsSignRequest querySmsSignRequest = new QuerySmsSignRequest();
        querySmsSignRequest.setSignName(smsSignVo.getSignName());
        // 复制代码运行请自行打印 API 的返回值
        QuerySmsSignResponse response = aliyunSmsConfig.querySmsSign(querySmsSignRequest);
        //受理状态
        String code = response.getBody().getCode();
        if ("OK".equals(code)){
            Integer SignStatus =response.getBody().getSignStatus();
            //审核通过
            if (SignStatus==1){
                smsSignVo.setAuditStatus(SuperConstant.STATUS_PASS_AUDIT);
                smsSignVo.setAuditMsg("审核通过");
                return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
            //审核失败
            }else if (SignStatus==2){
                smsSignVo.setAuditStatus(SuperConstant.STATUS_FAIL_AUDIT);
                smsSignVo.setAuditMsg(response.getBody().getReason());
                return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
            }else {
                return true;
            }
        }else {
            return false;
        }
    }
}
