package com.framework.cloud.gateway.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 租户角色权限 模型
 *
 * @author wusiwei
 */
@Data
public class RolePermissionModel implements Serializable {
    private static final long serialVersionUID = -1246766903278623535L;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色标识")
    private String code;

    @ApiModelProperty(value = "权限id")
    private Long permissionId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

}
