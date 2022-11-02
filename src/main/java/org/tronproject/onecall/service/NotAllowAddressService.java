package org.tronproject.onecall.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tron.easywork.util.AccountUtils;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-29 13:45
 */
@Service
@Slf4j
public class NotAllowAddressService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public static final String EXCLUDE_ADDRESS_KEY = "EXCLUDE_ADDRESS_KEY";

    public void process(String json) throws JsonProcessingException {
        if (null != json) {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode data = jsonNode.get("data");
            if (data.isArray()) {
                Set<String> addresses = new HashSet<>();
                data.forEach(
                        e -> {
                            String address = e.get("address").asText();
                            if (AccountUtils.isTronAddress(address)) {
                                addresses.add(address);
                            }
                        }
                );
                redisTemplate.opsForSet().add(EXCLUDE_ADDRESS_KEY, addresses.toArray(new String[0]));
            }
        }
    }

    @PostConstruct
    public void init() throws JsonProcessingException {
        log.debug("开始从 apilist.tronscanapi.com 获取需要排除监听的TRX地址");
        Long size = redisTemplate.opsForSet().size(EXCLUDE_ADDRESS_KEY);
        if (null != size && size >= 1000) {
            log.debug("系统中已经存在排除列表！总数：{}", size);
            return;
        }

        int pageSize = 50;

        for (int i = 0; i < 20; i++) {
            String url = "https://apilist.tronscanapi.com/api/account/list?limit=" + pageSize + "&start=" + i * pageSize;
            String json = restTemplate.getForObject(url, String.class);
            this.process(json);
        }

        log.debug("本次排除地址总数：{}", redisTemplate.opsForSet().size(EXCLUDE_ADDRESS_KEY));
    }

}
