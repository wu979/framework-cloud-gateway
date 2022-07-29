package com.framework.cloud.gateway.domain.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: wsw-starter-cloud
 * @description: Swagger聚合配置
 * @author: wsw
 * @create: 2021-08-27 17:44
 **/
@Data
@Component
@ConfigurationProperties(prefix = "framework.swagger")
public class SwaggerProperties {

    /**
     * 接口路径（全局配置）
     */
    private String docsPath;

    /**
     * 文档版本（全局配置）
     */
    private String version;

    /**
     * 启用分组
     */
    private Boolean enableGroup;

    /**
     * 不分组路由名称
     */
    private Set<String> ignoreGroup = new HashSet<>();

    /**
     * 不自动生成文档的路由名称
     */
    private Set<String> ignoreRoutes = new HashSet<>();

    /**
     * 是否显示该路由
     */
    public boolean isShowRoute(String route) {
        return ignoreRoutes.size() <= 0 || !ignoreRoutes.contains(route);
    }

}
