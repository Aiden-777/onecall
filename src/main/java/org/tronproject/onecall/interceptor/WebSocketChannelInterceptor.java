package org.tronproject.onecall.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-22 12:27
 */
@Component
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 在消息发送之前调用，方法中可以对消息进行修改，如果此方法返回值为空，则不会发生实际的消息发送调用
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // Session ID
        String sessionId = accessor.getSessionId();
        log.debug("sessionId=" + sessionId);

        /* 1. 判断是否为首次连接请求，如果已经连接过，直接返回message
        2. 网上有种写法是在这里封装认证用户的信息，本文是在http阶段，websockt 之前就做了认证的封装，所以这里直接取的信息*/

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.debug("WebSocket Connecting !");

            // 检查 token
            String token = accessor.getFirstNativeHeader("token");
            if (StringUtils.hasText(token) && "123".equals(token)) {
                log.debug(("认证成功！"));
                log.debug("token: " + token);
            } else {
                log.warn("websocket认证失败！");
                return null;
            }

            /*// 检查用户
            String username = accessor.getFirstNativeHeader("username");
            if (!StringUtils.hasText(username)) {
                return null;
            }
            // 用户是否已经在线
            Long online = redisTemplate.opsForSet().add("ws-online-users", username);
            if (null != online && online <= 0) {
                log.warn("用户已经在线");
                return null;
            }

            log.debug("添加用户{}：{}", username, online);
            accessor.setUser(() -> username);*/

            /*
             * 1. 这里直接封装到StompHeaderAccessor 中，可以根据自身业务进行改变
             * 2. 封装大搜StompHeaderAccessor中后，可以在@Controller / @MessageMapping注解的方法中直接带上StompHeaderAccessor
             *    就可以通过方法提供的 getUser()方法获取到这里封装user对象
             * 2. 例如可以在这里拿到前端的信息进行登录鉴权
             */
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.debug("断开连接！");
        }
        return message;
    }

    /**
     * 在消息发送后立刻调用，boolean值参数表示该调用的返回值
     */
    @Override
    public void postSend(@NotNull Message<?> message, @NotNull MessageChannel messageChannel, boolean b) {

    }

    /**
     * 1. 在消息发送完成后调用，而不管消息发送是否产生异常，在次方法中，我们可以做一些资源释放清理的工作
     * 2. 此方法的触发必须是preSend方法执行成功，且返回值不为null,发生了实际的消息推送，才会触发
     */
    @Override
    public void afterSendCompletion(@NotNull Message<?> message, @NotNull MessageChannel messageChannel, boolean b, Exception e) {

    }

    /**
     * 1. 在消息被实际检索之前调用，如果返回false,则不会对检索任何消息，只适用于(PollableChannels)，
     * 2. 在websocket的场景中用不到
     */
    @Override
    public boolean preReceive(@NotNull MessageChannel messageChannel) {
        return true;
    }

    /**
     * 1. 在检索到消息之后，返回调用方之前调用，可以进行信息修改，如果返回null,就不会进行下一步操作
     * 2. 适用于PollableChannels，轮询场景
     */
    @Override
    public Message<?> postReceive(@NotNull Message<?> message, @NotNull MessageChannel messageChannel) {
        return message;
    }

    /**
     * 1. 在消息接收完成之后调用，不管发生什么异常，可以用于消息发送后的资源清理
     * 2. 只有当preReceive 执行成功，并返回true才会调用此方法
     * 2. 适用于PollableChannels，轮询场景
     */
    @Override
    public void afterReceiveCompletion(Message<?> message, @NotNull MessageChannel messageChannel, Exception e) {
    }
}
