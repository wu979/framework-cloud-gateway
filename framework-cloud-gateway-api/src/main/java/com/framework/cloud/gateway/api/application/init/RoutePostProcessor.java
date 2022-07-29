package com.framework.cloud.gateway.api.application.init;

import com.framework.cloud.cache.cache.RedisCache;
import com.framework.cloud.common.result.Result;
import com.framework.cloud.core.event.ApplicationInitializingEvent;
import com.framework.cloud.gateway.common.constant.GatewayConstant;
import com.framework.cloud.gateway.common.rpc.vo.GatewayRouteListVO;
import com.framework.cloud.gateway.domain.feign.PlatformFeignService;
import com.framework.cloud.gateway.domain.utils.RouteDefinitionUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wusiwei
 */
@Slf4j
@Component
@AutoConfigureOrder(Integer.MIN_VALUE)
public class RoutePostProcessor implements ApplicationListener<ApplicationInitializingEvent>, ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    @Resource
    private RedisCache redisCache;
    @Resource
    private PlatformFeignService platformFeignService;
    @Qualifier("cacheRouteDefinitionRepository")
    @Resource
    private RouteDefinitionRepository routeDefinitionRepository;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationInitializingEvent event) {
        log.info("Initialized routes");
        redisCache.delete(GatewayConstant.ROUTES);
        Result<List<GatewayRouteListVO>> result = platformFeignService.list();
        if (!result.success()) {
            throw new NullPointerException("Initialized routes error");
        }
        List<GatewayRouteListVO> list = result.getData();
        for (GatewayRouteListVO route : list) {
            RouteDefinition routeDefinition = RouteDefinitionUtil.buildRouteDefinition(route);
            routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
        }
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

}
