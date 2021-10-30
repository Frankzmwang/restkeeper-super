package com.itheima.restkeeper;

import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.SmsSendRecordVo;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @ClassName SmsSendFace.java
 * @Description 邮件发送dubbo接口
 */
public interface SmsSendFace {

    /***
     * @description 发送短信接口
     * @param templateNo 应用模板编号
     * @param sginNo 应用签名编号
     * @param loadBalancerType 通道负载均衡策略
     * @param mobiles 手机号
     * @param templateParam 模板动态参数
     */
    Boolean SendSms(
            String templateNo,
            String sginNo,
            String loadBalancerType,
            Set<String> mobiles,
            LinkedHashMap<String,String> templateParam) throws ProjectException;

    /***
     * @description 查询短信接受情况
     * @param smsSendRecordVo 发送记录
     * @return
     */
    Boolean querySendSms(SmsSendRecordVo smsSendRecordVo) throws ProjectException;

    /***
     * @description 重试发送
     * @param smsSendRecordVo
     * @return
     */
    Boolean retrySendSms(SmsSendRecordVo smsSendRecordVo) throws ProjectException;
}
