package org.tronproject.onecall.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-22 12:36
 */
@Slf4j
@Component
public class StompHandshakeInterceptor extends HttpSessionHandshakeInterceptor {


    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes)
            throws Exception {
        URI uri = request.getURI();

        if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
            HttpServletRequest req = servletServerHttpRequest.getServletRequest();
            Map<String, String[]> parameterMap = req.getParameterMap();
            parameterMap.forEach((k, v) -> log.debug("{}:::{}", k, v));
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
