package com.framework.cloud.gateway.infrastructure.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 *
 * @author wusiwei
 */
public class RequestBodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType) || MediaType.APPLICATION_JSON.equals(contentType)) {
                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            DataBufferUtils.retain(dataBuffer);
                            Flux<DataBuffer> cachedFlux = Flux
                                    .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                    exchange.getRequest()) {
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    return cachedFlux;
                                }
                            };
                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        });
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
