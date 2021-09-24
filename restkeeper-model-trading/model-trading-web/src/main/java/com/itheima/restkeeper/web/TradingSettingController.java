package com.itheima.restkeeper.web;

import com.itheima.restkeeper.TradingSettingFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.TradingSettingEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.TradingSettingVo;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName TradingSettingController.java
 * @Description 支付配置服务controller
 */
@RestController
@Slf4j
@RequestMapping("trading-setting")
@Api(tags = "支付配置controller")
public class TradingSettingController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    TradingSettingFace tradingSettingFace;

    /**
     * @Description 保存或修改支付配置
     * @param tradingSettingVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "保存或修改支付配置",notes = "保存或修改配置")
    @ApiImplicitParam(name = "tradingSettingVo",value = "支付配置对象",required = true,dataType = "TradingSettingVo")
    ResponseWrap<TradingSettingVo> saveOrUpdateTradingSetting(
            @RequestBody TradingSettingVo tradingSettingVo) throws ProjectException {
        try {
            TradingSettingVo tradingSettingVoResult = tradingSettingFace
                    .saveOrUpdateTradingSetting(tradingSettingVo);
            return ResponseWrapBuild.build(TradingSettingEnum.SUCCEED,tradingSettingVoResult);
        } catch (Exception e) {
            log.error("修改支付配置异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingSettingEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 查询支付配置异常
     * @return
     */
    @GetMapping
    @ApiOperation(value = "查询支付配置",notes = "查询配置")
    ResponseWrap<TradingSettingVo> findTradingSettingByEnterpriseId() throws ProjectException {
        try {
            TradingSettingVo tradingSettingVoResult = tradingSettingFace
                    .findTradingSettingByEnterpriseId();
            return ResponseWrapBuild.build(TradingSettingEnum.SUCCEED,tradingSettingVoResult);
        } catch (Exception e) {
            log.error("查询支付配置异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(TradingSettingEnum.SELECT_FAIL);
        }
    }

}
