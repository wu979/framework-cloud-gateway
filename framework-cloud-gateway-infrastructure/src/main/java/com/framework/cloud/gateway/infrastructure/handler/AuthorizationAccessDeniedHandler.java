package com.framework.cloud.gateway.infrastructure.handler;

import com.framework.cloud.common.enums.GlobalMessage;
import com.framework.cloud.gateway.infrastructure.response.ResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 403
 *
 * @author wusiwei
 */
public class AuthorizationAccessDeniedHandler implements ServerAccessDeniedHandler, ResponseUtil {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException e) {
        return error(exchange, GlobalMessage.AUTHENTICATION_PERMISSION.getCode(), e.getMessage());
    }
}
