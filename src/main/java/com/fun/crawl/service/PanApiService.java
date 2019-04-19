package com.fun.crawl.service;

import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.utils.PanCoreUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanApiService {

    public static final String PANHOST = "https://pan.baidu.com";//访问订单系统接口的地址
    public static final String app_id = "250528";//访问订单系统接口的地址
    public static final String channel = "chunlei";//访问订单系统接口的地址

    /**
     * 获取用户百度网盘 文件列表
     *
     * @param parmsMap
     * @return
     */


    /**
     *
     * @param bdsToken
     * @param page
     * @param pageSize
     * @param dir
     * @param order   默认 time时间 size  大小  name 名称
     * @param desc 1  降序
     * @param showempty 0
     * @return
     */
    public static List<FileExtend> list(String bdsToken, int page, int pageSize, String dir, String order, int desc, int showempty, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("order",order );
        parmsMap.put("desc", desc + "");
        parmsMap.put("showempty", showempty + "");
        parmsMap.put("page", page + "");
        parmsMap.put("num", pageSize + "");
        parmsMap.put("dir", dir + "");
        parmsMap.put("t", "");
        parmsMap.put("channel", channel);
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");
        parmsMap.put("startLogTime", System.currentTimeMillis() + "");
        String jsonStr = PanCoreUtil.visit(PANHOST, "/api/list", parmsMap, "GET", cookie);
        List<FileExtend> jsStr = (List<FileExtend>) JSONObject.parseObject(jsonStr).get("list"); //将字符串{“id”：1}
        return jsStr;
    }


}
