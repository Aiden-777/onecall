package org.tronproject.onecall.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.tron.easywork.model.BlockHeaderInfo;
import org.tron.easywork.model.BlockInfo;
import org.tron.easywork.model.TransferInfo;
import org.tron.easywork.util.BlockParser;
import org.tron.trident.proto.Chain;
import org.tronproject.onecall.config.WebsocketStompConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-20 15:00
 */
@Component
@Slf4j
public class BlockService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private TransferNotifyService transferNotifyService;


    @Async
    public void process(Chain.Block block) {
        long blockHeight = block.getBlockHeader().getRawData().getNumber();
        long timestamp = block.getBlockHeader().getRawData().getTimestamp();

        // 区块ID
        String blockId = BlockParser.parseBlockId(block);

        // 区块信息实体
        BlockHeaderInfo blockHeaderInfo = new BlockHeaderInfo(blockId, blockHeight, block.getTransactionsCount(), new Date(timestamp));

        ArrayList<TransferInfo> transfers = new ArrayList<>();

        // 原始交易数据
        List<Chain.Transaction> transactionsList = block.getTransactionsList();
        // 数据处理
        transactionsList.forEach(transaction -> {
            // 转账信息
            TransferInfo transferInfo = transactionService.process(transaction, blockHeaderInfo);
            if (null != transferInfo) {
                transfers.add(transferInfo);
            }
        });

        BlockInfo blockInfo = new BlockInfo(blockHeaderInfo, transfers);

        log.debug("高度：{}:::交易数：{}:::识别转账数：{}"
                , blockHeaderInfo.getHeight()
                , blockHeaderInfo.getCount()
                , blockInfo.getTransfers().size()
        );

        // 过滤监听
        transferNotifyService.transferFind(transfers);

        // 插入缓存
        Boolean insertResult = redisTemplate
                .opsForValue()
                .setIfAbsent(String.valueOf(blockHeight), blockInfo, 300, TimeUnit.SECONDS);

        // 广播 区块头信息
        simpMessagingTemplate.convertAndSend(WebsocketStompConfig.TOPIC__BLOCK_HEADER, blockHeaderInfo);

        // 广播 区块信息 - 带交易
        simpMessagingTemplate.convertAndSend(WebsocketStompConfig.TOPIC__BLOCK, blockInfo);
    }

}
