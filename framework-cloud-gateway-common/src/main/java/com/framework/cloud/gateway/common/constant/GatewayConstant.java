package com.framework.cloud.gateway.common.constant;

/**
 * @author wusiwei
 */
public class GatewayConstant {

    /**
     * 网关动态路由
     */
    public static final String ROUTES = "gateway:routes";

    /**
     * 网关前缀
     */
    public static final String SERVER_LB = "lb://";

    /**
     * Swagger分组
     */
    public static final String GROUP = "?group=";

    /**
     * 加载顺序
     */
    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10001;
}
