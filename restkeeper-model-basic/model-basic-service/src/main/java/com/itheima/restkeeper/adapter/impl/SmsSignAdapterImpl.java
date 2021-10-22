package com.itheima.restkeeper.adapter.impl;

import com.itheima.restkeeper.adapter.SmsSignAdapter;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsSendHandler;
import com.itheima.restkeeper.handler.SmsSignHandler;
import com.itheima.restkeeper.pojo.SmsSign;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.RegisterBeanHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SmsSignAdapterImpl.java
 * @Description 签名适配器实现
 */
@Service("smsSignAdapter")
public class SmsSignAdapterImpl implements SmsSignAdapter {

    @Autowired
    SmsSignHandler aliyunSmsSignHandler;

    @Autowired
    SmsSignHandler tencentSmsSignHandler;

    @Autowired
    SmsSignHandler baiduSmsSignHandler;

    @Autowired
    RegisterBeanHandler registerBeanHandler;

    private static Map<String,String> SmsSignHandlers =new HashMap<>();

    static {
        SmsSignHandlers.put(SuperConstant.ALIYUN_SMS,"aliyunSmsSignHandler");
        SmsSignHandlers.put(SuperConstant.TENCENT_SMS,"tencentSmsSignHandler");
        SmsSignHandlers.put(SuperConstant.BAIDU_SMS,"baiduSmsSignHandler");
    }

    @Override
    public SmsSignVo addSmsSign(SmsSignVo smsSignVo) throws Exception {
        String channelLabel = smsSignVo.getChannelLabel();
        String stringSmsSignHandler = SmsSignHandlers.get(channelLabel);
        SmsSignHandler smsSignHandler =registerBeanHandler.getBean(stringSmsSignHandler,SmsSignHandler.class);
        return BeanConv.toBean(smsSignHandler.addSmsSign(smsSignVo),SmsSignVo.class);
    }

    @Override
    public Boolean deleteSmsSign(SmsSignVo smsSignVo) throws Exception {
        String channelLabel = smsSignVo.getChannelLabel();
        String stringSmsSignHandler = SmsSignHandlers.get(channelLabel);
        SmsSignHandler smsSignHandler =registerBeanHandler.getBean(stringSmsSignHandler,SmsSignHandler.class);
        return smsSignHandler.deleteSmsSign(smsSignVo);
    }

    @Override
    public Boolean modifySmsSign(SmsSignVo smsSignVo) throws Exception {
        String channelLabel = smsSignVo.getChannelLabel();
        String stringSmsSignHandler = SmsSignHandlers.get(channelLabel);
        SmsSignHandler smsSignHandler =registerBeanHandler.getBean(stringSmsSignHandler,SmsSignHandler.class);
        return smsSignHandler.modifySmsSign(smsSignVo);
    }

    @Override
    public Boolean querySmsSign(SmsSignVo smsSignVo) throws Exception {
        String channelLabel = smsSignVo.getChannelLabel();
        String stringSmsSignHandler = SmsSignHandlers.get(channelLabel);
        SmsSignHandler smsSignHandler =registerBeanHandler.getBean(stringSmsSignHandler,SmsSignHandler.class);
        return smsSignHandler.querySmsSign(smsSignVo);
    }
}
