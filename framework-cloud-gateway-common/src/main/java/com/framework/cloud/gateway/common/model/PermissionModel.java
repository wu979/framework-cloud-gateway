package com.framework.cloud.gateway.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限 模型
 *
 * @author wusiwei
 */
@Data
public class PermissionModel implements Serializable {
    private static final long serialVersionUID = -4358684547495483806L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "权限编码")
    private String code;

    @ApiModelProperty(value = "权限类型")
    private String type;

    @ApiModelProperty(value = "请求路径")
    private String path;

}
