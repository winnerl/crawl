package com.fun.crawl.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PanCoreUtil {

    private static final String CHARSET_NAME = "UTF-8";

    public static final String PHOST = "https://pan.baidu.com";//访问订单系统接口的地址


    final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    String Host = "";
    String Accept = "";
    String Referer = "";
    String AcceptEncoding = "";
    String AcceptLanguage = "";
    String Cookie = "";

    public HashMap<String, String> cookieMap = new HashMap<>();


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
    public static Response getRequest(String host, String apiUrl, Map<String, String> params, Map<String, String> headers) {
        Request request = null;
        Request.Builder uilder = new Request.Builder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            uilder.addHeader(header.getKey(), header.getValue());
        }
        uilder.url(getRequestURL(host, apiUrl));
        request=uilder.get().build();

        try {
            Response response = execute(request);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * map集合转json字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToJson(Map<String, String> inputMap, boolean boo)
            throws UnsupportedEncodingException {
        String res = null;
        if (inputMap != null && inputMap.size() > 0) {
            JSONObject js = new JSONObject();
            Iterator<String> iterator = inputMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(inputMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    try {
                        js.put(name, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (js.length() > 0) {
                res = js.toString();
                if (boo) {
                    res = URLEncoder.encode(res, "UTF-8");
                }
            }
        }
        return res;
    }


    /**
     * 将Json对象转换成Map
     *
     * @param jsonObject json对象
     * @return Map对象
     * @throws JSONException
     */
    public static Map<String, String> toMap(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String, String> result = new HashMap<String, String>();
        Iterator<?> iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = String.valueOf(iterator.next());
            value = String.valueOf(jsonObject.get(key));
            result.put(key, value);
        }
        return result;
    }


    /**
     * 组合请求地址
     *
     * @param apiUrl
     * @return
     */
    public static String getRequestURL(String host, String apiUrl) {
        if (apiUrl.indexOf("/") == 0
                && (host.lastIndexOf("/") == (host.length() - 1))) {
            return host.substring(0, host.lastIndexOf("/")) + apiUrl;
        } else if (apiUrl.indexOf("/") != 0
                && (host.lastIndexOf("/") != (host.length() - 1))) {
            return host + "/" + apiUrl;
        } else {
            return host + apiUrl;
        }
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
    public static Map<String, String> getMainHeader() {
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

        //接口token 需要在 home 页面获取


        String headString = "Host: pan.baidu.com\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                "Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "Cookie: BAIDUID=08AB14E76DCD8A78F72B9E44C3FB0DB7:FG=1; pan_login_way=1; PANWEB=1; STOKEN=23e6d05de52cb79ac2b73aff7bf25e766f87184a16dd391ae0f8345790f46bfd; SCRC=2db3249d7530488312783c795b7b4de6; Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1555139848,1555140447,1555290212,1555311152; BIDUPSID=8E975DFAD0107B03976AF3AAC9B0C75C; PSTM=1555140199; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; BDCLND=UU2QzJx0xhG3OFWtIkWd15sqgAr5Pje17SH%2FEhdfCDQ%3D; recommendTime=guanjia2019-04-12%2016%3A00%3A00; H_PS_PSSID=1439_21083_28724_28557_28833_28584_28519_22160; Hm_lpvt_7a3960b6f067eb0085b7f96ff5e660b0=1555463151; BDSFRCVID=cm_OJeCmH6VrYbv9RqzB8g5o-eKK0gOTHlxUfbabHdelkFkVJeC6EG0Ptf8g0KubFf7VogKK0gOTH6KF_2uxOjjg8UtVJeC6EG0P3J; H_BDCLCKID_SF=tJIeoD8XJIt3DJj1hKTD2t0_5qOyetJya4o2WDvO0-5cOR5Jj65KhxLnMfK8QJoqKbbM_RAE5fcFMCDG3MA--t4j-PjbqTvn0aO7X4Pb2hv8sq0x0MOle-bQypoaht6M3COMahkb5h7xO-nmQlPK5JkgMx6MqpQJQeQ-5KQN3KJmhpFuD6t2D6QXjaAs2tQfKK6HstnJK4__Hn7zq4b_ePtDBPnZKxJLWncpQfTtbDTCqtcte-thhIu8yP4jKMRnWIJO-RQtKRo1eb51MUjO3xI8LNj405OTbIFO0KJcbRoPeC3KhPJvyT8sXnO7tfnlXbrtXp7_2J0WStbKy4oTjxL1Db0eBjT2-DA_oCIaJKbP; BDUSS=QwbDdvQWlUR0VWRGRwdTFidm9Db1JVVFFTWENaMHlTQkJMckxNcnJMM3JDTjVjSVFBQUFBJCQAAAAAAAAAAAEAAADI-sctu9jS5MyrtuDIxrK7wcsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOt7tlzre7ZcQ3; PANPSC=16572633628025728710%3A21uDTjxn9Pe6kalYtk3FKFcS2d9ns3O5Cn%2FlHrMxBf7X8jUThae8cWT%2FMK3iZvOoBm6rqQ5QdYJKQBQGpzFPbp40U0phyMOTQjQ1mFHTd8V1tt%2Bk9Cane%2BomA%2FKeZTApdS8c7ndIzLEQUHeSSbUhhY8e6VJTHE1feWJuIrNi6qPZLWkEvKQIV8RGWeJqml0IsU5M%2BqkL1UACymlhGn6dzwpdkJYlD3TR2h5bW48I9P8AaOcRYJwoAho0KqIdgIPh%2FCnlTGyvF701AgfzvP1xNvmILPnNhRyz0P3r6Z3Ee0s%3D; delPer=0; PSINO=6\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "Cache-Control: max-age=0\n";


        String[] split = headString.split("\n");


        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            String[] head = s.split(": ");
//            if (s.indexOf("Cookie")!=-1) {
//                Map<String, Object> cookieMap = new HashMap<>();
//
//                map.put(head[0],cookieMap);
//            }else {
//
//            }

            map.put(head[0], head[1]);

        }


        Response response = getRequest("https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=", "", map, map);
        System.out.println(response.headers().toMultimap());

    }

}
