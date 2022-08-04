package com.framework.cloud.gateway.infrastructure.mq.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 网关动态路由 领域事件
 *
 * @author wusiwei
 */
public interface GatewayRouteChannel {

    String IN = "gateway-route-channel";
    @Input(IN)
    SubscribableChannel input();


    String DELETE_IN = "gateway-route-delete-channel";
    @Input(DELETE_IN)
    SubscribableChannel deleteInput();
}
