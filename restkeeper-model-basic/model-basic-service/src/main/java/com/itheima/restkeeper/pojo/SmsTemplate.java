package com.itheima.restkeeper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.restkeeper.basic.BasicPojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * @Description：模板表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("tab_sms_template")
@ApiModel(value="SmsTemplate对象", description="模板表")
public class SmsTemplate extends BasicPojo {

    private static final long serialVersionUID = 1L;

    @Builder
    public SmsTemplate(Long id,String channelLabel,String templateName,String templateType,String templateNo,String templateCode,String content,String international,Long signId,String signName,String signCode,String remark,String acceptStatus,String acceptMsg,String auditStatus,String auditMsg){
        super(id);
        this.channelLabel=channelLabel;
        this.templateName=templateName;
        this.templateType=templateType;
        this.templateNo=templateNo;
        this.templateCode=templateCode;
        this.content=content;
        this.international=international;
        this.signId=signId;
        this.signName=signName;
        this.signCode=signCode;
        this.remark=remark;
        this.acceptStatus=acceptStatus;
        this.acceptMsg=acceptMsg;
        this.auditStatus=auditStatus;
        this.auditMsg=auditMsg;
    }

    @ApiModelProperty(value = "通道唯一标识")
    private String channelLabel;

    @ApiModelProperty(value = "魔板名称")
    private String templateName;

    @ApiModelProperty(value = "短信类型： 0、通知 1、营销")
    private String templateType;

    @ApiModelProperty(value = "应用模板编号：多通道编号相同则认为是一个模板多个通道公用")
    private String templateNo;

    @ApiModelProperty(value = "三方应用模板code")
    private String templateCode;

    @ApiModelProperty(value = "模板内容")
    private String content;

    @ApiModelProperty(value = "是否国际/港澳台短信：	0：表示国内短信。	1：表示国际/港澳台短信。")
    private String international;

    @ApiModelProperty(value = "签名表ID")
    private Long signId;

    @ApiModelProperty(value = "签名名称")
    private String signName;

    @ApiModelProperty(value = "三方签名code:发送短信可能用到")
    private String signCode;

    @ApiModelProperty(value = "			短信申请说明")
    private String remark;

    @ApiModelProperty(value = "是否受理成功")
    private String acceptStatus;

    @ApiModelProperty(value = "受理返回信息")
    private String acceptMsg;

    @ApiModelProperty(value = "审核状态")
    private String auditStatus;

    @ApiModelProperty(value = "审核信息")
    private String auditMsg;


}
