package com.fun.crawl.config.mobile.provider;

import com.fun.crawl.config.mobile.token.MobileLoginAuthenticationToken;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 手机短信登录认证提供者
 *
 * @author ： CatalpaFlat
 * @date ：Create in 22:14 2017/12/20
 */
public class MobileLoginAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(MobileLoginAuthenticationProvider.class.getName());

    @Getter
    @Setter
    private UserDetailsService customUserDetailsService;

    public MobileLoginAuthenticationProvider() {
        logger.info("MobileLoginAuthenticationProvider loading ...");
    }

    /**
     * 认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //获取过滤器封装的token信息
        MobileLoginAuthenticationToken authenticationToken = (MobileLoginAuthenticationToken) authentication;
        String mabileName = (String) authenticationToken.getPrincipal();
        String inputCode = (String) authenticationToken.getCredentials();
        //获取用户信息（数据库认证）
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(mabileName);
        //不通过
        if (userDetails == null) {

            throw new UsernameNotFoundException("用户名/密码无效");

        } else if (!userDetails.isEnabled()) {

            throw new DisabledException("用户已被禁用");

        } else if (!userDetails.isAccountNonExpired()) {

            throw new AccountExpiredException("账号已过期");

        } else if (!userDetails.isAccountNonLocked()) {

            throw new LockedException("账号已被锁定");

        } else if (!userDetails.isCredentialsNonExpired()) {

            throw new LockedException("凭证已过期");
        }

        //通过
        MobileLoginAuthenticationToken authenticationResult = new MobileLoginAuthenticationToken(userDetails, inputCode, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    /**
     * 根据token类型，来判断使用哪个Provider
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MobileLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
