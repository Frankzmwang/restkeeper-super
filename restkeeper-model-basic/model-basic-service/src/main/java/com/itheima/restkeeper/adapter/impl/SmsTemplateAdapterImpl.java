package com.itheima.restkeeper.adapter.impl;

import com.itheima.restkeeper.adapter.SmsTemplateAdapter;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.handler.SmsSignHandler;
import com.itheima.restkeeper.handler.SmsTemplateHandler;
import com.itheima.restkeeper.pojo.SmsTemplate;
import com.itheima.restkeeper.req.SmsTemplateVo;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.RegisterBeanHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SmsTemplateAdapterImpl.java
 * @Description 模板适配器实现
 */
@Service("smsTemplateAdapter")
public class SmsTemplateAdapterImpl implements SmsTemplateAdapter {

    @Autowired
    RegisterBeanHandler registerBeanHandler;

    private static Map<String,String> smsTemplateHandlers =new HashMap<>();

    static {
        smsTemplateHandlers.put(SuperConstant.ALIYUN_SMS,"aliyunSmsTemplateHandler");
        smsTemplateHandlers.put(SuperConstant.TENCENT_SMS,"tencentSmsTemplateHandler");
        smsTemplateHandlers.put(SuperConstant.BAIDU_SMS,"baiduSmsTemplateHandler");
    }
    
    @Override
    public SmsTemplateVo addSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        String channelLabel = smsTemplateVo.getChannelLabel();
        String stringSmsTemplateHandler = smsTemplateHandlers.get(channelLabel);
        SmsTemplateHandler smsTemplateHandler =registerBeanHandler
                .getBean(stringSmsTemplateHandler,SmsTemplateHandler.class);
        return BeanConv.toBean(smsTemplateHandler.addSmsTemplate(smsTemplateVo),SmsTemplateVo.class);
    }

    @Override
    public Boolean deleteSmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        String channelLabel = smsTemplateVo.getChannelLabel();
        String stringSmsTemplateHandler = smsTemplateHandlers.get(channelLabel);
        SmsTemplateHandler smsTemplateHandler =registerBeanHandler
                .getBean(stringSmsTemplateHandler,SmsTemplateHandler.class);
        return smsTemplateHandler.deleteSmsTemplate(smsTemplateVo);
    }

    @Override
    public Boolean modifySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        String channelLabel = smsTemplateVo.getChannelLabel();
        String stringSmsTemplateHandler = smsTemplateHandlers.get(channelLabel);
        SmsTemplateHandler smsTemplateHandler =registerBeanHandler
                .getBean(stringSmsTemplateHandler,SmsTemplateHandler.class);
        return smsTemplateHandler.modifySmsTemplate(smsTemplateVo);
    }

    @Override
    public Boolean querySmsTemplate(SmsTemplateVo smsTemplateVo) throws Exception {
        String channelLabel = smsTemplateVo.getChannelLabel();
        String stringSmsTemplateHandler = smsTemplateHandlers.get(channelLabel);
        SmsTemplateHandler smsTemplateHandler =registerBeanHandler
                .getBean(stringSmsTemplateHandler,SmsTemplateHandler.class);
        return smsTemplateHandler.querySmsTemplate(smsTemplateVo);
    }
}
