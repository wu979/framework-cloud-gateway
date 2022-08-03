package com.framework.cloud.gateway.infrastructure.mq.subscribe;

import com.framework.cloud.gateway.domain.event.GatewayRouteDeleteEvent;
import com.framework.cloud.gateway.domain.event.GatewayRouteEvent;
import com.framework.cloud.gateway.domain.utils.RouteDefinitionUtil;
import com.framework.cloud.gateway.infrastructure.mq.channel.GatewayRouteChannel;
import com.framework.cloud.gateway.infrastructure.mq.channel.GatewayRouteDeleteChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 动态路由领域事件订阅
 *
 * @author wusiwei
 */
@EnableBinding({GatewayRouteChannel.class, GatewayRouteDeleteChannel.class})
public class GatewayRouteSubscribe implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Qualifier("cacheRouteDefinitionRepository")
    @Resource
    private RouteDefinitionRepository routeDefinitionRepository;

    @StreamListener(GatewayRouteChannel.IN)
    public void gatewayRouteEvent(@Payload GatewayRouteEvent event) {
        RouteDefinition routeDefinition = RouteDefinitionUtil.buildRouteDefinition(event);
        if (!event.getSaveOrUpdate()) {
            routeDefinitionRepository.delete(Mono.just(routeDefinition.getId())).subscribe();
        }
        routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @StreamListener(GatewayRouteDeleteChannel.IN)
    public void gatewayRouteEvent(@Payload GatewayRouteDeleteEvent event) {
        routeDefinitionRepository.delete(Mono.just(event.getName())).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
