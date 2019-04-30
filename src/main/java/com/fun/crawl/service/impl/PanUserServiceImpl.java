package com.fun.crawl.service.impl;

import com.fun.crawl.base.service.impl.BaseServiceImpl;
import com.fun.crawl.mapper.PanUserMapper;
import com.fun.crawl.model.PanUser;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.utils.PanCoreUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.fun.crawl.utils.PanCoreUtil.toMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
@Service
public class PanUserServiceImpl extends BaseServiceImpl<PanUserMapper, PanUser> implements PanUserService {


    @Override
    public Map<String, String> getQrCodeUrl(HttpServletRequest request) {
        Map<String, String> codeSignAndCodeURL = PanCoreUtil.getCodeSignAndCodeURL(null);
        request.getSession().setAttribute("diyCookie", PanCoreUtil.standard_cookie);

        return codeSignAndCodeURL;
    }


    @Override
    public Boolean unicast(String sign) {
        Map<String, String> map = PanCoreUtil.vertifyCodeUnicast(sign);
        if (map.containsKey("channel_v")) {
            String channel_v = map.get("channel_v");
            Map<String, String> channel_vmap = toMap(channel_v);
            if ("0".equals(channel_vmap.get("status"))) {
                String v3Bduss = channel_vmap.get("v");
                Map<String, String> v3map = PanCoreUtil.v3Login(v3Bduss, null);


            }
        }

        String tempcookie = "";
        ConcurrentHashMap<String, String> mapa = new ConcurrentHashMap<>();
        String[] temparray = tempcookie.split("; ");
        for (int i = 0; i < temparray.length; i++) {
            String keyVal = temparray[i];
            String[] keyValarr = keyVal.split("=");
            mapa.put(keyValarr[0], keyValarr[1]);
        }




        return null;
    }


}
