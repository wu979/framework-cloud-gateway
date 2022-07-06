package com.framework.cloud.gateway.domain.route;

import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway routing distribution customized L2 cache
 *
 * @author wusiwei
 */
@AllArgsConstructor
public class CacheRouteDefinitionRepository implements RouteDefinitionRepository {

    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * get route
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return null;
    }

    /**
     * save route
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    /**
     * delete route
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}
