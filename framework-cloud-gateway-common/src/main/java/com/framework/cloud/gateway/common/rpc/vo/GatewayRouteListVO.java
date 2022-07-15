package com.framework.cloud.gateway.common.rpc.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 * @author wusiwei
 */
@Data
public class GatewayRouteListVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "路由")
    private String path;

    @ApiModelProperty(value = "断言")
    private String predicates;

    @ApiModelProperty(value = "过滤")
    private String filters;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
