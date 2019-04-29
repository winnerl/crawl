package com.fun.crawl.config.mobile.config;

import com.fun.crawl.config.mobile.filter.MobileLoginAuthenticationFilter;
import com.fun.crawl.config.mobile.provider.MobileLoginAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 手机短信验证码认证配置
 * 1.认证过程
 *
 *  生成未认证成功的AuthenticationToken           生成认证成功的AuthenticationToken
 *        ↑                                                 ↑
 * AuthenticationFilter  ->  AuthenticationManager -> AuthenticationProvider
 *                                                           ↓
 *                                                      UserDetails（认证）
 *
 * 2. 将AuthenticationFilter加入到security过滤链（资源服务器中配置），如：
 *  http.addFilterBefore(AuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
 *
 * @author ： CatalpaFlat
 * @date ：Create in 22:22 2017/12/20
 */
@Configuration
public class MobileLoginAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private static final Logger logger = LoggerFactory.getLogger(MobileLoginAuthenticationSecurityConfig.class.getName());
    @Value("${login.mobile.url}")
    private String defaultMobileLoginUrl;
    @Value("${login.mobile.parameter}")
    private String defaultMobileLoginParameter;
    @Value("${login.mobile.codeParameter}")
    private String defaultMobileCodeParameter;
    @Value("${login.mobile.httpMethod}")
    private String defaultMobileLoginHttpMethod;

    @Autowired
    private UserDetailsService customUserDetailsService;


    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;



    public MobileLoginAuthenticationSecurityConfig() {
        logger.info("MobileLoginAuthenticationSecurityConfig loading ...");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        MobileLoginAuthenticationFilter mobileLoginAuthenticationFilter = new MobileLoginAuthenticationFilter(defaultMobileLoginUrl ,defaultMobileLoginParameter,defaultMobileCodeParameter,defaultMobileLoginHttpMethod);
        mobileLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        mobileLoginAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        mobileLoginAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);

        MobileLoginAuthenticationProvider mobileLoginAuthenticationProvider = new MobileLoginAuthenticationProvider();
        mobileLoginAuthenticationProvider.setCustomUserDetailsService(customUserDetailsService);

        http.authenticationProvider(mobileLoginAuthenticationProvider)
                .addFilterAfter(mobileLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
