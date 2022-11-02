package org.tronproject.onecall.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.tron.easywork.enums.TransferType;
import org.tron.easywork.model.TransferInfo;
import org.tronproject.onecall.config.WebsocketStompConfig;
import org.tronproject.onecall.model.entity.CallbackNotify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-15 21:35
 */
@Service
@Slf4j
public class TransferNotifyService {

    /**
     * 需要通知的账户信息
     */
    public static final Map<String, CallbackNotify> NOTIFIES = new HashMap<>();


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Async
    public void transferFind(ArrayList<TransferInfo> transfers) {
        if (null == transfers || transfers.size() == 0) {
            return;
        }

        transfers.forEach(
                transfer -> {
                    // 检查转账状态
                    /*if (transfer.getStatus() == TransactionStatus.FAILED) {
                        return;
                    }*/
                    // 系统默认trx通知最低金额为 1trx
                    if (transfer.getTransferType() == TransferType.TRX && transfer.getAmount().compareTo(BigDecimal.valueOf(1000000)) < 0) {
                        return;
                    }
                    // 从候选池中获取匹配的通知
                    CallbackNotify callbackNotify = NOTIFIES.get(transfer.getTo());
                    if (callbackNotify != null) {
                        this.notify(transfer, callbackNotify);
                    }
                }
        );

    }

    public void notify(TransferInfo transfer, CallbackNotify notify) {
        // 检查转账类型
        if (transfer.getTransferType() != notify.getTransferType()) {
            return;
        }
        // 检查最低金额
        if (null != notify.getMinAmount() && transfer.getAmount().compareTo(notify.getMinAmount()) < 0) {
            return;
        }
        // 检查合约目标
        if (!transfer.contractTargetEquals(notify.getContractTarget())) {
            return;
        }
        log.info("发现通知：{}", transfer);
        simpMessagingTemplate.convertAndSend(WebsocketStompConfig.TOPIC_TRANSFER_NOTIFY, transfer);

        // 通知回调网址
    }


}
