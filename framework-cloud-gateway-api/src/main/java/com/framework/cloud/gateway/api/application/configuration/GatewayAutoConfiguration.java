package com.framework.cloud.gateway.api.application.configuration;

import com.framework.cloud.gateway.domain.properties.GatewayProperties;
import com.framework.cloud.gateway.infrastructure.filter.GatewayCorsFilter;
import com.framework.cloud.gateway.infrastructure.filter.LoadBalancerFilter;
import com.framework.cloud.gateway.infrastructure.filter.RequestBodyFilter;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author wusiwei
 */
@AllArgsConstructor
@EnableConfigurationProperties(GatewayProperties.class)
@ImportAutoConfiguration({GatewayExceptionConfiguration.class, AuthenticationTokenConfiguration.class, AuthenticationResourceConfiguration.class})
public class GatewayAutoConfiguration {

    private final GatewayProperties gatewayProperties;

    @Bean
    @ConditionalOnMissingBean(LoadBalancerFilter.class)
    public LoadBalancerFilter loadBalancerFilter(LoadBalancerClientFactory clientFactory) {
        return new LoadBalancerFilter(gatewayProperties, clientFactory);
    }

    @Bean
    @ConditionalOnMissingBean(RequestBodyFilter.class)
    public RequestBodyFilter requestBodyFilter() {
        return new RequestBodyFilter();
    }

    @Bean
    @ConditionalOnMissingBean(GatewayCorsFilter.class)
    public GatewayCorsFilter gatewayCorsFilter() {
        return new GatewayCorsFilter();
    }
}
