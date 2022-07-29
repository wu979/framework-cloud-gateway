package com.framework.cloud.gateway.infrastructure.service;

import com.framework.cloud.gateway.domain.PermissionFeature;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;

/**
 *
 *
 * @author wusiwei
 */
public class PermissionServiceImpl implements PermissionFeature {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(Authentication authentication, String method, String url) {

        return true;
    }
}
