package com.framework.cloud.gateway.domain;

import org.springframework.security.core.Authentication;

/**
 * @author wusiwei
 */
public interface PermissionFeature {

    /**
     * 权限检查
     */
    boolean hasPermission(Authentication authentication, String method, String url);
}
