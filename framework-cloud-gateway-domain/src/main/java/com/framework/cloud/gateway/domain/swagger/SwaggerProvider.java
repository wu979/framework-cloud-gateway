package com.framework.cloud.gateway.domain.swagger;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.framework.cloud.common.constant.SpecialConstant;
import com.framework.cloud.common.enums.GlobalNumber;
import com.framework.cloud.gateway.common.constant.GatewayConstant;
import com.framework.cloud.gateway.domain.properties.SwaggerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Swagger
 *
 * @author wusiwei
 */
@Primary
@Component
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerProvider implements SwaggerResourcesProvider {

    public SwaggerProvider(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    private final RouteLocator routeLocator;

    @Resource
    private SwaggerProperties swaggerProperties;

    @Resource
    private HashOperations<String, String, RouteDefinition> hashOperations;

    @Resource
    private NacosDiscoveryClient nacosDiscoveryClient;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        Map<String, List<PredicateDefinition>> definition = getDefinition();
        for (Map.Entry<String, List<PredicateDefinition>> entry : definition.entrySet()) {
            String id = entry.getKey();
            List<PredicateDefinition> predicateList = entry.getValue();
            predicateList.forEach(predicateDefinition -> {
                if (!("Path").equalsIgnoreCase(predicateDefinition.getName())) {
                    return;
                }
                Map<String, String> args = predicateDefinition.getArgs();
                String s = args.get(NameUtils.GENERATED_NAME_PREFIX + GlobalNumber.ZERO.getLongValue());
                String replace = s.replace(SpecialConstant.SLASH_ASTERISK, swaggerProperties.getDocsPath());
                SwaggerResource swaggerResource = swaggerResource(id, replace);
                resources.add(swaggerResource);
            });
        }
        return resources;
    }

    private Map<String, List<PredicateDefinition>> getDefinition() {
        //当前所有健康实例
        List<String> services = nacosDiscoveryClient.getServices();
        //节点ID
        Set<String> routes = new HashSet<>();
        //网关
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        //获取缓存的节点
        List<RouteDefinition> routeDefinitionList = hashOperations.values(GatewayConstant.ROUTES);
        return routeDefinitionList
                .stream()
                .filter(route -> routes.contains(route.getId()))
                .filter(route -> services.contains(route.getUri().toString().replace(GatewayConstant.SERVER_LB, StringPool.EMPTY)))
                .filter(route -> swaggerProperties.isShowRoute(route.getId()))
                .collect(Collectors.toMap(RouteDefinition::getId, RouteDefinition::getPredicates));
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setSwaggerVersion(swaggerProperties.getVersion());
        if (!swaggerProperties.getEnableGroup()) {
            swaggerResource.setLocation(location);
        } else {
            if (swaggerProperties.getIgnoreGroup().contains(name)) {
                swaggerResource.setLocation(location);
            } else {
                swaggerResource.setLocation(location + GatewayConstant.GROUP + name);
            }
        }
        return swaggerResource;
    }


}
