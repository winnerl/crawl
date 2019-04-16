package com.fun.crawl.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PanCoreUtil {

    private static final String CHARSET_NAME = "UTF-8";

    final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    String Host = "";
    String Accept = "";
    String Referer = "";
    String AcceptEncoding = "";
    String AcceptLanguage = "";
    String Cookie = "";

    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .pingInterval(30, TimeUnit.SECONDS).build();

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public static void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 这里使用了HttpClinet的API。只是为了方便
     *
     * @param params
     * @return
     */
    public static String formatParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, CHARSET_NAME);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatParams(params);
    }

    /**
     * 接口请求返回response
     *
     * @param host      请求接口的域名地址
     * @param apiUrl    请求接口名称，格式如："/api/list"
     * @param inputMap  参数集合Map<key,value>，key参数名称，value参数值,都是字符串
     * @param method    提交方式：DELETE，POST，PUT，GET,"GET"直接地址请求，默认："POST"
     * @param onlyValue 请求参数只有一个的时候的值(GET请求用到，其他请求为null即可)
     * @return 响应结果
     */
    public static Response getRequest(String host) {
        Request request = null;

        return null;
    }

    /**
     * 获取百度登陆的GID
     *
     * @param url
     * @param params
     * @return
     */
    public static String getGId() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            engine.eval(ConstantUtils.LOGIN_JS_TEXT);
            Invocable inv = (Invocable) engine;
            String res = (String) inv.invokeFunction("getGid", new String[]{"5050412", "D"});
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取百度Callback
     *
     * @param url
     * @param params
     * @return
     */
    public static String getCallback() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            engine.eval(ConstantUtils.LOGIN_JS_TEXT);
            Invocable inv = (Invocable) engine;
            String res = (String) inv.invokeFunction("getCallback", new String[]{"5050412", "D"});
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取百度Callback
     *
     * @param url
     * @param params
     * @return
     */
    public static String getToken(String gid, String callback, Map headers) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            engine.eval(ConstantUtils.LOGIN_JS_TEXT);
            Invocable inv = (Invocable) engine;
            String res = (String) inv.invokeFunction("getCallback", new String[]{"5050412", "D"});
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公共头部Header
     *
     * @return
     */
    public static Map getMainHeader() {
        Map header = new HashMap();
        header.put("Host", "https://pan.baidu.com");
        header.put("User-Agent", "Mozilla/8.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        header.put("upgrade-insecure-requests", "1");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Connection", "keep-alive");
        return header;
    }

    public static Response visitPost(String host, String apiUrl) {
        return null;
    }

    public static void main(String[] args) {
//        System.out.println(getGId());
//        System.out.println(getCallback());
    }

}
