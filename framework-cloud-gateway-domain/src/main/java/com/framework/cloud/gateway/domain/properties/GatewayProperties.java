package com.framework.cloud.gateway.domain.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wusiwei
 */
@Data
@ConfigurationProperties(prefix = "framework.gateway")
public class GatewayProperties {

    /**
     * 分发版本号
     */
    private String version;

    /**
     * 公钥路径
     */
    private String publicKey;

    /**
     * 过滤路由
     */
    private String[] ignoredUrl;

}
