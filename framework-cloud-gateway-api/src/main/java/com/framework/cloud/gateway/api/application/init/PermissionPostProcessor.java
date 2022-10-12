package com.framework.cloud.gateway.api.application.init;

import cn.hutool.core.collection.CollectionUtil;
import com.framework.cloud.cache.cache.RedisCache;
import com.framework.cloud.common.result.Result;
import com.framework.cloud.event.application.ApplicationInitializingEvent;
import com.framework.cloud.gateway.common.constant.GatewayConstant;
import com.framework.cloud.gateway.common.model.PermissionModel;
import com.framework.cloud.gateway.common.model.RolePermissionModel;
import com.framework.cloud.gateway.common.rpc.vo.PermissionRoleListVO;
import com.framework.cloud.gateway.common.rpc.vo.RolePermissionListVO;
import com.framework.cloud.gateway.domain.feign.UserFeignService;
import com.framework.cloud.gateway.infrastructure.converter.PermissionConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wusiwei
 */
@Slf4j
@Component
@AutoConfigureOrder(GatewayConstant.LOAD_BALANCER_CLIENT_FILTER_ORDER + 101)
public class PermissionPostProcessor implements ApplicationListener<ApplicationInitializingEvent> {

    @Resource
    private PermissionConverter permissionConverter;
    @Resource
    private UserFeignService userFeignService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private HashOperations<String, Long, List<RolePermissionModel>> hashOperations;

    @Override
    public void onApplicationEvent(ApplicationInitializingEvent event) {
        log.info("Initialized permission");
        redisCache.delete(GatewayConstant.PERMISSION, GatewayConstant.PERMISSION_ROLE);
        Result<List<PermissionRoleListVO>> result = userFeignService.list();
        if (!result.success()) {
            return;
        }
        List<PermissionRoleListVO> list = result.getData();
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<PermissionModel> permissionModels = permissionConverter.permissionToModel(list);
        redisCache.putAll(GatewayConstant.PERMISSION, permissionModels);
        redisCache.persist(GatewayConstant.PERMISSION);
        List<RolePermissionListVO> roles = list.stream().map(PermissionRoleListVO::getRolePermissionList).flatMap(Collection::stream).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(roles)) {
            return;
        }
        List<RolePermissionModel> rolePermissionModels = permissionConverter.roleToModel(roles);
        Map<Long, List<RolePermissionModel>> rolePermission = rolePermissionModels.stream().collect(Collectors.groupingBy(RolePermissionModel::getTenantId));
        hashOperations.putAll(GatewayConstant.PERMISSION_ROLE, rolePermission);
        redisCache.persist(GatewayConstant.PERMISSION_ROLE);
    }

}
