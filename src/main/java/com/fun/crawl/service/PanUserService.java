package com.fun.crawl.service;

import com.fun.crawl.model.PanUser;
import com.fun.crawl.base.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
public interface PanUserService extends BaseService<PanUser> {

    Map<String, String> getQrCodeUrl(HttpServletRequest request);

    /**
     * 同步百度云的账号资料
     * @param cookie
     * @param panUser
     * @return
     */
    PanUser sysPanUser(String cookie, PanUser panUser);

    Boolean unicast(String sign, HttpServletRequest request);

    PanUser selectByUk(Long uk);

    /**
     * cookie 字符转map
     * @param tempcookie
     * @return
     */
    ConcurrentHashMap<String, String> cookieMap(String tempcookie);
}
