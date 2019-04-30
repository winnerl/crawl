package com.fun.crawl.config.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.crawl.constants.SecurityConstants;
import com.fun.crawl.enums.ResponseCodeEnum;
import com.fun.crawl.exception.PermissionDefinedException;
import com.fun.crawl.security.UserDetailsImpl;
import com.fun.crawl.util.ApiResult;
import com.fun.crawl.util.JwtTokenUtil;
import com.fun.crawl.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author: Lan
 * @date: 2019/4/8 15:27
 * @description:处理登录请求
 */

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String usernameParamter = SecurityConstants.SPRING_SECURITY_USERNAME_KEY;
    private String passwordParamter = SecurityConstants.SPRING_SECURITY_PASSWORD_KEY;



    private AuthenticationManager authenticationManager;



    public JwtLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * 请求登录
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String userName = usernameParamter(request);
            String password = passwordParamter(request);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password, new ArrayList<>()));
        } catch (Exception e) {
            log.error("数据读取错误", e);
        }
        return null;
    }

    /**
     * 登录成功后
     * 返回结果信息
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDTO = (UserDetailsImpl) authResult.getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();

        if (jwtTokenUtil == null) {
            jwtTokenUtil = (JwtTokenUtil) SpringUtils.getBean("jwtTokenUtil");
        }
        String token = jwtTokenUtil.createToken(userDTO);
        //将token放置请求头返回
        response.addHeader(jwtTokenUtil.getTokenHeader(), jwtTokenUtil.getTokenPrefix() + token);
        //设置登录成功后返回 登录信息
        ApiResult<String> result = new ApiResult<>(token, ResponseCodeEnum.SUCCESS);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(result));


    }

    /**
     * 登录失败
     * 返回结果信息
     *
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ApiResult<String> result = new ApiResult<>(new PermissionDefinedException(), ResponseCodeEnum.NOT_LOGIN);
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(result));

    }

    private String usernameParamter(HttpServletRequest request) {
        return request.getParameter(usernameParamter);

    }

    private String passwordParamter(HttpServletRequest request) {
        return request.getParameter(passwordParamter);
    }


}
