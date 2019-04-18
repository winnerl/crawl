package com.fun.crawl.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PanCoreUtil {

    private static final String CHARSET_NAME = "UTF-8";

    public static final String PHOST = "https://pan.baidu.com";//访问订单系统接口的地址
    public static final String PAN_PASSPORT_HOST = "https://passport.baidu.com";//访问订单系统接口的地址

    public static ConcurrentHashMap<String, String> standard_cookieMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> standard_headMap = new ConcurrentHashMap<>();

    static {
        standard_headMap.put("host", "pan.baidu.com");
        standard_headMap.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        standard_headMap.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        standard_headMap.put("accept-language", "zh-CN,zh;q=0.9");
        standard_headMap.put("upgrade-insecure-requests", "1");
        standard_headMap.put("accept-encoding", "gzip, deflate, br");
    }

    public static String standard_cookie = "";
    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
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
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                uilder.addHeader(header.getKey(), header.getValue());
            }
        }
        String requestURL = getRequestURL(host, apiUrl);
        if (params != null) {
            requestURL = requestURL + mapToGetString(params, false);
        }
        uilder.url(requestURL);
        request = uilder.get().build();

        try {
            Response response = execute(request);

//            //更新全局 map 和全局cookie
//            Map<String, List<String>> map = response.headers().toMultimap();
//            for (String key : map.keySet()) {
//                if (key != null) {
//                    if (key.equals("set-cookie")) {
//                        String tempcookie = "";
//                        for (String value : map.get(key)) {
//                            String[] temparray = value.split("; ");
//                            String[] sp = temparray[0].split("=", 2);
//                            standard_cookieMap.put(sp[0], sp[1]);
//                        }
//
//                        Set<String> ks = standard_cookieMap.keySet();
//                        Iterator<String> it = ks.iterator();
//                        while (it.hasNext()) {
//                            String skey = it.next();
//                            String value = standard_cookieMap.get(skey);
//                            tempcookie += skey + "=" + value + ";";
//                        }
//                        standard_cookie = tempcookie;
//                        standard_headMap.put("Cookie", map.get(key).toString());
//                    } else {
//                        standard_headMap.put(key, map.get(key).toString());
//                    }
//                }
//            }
            //更新全局cookie
            Headers head = response.headers();
            String tempcookie = "";
            for (String value : head.values("Set-Cookie")) {
                String[] temparray = value.split("; ");
                String[] sp = temparray[0].split("=", 2);
                standard_cookieMap.put(sp[0], sp[1]);
            }

            Set<String> ks = standard_cookieMap.keySet();
            Iterator<String> it = ks.iterator();
            while (it.hasNext()) {
                String skey = it.next();
                String value = standard_cookieMap.get(skey);
                tempcookie += skey + "=" + value + ";";
            }
            standard_cookie = tempcookie;


//            for (String key : names) {
//                if (key != null) {
//                    if (key.equals("Set-Cookie")) {
//                        String tempcookie = "";
//                        for (String value : head.values("Set-Cookie")) {
//                            String[] temparray = value.split("; ");
//                            String[] sp = temparray[0].split("=", 2);
//                            standard_cookieMap.put(sp[0], sp[1]);
//                        }
//
//                        Set<String> ks = standard_cookieMap.keySet();
//                        Iterator<String> it = ks.iterator();
//                        while (it.hasNext()) {
//                            String skey = it.next();
//                            String value = standard_cookieMap.get(skey);
//                            tempcookie += skey + "=" + value + ";";
//                        }
//                        standard_cookie = tempcookie;
//                    }
//                }
//            }


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
     * GET 字符方法构建
     * map集合转  String字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToGetString(Map<String, String> inputMap, boolean boo) {
        String res = null;
        if (inputMap != null && inputMap.size() > 0) {
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = inputMap.keySet().iterator();
            int sum = 0;
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(inputMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    if (sum == 0) {
                        sb.append("?");
                    } else {
                        sb.append("&");
                    }
                    sum++;
                    sb.append(name);
                    sb.append("=");
                    sb.append(value);
                }
            }
            if (sb.length() > 0) {
                res = sb.toString();
                if (boo) {

                    try {
                        res = URLEncoder.encode(res, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("字符中ENCODE失败", e);
                    }
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
        if (StringUtils.isEmpty(apiUrl)) {
            return host;
        }
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
     * 第一步
     * 获取登录二维码 签名 Sign 和二维码地址
     * <p>
     * lp	pc
     * qrloginfrom	pc
     * gid	131E659-9509-445E-B2A4-9FB2E0A74185
     * callback	tangram_guid_1555550466918
     * apiver	v3
     * tt	1555550466936
     * tpl	netdisk
     * _1555550466942
     *
     * @param url
     * @param params
     * @return {imgurl=passport.baidu.com/v2/api/qrcode?sign=8f97a89d9e25c6056df7bf6d76b356d7&uaonly=&client_id=&lp=pc&client=&qrloginfrom=pc&wechat=&traceid=, errno=0, sign=8f97a89d9e25c6056df7bf6d76b356d7, prompt=登录后威马将获得百度帐号的公开信息（用户名、头像）}
     */
    public static Map<String, String> getCodeSignAndCodeURL(Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("lp", "pc");
        params.put("qrloginfrom", "pc");
        params.put("gid", getGId());
        params.put("callback", getCallback());
        params.put("apiver", "v3");
        params.put("tt", System.currentTimeMillis() + "");
        params.put("tpl", "netdisk");
        params.put("_", System.currentTimeMillis() + "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Host", "passport.baidu.com");
        Response response = getRequest(PAN_PASSPORT_HOST, "/v2/api/getqrcode", params, headers);
        try {


            String res = response.body().string();
            System.out.println(res);
            res = subJStoJson(res);//返回数据 截取获取 json
            return toMap(res);//json 转Map
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 第二步，定时检测扫码情况
     *
     * @param headers
     * @return
     */
    public static Map<String, String> vertifyCodeUnicast(String channel_id) {
        Map<String, String> params = new HashMap<>();
        params.put("channel_id", channel_id);
        params.put("gid", getGId());
        params.put("callback", getCallback());
        params.put("apiver", "v3");
        params.put("tt", System.currentTimeMillis() + "");
        params.put("tpl", "netdisk");
        params.put("_", System.currentTimeMillis() + "");
        Response response = getRequest(PAN_PASSPORT_HOST, "/channel/unicast", params, null);
        try {
            String res = response.body().string();
            System.out.println(res);
            res = subJStoJson(res);//返回数据 截取获取 json
            return toMap(res);//json 转Map
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 第三步，调用V3登录
     *
     * @param headers
     * @return
     */
    public static Map<String, String> v3Login(String bduss, Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("bduss", bduss);
        params.put("loginVersion", "v4");
        params.put("u", "https%3A%2F%2Fpan.baidu.com%2Fdisk%2Fhome");
        params.put("callback", getCallback());
        params.put("apiver", "v3");
        params.put("v", System.currentTimeMillis() + "");
        params.put("traceid", "");
        params.put("tt", System.currentTimeMillis() + "");
        params.put("tpl", "netdisk");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Host", "passport.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/");
        Response response = getRequest(PAN_PASSPORT_HOST, "/v3/login/main/qrbdusslogin", params, null);
        try {
            String res = response.body().string();
            System.out.println(res);
            res = subJStoJson(res);//返回数据 截取获取 json
            return toMap(res);//json 转Map
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 第4步，https://passport.baidu.com/v3/login/api/auth/?return_type=5&tpl=netdisk&u=https%3A%2F%2Fpan.baidu.com%2Fdisk%2Fhome
     * <p>
     * 登录信息存入cookie
     *
     * @param stoken passport 获取的stoken值
     * @return
     */
    public static String v3LoginAuthGetToken(Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("return_type", "5");
        params.put("tpl", "netdisk");
        params.put("u", "https://pan.baidu.com/disk/home");
        if (headers == null) {
            headers = getMainHeader();
        }

        headers.put("Host", "passport.baidu.com");
        headers.put("Cookie", "UBI=fi_PncwhpxZ%7ETaJc9Ct7B5fdOHLCvaJSQD0; STOKEN=e56597bcc213e3494f4a12097f3fce8f9bab0e4f11fc58bb60391f85e3cb32cd; BDUSS=0hPYWtuZndWbUNwZn5YMW9iaFMzSXR0RTRyU0JNVmVidUwxMnNKekE0S3YxZDljSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAK9IuFyvSLhcd; PTOKEN=d297860f21025de03050b4ca32f04c97; BAIDUID=1319C599D1197156E22B024D5A7C9C60:FG=1; cflag=13%3A3; BIDUPSID=0B722C79E86D9EF7813210F2E70E45BB; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598")
                ;

        Response response = getRequest("https://passport.baidu.com/v3/login/api/auth/?return_type=5&tpl=netdisk&u=https%3A%2F%2Fpan.baidu.com%2Fdisk%2Fhome", "", null, headers);
        boolean redirect = response.isRedirect();
        System.out.println(redirect);
        System.out.println(response);
        System.out.println(response.request().url());
        String location = response.headers().get("Location");
        return location;
    }


    /**
     * 第5步，需要 调用/disk/home  次 首先调用 需要token  然后 再调用不需要Token的。
     * <p>
     * 登录信息存入cookie
     *
     * @param stoken passport 获取的stoken值
     * @return
     */
    public static void sendTodiskHome(String stoken, Map<String, String> headers, Boolean needStoken) {
        Map<String, String> params = new HashMap<>();
        params.put("errno", "0");
        params.put("errmsg", "Auth Login Sucess");
        params.put("bduss", "");
        params.put("ssnerror", "0");
        params.put("traceid", "");
        if (needStoken) {
            params.put("stoken", stoken);
        }

        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Host", "passport.baidu.com");
//        headers.put("Referer", "https://pan.baidu.com/");
        headers.put("Cookie", standard_cookie);
        Response response = getRequest(PAN_PASSPORT_HOST, "/v3/login/main/qrbdusslogin", params, headers);
    }


    /**
     * 公共头部Header
     *
     * @return
     */
    public static Map<String, String> getMainHeader() {
        Map header = new HashMap();
        header.put("Host", "https://pan.baidu.com");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Connection", "keep-alive");
        return header;
    }

    /**
     * @param host
     * @param apiUrl
     * @return
     */
    public static String subJStoJson(String JsStr) {
        int front = JsStr.indexOf("(");
        int last = JsStr.indexOf(")");
        String substring = JsStr.substring(front + 1, last);
        return substring;
    }

    public static Response visitPost(String host, String apiUrl) {
        return null;
    }


    public static void main(String[] args) {

        //接口token 需要在 home 页面获取
//Cookie: HOSUPPORT=1; UBI=fi_PncwhpxZ%7ETaO3i%7EsRfw7uBrZuVPHpzf4LHvUyVlzBBzX6KBTHc6ktfCJOWzUR8m8xZTU9inmsHvZ5XxS; HISTORY=0e797aedc109c72c2cb3043944fc878e21d59be6a5fc2367235701ea5f1bc2160db77be7c7c7; USERNAMETYPE=2; SAVEUSERID=8a6afe0028fdd25be6a57346794aa97df33d09d7; Hm_lvt_90056b3f84f90da57dc0f40150f005d5=1541151314,1541818780; pplogid=7509CqrqEbE3HtDnE%2BJ7JdKBzzRiuKvq4wJOLAyddJoVMF78sngFD6YBfdD%2FihKrQycT; BAIDUID=97E293FD8682A16BADCE0A05E09BED44:FG=1

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
//        System.out.println(map);

//        Response response = getRequest("https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=", "", map, map);
//        System.out.println(response.headers().toMultimap());
//        System.out.println(response.headers().get("set-cookie"));

        //获取登陆二维码
        //第一步  通过此URL获取 js
//        Map<String, String> codeSignAndCodeURL = getCodeSignAndCodeURL(null);
//        System.out.println(codeSignAndCodeURL);
//        Map<String, String> map1 = vertifyCodeUnicast("fa0fa91d8faf032239e9a332f2e8fb0b", null);
//        Response request = getRequest("https://passport.baidu.com/channel/unicast?channel_id=fa0fa91d8faf032239e9a332f2e8fb0b&tpl=netdisk&gid=9A52DC4-A852-4621-A9DF-2C5358A40156&callback=tangram_guid_1555567027169&apiver=v3&tt=1555567119109&_=1555567119109", "", null, null);
        try {
            String s = v3LoginAuthGetToken(null);
            //System.out.println(request.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
