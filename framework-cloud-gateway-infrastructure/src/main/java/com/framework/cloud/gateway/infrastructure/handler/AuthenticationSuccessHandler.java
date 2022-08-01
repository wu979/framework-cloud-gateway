package com.framework.cloud.gateway.infrastructure.handler;

import lombok.SneakyThrows;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication successful processor
 *
 * @author wusiwei
 */
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpRequest request = exchange.getRequest().mutate().build();
        return webFilterExchange.getChain().filter(exchange.mutate().request(request).build());
    }

}
