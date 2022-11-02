package org.tronproject.onecall.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.tron.easywork.factory.ApiWrapperFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-20 14:29
 */
@Component
@ConfigurationProperties(prefix = "tron-blockchain")
@Getter
@Setter
@ToString
@Validated
public class BlockChainConfig {

    /**
     * apiKey <a href="https://cn.developers.tron.network/reference/apikey">...</a>
     */
    private String apiKey;

    /**
     * 私钥 - 随意填写合法私钥
     */
    @NotBlank(message = "私钥不能为空")
    private String privateKey;

    /**
     * Tron网络类型 Mainnet  Shasta  Nile
     */
    @NotNull(message = "网络类型不能为空")
    private ApiWrapperFactory.NetType netType;

    /**
     * trc20合约地址
     */
    @NotBlank(message = "trc20合约地址不能为空")
    private String trc20ContractAddress;

    /**
     * 最低记录值 - 单位个
     */
    @NotNull(message = "最低记录值不能为空")
    private BigDecimal minAmount;

    private String grpcEndpoint;
    private String grpcEndpointSolidity;

}
