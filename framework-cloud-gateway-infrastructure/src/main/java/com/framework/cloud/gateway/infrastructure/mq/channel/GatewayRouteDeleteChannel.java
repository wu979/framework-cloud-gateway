package com.framework.cloud.gateway.infrastructure.mq.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 网关动态路由 领域事件
 *
 * @author wusiwei
 */
public interface GatewayRouteDeleteChannel {

    String IN = "gateway-route-delete-channel";

    @Input(IN)
    SubscribableChannel input();

}
