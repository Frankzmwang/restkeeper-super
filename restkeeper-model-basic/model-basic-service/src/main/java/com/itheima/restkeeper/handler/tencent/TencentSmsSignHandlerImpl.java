package com.itheima.restkeeper.handler.tencent;

import com.alibaba.fastjson.JSONObject;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsSignHandler;
import com.itheima.restkeeper.pojo.SmsSign;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.service.ISmsSignService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName TencentSmsSignHandlerImpl.java
 * @Description 阿里云签名处理器接口
 */
@Service("tencentSmsSignHandler")
public class TencentSmsSignHandlerImpl implements SmsSignHandler {

    @Lazy
    @Autowired
    SmsClient tencentSmsConfig;

    @Autowired
    ISmsSignService smsSignService;

    @Override
    public SmsSign addSmsSign(SmsSignVo smsSignVo) throws Exception {
        // 实例化一个请求对象,每个接口都会对应一个request对象
        AddSmsSignRequest request = new AddSmsSignRequest();
        //签名名称
        request.setSignName(smsSignVo.getSignName());
        //签名类型。其中每种类型后面标注了其可选的 DocumentType（证明类型）：
        //0：公司，可选 DocumentType 有（0，1，2，3）。
        //1：APP，可选 DocumentType 有（0，1，2，3，4） 。
        //2：网站，可选 DocumentType 有（0，1，2，3，5）。
        //3：公众号或者小程序，可选 DocumentType 有（0，1，2，3，6）。
        //4：商标，可选 DocumentType 有（7）。
        //5：政府/机关事业单位/其他机构，可选 DocumentType 有（2，3）。
        request.setSignType(Long.valueOf(smsSignVo.getSignType()));
        //证明类型：
        //0：三证合一。
        //1：企业营业执照。
        //2：组织机构代码证书。
        //3：社会信用代码证书。
        //4：应用后台管理截图（个人开发APP）。
        //5：网站备案后台截图（个人开发网站）。
        //6：小程序设置页面截图（个人认证小程序）。
        //7：商标注册书
        request.setDocumentType(Long.valueOf(smsSignVo.getDocumentType()));
        //是否国际/港澳台短信：
        //0：表示国内短信。
        //1：表示国际/港澳台短信。
        request.setInternational(Long.valueOf(smsSignVo.getInternational()));
        //签名用途：0：自用。1：他用。
        request.setSignPurpose(Long.valueOf(smsSignVo.getSignPurpose()));
        //签名对应的资质证明图片需先进行 base64 编码格式转换
        request.setProofImage(smsSignVo.getProofImage());
        //签名的申请备注。
        request.setRemark(smsSignVo.getRemark());
        // 返回的resp是一个AddSmsSignResponse的实例，与请求对象对应
        AddSmsSignResponse response = tencentSmsConfig.AddSmsSign(request);
        Long signId = response.getAddSignStatus().getSignId();
        if (!EmptyUtil.isNullOrEmpty(signId)){
            //受理成功
            smsSignVo.setAcceptStatus(SuperConstant.YES);
            smsSignVo.setAcceptMsg("受理成功");
            //审核中
            smsSignVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsSignVo.setAuditMsg("审核中");
            smsSignVo.setSignCode(String.valueOf(signId));
        }else {
            smsSignVo.setAcceptStatus(SuperConstant.NO);
            smsSignVo.setAcceptMsg("受理失败");
        }
        //本地持久化
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
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DeleteSmsSignRequest request = new DeleteSmsSignRequest();
        request.setSignId(Long.valueOf(smsSignVo.getSignCode()));
        // 返回的resp是一个DeleteSmsSignResponse的实例，与请求对象对应
        DeleteSmsSignResponse response = tencentSmsConfig.DeleteSmsSign(request);
        String deleteStatus = response.getDeleteSignStatus().getDeleteStatus();
        if ("return successfully!".equals(deleteStatus)){
            return smsSignService.removeById(smsSignVo.getId());
        }
        return false;
    }

