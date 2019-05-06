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
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import static com.fun.crawl.utils.VisitApiUtil.mapToPostStr;

@Slf4j
public class PanCoreUtil {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String POST_TPYE = "multipart/form-data; boundary=" + "-----WP----";

    public static final String PHOST = "https://pan.baidu.com";//访问订单系统接口的地址
    public static final String PAN_PASSPORT_HOST = "https://passport.baidu.com";//访问订单系统接口的地址

    public static ConcurrentHashMap<String, String> standard_cookieMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> standard_headMap = new ConcurrentHashMap<>();

    public static String standard_cookie = "";
    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
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
            if (head.values("Set-Cookie") != null && head.values("Set-Cookie").size() > 0) {
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
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接口请求返回response
     * 使用xmlHttpHead xml_headMap 头部信息
     *
     * @param host      请求接口的域名地址
     * @param apiUrl    请求接口名称，格式如："/api/list"
     * @param inputMap  参数集合Map<key,value>，key参数名称，value参数值,都是字符串
     * @param method    提交方式：DELETE，POST，PUT，GET,"GET"直接地址请求，默认："POST"
     * @param onlyValue 请求参数只有一个的时候的值(GET请求用到，其他请求为null即可)
     * @return 响应结果
     */
    public static String visit(String host, String apiUrl, Map<String, String> inputMap, String method, String cookie, Map<String, String> headerMap) {
        log.info("visit,请求方式：" + method + ",请求接口：" + apiUrl + ",请求参数：" + inputMap);
        String jsonStr = null;
        long startTime = System.currentTimeMillis();
        Map<String, String> headers;
        if (headerMap != null) {
            headers = headerMap;
        } else {
            headers = xmlHttpHead();
        }
        Request request = null;
        Request.Builder uilder = new Request.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                uilder.addHeader(header.getKey(), header.getValue());
            }

            if (StringUtils.isNotEmpty(cookie)) {
                uilder.addHeader("Cookie", cookie);
            } else {
                uilder.addHeader("Cookie", standard_cookie);
            }

        }
        String requestURL = getRequestURL(host, apiUrl);

