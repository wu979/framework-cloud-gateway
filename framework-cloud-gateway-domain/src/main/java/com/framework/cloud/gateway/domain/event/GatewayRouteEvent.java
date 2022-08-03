package com.framework.cloud.gateway.domain.event;

import com.framework.cloud.stream.domain.DomainEvent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关领域事件
 *
 * @author wusiwei
 */
@Data
public class GatewayRouteEvent implements DomainEvent {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "服务名称")
    private String name;

    @ApiModelProperty(value = "路由")
    private String path;

    @ApiModelProperty(value = "断言")
    private String predicates;

    @ApiModelProperty(value = "过滤")
    private String filters;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "新增、修改")
    private Boolean saveOrUpdate;
}
