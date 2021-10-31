package com.itheima.restkeeper.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.SmsSendFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.BasicEnum;
import com.itheima.restkeeper.enums.SmsSignEnum;
import com.itheima.restkeeper.enums.SmsTemplateEnum;
import com.itheima.restkeeper.req.SendMessageVo;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.req.SmsTemplateVo;
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




}
