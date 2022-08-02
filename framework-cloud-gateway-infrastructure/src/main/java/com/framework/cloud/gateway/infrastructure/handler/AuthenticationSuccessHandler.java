package com.framework.cloud.gateway.infrastructure.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import com.framework.cloud.common.enums.GlobalNumber;
import com.framework.cloud.common.utils.FastJsonUtil;
import com.framework.cloud.holder.constant.HeaderConstant;
import com.framework.cloud.holder.model.LoginTenant;
import com.framework.cloud.holder.model.LoginUser;
import lombok.SneakyThrows;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication successful processor
 *
 * @author wusiwei
 */
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>(GlobalNumber.FOUR.getIntValue());
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication) authentication;
        OAuth2Request oAuth2Request = oauth2Authentication.getOAuth2Request();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        LoginTenant loginTenant = new LoginTenant();
        loginTenant.setId(loginUser.getId());
        loginTenant.setTenantName(oAuth2Request.getClientId());
        header.add(HeaderConstant.X_USER_HEADER, Base64.encode(FastJsonUtil.toJSONString(loginUser)));
        header.add(HeaderConstant.X_TENANT_HEADER, Base64.encode(FastJsonUtil.toJSONString(loginTenant)));
        header.add(HeaderConstant.X_AUTHORITIES_HEADER, Base64.encode(CollectionUtil.join(authentication.getAuthorities(), ",")));
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpRequest request = exchange.getRequest().mutate().headers(h -> h.addAll(header)).build();
        return webFilterExchange.getChain().filter(exchange.mutate().request(request).build());
    }

}
