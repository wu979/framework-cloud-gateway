package com.framework.cloud.gateway.infrastructure.filter;

import com.framework.cloud.common.utils.UUIDUtil;
import com.framework.cloud.holder.constant.HeaderConstant;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 *
 *
 * @author wusiwei
 */
public class TraceIdFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate().header(HeaderConstant.TRACE_ID, UUIDUtil.uuid()).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
