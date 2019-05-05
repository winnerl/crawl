package com.fun.crawl.service;

import com.fun.crawl.model.PanUser;
import com.fun.crawl.base.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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


    Boolean unicast(String sign,HttpServletRequest request);
}
