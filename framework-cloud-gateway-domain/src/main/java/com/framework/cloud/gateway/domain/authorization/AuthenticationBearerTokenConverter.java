package com.framework.cloud.gateway.domain.authorization;

import com.framework.cloud.cache.cache.RedisCache;
import com.framework.cloud.common.enums.GlobalMessage;
import com.framework.cloud.common.utils.RegexUtil;
import com.framework.cloud.holder.constant.CacheConstant;
import com.framework.cloud.holder.constant.HeaderConstant;
import com.framework.cloud.holder.constant.OauthConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token request header parser
 *
 * @author wusiwei
 */
@RequiredArgsConstructor
public class AuthenticationBearerTokenConverter implements ServerAuthenticationConverter {

    private static final Pattern PATTERN = Pattern.compile(RegexUtil.REGEX_TOKEN, Pattern.CASE_INSENSITIVE);

    private final RedisCache redisCache;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono
                .fromCallable(() -> token(exchange.getRequest()))
                .map((token) -> {
                    if (token.isEmpty()) {
                        BearerTokenError error = invalidTokenError();
                        throw new OAuth2AuthenticationException(error);
                    } else {
                        return new BearerTokenAuthenticationToken(token);
                    }
                });
    }

    private String token(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HeaderConstant.AUTHORIZATION);
        String headerToken = null;
        if (StringUtils.hasText(authorization)) {
            headerToken = resolveFromAuthorizationHeader(authorization);
        }
        String parameterToken = request.getQueryParams().getFirst(OauthConstant.ACCESS_TOKEN);
        if (StringUtils.hasText(headerToken)) {
            if (StringUtils.hasText(parameterToken)) {
                BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
                throw new OAuth2AuthenticationException(error);
            } else {
                return headerToken;
            }
        } else {
            if (StringUtils.hasText(parameterToken)) {
                headerToken = resolveFromAuthorizationHeader(parameterToken);
                if (StringUtils.hasText(headerToken) && isParameterTokenSupportedForRequest(request)) {
                    return headerToken;
                }
            }
        }
        return null;
    }

    private String resolveFromAuthorizationHeader(String authorization) {
        String key = CacheConstant.ACCESS_TOKEN + authorization;
        String accessToken = redisCache.get(key, String.class);
        if (!StringUtils.startsWithIgnoreCase(accessToken, HeaderConstant.BEARER)) {
            BearerTokenError error = invalidTokenError(GlobalMessage.AUTHENTICATION_ERROR.getMsg());
            throw new OAuth2AuthenticationException(error);
        }
        Matcher matcher = PATTERN.matcher(accessToken);
        if (!matcher.matches()) {
            BearerTokenError error = invalidTokenError();
            throw new OAuth2AuthenticationException(error);
        } else {
            return matcher.group(HeaderConstant.TOKEN);
        }
    }


    private static BearerTokenError invalidTokenError(String msg) {
        return BearerTokenErrors.invalidToken(msg);
    }

    private static BearerTokenError invalidTokenError() {
        return invalidTokenError(GlobalMessage.AUTHENTICATION_ERROR.getMsg());
    }

    private boolean isParameterTokenSupportedForRequest(ServerHttpRequest request) {
        return HttpMethod.GET.equals(request.getMethod());
    }
}
