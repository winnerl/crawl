package com.fun.crawl.config.mobile.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局的自定义失败处理器
 *
 * @author ： CatalpaFlat
 * @date ：Create in 21:17 2017/12/20
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class.getName());

//    @Value("${response.type}")
//    public String responseType;

    @Autowired
    private ObjectMapper objectMapper;

    public CustomAuthenticationFailureHandler() {
        logger.info("CustomAuthenticationFailureHandler loading ...");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        /*
         * exception：错误信息（认证过程中的错误）
         */
        logger.error("Authentication does not pass");


    }
}
