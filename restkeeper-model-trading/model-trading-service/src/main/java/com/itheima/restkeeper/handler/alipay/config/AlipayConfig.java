package com.itheima.restkeeper.handler.alipay.config;

import com.alibaba.fastjson.JSONArray;
import com.alipay.easysdk.kernel.Config;
import com.itheima.restkeeper.constant.TradingConstant;
import com.itheima.restkeeper.pojo.PayChannel;
import com.itheima.restkeeper.req.OtherConfigVo;
import com.itheima.restkeeper.req.PayChannelVo;
import com.itheima.restkeeper.service.IPayChannelService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AlipayConfig.java
 * @Description 支付宝配置类
 */
@Configuration
@Slf4j
public class AlipayConfig {

    @Autowired
    IPayChannelService payChannelService;

    @Autowired
    RedissonClient redissonClient;

    private static Map<String,Config> configHashMap =new ConcurrentHashMap<>();

    /***
     * @description 支付配置文件初始化
     */
    @PostConstruct
    public void InitAliPayConfig() {
        //1、查询所有商户支付配置列表
        List<PayChannel> payChannels = payChannelService.findPayChannelList(TradingConstant.TRADING_CHANNEL_ALI_PAY);
        List<PayChannelVo> payChannelVos = BeanConv.toBeanList(payChannels,PayChannelVo.class);
        if (!EmptyUtil.isNullOrEmpty(payChannelVos)){
            payChannelVos.forEach(n->{
                List <OtherConfigVo> otherConfigVos = JSONArray.parseArray(n.getOtherConfig(),OtherConfigVo.class);
                n.setOtherConfigs(otherConfigVos);
                createOrUpdateConfig(n);
            });
        }
    }

    /***
     * @description 初始化或更新配置进入redis
     * @param payChannelVo 配置信息
     * @return
     */
    public void createOrUpdateConfig(PayChannelVo payChannelVo){
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost =payChannelVo.getDomain();
        config.signType = "RSA2";
        config.appId = payChannelVo.getAppId();
        //2.1、配置应用私钥
        config.merchantPrivateKey = payChannelVo.getMerchantPrivateKey();
        //2.2、配置支付宝公钥
        config.alipayPublicKey = payChannelVo.getPublicKey();
        //2.3、可设置异步通知接收服务地址（可选）
        //config.notifyUrl = "<-- 请填写您的支付类接口异步通知接收服务地址，例如：https://www.test.com/callback -->";
        //2.4、设置AES密钥，调用AES加解密相关接口时需要（可选）
        config.encryptKey = payChannelVo.getEncryptKey();
        //2.5、配置信息放入configHashMap中
        String key = payChannelVo.getChannelLabel()+payChannelVo.getEnterpriseId();
        configHashMap.put(key,config);
    }

    /***
     * @description 移除配置
     * @return
     */
    public void removeConfig(String channelLabel, Long enterpriseId){
        String key = channelLabel+enterpriseId;
        configHashMap.remove(key);
    }

    /***
     * @description 获得配置
     * @return
     */
    public Config queryConfig(String channelLabel, Long enterpriseId){
        String key = channelLabel+enterpriseId;
        return configHashMap.get(key);
    }
}
