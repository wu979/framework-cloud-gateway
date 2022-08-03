package com.framework.cloud.gateway.domain.utils;

import com.framework.cloud.common.constant.HttpConstant;
import com.framework.cloud.common.utils.FastJsonUtil;
import com.framework.cloud.gateway.common.rpc.vo.GatewayRouteListVO;
import com.framework.cloud.gateway.domain.event.GatewayRouteEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 路由数据转换
 *
 * @author wusiwei
 */
public class RouteDefinitionUtil {

    public static RouteDefinition buildRouteDefinition(GatewayRouteEvent gatewayRoute) {
        return buildRouteDefinition(
                gatewayRoute.getPath(), gatewayRoute.getName(),
                gatewayRoute.getPredicates(), gatewayRoute.getFilters(), gatewayRoute.getSort());
    }

    public static RouteDefinition buildRouteDefinition(GatewayRouteListVO gatewayRoute) {
        return buildRouteDefinition(
                gatewayRoute.getPath(), gatewayRoute.getName(),
                gatewayRoute.getPredicates(), gatewayRoute.getFilters(), gatewayRoute.getSort());
    }

    private static RouteDefinition buildRouteDefinition(String path, String name, String predicates, String filters, Integer sort) {
        RouteDefinition definition = new RouteDefinition();
        URI uri ;
        if (path.startsWith(HttpConstant.LB)) {
            uri = UriComponentsBuilder.fromUriString(path).build().toUri();
        } else {
            uri = UriComponentsBuilder.fromHttpUrl(path).build().toUri();
        }
        definition.setId(name);
        definition.setPredicates(FastJsonUtil.toJavaList(predicates, PredicateDefinition.class));
        definition.setFilters(FastJsonUtil.toJavaList(filters, FilterDefinition.class));
        definition.setUri(uri);
        definition.setOrder(sort);
        return definition;
    }
}
