package com.itheima.restkeeper.handler.aliyun.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.service.ISmsChannelService;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.RegisterBeanHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AlipayConfig.java
 * @Description 支付宝配置类
 */
@Slf4j
@Configuration
public class AliyunSmsConfig {

    @Autowired
    ISmsChannelService smsChannelService;

    private static Map<String,Client> clientHashMap =new ConcurrentHashMap<>();

    /***
     * @description 初始化短信配置
     */
    @PostConstruct
    public void initSmsConfig() {
        //查询阿里云SMS的配置信息
        SmsChannel smsChannel = smsChannelService.findChannelByChannelLabel(SuperConstant.ALIYUN_SMS);
        if (EmptyUtil.isNullOrEmpty(smsChannel)){
            log.warn("阿里云SMS的未配置");
            return;
        }
        this.createOrUpdateClient(smsChannel);
    }

    public void createOrUpdateClient(SmsChannel smsChannel){
        Config config = new Config()
            // 阿里云AccessKey ID
            .setAccessKeyId(smsChannel.getAccessKeyId())
            // 阿里云AccessKey Secret
            .setAccessKeySecret(smsChannel.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsChannel.getDomain();
        try {
            Client client = new Client(config);
            if (clientHashMap.containsKey("aliyunSmsClient")){
                clientHashMap.replace("aliyunSmsClient",client);
            }else {
                clientHashMap.put("aliyunSmsClient",client);
            }
        } catch (Exception e) {
            log.error("阿里云SMS的配置信息出错：{}", ExceptionsUtil.getStackTraceAsString(e));
        }
    }

    /***
     * @description 移除配置
     * @return
     */
    public void removeClient(){
        clientHashMap.clear();
    }

    /***
     * @description 获得配置
     * @return
     */
    public Client queryClient(){
        return clientHashMap.get("aliyunSmsClient");
    }
}
