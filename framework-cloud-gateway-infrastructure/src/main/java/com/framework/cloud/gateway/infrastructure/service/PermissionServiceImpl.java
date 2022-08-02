package com.framework.cloud.gateway.infrastructure.service;

import cn.hutool.core.collection.CollectionUtil;
import com.framework.cloud.cache.cache.RedisCache;
import com.framework.cloud.common.enums.GlobalRoleType;
import com.framework.cloud.gateway.common.constant.GatewayConstant;
import com.framework.cloud.gateway.common.model.PermissionModel;
import com.framework.cloud.gateway.common.model.RolePermissionModel;
import com.framework.cloud.gateway.domain.PermissionFeature;
import com.framework.cloud.holder.model.LoginUser;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wusiwei
 */
@Service
public class PermissionServiceImpl implements PermissionFeature {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Resource
    private RedisCache redisCache;

    @Resource
    private HashOperations<String, Long, List<RolePermissionModel>> hashOperations;

    @Override
    public boolean hasPermission(Authentication authentication, String method, String url) {
        //前端跨域请求预检放行
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(method)) {
            return true;
        }
        //匿名不放行
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        LoginUser userDetail = (LoginUser) authentication.getPrincipal();
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) authentication.getAuthorities();
        if (CollectionUtil.isEmpty(authorities)) {
            return false;
        }
        //用户角色
        Set<String> userRole = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toSet());
        //超级管理员 不需认证
        if (userRole.contains(GlobalRoleType.ROLE_ADMIN.toString())) {
            return true;
        }
        //所有权限
        List<PermissionModel> permissionList = redisCache.getAll(GatewayConstant.PERMISSION, PermissionModel.class);
        //当前路径权限
        PermissionModel permissionModel = permissionList.stream().filter(permission -> matcher.match(url, permission.getPath())).findFirst().orElse(null);
        if (null == permissionModel) {
            return false;
        }
        //所有权限角色
        List<RolePermissionModel> rolePermissionModel = hashOperations.get(GatewayConstant.PERMISSION_ROLE, userDetail.getTenantId());
        if (CollectionUtil.isEmpty(rolePermissionModel)) {
            return false;
        }
        //所有角色
        Set<String> roleCodes = rolePermissionModel.stream().filter(role -> permissionModel.getId().equals(role.getPermissionId())).map(RolePermissionModel::getCode).collect(Collectors.toSet());
        //交集 > 0 有此权限需要的 角色
        return CollectionUtil.intersection(userRole, roleCodes).size() > 0;

    }
}
