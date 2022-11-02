package org.tronproject.onecall.service;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.easywork.exception.FunctionSelectorException;
import org.tron.easywork.exception.SmartParamDecodeException;
import org.tron.easywork.handler.transfer.BaseTransferHandler;
import org.tron.easywork.handler.transfer.LocalTransferContext;
import org.tron.easywork.model.BlockHeaderInfo;
import org.tron.easywork.model.TransferInfo;
import org.tron.easywork.util.TransactionParser;
import org.tron.trident.proto.Chain;
import org.tronproject.onecall.config.BlockChainConfig;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-20 15:03
 */
@Component
@Slf4j
public class TransactionService {

    @Autowired
    private LocalTransferContext localTransferContext;

    @Autowired
    private BlockChainConfig blockChainConfig;

    public TransferInfo process(Chain.Transaction transaction, BlockHeaderInfo blockHeaderInfo) {
        // 获取交易内容处理器
        BaseTransferHandler handler = localTransferContext.getHandler(
                transaction.getRawData().getContract(0).getType()
        );
        if (null == handler) {
            return null;
        }
        try {
            TransferInfo transfer = handler.parse(transaction);
            // 设置上链时间
            transfer.setBroadcastTime(blockHeaderInfo.getCreateTime());
            // log.debug("状态：{}\t类型：{}\t到账地址：{}\t金额：{}", transfer.getStatus(), transfer.getTransferType(), transfer.getTo(), transfer.getAmount());
            return transfer;
        } catch (InvalidProtocolBufferException e) {
            log.error("unpack解包异常");
            e.printStackTrace();
        } catch (SmartParamDecodeException e) {
            log.error("智能合约参数解码异常:{}", e.getMessage());
            e.printStackTrace();
        } catch (FunctionSelectorException e) {
            // 智能合约函数选择器错误
        } catch (Exception e) {
            String tid = TransactionParser.getTransactionId(transaction);
            log.error("严重错误，交易ID：{}\t{}", tid, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
