package com.framework.cloud.gateway.api.application.configuration;

import com.framework.cloud.cache.cache.RedisCache;
import com.framework.cloud.gateway.domain.PermissionFeature;
import com.framework.cloud.gateway.domain.properties.GatewayProperties;
import com.framework.cloud.gateway.domain.authorization.AuthenticationBearerTokenConverter;
import com.framework.cloud.gateway.domain.authorization.AuthenticationPermissionManager;
import com.framework.cloud.gateway.domain.authorization.AuthenticationReactiveManager;
import com.framework.cloud.gateway.infrastructure.handler.AuthenticationFailureHandler;
import com.framework.cloud.gateway.infrastructure.handler.AuthenticationSuccessHandler;
import com.framework.cloud.gateway.infrastructure.handler.AuthorizationAccessDeniedHandler;
import com.framework.cloud.gateway.infrastructure.handler.AuthorizationEntryPointHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

/**
 * @author wusiwei
 */
@AllArgsConstructor
@EnableWebFluxSecurity
public class AuthenticationResourceConfiguration {

    private final RedisCache redisCache;
    private final GatewayProperties gatewayProperties;
    private final PermissionFeature permissionFeature;
    private final ResourceServerTokenServices resourceServerTokenServices;

    /**
     * Http 过滤链
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        String[] ignoredUrl = gatewayProperties.getIgnoredUrl();
        ServerAuthenticationConverter converter = new AuthenticationBearerTokenConverter(redisCache);
        ReactiveAuthenticationManager authorizationManager = new AuthenticationReactiveManager(resourceServerTokenServices);
        ServerAuthenticationFailureHandler failureHandler = new AuthenticationFailureHandler();
        ServerAuthenticationSuccessHandler successHandler = new AuthenticationSuccessHandler();
        AuthenticationWebFilter oauth2Filter = new AuthenticationWebFilter(authorizationManager);
        oauth2Filter.setServerAuthenticationConverter(converter);
        oauth2Filter.setAuthenticationFailureHandler(failureHandler);
        oauth2Filter.setAuthenticationSuccessHandler(successHandler);
        //http过滤链
        http.addFilterAt(oauth2Filter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll();
        if (ignoredUrl.length > 0) {
            http.authorizeExchange().pathMatchers(gatewayProperties.getIgnoredUrl()).permitAll();
        }
        http.authorizeExchange().anyExchange().access(new AuthenticationPermissionManager(permissionFeature));
        http.authorizeExchange().and().exceptionHandling().accessDeniedHandler(new AuthorizationAccessDeniedHandler());
        http.authorizeExchange().and().exceptionHandling().authenticationEntryPoint(new AuthorizationEntryPointHandler());
        http.authorizeExchange().and().headers().frameOptions().disable();
        http.authorizeExchange().and().httpBasic().disable().csrf().disable();
        return http.build();
    }
}
