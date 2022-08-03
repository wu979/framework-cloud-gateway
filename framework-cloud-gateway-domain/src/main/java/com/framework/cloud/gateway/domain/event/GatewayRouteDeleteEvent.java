package com.framework.cloud.gateway.domain.event;

import com.framework.cloud.stream.domain.DomainEvent;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关删除领域 事件
 *
 * @author wusiwei
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRouteDeleteEvent implements DomainEvent {

    @ApiModelProperty(value = "服务名称")
    private String name;
}
