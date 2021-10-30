package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.SmsSendFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.SmsSignEnum;
import com.itheima.restkeeper.req.SendMessageVo;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @ClassName SmsSendController.java
 * @Description 测试短信发送
 */
@RestController
@RequestMapping("sms-send")
@Slf4j
@Api(tags = "短信controller")
public class SmsSendController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    SmsSendFace smsSendFace;

    @PostMapping
    @ApiOperation(value = "短信发送测试",notes = "短信发送测试")
    public Boolean test() {
        String templateNo = "template_00001";
        String sginNo= "sign_0001";
        String loadBalancerType= SuperConstant.ROUND_ROBIN;
        Set<String> mobiles=new HashSet<>();
        mobiles.add("15156403088");
        LinkedHashMap<String,String> templateParam = new LinkedHashMap<>();
        templateParam.put("code","123456");
        SendMessageVo sendMessageVo = SendMessageVo.builder()
                .templateNo(templateNo)
                .sginNo(sginNo)
                .loadBalancerType(loadBalancerType)
                .mobiles(mobiles)
                .templateParam(templateParam)
                .build();
        return smsSendFace.SendSms(sendMessageVo);
    }
}
