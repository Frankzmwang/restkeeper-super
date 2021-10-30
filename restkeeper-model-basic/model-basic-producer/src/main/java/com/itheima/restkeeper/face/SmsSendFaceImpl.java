package com.itheima.restkeeper.face;

import com.itheima.restkeeper.SmsSendFace;
import com.itheima.restkeeper.adapter.SmsSendAdapter;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.SmsSendRecord;
import com.itheima.restkeeper.req.SmsSendRecordVo;
import com.itheima.restkeeper.utils.BeanConv;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @ClassName SmsSendFace.java
 * @Description dubbo短信发送接口
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "SendSms",retries = 0),
                @Method(name = "querySendSms",retries = 2),
                @Method(name = "retrySendSms",retries = 0)
        })
public class SmsSendFaceImpl implements SmsSendFace {

    @Autowired
    SmsSendAdapter smsSendAdapter;


    @Override
    public Boolean SendSms(String templateNo,
                           String sginNo,
                           String loadBalancerType,
                           Set<String> mobiles,
                           LinkedHashMap<String, String> templateParam) throws ProjectException {
        return smsSendAdapter.SendSms(templateNo,sginNo,loadBalancerType,mobiles,templateParam);
    }

    @Override
    public Boolean querySendSms(SmsSendRecordVo smsSendRecordVo) throws ProjectException {
        SmsSendRecord smsSendRecord = BeanConv.toBean(smsSendRecordVo, SmsSendRecord.class);
        return smsSendAdapter.querySendSms(smsSendRecord);
    }

    @Override
    public Boolean retrySendSms(SmsSendRecordVo smsSendRecord) throws ProjectException {
        return null;
    }
}
