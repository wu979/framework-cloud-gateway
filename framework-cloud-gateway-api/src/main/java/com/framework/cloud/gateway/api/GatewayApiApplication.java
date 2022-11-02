package com.framework.cloud.gateway.api;

import com.framework.cloud.cache.annotation.EnableCache;
import com.framework.cloud.core.annotation.FrameworkApplication;
import com.framework.cloud.feign.annotation.EnableFeign;
import com.framework.cloud.logging.configuration.AutoLoggingConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@EnableCache
@EnableFeign
@EnableDiscoveryClient
@FrameworkApplication(componentScan = "com.framework.cloud.gateway", exclude = { ReactiveUserDetailsServiceAutoConfiguration.class, AutoLoggingConfiguration.class })
public class GatewayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApiApplication.class, args);
        log.info("Gateway service started successfully");
    }

}