    @Override
    public Boolean modifySmsSign(SmsSignVo smsSignVo) throws Exception {
        // 实例化一个请求对象,每个接口都会对应一个request对象
        ModifySmsSignRequest request = new ModifySmsSignRequest();
        //签名编号
        request.setSignId(Long.valueOf(smsSignVo.getSignCode()));
        //签名名称
        request.setSignName(smsSignVo.getSignName());
        //签名类型。其中每种类型后面标注了其可选的 DocumentType（证明类型）：
        //0：公司，可选 DocumentType 有（0，1，2，3）。
        //1：APP，可选 DocumentType 有（0，1，2，3，4） 。
        //2：网站，可选 DocumentType 有（0，1，2，3，5）。
        //3：公众号或者小程序，可选 DocumentType 有（0，1，2，3，6）。
        //4：商标，可选 DocumentType 有（7）。
        //5：政府/机关事业单位/其他机构，可选 DocumentType 有（2，3）。
        request.setSignType(Long.valueOf(smsSignVo.getSignType()));
        //证明类型：
        //0：三证合一。
        //1：企业营业执照。
        //2：组织机构代码证书。
        //3：社会信用代码证书。
        //4：应用后台管理截图（个人开发APP）。
        //5：网站备案后台截图（个人开发网站）。
        //6：小程序设置页面截图（个人认证小程序）。
        //7：商标注册书
        request.setDocumentType(Long.valueOf(smsSignVo.getDocumentType()));
        //是否国际/港澳台短信：
        //0：表示国内短信。
        //1：表示国际/港澳台短信。
        request.setInternational(Long.valueOf(smsSignVo.getInternational()));
        //签名用途：
        //0：自用。
        //1：他用。
        request.setSignPurpose(Long.valueOf(smsSignVo.getSignPurpose()));
        //签名对应的资质证明图片需先进行 base64 编码格式转换
        request.setProofImage(smsSignVo.getProofImage());
        //签名的申请备注。
        request.setRemark(smsSignVo.getRemark());
        // 返回的resp是一个ModifySmsSignResponse的实例，与请求对象对应
        ModifySmsSignResponse response = tencentSmsConfig.ModifySmsSign(request);
        Long signId = response.getModifySignStatus().getSignId();
        if (!EmptyUtil.isNullOrEmpty(signId)){
            //受理成功
            smsSignVo.setAcceptStatus(SuperConstant.YES);
            smsSignVo.setAcceptMsg("受理成功");
            //审核中
            smsSignVo.setAuditStatus(SuperConstant.STATUS_IN_AUDIT);
            smsSignVo.setAuditMsg("审核中");
            smsSignVo.setSignCode(String.valueOf(signId));
            //本地持久化
            return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
        }else {
            //受理失败
            smsSignVo.setAcceptStatus(SuperConstant.NO);
            smsSignVo.setAcceptMsg("受理失败");
            //重置审核状态
            smsSignVo.setAuditStatus(null);
            smsSignVo.setAuditMsg(null);
            smsSignVo.setSignCode(null);
            smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
        }
        return false;
    }

    @Override
    public Boolean querySmsSign(SmsSignVo smsSignVo) throws Exception {
        //实例化一个请求对象,每个接口都会对应一个request对象
        DescribeSmsSignListRequest request = new DescribeSmsSignListRequest();
        //是否国际/港澳台短信： 0：表示国内短信。 1：表示国际/港澳台短信。
        request.setInternational(Long.valueOf(smsSignVo.getInternational()));
        //签名ID数组。 注：默认数组最大长度100。
        List<Long> signCodes = new ArrayList<>();
        signCodes.add(Long.valueOf(smsSignVo.getSignCode()));
        request.setSignIdSet(signCodes.stream().toArray(Long[]::new));
        //返回的resp是一个DescribeSmsSignListResponse的实例与请求对象对应
        DescribeSmsSignListResponse response = tencentSmsConfig.DescribeSmsSignList(request);
        //处理结果
        DescribeSignListStatus[] describeSignListStatusSet = response.getDescribeSignListStatusSet();
        if (describeSignListStatusSet.length==1){
            DescribeSignListStatus describeSignListStatus = describeSignListStatusSet[0];
            Long statusCode = describeSignListStatus.getStatusCode();
            //审核通过
            if (statusCode==0){
                smsSignVo.setAuditStatus(SuperConstant.STATUS_PASS_AUDIT);
                smsSignVo.setAuditMsg("审核通过");
                return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
            //审核失败
            }else if (statusCode==-1){
                smsSignVo.setAuditStatus(SuperConstant.STATUS_FAIL_AUDIT);
                smsSignVo.setAuditMsg(describeSignListStatus.getReviewReply());
                return smsSignService.updateById(BeanConv.toBean(smsSignVo,SmsSign.class));
            }else {
                return true;
            }
        }
        return false;
    }
}
