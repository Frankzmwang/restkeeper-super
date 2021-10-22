package com.itheima.restkeeper.handler;

import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.pojo.SmsSendRecord;
import com.itheima.restkeeper.pojo.SmsTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName SmsSendHandler.java
 * @Description 邮件发送处理器
 */
public interface SmsSendHandler {

    /***
     * @description 发送短信接口
     * @param smsTemplate 模板
     * @param smsChannel 渠道
     * @param mobiles 手机号
     * @param templateParam 模板动态参数
     */
    Boolean SendSms(
        SmsTemplate smsTemplate,
        SmsChannel smsChannel,
        Set<String> mobiles,
        LinkedHashMap<String, String> templateParam) throws Exception;

    /***
     * @description 查询短信接受情况
     * @param smsSendRecord 发送记录
     * @return
     */
    Boolean querySendSms(SmsSendRecord smsSendRecord) throws Exception;

    /***
     * @description 重试发送
     * @param smsSendRecord
     * @return
     */
    Boolean retrySendSms(SmsSendRecord smsSendRecord) throws Exception;
}
