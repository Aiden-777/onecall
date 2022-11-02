package org.tronproject.onecall.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.tron.easywork.enums.TransferType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-23 20:34
 */
@Getter
@Setter
@ToString
public class CallbackNotify {

    /**
     * 账户地址
     */
    @NotBlank(message = "账户地址不能为空")
    private String address;

    /**
     * 转账类型
     */
    @NotNull(message = "转账类型不能为空")
    private TransferType transferType;

    /**
     * 合约目标(trc20合约地址|trc10地址)
     */
    private String contractTarget;

    /**
     * 最低通知金额
     */
    private BigDecimal minAmount;

    /**
     * 回调网址
     */
    private String callbackUrl;

    /**
     * 创建时间
     */
    private Date createTime;

}
