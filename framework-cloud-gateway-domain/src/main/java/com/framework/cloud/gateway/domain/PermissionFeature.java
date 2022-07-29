package com.framework.cloud.gateway.domain;

import org.springframework.security.core.Authentication;

/**
 * @author wusiwei
 */
public interface PermissionFeature {

    boolean hasPermission(Authentication authentication, String method, String url);
}
