package com.fun.crawl.constants;

/**
 * @description: 安全配置常量
 */
public interface SecurityConstants {

    /**
     * token的header key
     */
   String TOKEN_HEADER = "Authorization";

   String CLOUD = "fisher";

   String CLOUD_PREFIX = "fisher_";

    /**
     * jwt 加密key
     */
   String SIGN_KEY = "FISHER";




   String SPRING_SECURITY_MOBILE_KEY = "mobile";

   String SPRING_SECURITY_CODE_KEY = "code";


   String SPRING_SECURITY_USERNAME_KEY = "username";

   String SPRING_SECURITY_PASSWORD_KEY = "password";

    /**
     * 手机验证码登录的地址
     */
   String SPRING_SECURITY_MOBILE_TOKEN_URL = "/mobile/token";


   String REDIS_CODE_PREFIX = "fisher_code_";

   Integer REDIS_CODE_EXPIRE = 60;

}
