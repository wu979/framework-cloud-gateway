package com.framework.cloud.gateway.api.application.configuration;

import com.framework.cloud.common.result.Result;
import com.framework.cloud.gateway.domain.feign.OauthFeignService;
import com.framework.cloud.gateway.domain.properties.GatewayProperties;
import com.framework.cloud.gateway.infrastructure.converter.AuthenticationConverter;
import com.framework.cloud.gateway.infrastructure.converter.AuthenticationTokenConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

/**
 * @author wusiwei
 */
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(GatewayProperties.class)
@AutoConfigureAfter(AuthenticationResourceConfiguration.class)
public class AuthenticationTokenConfiguration {

    private final GatewayProperties gatewayProperties;
    private final OauthFeignService oauthFeignService;

    @Bean
    @Primary
    public DefaultTokenServices tokenServices(TokenStore tokenStore) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        return defaultTokenServices;
    }

    @Bean
    @Primary
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        DefaultAccessTokenConverter defaultConverter = new DefaultAccessTokenConverter();
        defaultConverter.setUserTokenConverter(new AuthenticationConverter());
        JwtAccessTokenConverter converter = new AuthenticationTokenConverter();
        converter.setVerifierKey(getPublicKey());
        converter.setAccessTokenConverter(defaultConverter);
        return converter;
    }

    private String getPublicKey() {
        ClassPathResource resource = new ClassPathResource(gatewayProperties.getPublicKey());
        String publicKey = null;
        try {
            publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (Exception e) {
            log.error("本地公钥获取失败: {}", e);
        }
        if (StringUtils.isBlank(publicKey)) {
            Result<String> result = oauthFeignService.getPublicKey();
            if (result.success()) {
                publicKey = result.getData();
            } else {
                throw new Error("获取令牌公钥失败!");
            }
        }
        return publicKey;
    }
}
