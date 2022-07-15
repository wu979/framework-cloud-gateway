package com.framework.cloud.gateway.api;

import com.framework.cloud.cache.annotation.EnableCache;
import com.framework.cloud.core.RequestContextConfiguration;
import com.framework.cloud.feign.annotation.EnableFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableFeign
@EnableCache
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.framework.cloud.gateway.domain.feign")
@SpringBootApplication(
        scanBasePackages = "com.framework.cloud.gateway",
        exclude = {ReactiveUserDetailsServiceAutoConfiguration.class, RequestContextConfiguration.class}
)
public class GatewayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApiApplication.class, args);
    }

}
