package com.fun.crawl.utils;

import okhttp3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CrawlTest {

    // 创建HTTP客户端
    OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                // 使用ConcurrentMap存储cookie信息，因为数据在内存中，所以只在程序运行阶段有效，程序结束后即清空
                private ConcurrentMap<String, List<Cookie>> storage = new ConcurrentHashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    String host = url.host();
                    if (cookies != null && !cookies.isEmpty()) {
                        storage.put(host, cookies);
                    }
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    String host = url.host();
                    List<Cookie> list = storage.get(host);
                    return list == null ? new ArrayList<>() : list;
                }
            })
            .build();

    final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

//    // 模拟表单登录
//    Request request = new Request.Builder()
//            .url("https://www.oschina.net/action/user/hash_login?from=")
//            .post(new FormBody.Builder()
//                    .add("email", "修改为你自己的帐号")
//                    .add("pwd", "修改为你自己的密码，使用SHA-1加密")
//                    .add("verifyCode", "")
//                    .add("save_login", "1")
//                    .build())
//            .addHeader("User-Agent", userAgent)
//            .build();
//
//    // 执行模拟登录请求
//    Response response = client.newCall(request).execute();
//
//// response status
//if (!response.Builder()) {
//        log.info("code = {}, message = {}", response.code(), response.message());
//        return;
//    } else {
//        log.info("登录成功 !");
//    }
//
//
//// 请求包含用户状态信息的网页，观察能否正确请求并取得状态信息
//    request = new Request.Builder()
//            .url("https://www.oschina.net/")
//        .addHeader("User-Agent", userAgent)
//        .get()
//        .build();
//
//// 执行GET请求，并在异步回调中处理请求
//    response = client.newCall(request).execute();
//
//// 打印登录用户名，验证是否获取到了用户的登录信息(状态信息)
//if (response.isSuccessful()) {
//        String content = response.body().string();
//
//        Matcher matcher = Pattern.compile("<span class=\"name\">(.*)</span>，您好&nbsp;").matcher(content);
//        if (matcher.find()) {
//            log.info("登录用户：{}", matcher.group(1));
//        } else {
//            log.info("用户未登录");
//        }
//
//    }


}

