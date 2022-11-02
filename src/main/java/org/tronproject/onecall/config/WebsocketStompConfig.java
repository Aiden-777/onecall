package org.tronproject.onecall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.tronproject.onecall.interceptor.StompHandshakeInterceptor;
import org.tronproject.onecall.interceptor.WebSocketChannelInterceptor;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-22 12:16
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketStompConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketChannelInterceptor webSocketChannelInterceptor;

    @Autowired
    private StompHandshakeInterceptor stompHandshakeInterceptor;

    public static final String ENDPOINT = "/stomp";

    public static final String APPLICATION_DESTINATION_PREFIXES = "/app";

    public static final String USER_DESTINATION_PREFIX = "/user";

    public static final String TOPIC__BLOCK_HEADER = "/block-header";

    public static final String TOPIC__BLOCK = "/block";

    public static final String TOPIC_TRANSFER_NOTIFY = "/transfer-notify";


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT)
//                 .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*")
                .addInterceptors(stompHandshakeInterceptor)
        ;
//                .withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 定义客户端访问服务端消息接口时的前缀
        registry.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIXES);
        // 定义点对点推送时的前缀为/user
        registry.setUserDestinationPrefix(USER_DESTINATION_PREFIX);
        // 配置服务端推送消息给客户端的代理路径
        registry.enableSimpleBroker(TOPIC__BLOCK_HEADER, TOPIC__BLOCK, TOPIC_TRANSFER_NOTIFY);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        /*
         * 配置消息线程池
         * 1. corePoolSize 配置核心线程池，当线程数小于此配置时，不管线程中有无空闲的线程，都会产生新线程处理任务
         * 2. maxPoolSize 配置线程池最大数，当线程池数等于此配置时，不会产生新线程
         * 3. keepAliveSeconds 线程池维护线程所允许的空闲时间，单位秒
         */
        registration.taskExecutor().corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(60);
        /*
         * 添加stomp自定义拦截器，可以根据业务做一些处理         * 消息拦截器，实现ChannelInterceptor接口
         */
//        registration.interceptors(webSocketChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(5).maxPoolSize(20).keepAliveSeconds(60);
    }

}
