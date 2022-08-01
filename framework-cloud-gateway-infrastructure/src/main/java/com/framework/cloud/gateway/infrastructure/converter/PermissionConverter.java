package com.framework.cloud.gateway.infrastructure.converter;

import com.framework.cloud.gateway.common.model.PermissionModel;
import com.framework.cloud.gateway.common.model.RolePermissionModel;
import com.framework.cloud.gateway.common.rpc.vo.PermissionRoleListVO;
import com.framework.cloud.gateway.common.rpc.vo.RolePermissionListVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author wusiwei
 */
@Mapper(componentModel = "spring")
public interface PermissionConverter {


    List<PermissionModel> permissionToModel(List<PermissionRoleListVO> list);

    List<RolePermissionModel> roleToModel(List<RolePermissionListVO> list);
}
