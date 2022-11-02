package org.tronproject.onecall.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tronproject.onecall.model.vo.R;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-20 09:05
 */
@RestController
@Slf4j
public class BlockInfoController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/getBlockInfoByHeight/{height}")
    public R register(@PathVariable String height) {

        Object o = redisTemplate.opsForValue().get(height);

        if (null == o) {
            return R.error("已经超出最近100个块");
        }

        return R.ok(o);
    }

}
