package org.tronproject.onecall.scheduled;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.exceptions.IllegalException;
import org.tron.trident.proto.Chain;
import org.tronproject.onecall.service.BlockService;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-13 01:22
 */
@Component
@Slf4j
public class BlockReadScheduled {

    /**
     * 当前区块高度
     */
    private static long currentHeight = 0L;

    @Autowired
    private ApiWrapper wrapper;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BlockService blockService;

    @Scheduled(fixedRate = 3000)
    public void readBlocks() {
        // 如果当前块为0，获取当前块
        if (currentHeight == 0L) {
            try {
                Chain.Block block = wrapper.getNowBlock();
                // 区块高度
                currentHeight = block.getBlockHeader().getRawData().getNumber();
                log.info("当前最新区块高度为：{}，区块扫描已经开始......", currentHeight);
                currentHeight++;
            } catch (IllegalException e) {
                e.printStackTrace();
                log.error("获取最新区块高度失败，停止程序！！！");
                int exit = SpringApplication.exit(context, () -> 0);
                System.exit(exit);
            }
        } else {
            try {
                log.debug("-------------------------");
                // 根据区块高度查询区块信息
                Chain.Block block = wrapper.getBlockByNum(currentHeight);
                // 检查交易数量
                int transactionsCount = block.getTransactionsCount();
                log.debug("开始处理【{}】块内容,交易个数：{}", currentHeight, transactionsCount);
                // 异步处理块信息
                blockService.process(block);
                // 高度+1
                currentHeight++;
            } catch (IllegalException e) {
                log.debug("【{}】高度的区块读取失败，块不存在", currentHeight);
            } catch (StatusRuntimeException e) {
                log.error("错误，运行状态异常:{}", e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                log.error("严重错误，兜底异常！！！{}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
