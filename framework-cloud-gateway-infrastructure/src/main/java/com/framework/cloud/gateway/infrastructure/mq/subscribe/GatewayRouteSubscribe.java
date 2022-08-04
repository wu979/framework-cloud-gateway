package com.framework.cloud.gateway.infrastructure.mq.subscribe;

import com.framework.cloud.gateway.domain.event.GatewayRouteDeleteEvent;
import com.framework.cloud.gateway.domain.event.GatewayRouteEvent;
import com.framework.cloud.gateway.domain.utils.RouteDefinitionUtil;
import com.framework.cloud.gateway.infrastructure.mq.channel.GatewayRouteChannel;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@EnableBinding(GatewayRouteChannel.class)
public class GatewayRouteSubscribe implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Qualifier("cacheRouteDefinitionRepository")
    @Resource
    private RouteDefinitionRepository routeDefinitionRepository;

    @StreamListener(GatewayRouteChannel.IN)
    public void gatewayRouteEvent(@Payload GatewayRouteEvent event) {
        log.info("消费");
        RouteDefinition routeDefinition = RouteDefinitionUtil.buildRouteDefinition(event);
        routeDefinitionRepository.delete(Mono.just(routeDefinition.getId())).subscribe();
        routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @StreamListener(GatewayRouteChannel.DELETE_IN)
    public void gatewayRouteEvent(@Payload GatewayRouteDeleteEvent event) {
        log.info("消费");
        routeDefinitionRepository.delete(Mono.just(event.getName())).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
