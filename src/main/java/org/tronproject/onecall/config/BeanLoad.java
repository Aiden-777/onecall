package org.tronproject.onecall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tron.easywork.factory.ApiWrapperFactory;
import org.tron.easywork.handler.transfer.BaseTransferHandler;
import org.tron.easywork.handler.transfer.LocalTransferContext;
import org.tron.easywork.handler.transfer.Trc20TransferHandler;
import org.tron.easywork.handler.transfer.TrxTransferHandler;
import org.tron.easywork.model.Trc20ContractInfo;
import org.tron.easywork.util.Trc20Utils;
import org.tron.trident.core.ApiWrapper;

import java.util.Map;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-20 14:41
 */
@Component
public class BeanLoad {

    @Autowired
    private BlockChainConfig blockChainConfig;

    @Bean
    public Trc20TransferHandler trc20TransferHandler() {
        return new Trc20TransferHandler();
    }

    @Bean
    public TrxTransferHandler trxTransferHandler() {
        return new TrxTransferHandler();
    }

    @Bean
    public LocalTransferContext localTransferContext(Map<String, BaseTransferHandler> transferHandlers) {
        return new LocalTransferContext(transferHandlers);
    }

    @Bean
    public ApiWrapper wrapper() {
        if (StringUtils.hasText(blockChainConfig.getGrpcEndpoint()) &&
                StringUtils.hasText(blockChainConfig.getGrpcEndpointSolidity())) {
            return ApiWrapperFactory.create(blockChainConfig.getGrpcEndpoint(), blockChainConfig.getGrpcEndpointSolidity(),
                    blockChainConfig.getPrivateKey(), blockChainConfig.getApiKey());
        }
        return ApiWrapperFactory.create(blockChainConfig.getNetType(), blockChainConfig.getPrivateKey(), blockChainConfig.getApiKey());
    }

    @Bean
    public Trc20ContractInfo trc20ContractInfo(BlockChainConfig blockChainConfig, ApiWrapper wrapper) {
        return Trc20Utils.readTrc20ContractInfo(blockChainConfig.getTrc20ContractAddress(), wrapper);
    }

}
