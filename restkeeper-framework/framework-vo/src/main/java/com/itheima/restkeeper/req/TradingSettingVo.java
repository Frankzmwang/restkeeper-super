package com.itheima.restkeeper.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itheima.restkeeper.basic.BasicVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * @ClassName EnterpriseSettingVo.java
 * @Description 企业配置信息
 */
@Data
@NoArgsConstructor
public class TradingSettingVo extends BasicVo {

    private static final long serialVersionUID = 1L;

    @Builder
    public TradingSettingVo(Long id,String alipayAppid,String alipayPublicKey,String alipayMerchantPrivateKey,String alipayEncryptKey,Long enterpriseId){
        super(id);
        this.alipayAppid=alipayAppid;
        this.alipayPublicKey=alipayPublicKey;
        this.alipayMerchantPrivateKey=alipayMerchantPrivateKey;
        this.alipayEncryptKey=alipayEncryptKey;
        this.enterpriseId=enterpriseId;
    }

    @ApiModelProperty(value = "阿里商户appid")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String alipayAppid;

    @ApiModelProperty(value = "阿里公钥")
    private String alipayPublicKey;

    @ApiModelProperty(value = "阿里商户私钥")
    private String alipayMerchantPrivateKey;

    @ApiModelProperty(value = "阿里AES混淆密钥")
    private String alipayEncryptKey;

    @ApiModelProperty(value = "商户ID【系统内部识别使用】")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long enterpriseId;

    @ApiModelProperty(value = "选中节点")
    private String[] checkedIds;
}
