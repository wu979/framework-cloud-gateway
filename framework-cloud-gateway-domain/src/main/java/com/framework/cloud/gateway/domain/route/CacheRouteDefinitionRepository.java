package com.framework.cloud.gateway.domain.route;

import com.alibaba.ttl.internal.javassist.NotFoundException;
import com.framework.cloud.gateway.common.constant.GatewayConstant;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Gateway routing distribution customized L2 cache
 *
 * @author wusiwei
 */
@Component("cacheRouteDefinitionRepository")
public class CacheRouteDefinitionRepository implements RouteDefinitionRepository {

    @Resource
    private HashOperations<String, String, RouteDefinition> hashOperations;

    /**
     * get route
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>(hashOperations.values(GatewayConstant.ROUTES));
        return Flux.fromIterable(routeDefinitions);
    }

    /**
     * save route
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route
                .flatMap(routeDefinition -> {
                    hashOperations.put(GatewayConstant.ROUTES, routeDefinition.getId(), routeDefinition);
                    return Mono.empty();
                });
    }

    /**
     * delete route
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (hashOperations.hasKey(GatewayConstant.ROUTES, id)) {
                hashOperations.delete(GatewayConstant.ROUTES, id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("路由文件没有找到: " + routeId)));
        });
    }
}