        try {
            Map<String, String> requestMap = inputMap;//请求参数

            if ("GET".equals(method.toUpperCase())) {
                if (requestMap != null) {
                    requestURL = requestURL + mapToGetString(requestMap, true);
                }
            }
            uilder.url(requestURL);
            if ("GET".equals(method.toUpperCase())) {
                request = uilder.get().build();
            } else if ("POST_STRING".equals(method.toUpperCase())) {
                FormBody.Builder builder = new FormBody.Builder();
                //java 8 遍历map entry
//                System.out.println(requestMap);
//                requestMap.entrySet().forEach(key -> builder.add(key.getKey(), key.getValue()));
                FormBody formBody = builder.build();
                request = uilder.post(formBody).build();
            } else if ("POST_PARM".equals(method.toUpperCase())) {
                FormBody.Builder builder = new FormBody.Builder();
                //java 8 遍历map entry
                requestMap.entrySet().forEach(key -> builder.add(key.getKey(), key.getValue()));
                FormBody formBody = builder.build();
                request = uilder.post(formBody).build();
//                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//                String postString = mapToPostString(requestMap, true);
//                RequestBody body = RequestBody.create(mediaType, postString);
//                request = uilder.post(body).build();
            } else {
                RequestBody body = RequestBody.create(MediaType.parse(POST_TPYE), mapToPostStr(requestMap));//请求参数
                if ("DELETE".equals(method.toUpperCase())) {
                    request = uilder.delete(body).build();
                } else if ("POST".equals(method.toUpperCase())) {
                    request = uilder.post(body).build();
                } else if ("PUT".equals(method.toUpperCase())) {
                    request = uilder.put(body).build();
                }
            }
            Response response = execute(request);

            Headers respronseHeader = response.headers();
            boolean isGzip = false;
            //Content-Encoding: gzip
            for (String value : respronseHeader.values("Content-Encoding")) {
                if (value.equals("gzip")) {
                    isGzip = true;
                }
            }
            if (isGzip) {
                InputStream is = response.body().byteStream();
                //gzip 解压数据
                GZIPInputStream gzipIn = new GZIPInputStream(is);
                jsonStr = streamToStr(gzipIn, "UTF-8");
            } else {
                jsonStr = response.body().string();
            }

        } catch (Exception e) {
            log.error("req,请求接口异常", e);
        } finally {
            log.info("req请求接口：" + apiUrl + ",响应时间为" + ((System.currentTimeMillis() - startTime)) + "ms,响应结果：" + jsonStr);
        }
        return jsonStr;
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
                    if (boo) {
                        try {
                            value = URLEncoder.encode(value, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            log.error("字符中ENCODE失败", e);
                        }
                    }
                    sb.append(value);
                }
            }
            if (sb.length() > 0) {
                res = sb.toString();
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
    public static String mapToPostString(Map<String, String> inputMap, boolean boo) {
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
                        sb.append("");
                    } else {
                        sb.append("&");
                    }
                    sum++;
                    sb.append(name);
                    sb.append("=");
                    if (boo) {
                        try {
                            value = URLEncoder.encode(value, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            log.error("字符中ENCODE失败", e);
                        }
                    }
                    sb.append(value);
                }
            }
            if (sb.length() > 0) {
                res = sb.toString();
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
    public static Map<String, String> mapToToEncode(Map<String, String> inputMap) {
        Map<String, String> result = new HashMap<String, String>();
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
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("字符中ENCODE失败", e);
                    }
                    result.put(name, value);
                }
            }

        }
        return result;
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
     * 下载时候的sign
     * <p>
     * 登录时候返回的签名值
     *
     * @param sign3
     * @param sign1
     * @return
     */
    public static String getDownloadSign(String sign3, String sign1) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            engine.eval(ConstantUtils.SIGN_JS_TEXT);
            Invocable inv = (Invocable) engine;
            String res = (String) inv.invokeFunction("s", new String[]{sign3, sign1});
            return Base64encodeJS(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载时候的sign
     *
     * @param url
     * @param params
     * @return
     */
    public static String Base64encodeJS(String String) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            engine.eval(ConstantUtils.BASE_64_JS_TEXT);
            Invocable inv = (Invocable) engine;
            String res = (String) inv.invokeFunction("base64", new String[]{String});
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * get  SIgn2
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
     * get  SIgn2
     *
     * @param url
     * @param params
     * @return
     */
    public static String makePrivatePassword() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            engine.eval(ConstantUtils.makePrivatePassword_TEXT);
            Invocable inv = (Invocable) engine;
            String res = (String) inv.invokeFunction("makePrivatePassword", new String[]{});
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
            String res = (String) inv.invokeFunction("getCallback");
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
            res = subJStoJson(res);//返回数据 截取获取 json
            return toMap(res);//json 转Map
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * pan.baidu.com
     * 第4步，https://passport.baidu.com/v3/login/api/auth/?return_type=5&tpl=netdisk&u=https%3A%2F%2Fpan.baidu.com%2Fdisk%2Fhome
     * <p>
     * 登录信息存入cookie
     * <p>
     * <p>
     * params.put("return_type", "5");
     * params.put("tpl", "netdisk");
     * params.put("u", "https://pan.baidu.com/disk/home");
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
        headers.put("Cookie", standard_cookie);
        Response response = getRequest(PAN_PASSPORT_HOST, "/v3/login/api/auth/", params, headers);


//        Response preRepsonse = response.priorResponse().priorResponse();
        String location = response.headers().get("Location");//是重定向URL
        return location;
    }

    /**
     * 第6步，需要 调用/disk/home  不带参数
     * <p>
     * 登录信息存入cookie
     * 更新Cookie: BAIDUID=DC40829662EC00F42854FF80DC368CF6:FG=1; BDUSS=EMxRDc0SVQtU3pMM0x6WHZ4cGhSWU5EU2lsSXo5c1l6N35Cdk5JVUtyTmxDdUJjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGV9uFxlfbhcN; pan_login_way=1
     *
     * @param stoken passport 获取的stoken值
     * @return
     */
    public static void diskHome() {
        String tempcookie = "";
        Map<String, String> headers = getMainHeader();
        standard_cookie = "";//情况cookie
        standard_cookieMap.put("pan_login_way", "1");
        Set<String> ks = standard_cookieMap.keySet();
        Iterator<String> it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;

        headers.put("Host", "pan.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/");
        headers.put("Cookie", standard_cookie);
        Response response = getRequest(PHOST, "/disk/home", null, headers);
        Response preResponse = response.priorResponse();
//        System.out.println(standard_cookieMap);
    }

    /**
     * 第7步，需要 调用/disk/home  直接通过URL
     *
     * @param stoken passport 获取的stoken值
     * @return
     */
    public static void sendTodiskHomeOne(String DURL) {
        String tempcookie = "";
        Map<String, String> headers = getMainHeader();
        standard_cookie = "";//情况cookie
        standard_cookieMap.put("pan_login_way", "1");
        standard_cookieMap.put("PANWEB", "1");
        Set<String> ks = standard_cookieMap.keySet();
        Iterator<String> it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;

        headers.put("Host", "pan.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/");
        headers.put("Cookie", standard_cookie);
        Response preResponse = getRequest(DURL, "", null, headers);
//        Response preResponse = response.priorResponse().priorResponse();

        //更新全局cookie
        Headers head = preResponse.headers();
        tempcookie = "";
        for (String value : head.values("Set-Cookie")) {
            String[] temparray = value.split("; ");
            String[] sp = temparray[0].split("=", 2);
            standard_cookieMap.put(sp[0], sp[1]);
        }

        ks = standard_cookieMap.keySet();
        it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;

    }

    /**
     * 第8步，需要 调用/disk/home  调用不需要Token的。
     * <p>
     * 登录信息存入cookie
     *
     * @param stoken passport 获取的stoken值
     * @return
     */
    public static Map<String, String> sendTodiskHomeTwo() {
        Map<String, String> params = new HashMap<>();
        params.put("errno", "0");
        params.put("errmsg", "Auth Login Sucess");
        params.put("bduss", "");
        params.put("ssnerror", "0");
        params.put("traceid", "");
        String tempcookie = "";
        Map<String, String> headers = getMainHeader();
        standard_cookie = "";//情况cookie
        standard_cookieMap.put("pan_login_way", "1");
        standard_cookieMap.put("PANWEB", "1");

        Set<String> ks = standard_cookieMap.keySet();
        Iterator<String> it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;

        headers.put("Host", "pan.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/");
        headers.put("Cookie", standard_cookie);
        Response response = getRequest(PHOST, "/disk/home", params, headers);

        //更新全局cookie
        Headers head = response.headers();
        tempcookie = "";
        for (String value : head.values("Set-Cookie")) {
            String[] temparray = value.split("; ");
            String[] sp = temparray[0].split("=", 2);
            standard_cookieMap.put(sp[0], sp[1]);
        }

        ks = standard_cookieMap.keySet();
        it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;
        String token = "";
        try {
            String html;

            InputStream is = response.body().byteStream();
            //gzip 解压数据
            GZIPInputStream gzipIn = new GZIPInputStream(is);
            html = streamToStr(gzipIn, "UTF-8");
            int start = html.indexOf("var context=");
            int end = html.indexOf("var yunData = require('disk-system:widget/data/yunData.js');");
            if (start != -1 && end != -1) {

                token = html.substring(start + 12, end);
                token = token.substring(0, token.lastIndexOf(";"));
                try {
                    Map<String, String> map = toMap(token);
                    return map;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Map<String, String> home() {
        Map<String, String> headers = getMainHeader();
        headers.put("Host", "pan.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/");
        headers.put("Cookie", standard_cookie);
        Response response = getRequest(PHOST, "/disk/home?", null, headers);
        String tempcookie = "";
        Set<String> ks = standard_cookieMap.keySet();
        Iterator<String> it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;

        if (!response.isSuccessful()){
            return null;
        }
        //更新全局cookie
        Headers head = response.headers();
        tempcookie = "";
        for (String value : head.values("Set-Cookie")) {
            String[] temparray = value.split("; ");
            String[] sp = temparray[0].split("=", 2);
            standard_cookieMap.put(sp[0], sp[1]);
        }

        ks = standard_cookieMap.keySet();
        it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = standard_cookieMap.get(skey);
            tempcookie += skey + "=" + value + ";";
        }
        standard_cookie = tempcookie;
        String token = "";
        try {
            String html;

            InputStream is = response.body().byteStream();
            //gzip 解压数据
            GZIPInputStream gzipIn = new GZIPInputStream(is);
            html = streamToStr(gzipIn, "UTF-8");
            int start = html.indexOf("var context=");
            int end = html.indexOf("var yunData = require('disk-system:widget/data/yunData.js');");
            if (start != -1 && end != -1) {

                token = html.substring(start + 12, end);
                token = token.substring(0, token.lastIndexOf(";"));
                try {
                    Map<String, String> map = toMap(token);
                    return map;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> apiSys(String hao123ParamUrl,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("bdu", hao123ParamUrl);
        params.put("t", System.currentTimeMillis() + "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Host", "ckpass.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/");
        Response response = getRequest("https://ckpass.baidu.com", "/api/sync", params, headers);
        System.out.println(response);
        return null;
    }

    public static Map<String, String> cmsdata(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("do", "download");
        params.put("_", ""+System.currentTimeMillis());
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        headers.put("Cookie", standard_cookie);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Host", "pan.baidu.com");
        Response response = getRequest(PHOST, "/disk/cmsdata", params, headers);
        return null;
    }
    public static Map<String, String> cmsdata2(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("do", "manual");
        params.put("ch", "download_limit");
        params.put("_", ""+System.currentTimeMillis());
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        headers.put("Cookie", standard_cookie);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Host", "pan.baidu.com");
        Response response = getRequest(PHOST, "/disk/cmsdata", params, headers);
        return null;
    }

    public static Map<String, String> en3(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        headers.put("Cookie", standard_cookie);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Host", "pan.baidu.com");
        Response response = getRequest(PHOST, "/enterprise/user/check", params, headers);
        return null;
    }

    public static Map<String, String> refresh(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("force", "1");
        params.put("setread", "0");
        params.put("begin", ""+System.currentTimeMillis());
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        headers.put("Cookie", standard_cookie);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Host", "pan.baidu.com");
        Response response = getRequest(PHOST, "/pcloud/counter/refreshcount", params, headers);
        return null;
    }

    public static Map<String, String> diffall(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("message", "2");
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        headers.put("Cookie", standard_cookie);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Host", "pan.baidu.com");
        Response response = getRequest(PHOST, "/api/diffall", params, headers);
        return null;
    }

    public static Map<String, String> reportUser(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }


        String getString = PanCoreUtil.mapToGetString(params, true);


        Map<String, String> postMap = new HashMap<>();
        postMap.put("timestamp",System.currentTimeMillis()/1000+"");
        postMap.put("action","web_home");

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        headers.put("Cookie", standard_cookie);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Host", "pan.baidu.com");
        visit(PHOST, "/api/report/user"+getString, postMap, "POST_PARM",standard_cookie,headers);
        return null;
    }


    public static Map<String, String> membership(String bdsToken,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("method", "query");
        params.put("channel", "chunlei");
        params.put("web", "1");
        params.put("app_id", "250528");
        params.put("bdstoken", bdsToken);
        params.put("clienttype", "0");
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        Map<String, String> postMap = new HashMap<>();
        postMap.put("user_id",1+"");

        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Referer", "https://pan.baidu.com/disk/home?");
        String getString = PanCoreUtil.mapToGetString(params, true);
         visit(PHOST, "/rest/2.0/membership/isp"+getString, postMap, "POST_PARM",standard_cookie,headers);
        return null;
    }


    public static Map<String, String> statistics(  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("clienttype", "0");
        params.put("version", "v5");
        params.put("op", "download");
        params.put("type", "webdownload");
        params.put("from", "dlink");
        params.put("product", "pan");
        params.put("success", "1");
        params.put("reason", "0");
        params.put("ajaxstatus", "200");
        params.put("ajaxurl", "/api/download");
        params.put("ajaxdata", "\"success\"");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "image/webp,*/*");
        headers.put("Host", "update.pan.baidu.com");
        headers.put("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=");
        headers.put("Cookie", standard_cookie);
        Response response = getRequest("https://update.pan.baidu.com", "/statistics", params, headers);
        return null;
    }
    public static Map<String, String> XDUSS(String xduss,  Map<String, String> headers) {
        Map<String, String> params = new HashMap<>();
        params.put("method", "upload");
        params.put("app_id", "250528");
        params.put("channel", "chunlei");
        params.put("clienttype", "0");
        params.put("web", "1");
        params.put("BDUSS", xduss);
        params.put("logid", "");
        if (headers == null) {
            headers = getMainHeader();
        }
        headers.put("Accept", "*/*");
        headers.put("Host", "c3.pcs.baidu.com");
        headers.put("Cookie", standard_cookie);
        String toGetString = mapToGetString(params, false);
        headers.put("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=");
        final String post = visit("https://c3.pcs.baidu.com", "/rest/2.0/pcs/superfile2" + toGetString, null, "POST", "", headers);
        return null;
    }





    public static String v3LoginAuthGetTokenForFileStoken(Map<String, String> headers) {
        if (headers == null) {
            headers = getMainHeader();
        }
            headers.put("Host", "pcs.baidu.com");
            headers.put("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=");
            headers.put("Accept", " image/webp,image/apng,image/*,*/*;q=0.8");
            headers.put("Cookie", standard_cookie);
        Response response = getRequest("https://pcs.baidu.com/rest/2.0/pcs/file?method=plantcookie&type=stoken&source=pcs", "",null , headers);

        String u = response.headers().get("Location");//是重定向URL




        headers.put("Host", "passport.baidu.com");
        headers.put("Cookie", standard_cookie);
        Request request = null;
        Request.Builder uilder = new Request.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                uilder.addHeader(header.getKey(), header.getValue());
            }
        }
         response = getRequest(u, "", null, headers);
        String location = response.headers().get("Location");//是重定向URL
        try {


            String f_Cookie = "";
            Set<String> ksa = standard_cookieMap.keySet();
            Iterator<String> ita = ksa.iterator();
            while (ita.hasNext()) {
                String skey = ita.next();
                String value = standard_cookieMap.get(skey);
                if (skey.equals("BDUSS")) {
                    f_Cookie += skey + "=" + value + ";";
                }
//                if (skey.equals("BAIDUID")) {
//                    f_Cookie += skey + "=" + value + ";";
//                }

            }

//
//            /**           再次请求   */
            headers.put("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=");
            headers.put("Accept", " image/webp,image/apng,image/*,*/*;q=0.8");
            headers.put("Cookie", f_Cookie);

            //获取pscset
            uilder = new Request.Builder();
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    uilder.addHeader(header.getKey(), header.getValue());
                }
            }
            uilder.url("https://pcs.baidu.com/rest/2.0/pcs/file?method=plantcookie&type=ett");
            request = uilder.get().build();
            response = execute(request);
            Headers head = response.headers();
            if (head.values("Set-Cookie") != null && head.values("Set-Cookie").size() > 0) {
                for (String value : head.values("Set-Cookie")) {
                    String[] temparray = value.split("; ");
                    String[] sp = temparray[0].split("=", 2);
                        standard_cookieMap.put(sp[0], sp[1]);
                        if (sp[0].equals("pcsett")){
                            f_Cookie += sp[0] + "=" + sp[1] + ";";
                        }
                }
            }


//            OkHttpClient client = new OkHttpClient();
//
//            Request requestOK = new Request.Builder()
//                    .url(location)
//                    .get()
//                    .addHeader("Host", "pcs.baidu.com")
//                    .addHeader("Connection", "keep-alive")
//                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")
//                    .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
//                    .addHeader("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=")
//                    .addHeader("Accept-Encoding", "gzip, deflate, br")
//                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
////                    .addHeader("Cookie", "BAIDUID=18067EB88EA75599D3F4E9858274C15F:FG=1; BDUSS=GlhRWNmTEhwekNZdEwyVVBUTXFYS05vcVM4SzZublFTQ2U1bEVzR1dCSUtxLXRjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoexFwKHsRce; pcsett=1556443023-a91d56df9425c1e33931af4426717213")
//                    .addHeader("Cookie", "" +f_Cookie)
//                    .addHeader("cache-control", "no-cache")
//                    .addHeader("Postman-Token", "b7ceb49d-4c5d-46c7-9ac1-be932bb96749")
//                    .build();
//
//            Response responseOK = client.newCall(requestOK).execute();

            /**           再次请求   */
            headers.clear();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
            headers.put("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=");
            headers.put("Accept", " image/webp,image/apng,image/*,*/*;q=0.8");
            headers.put("Cookie", f_Cookie);
//            System.out.println(f_Cookie);
            headers.put("Host", "pcs.baidu.com");
            headers.put("Accept-Language", "zh-CN,zh;q=0.9");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Connection", "keep-alive");

            request = null;
            uilder = new Request.Builder();
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    uilder.addHeader(header.getKey(), header.getValue());
                }
            }

            uilder.url(location);
            request = uilder.get().build();


            response = execute(request);

               head = response.headers();
            String tempcookie = "";
            if (head.values("Set-Cookie") != null && head.values("Set-Cookie").size() > 0) {
                for (String value : head.values("Set-Cookie")) {
                    String[] temparray = value.split("; ");
                    String[] sp = temparray[0].split("=", 2);
                    if (sp[0].equals("STOKEN")) {//百度网盘文件下载的 STOKEN 和接口STOKEN不一致
                        standard_cookieMap.put("FILE_" + sp[0], sp[1]);
                    }
                }

                Set<String> ks = standard_cookieMap.keySet();
                Iterator<String> it = ks.iterator();
                while (it.hasNext()) {
                    String skey = it.next();
                    String value = standard_cookieMap.get(skey);
                    tempcookie += skey + "=" + value + ";";
                }
                standard_cookie = tempcookie;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String streamToStr(InputStream inputStream, String chartSet) {

        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, chartSet));
            String con;
            while ((con = br.readLine()) != null) {
                builder.append(con);
            }
            br.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }


    /**
     * 登录公共头部
     *
     * @return
     */
    public static Map<String, String> getMainHeader() {
        standard_headMap.put("Host", "pan.baidu.com");
        standard_headMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        standard_headMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        standard_headMap.put("Accept-Language", "zh-CN,zh;q=0.9");
        standard_headMap.put("Upgrade-Insecure-Requests", "1");
        standard_headMap.put("Accept-Encoding", "gzip, deflate, br");
        standard_headMap.put("Connection", "keep-alive");
        return standard_headMap;


    }

    /**
     * pan.baidu.com 请求接口公共头部
     *
     * @return
     */

    public static Map<String, String> xmlHttpHead() {
        Map<String, String> map = new HashMap<>();
        map.put("Host", "pan.baidu.com");
        map.put("Referer", "https://pan.baidu.com/disk/home");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        map.put("Accept", "application/json, text/javascript, */*; q=0.01");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        map.put("X-Requested-With", "XMLHttpRequest");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Connection", "keep-alive");
        return map;
    }

    /**
     * @param host
     * @return
     * @xml_headMap apiUrl
     */
    public static String subJStoJson(String JsStr) {
        int front = JsStr.indexOf("(");
        int last = JsStr.indexOf(")");
        String substring = JsStr.substring(front + 1, last);
        return substring;
    }

    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    public static Response visitPost(String host, String apiUrl) {
        return null;
    }


    public static void main(String[] args) throws IOException {

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
//        try {
//            String s = v3LoginAuthGetToken(null);
//            //System.out.println(request.body().string());
//            Map<String, Object> urlParams = getUrlParams(s);
//            try {
//            String stoken = (String) urlParams.get("stoken");
//
//                stoken = URLDecoder.decode(stoken, "UTF-8");
//                System.out.println(stoken);
//
//            System.out.println(s);
//
//                s = URLDecoder.decode(s, "UTF-8");
//
//                System.out.println(s);
//
//                s = URLEncoder.encode(s, "UTF-8");
//                System.out.println(s);
//            } catch (UnsupportedEncodingException e) {
//                log.error("字符中ENCODE失败", e);
//            }
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Map<String, String> mainHeader = new HashMap<>();
//        mainHeader.put("Host", "pan.baidu.com");
//        mainHeader.put("Referer", "https://pan.baidu.com/");
//        mainHeader.put("Connection", "keep-alive");
//        mainHeader.put("Accept-Encoding", "gzip, deflate, br");
//        mainHeader.put("Accept-Language", "zh-CN,zh;q=0.9");
//        mainHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        mainHeader.put("Upgrade-Insecure-Requests", "1");
//        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//
//        mainHeader.put("Cookie", "BDUSS=EMxRDc0SVQtU3pMM0x6WHZ4cGhSWU5EU2lsSXo5c1l6N35Cdk5JVUtyTmxDdUJjRVFBQUFBJCQAAAAAA" +
//                "AAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
//                "AAAAAAAAAAAGV9uFxlfbhcN; pan_login_way=1; PANWEB=1; BAIDUID=77D3ABCE13E12DE3D4B5BA636CEFF11E:FG=1");
//        System.setProperty("https.protocols", "TLSv1");
//        Response response = getRequest("https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&stoken=XUsFlfd8pfx4adWmHDmWyxY%2FFjCuosSvm3kbrq%2FD5SXX7vEH4UVbzvHpZByh" +
//                "5UiEmxgJW0UYniUfe8joapLnZfiMhZP6FszF8sXMuU7gPHy95JGUgWcKpEpHZFkZpdJGsKEsCAHm%2B%2FJZXM0ke%2FYSTtdcyhXaNG%2BBT1qmcqwZRTEwj6x%2Fa%2F1Vsva4oZHtB" +
//                "fMgTnJ8xph4EqDpjurRA1YvnDVk7bDLLwE4707QiFAuM8WJXIX1PFmCXwd5LdvFjIv0SQFPgreaANXid" +
//                "T2kFO1bRg%3D%3D&bduss=&ssnerror=0&traceid=", "", null, mainHeader);
//        System.out.println(response.headers().toMultimap());
//        String entx = getDownloadSign("e8c7d729eea7b54551aa594f942decbe", "1f40cabf1024572b56cf4af74adad7444c8e85bd");
//        System.out.println(entx);
        String s = makePrivatePassword();
        System.out.println(s);


        String cookie = "STOKEN=5592876a4448fb5047d6b3383d723338d8438d95753b1ba9dda7e9b8189e89ff;BDUSS=EtjMy1DWnJmSXA4UHhEdWhzRXozYklvbTM2TU42bkJtN35scXJSRjluaTVnZXRjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALn0w1y59MNce;PTOKEN=628abd7264826b75b540afb81a6f32f3;PASSID=ewR7By;pan_login_way=1;SCRC=544485d467634508dbfb4cdffc1eb8a6;PANWEB=1;BAIDUID=80F3CDB1B548C22FC5B6FE1D725F9B7D:FG=1;UBI=fi_PncwhpxZ%7ETaJc8aUfxDuyR4y0IDSOmGG;PANPSC=6013682671871170998%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdtAU2ePp5AkEnPc7e%2BR5fDLLvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D;";

        Map<String, String> mapa = new HashMap<>();
        String[] split1 = cookie.split(";");
        for (int i = 0; i < split1.length; i++) {
            String s1 = split1[i];
            String[] split2 = s1.split("=");
            if (split2.equals("")) {
                mapa.put(split2[0], split2[1]);
            }
            switch (split2[0]) {
                case "BAIDUID":
                    //;
                    break;
                case "FG":
                    //;
                    break;
                case "BIDUPSID":
                    //;
                    break;
                case "BDUSS":
                    //;
                    break;
                case "STOKEN":
                    //;
                    break;

            }

        }
        System.out.println(mapa);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-LLe2oWZWl5VQ8dsLTr3YYhL%2BtYA%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2704556179013296272&dp-callid=0&dstime=1556347257&r=295490698&vip=0")
                .get()
                .addHeader("Host", "d.pcs.baidu.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Referer", "https://pan.baidu.com/disk/home?")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "BAIDUID=34F6A7B37F4AFCD8F520C5040F1B5F44:" +
                                "FG=1; " +
                                "BIDUPSID=8E975DFAD0107B03976AF3AAC9B0C75C; " +
//                        "PSTM=1555656014; " +
                                "BDUSS=Q4OEV0ZnJMenlwWkV4cHpKMn5-T0toeFRkSXpmVXRLbmJRY3daRlE0OVBCZUZjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAE94uVxPeLlcaW; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; pcsett=1556431869-27570824f070481215eacf22ea97760b; " +
                                "STOKEN=2d5d2b97db33846ca216e620b08bfa636be16e9093107f037d6ff17855694b1e; "
//                        "cflag=13%3A3"
                )
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "7439d2ea-49c6-45c2-a145-71aef8e6e05a")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response);
    }


}
