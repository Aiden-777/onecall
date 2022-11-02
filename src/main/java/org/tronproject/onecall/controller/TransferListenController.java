package org.tronproject.onecall.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tron.easywork.enums.TransferType;
import org.tron.easywork.util.AccountUtils;
import org.tronproject.onecall.model.entity.CallbackNotify;
import org.tronproject.onecall.model.vo.R;
import org.tronproject.onecall.service.NotAllowAddressService;
import org.tronproject.onecall.service.TransferNotifyService;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-16 02:26
 */
@RestController
@RequestMapping("/transfer/listen")
@Slf4j
public class TransferListenController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/register")
    public R register(@RequestBody @Validated CallbackNotify callbackNotify) {
        if (callbackNotify.getTransferType() != TransferType.TRX && !StringUtils.hasText(callbackNotify.getContractTarget())) {
            return R.error("合约目标不能为空");
        }
        if (!AccountUtils.isTronAddress(callbackNotify.getAddress())) {
            return R.error("账户地址错误");
        }
        // 检查是否为禁止的地址
        Boolean member = redisTemplate.opsForSet().isMember(NotAllowAddressService.EXCLUDE_ADDRESS_KEY, callbackNotify.getAddress());
        if (Boolean.TRUE.equals(member)) {
            return R.error("[黑名单]不能添加该账户地址");
        }

        // 如果最低金额为0，则设为空
        if (null != callbackNotify.getMinAmount() && callbackNotify.getMinAmount().equals(BigDecimal.ZERO)) {
            callbackNotify.setMinAmount(null);
        }
        callbackNotify.setCreateTime(new Date());
        log.debug(callbackNotify.toString());
        TransferNotifyService.NOTIFIES.put(callbackNotify.getAddress(), callbackNotify);
        return R.ok();
    }

}
