package com.framework.cloud.gateway.common.rpc.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 权限 分页VO
 *
 * @author wusiwei
 */
@Data
public class PermissionRoleListVO {

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

    @ApiModelProperty(value = "角色列表")
    private List<RolePermissionListVO> rolePermissionList;

}