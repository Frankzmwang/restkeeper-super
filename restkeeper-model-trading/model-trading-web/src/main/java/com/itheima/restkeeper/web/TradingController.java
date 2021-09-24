package com.itheima.restkeeper.web;

import com.itheima.restkeeper.TradingFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.TradingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.TradingVo;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName TradingController.java
 * @Description 交易操作
 */
@RestController
@Slf4j
@RequestMapping("trading")
@Api(tags = "交易controller")
public class TradingController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    TradingFace tradingFace;

    /**
     * @Description 支付
     * @param tradingVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "支付",notes = "支付")
    @ApiImplicitParam(name = "tradingVo",value = "支付",required = true,dataType = "TradingVo")
    ResponseWrap<TradingVo> doPay(@RequestBody TradingVo tradingVo) throws ProjectException {
        try {
            TradingVo tradingVoResult = tradingFace.doPay(tradingVo);
            return ResponseWrapBuild.build(TradingEnum.SUCCEED,tradingVoResult);
        } catch (Exception e) {
            log.error("修改企业配置异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingEnum.FACE_TO_FACE_FAIL);
        }
    }
}
