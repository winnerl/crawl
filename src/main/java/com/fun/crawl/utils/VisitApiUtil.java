package com.fun.crawl.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 访问RESTAPI接口的请求客户端工具类
 */
@Slf4j
public class VisitApiUtil {


    private static final String CHARSET_NAME = "UTF-8";

    //每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
    private static final String BOUNDARY = "----------ZK2ZXFg01eZHqgaK84j02yH";

    private static final String POST_TPYE = "multipart/form-data; boundary=" + BOUNDARY;

    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .pingInterval(30, TimeUnit.SECONDS).build();


    private static  Map<String,String> cookieMap =new HashMap<>();


    /**
     * 返回OkHttpClient连接
     *
     * @param connectTimeout //连接超时 (秒) 10
     * @param readTimeout    //读超时  (秒) 30
     * @param writeTimeout   //写超时 (秒) 10
     * @param pingInterval   //心跳时间 (秒) 30
     * @return
     */
    public static OkHttpClient getConnect(Integer connectTimeout, Integer readTimeout, Integer writeTimeout, Integer pingInterval) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS);
        if (null != pingInterval && pingInterval > 0) {
            builder.pingInterval(pingInterval, TimeUnit.SECONDS);
        }
        return builder.build();

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
     * 接口请求
     *
     * @param host      请求接口的域名地址
     * @param apiUrl    请求接口名称，格式如："/api/list"
     * @param inputMap  参数集合Map<key,value>，key参数名称，value参数值,都是字符串
     * @param method    提交方式：DELETE，POST，PUT，GET,"GET"直接地址请求，默认："POST"
     * @param onlyValue 请求参数只有一个的时候的值(GET请求用到，其他请求为null即可)
     * @return 响应结果
     */
    public static String req(String host, String apiUrl, Map<String, String> inputMap, String method, String onlyValue) {
        log.info("visit,请求方式：" + method + ",请求接口：" + apiUrl + ",请求参数：" + inputMap);
        String jsonStr = null;
        long startTime = System.currentTimeMillis();
        try {
            Map<String, String> requestMap = inputMap;//请求参数
            if (apiUrl.lastIndexOf("/") == (apiUrl.length() - 1)) {
                apiUrl = apiUrl.substring(0, apiUrl.lastIndexOf("/"));
            }

            Map<String, String> signMap = null;//签名参数
            if ("GET".equals(method.toUpperCase()) && StringUtils.isNotBlank(onlyValue)) {
                //判断是否是带参数的get请求
                requestMap.put("urlflag", (100000 + new Random().nextInt(899999)) + "1" + (100000 + new Random().nextInt(899999)));
                apiUrl = apiUrl + "/" + onlyValue;
            }
            Request request = null;
            Request.Builder uilder = new Request.Builder()
                    .url(getRequestURL(host, apiUrl))//请求地址
                    .addHeader("cache-control", "no-cache");
            if ("GET".equals(method.toUpperCase())) {
                if (StringUtils.isBlank(onlyValue)) {
                    uilder.addHeader("headerquery", mapToJson(requestMap, true));//请求参数
                }
                request = uilder.get().build();
            } else {
                uilder.addHeader("content-type", POST_TPYE);
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
            jsonStr = response.body().string();
        } catch (Exception e) {
            log.error("req,请求接口异常", e);
            jsonStr = "{\"err\":1,\"result\":\"请求失败\"}";
        } finally {
            log.info("req请求接口：" + apiUrl + ",响应时间为" + ((System.currentTimeMillis() - startTime)) + "ms,响应结果：" + jsonStr);
        }
        return jsonStr;
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
    public static Response request(String host, String apiUrl, Map<String, String> inputMap, String method, String onlyValue) {
        log.info("visit,请求方式：" + method + ",请求接口：" + apiUrl + ",请求参数：" + inputMap);
        String jsonStr = null;
        long startTime = System.currentTimeMillis();
        try {
            Map<String, String> requestMap = inputMap;//请求参数
            if (apiUrl.lastIndexOf("/") == (apiUrl.length() - 1)) {
                apiUrl = apiUrl.substring(0, apiUrl.lastIndexOf("/"));
            }
            Map<String, String> signMap = null;//签名参数
            if ("GET".equals(method.toUpperCase()) && StringUtils.isNotBlank(onlyValue)) {
                //判断是否是带参数的get请求
                requestMap.put("urlflag", (100000 + new Random().nextInt(899999)) + "1" + (100000 + new Random().nextInt(899999)));
                apiUrl = apiUrl + "/" + onlyValue;
            }

            Request request = null;
            Request.Builder uilder = new Request.Builder()
                    .url(getRequestURL(host, apiUrl))//请求地址
                    .addHeader("cache-control", "no-cache");
            if ("GET".equals(method.toUpperCase())) {
                if (StringUtils.isBlank(onlyValue)) {
                    uilder.addHeader("headerquery", mapToJson(requestMap, true));//请求参数
                }
                request = uilder.get().build();
            } else {
                uilder.addHeader("content-type", POST_TPYE);
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
            return response;
        } catch (Exception e) {
            log.error("req,请求接口异常", e);
        } finally {
            log.info("req请求接口：" + apiUrl + ",响应时间为" + ((System.currentTimeMillis() - startTime)) + "ms,响应结果：" + jsonStr);
        }
        return null;
    }

    /**
     * map集合转String字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToString(Map<String, String> inputMap, boolean boo)
            throws UnsupportedEncodingException {
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
                    res = URLEncoder.encode(res, "UTF-8");
                }
            }
        }
        return res;
    }

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
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }


    /**
     * map集合转POST请求字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToPostStr(Map<String, String> inputMap) {
        String res = null;
        if (inputMap != null && inputMap.size() > 0) {
            try {
                // 头
                String boundary = BOUNDARY;

                // 传输内容
                StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);

                // 尾
//				String endBoundary = "\r\n--" + boundary + "--\r\n";  

                // 1. 处理文字形式的POST请求
                Iterator<String> iterator = inputMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String name = String.valueOf(iterator.next());
                    String value = String.valueOf(inputMap.get(name));
                    if (value == null) {
                        value = "";
                    }
                    if (StringUtils.isNotBlank(name)) {
                        contentBody.append("\r\n")
                                .append("Content-Disposition: form-data; name=\"")
                                .append(name + "\"")
                                .append("\r\n")
                                .append("\r\n")
                                .append(value)
                                .append("\r\n")
                                .append("--")
                                .append(boundary);
                    }
                }
                res = contentBody.toString();
            } catch (Exception e) {
            }
        }
        return res;
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
     * 获取map集合的随机一个value
     *
     * @param inputMap
     * @return
     */
    public static String getMapFirstValue(Map<String, String> inputMap) {
        if (inputMap != null && inputMap.size() > 0) {
            Iterator<String> iterator = inputMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = (String) iterator.next();
                return (String) inputMap.get(name);
            }
        }
        return "";
    }

    /**
     * POST组装参数集合
     *
     * @param inputMap
     * @return
     */
    public static List<NameValuePair> getParams(Map<String, String> inputMap) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        if (inputMap != null && inputMap.size() > 0) {
            Iterator<String> iterator = inputMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = (String) iterator.next();
                String value = (String) inputMap.get(name);
                if (value == null)
                    value = "";
                if (name != null)
                    params.add(new BasicNameValuePair(name, value));
            }
        }
        return params;
    }


    /**
     * 生成请求 URL
     *
     * @param dataMap
     * @return
     */
    public final static String getPayRequestUrl(Map<String, String> dataMap) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (sb.length() > 0)
                    sb.append("&");
                sb.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "?" + sb.toString();
    }

    /**
     * GET得到参数列表字符串
     *
     * @param paramValues 参数map对象
     * @return 参数列表字符串
     */
    public static String getParamsURL(Map<String, String> paramValues) {
        StringBuffer params = new StringBuffer();
        Set<String> key = paramValues.keySet();
        for (Iterator<String> it = key.iterator(); it.hasNext(); ) {
            String s = (String) it.next();
            if (params.length() == 0) {
                params.append("?" + s + "=" + paramValues.get(s));
            } else {
                params.append("&" + s + "=" + paramValues.get(s));
            }
        }
        return params.toString();
    }

    /**
     * 设置头部参数
     *
     * @param base
     * @param headerMap
     * @return
     */
    public static HttpRequestBase setHeaders(HttpRequestBase base, Map<String, String> headerMap) {
        base.setHeader("User-Agent", "Mozilla/4.0");
        base.setHeader("Accept-Language", "zh-cn");
        base.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        if (headerMap != null && headerMap.size() > 0) {
            Iterator<String> iterator = headerMap.keySet().iterator();
            while (iterator.hasNext()) {
                try {
                    String name = iterator.next().toString();
                    String value = headerMap.get(name).toString();
                    if (value == null) {
                        value = "";
                    }
                    if (name != null) {
                        base.setHeader(name, value);
                    }
                } catch (Exception e) {
                    log.error("setHeaders异常", e);
                }
            }
        }
        return base;
    }


    /**
     * 获取响应字符串内容
     *
     * @param response
     * @param charSet
     * @return
     */
    public static String getResponseSTR(HttpResponse response, String charSet) {
        StringBuffer sb = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String content;
        try {
            if (response.getEntity() != null) {
                is = response.getEntity().getContent();
                isr = new InputStreamReader(is, charSet);
                br = new BufferedReader(isr);
                sb = new StringBuffer();
                while ((content = br.readLine()) != null) {
                    sb.append(content + "\r\n");
                }
                br.close();
                isr.close();
                is.close();
            }
        } catch (Exception e) {
            log.error("getResponseSTR异常", e);
            return "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                    isr = null;
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                }
            }
            content = null;
        }
        return sb.toString();
    }

    public static String getHTMLCharSet(HttpResponse response) {
        String charSet = "gbk";
        try {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return charSet;
            }
            Header contentType = entity.getContentType();
            if (contentType != null
                    && contentType.getValue().toLowerCase().indexOf("charset=") != -1) {
                String content = contentType.getValue().toLowerCase().trim();
                charSet = content.substring(content.indexOf("charset=") + 8);
                if (charSet != null) {
                    charSet = charSet.trim();
                    if (charSet.indexOf("gb") != -1) {
                        charSet = "gbk";
                    } else if (charSet.indexOf("utf") != -1) {
                        charSet = "utf-8";
                    }
                    return charSet;
                }
            }
        } catch (Exception e) {
            log.error("getHTMLCharSet异常", e);
        }
        return "";
    }

    public static String getResponseHtml(HttpResponse response) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String content;
        String charSet = "ISO-8859-1";
        try {
            if (response.getEntity() != null) {
                is = response.getEntity().getContent();
                isr = new InputStreamReader(is, charSet);
                br = new BufferedReader(isr);
                while ((content = br.readLine()) != null) {
                    sb.append(content + "\r\n");
                }
                br.close();
                isr.close();
                is.close();
            }
        } catch (Exception e) {
            log.error("getResponseHtml异常", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                    isr = null;
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                }
            }
            content = null;
        }
        charSet = getCharSetByHtml(sb.toString());
        return charSet + "####" + new String(sb.toString().getBytes("ISO-8859-1"), charSet);
    }

    public static String getCharSetByHtml(String html) {
        String charSet = "gbk";
        try {
            html = html.replace("\n", "");
            Pattern p = Pattern.compile("charset\\s{0,1}=\\s{0,1}([\\w-]{3,12})[\"'>\\s]");
            Matcher m = p.matcher(html);
            if (m.find()) {
                String str = m.group();
                if (str.toLowerCase().indexOf("gb") != -1) {
                    charSet = "gbk";
                } else if (str.toLowerCase().indexOf("utf") != -1) {
                    charSet = "utf-8";
                } else {
                    charSet = str.trim();
                }
            }
        } catch (Exception e) {
            charSet = "gbk";
        }
        return charSet;
    }

    public static String getResponseHtml(HttpResponse response, String charSet) {
        StringBuffer sb = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String content;
        try {
            if (response.getEntity() != null) {
                is = response.getEntity().getContent();
                isr = new InputStreamReader(is, charSet);
                br = new BufferedReader(isr);
                sb = new StringBuffer();
                while ((content = br.readLine()) != null) {
                    sb.append(content + "\r\n");
                }
                br.close();
                isr.close();
                is.close();
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                    isr = null;
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                }
            }
            content = null;
        }
        return sb.toString();
    }


    /**
     * mapTomapObj
     *
     * @param paramsMap
     * @return
     */
    private static Map<String, Object> mapToObjmap(Map<String, String> paramsMap) {
        Map<String, Object> ss = new HashMap<>();
        if (paramsMap != null && paramsMap.size() > 0) {
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(paramsMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    ss.put(name, value);
                }
            }
        }
        return ss;
    }

    /**
     * mapTomap
     *
     * @param paramsMap
     * @return
     */
    private static Map<String, String> mapTomap(Map<String, Object> paramsMap) {
        Map<String, String> ss = new HashMap<>();
        if (paramsMap != null && paramsMap.size() > 0) {
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(paramsMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    ss.put(name, value);
                }
            }
        }
        return ss;
    }


    public static void main(String[] args) throws IOException {
        Response response = request("https://www.baidu.com","/", new HashMap<>(),"GET", "");
        Headers headers = response.headers();
        System.out.println(headers.toMultimap());

        System.out.println("---------------------");
        System.out.println(response.body().string());

    }


}
