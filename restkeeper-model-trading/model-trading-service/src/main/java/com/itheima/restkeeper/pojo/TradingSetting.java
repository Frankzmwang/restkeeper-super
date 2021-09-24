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
 * @Description：
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("tab_trading_setting")
@ApiModel(value="TradingSetting对象", description="")
public class TradingSetting extends BasicPojo {

    private static final long serialVersionUID = 1L;

    @Builder
    public TradingSetting(Long id,String alipayAppid,String alipayPublicKey,String alipayMerchantPrivateKey,String alipayEncryptKey,Long enterpriseId){
        super(id);
        this.alipayAppid=alipayAppid;
        this.alipayPublicKey=alipayPublicKey;
        this.alipayMerchantPrivateKey=alipayMerchantPrivateKey;
        this.alipayEncryptKey=alipayEncryptKey;
        this.enterpriseId=enterpriseId;
    }

    @ApiModelProperty(value = "阿里商户appid")
    private String alipayAppid;

    @ApiModelProperty(value = "阿里公钥")
    private String alipayPublicKey;

    @ApiModelProperty(value = "阿里商户私钥")
    private String alipayMerchantPrivateKey;

    @ApiModelProperty(value = "阿里AES混淆密钥")
    private String alipayEncryptKey;

    @ApiModelProperty(value = "商户ID【系统内部识别使用】")
    private Long enterpriseId;


}
