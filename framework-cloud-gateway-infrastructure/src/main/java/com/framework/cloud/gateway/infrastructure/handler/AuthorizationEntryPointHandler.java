package com.framework.cloud.gateway.infrastructure.handler;

import com.framework.cloud.common.enums.GlobalMessage;
import com.framework.cloud.gateway.infrastructure.response.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 401
 *
 * @author wusiwei
 */
public class AuthorizationEntryPointHandler implements ServerAuthenticationEntryPoint, ResponseUtil {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return error(exchange, GlobalMessage.AUTHENTICATION_ERROR.getCode(), e.getMessage());
    }
}
