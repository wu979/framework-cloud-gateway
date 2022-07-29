package com.framework.cloud.gateway.domain.authorization;

import cn.hutool.core.util.ObjectUtil;
import com.framework.cloud.common.enums.GlobalMessage;
import com.framework.cloud.holder.model.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

/**
 * Oauth2.0 authentication information parser
 *
 * @author wusiwei
 */
@AllArgsConstructor
public class AuthenticationReactiveManager implements ReactiveAuthenticationManager {

    private final ResourceServerTokenServices resourceServerTokenServices;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap((accessTokenValue -> {
                    OAuth2Authentication oAuth2Authentication = resourceServerTokenServices.loadAuthentication(accessTokenValue);
                    if (ObjectUtil.isNull(oAuth2Authentication)) {
                        return Mono.error(new InvalidTokenException(GlobalMessage.AUTHENTICATION_ERROR.getMsg()));
                    }
                    Object principal = oAuth2Authentication.getPrincipal();
                    if (!(principal instanceof LoginUser)) {
                        return Mono.error(new InvalidTokenException(GlobalMessage.AUTHENTICATION_ERROR.getMsg()));
                    }
                    LoginUser loginUser = (LoginUser) principal;
                    if (ObjectUtil.isNull(loginUser)) {
                        return Mono.error(new InvalidTokenException(GlobalMessage.AUTHENTICATION_ERROR.getMsg()));
                    }
                    return Mono.just(oAuth2Authentication);
                }))
                .cast(Authentication.class);
    }

}
