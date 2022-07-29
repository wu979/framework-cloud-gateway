package com.framework.cloud.gateway.infrastructure.handler;

import com.framework.cloud.common.enums.GlobalMessage;
import com.framework.cloud.gateway.infrastructure.response.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

/**
 * 401
 *
 * @author wusiwei
 */
public class AuthenticationFailureHandler implements ServerAuthenticationFailureHandler, ResponseUtil {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException e) {
        return error(exchange.getExchange(), GlobalMessage.AUTHENTICATION_ERROR.getCode(), e.getMessage());
    }

}
