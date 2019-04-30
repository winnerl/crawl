package com.fun.crawl.config.mobile.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局的认证成功处理器
 *
 * @author ： CatalpaFlat
 * @date ：Create in 21:31 2017/12/20
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class.getName());

//    @Value("${response.type}")
//    public String responseType;

    @Autowired
    private ObjectMapper objectMapper;

    public CustomAuthenticationSuccessHandler() {
        logger.info("CustomAuthenticationSuccessHandler loading ...");
    }

    /**
     * 登录成功被调用
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
         /*
         * authentication:封装认证信息（用户信息等）
         */
        logger.info("Authentication success");


    }
}
