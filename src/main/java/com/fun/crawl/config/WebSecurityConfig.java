package com.fun.crawl.config;

import com.fun.crawl.config.auth.endpoint.AuthExceptionEntryPoint;
import com.fun.crawl.config.auth.handler.CustomAccessDeniedHandler;
import com.fun.crawl.config.mobile.config.MobileLoginAuthenticationSecurityConfig;
import com.fun.crawl.config.auth.filter.JwtAuthenticationFilter;
import com.fun.crawl.config.auth.filter.JwtLoginFilter;
import com.fun.crawl.security.UserDetailsServiceImpl;
import com.fun.crawl.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @description: web security配置
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IgnoreUrlPropertiesConfig ignoreUrlPropertiesConfig;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private AuthExceptionEntryPoint authExceptionEntryPoint;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private MobileLoginAuthenticationSecurityConfig mobileLoginAuthenticationSecurityConfig;

    /**
     * 配置认证规则
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config
                = http.requestMatchers().anyRequest()
                .and()
                .authorizeRequests();


        http.exceptionHandling().authenticationEntryPoint(authExceptionEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);

        http.addFilter(new JwtLoginFilter(authenticationManager()))
                .addFilter(new JwtAuthenticationFilter(authenticationManager()));
        http.apply(mobileLoginAuthenticationSecurityConfig);

        ignoreUrlPropertiesConfig.getUrls().forEach(e -> {
            config.antMatchers(e).permitAll();
        });
        config
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .anyRequest()
//                添加权限过滤器
                .access("@permissionService.hasPermission(request,authentication)");
//                .and()
//               .headers().frameOptions().disable()
//                .and().csrf().disable();


//        http.logout().logoutSuccessUrl("/");//开启注销功能,并配置退出成功后重定向的的url
//        http.rememberMe().rememberMeParameter("remeber");//开启记住我功能,默认会记住14天


    }


    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("123456");
        System.out.println(encode);
    }

}
