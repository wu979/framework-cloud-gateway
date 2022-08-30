package com.framework.cloud.gateway.infrastructure.filter;

import cn.hutool.core.util.ObjectUtil;
import com.framework.cloud.gateway.domain.loadBalancer.VersionLoadBalancer;
import com.framework.cloud.gateway.domain.properties.GatewayProperties;
import com.framework.cloud.gateway.domain.utils.LoadBalancerUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.framework.cloud.gateway.common.constant.GatewayConstant.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static com.framework.cloud.gateway.common.constant.GatewayConstant.SERVER_LB;

/**
 * customize service distribution filters
 *
 * @author wusiwei
 */
@Slf4j
@AllArgsConstructor
public class LoadBalancerFilter implements GlobalFilter, Ordered {

    private final GatewayProperties gatewayProperties;
    private final LoadBalancerClientFactory clientFactory;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (url == null) {
            return chain.filter(exchange);
        }
        String schemePrefix = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
        if (!(SERVER_LB.equals(url.getScheme()) || SERVER_LB.equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);
        if (log.isTraceEnabled()) {
            log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
        }
        return this.choose(exchange).doOnNext((response) -> {
            if (!response.hasServer()) {
                throw NotFoundException.create(Boolean.TRUE, "Unable to find instance for " + url.getHost());
            } else {
                URI uri = exchange.getRequest().getURI();
                String overrideScheme = null;
                if (schemePrefix != null) {
                    overrideScheme = url.getScheme();
                }
                DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(response.getServer(), overrideScheme);
                URI requestUrl = LoadBalancerUtil.reconstructUri(serviceInstance, uri);
                if (log.isTraceEnabled()) {
                    log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                }
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
            }
        }).then(chain.filter(exchange));
    }

    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (ObjectUtil.isNull(uri)) {
            throw new NotFoundException("No loadbalancer available");
        }
        String uriHost = uri.getHost();
        ObjectProvider<ServiceInstanceListSupplier> lazyProvider = clientFactory.getLazyProvider(uriHost, ServiceInstanceListSupplier.class);
        VersionLoadBalancer loadBalancer = new VersionLoadBalancer(uriHost, gatewayProperties, lazyProvider);
        if (ObjectUtil.isNull(loadBalancer)) {
            throw new NotFoundException("No loadbalancer available for " + uriHost);
        } else {
            return loadBalancer.choose(new DefaultRequest<>(exchange.getRequest()));
        }
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }
}
