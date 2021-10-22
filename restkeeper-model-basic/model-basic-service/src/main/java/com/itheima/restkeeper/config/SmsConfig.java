package com.itheima.restkeeper.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.http.DefaultRetryPolicy;
import com.baidubce.http.RetryPolicy;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.service.ISmsChannelService;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SmsConfig.java
 * @Description 短信服务配置
 */
@Slf4j
@Configuration
public class SmsConfig {

    @Autowired
    ISmsChannelService smsChannelService;

    @Bean
    public Client aliyunSmsConfig(){
        //查询阿里云SMS的配置信息
        SmsChannel smsChannel = smsChannelService.findChannelByChannelLabel(SuperConstant.ALIYUN_SMS);
        if (EmptyUtil.isNullOrEmpty(smsChannel)){
            log.warn("阿里云SMS的未配置");
            return null;
        }
        Config config = new Config()
            // 阿里云AccessKey ID
            .setAccessKeyId(smsChannel.getAccessKeyId())
            // 阿里云AccessKey Secret
            .setAccessKeySecret(smsChannel.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsChannel.getDomain();
        try {
            return new Client(config);
        } catch (Exception e) {
           log.error("阿里云SMS的配置信息出错：{}", ExceptionsUtil.getStackTraceAsString(e));
           return null;
        }
    }

    @Bean
    public SmsClient tencentSmsConfig(){
        //查询腾讯云SMS的配置信息
        SmsChannel smsChannel = smsChannelService.findChannelByChannelLabel(SuperConstant.TENCENT_SMS);
        if (EmptyUtil.isNullOrEmpty(smsChannel)){
            log.warn("腾讯云SMS的未配置");
            return null;
        }
        /* 必要步骤：
         * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
         * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
         * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
         * 以免泄露密钥对危及你的财产安全。
         * CAM密匙查询: https://console.cloud.tencent.com/cam/capi*/
        Credential cred = new Credential(smsChannel.getAccessKeyId(), smsChannel.getAccessKeySecret());
        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        // 设置代理
        // httpProfile.setProxyHost("真实代理ip");
        // httpProfile.setProxyPort(真实代理端口);
        /* SDK默认使用POST方法。
         * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
        httpProfile.setReqMethod("POST");
        /* SDK有默认的超时时间，非必要请不要进行调整
         * 如有需要请在代码中查阅以获取最新的默认值 */
        httpProfile.setConnTimeout(60);
        /* SDK会自动指定域名。通常是不需要特地指定域名的，但是如果你访问的是金融区的服务
         * 则必须手动指定域名，例如sms的上海金融区域名： sms.ap-shanghai-fsi.tencentcloudapi.com */
        httpProfile.setEndpoint(smsChannel.getDomain());
        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象
         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
        SmsClient client = new SmsClient(cred, "ap-guangzhou",clientProfile);
        return client;
    }

    @Bean
    public com.baidubce.services.sms.SmsClient baiduSmsConfig(){
        //查询百度云SMS的配置信息
        SmsChannel smsChannel = smsChannelService.findChannelByChannelLabel(SuperConstant.BAIDU_SMS);
        if (EmptyUtil.isNullOrEmpty(smsChannel)){
            log.warn("百度云SMS的未配置");
            return null;
        }
        //百度云配置
        SmsClientConfiguration config = new SmsClientConfiguration()
            //站点
            .withEndpoint(smsChannel.getDomain())
            //百度云密钥
            .withCredentials(new DefaultBceCredentials(smsChannel.getAccessKeyId(), smsChannel.getAccessKeySecret()))
            // 设置HTTP最大连接数为10
            .withMaxConnections(10)
            // 设置TCP连接超时为5000毫秒
            .withConnectionTimeoutInMillis(5000)
            // 设置默重试最大的重试次数为3
            .withRetryPolicy(new DefaultRetryPolicy())
            //设置Socket传输数据超时的时间为2000毫秒
            .withSocketTimeoutInMillis(2000);
        return new com.baidubce.services.sms.SmsClient(config);
    }

}
