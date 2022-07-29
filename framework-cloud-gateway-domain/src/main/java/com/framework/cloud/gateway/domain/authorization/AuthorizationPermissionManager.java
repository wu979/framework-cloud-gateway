package com.framework.cloud.gateway.domain.authorization;

import com.framework.cloud.gateway.domain.PermissionFeature;
import lombok.AllArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication information authority processor
 *
 * @author wusiwei
 */
@AllArgsConstructor
public class AuthorizationPermissionManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final PermissionFeature permissionFeature;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethodValue();
        String path = request.getURI().getPath();
        return authentication
                .filter(Authentication::isAuthenticated)
                .map(authority -> {
                    boolean isPermission = permissionFeature.hasPermission(authority, method, path);
                    return new AuthorizationDecision(isPermission);
                })
                .defaultIfEmpty(new AuthorizationDecision(Boolean.FALSE));
    }
}
