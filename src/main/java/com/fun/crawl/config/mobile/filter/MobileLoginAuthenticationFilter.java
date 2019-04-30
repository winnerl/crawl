package com.fun.crawl.config.mobile.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.crawl.config.mobile.token.MobileLoginAuthenticationToken;
import com.fun.crawl.enums.ResponseCodeEnum;
import com.fun.crawl.exception.PermissionDefinedException;
import com.fun.crawl.security.UserDetailsImpl;
import com.fun.crawl.util.ApiResult;
import com.fun.crawl.util.JwtTokenUtil;
import com.fun.crawl.util.SpringUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 手机短信登录过滤器
 *
 * @author ： CatalpaFlat
 * @date ：Create in 21:57 2017/12/20
 */
public class MobileLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;
    private static final Logger logger = LoggerFactory.getLogger(MobileLoginAuthenticationFilter.class.getName());

    @Setter
    @Getter
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Getter
    @Setter
    private String mobileParameterName;
    @Getter
    @Setter
    private String mobileParameterCode;

    public MobileLoginAuthenticationFilter(String mobileLoginUrl, String mobileParameterName, String mobileParameterCode,
                                           String httpMethod) {

        super(new AntPathRequestMatcher(mobileLoginUrl, httpMethod));
        this.mobileParameterName = mobileParameterName;
        this.mobileParameterCode = mobileParameterCode;
        logger.info("MobileLoginAuthenticationFilter loading ...");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        //get mobile
        String mobile = obtainMobile(request);
        //get code
        String mobileCode = obtainMobileCode(request);

        //assemble token
        MobileLoginAuthenticationToken authRequest = new MobileLoginAuthenticationToken(mobile, mobileCode);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
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


    /**
     * 设置身份认证的详情信息
     */
    private void setDetails(HttpServletRequest request, MobileLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 获取手机号
     */
    private String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameterName);
    }

    /**
     * 获取手机号
     */
    private String obtainMobileCode(HttpServletRequest request) {
        return request.getParameter(mobileParameterName);
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter.
     * If set to true, and an authentication request is received which is not a
     * POST request, an exception will be raised immediately and authentication
     * will not be attempted. The <tt>unsuccessfulAuthentication()</tt> method
     * will be called as if handling a failed authentication.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
