package com.framework.cloud.gateway.infrastructure.converter;

import com.framework.cloud.common.utils.FastJsonUtil;
import com.framework.cloud.holder.constant.HeaderConstant;
import com.framework.cloud.holder.constant.OauthConstant;
import com.framework.cloud.holder.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author wusiwei
 */
@RequiredArgsConstructor
public class AuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        Collection<? extends GrantedAuthority> authorities = this.getAuthorities(map);
        if (map.containsKey(OauthConstant.USER_DETAIL)) {
            Object principal = map.get(OauthConstant.USER_DETAIL);
            LoginUser detail = FastJsonUtil.toJavaObject(principal, LoginUser.class);
            return new UsernamePasswordAuthenticationToken(detail, OauthConstant.CREDENTIALS, authorities);
        } else {
            if (map.containsKey(HeaderConstant.X_USER_HEADER)) {
                Object principal = map.get(HeaderConstant.X_USER_HEADER);
                LoginUser loginUser = FastJsonUtil.toJavaObject(principal, LoginUser.class);
                return new UsernamePasswordAuthenticationToken(loginUser, OauthConstant.CREDENTIALS, authorities);
            }
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(OauthConstant.AUTHORITIES)) {
            return null;
        } else {
            Object authorities = map.get(OauthConstant.AUTHORITIES);
            if (authorities instanceof String) {
                return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
            } else if (authorities instanceof Collection) {
                return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.collectionToCommaDelimitedString((Collection) authorities));
            } else {
                throw new IllegalArgumentException("Authorities must be either a String or a Collection");
            }
        }
    }
}
