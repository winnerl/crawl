//package com.fun.crawl.utils;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Base64;
//import android.util.Log;
//import com.okl.wechathelper.MyApplication;
//import com.sleepycat.je.log.LogUtils;
//import org.apache.commons.lang.reflect.MethodUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.io.*;
//import java.net.URL;
//import java.net.URLConnection;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//
//public class WebUtil {
//
//
//    //Get请求
//    public static String sendGet(String url, String param, Boolean hasParam) {
//        StringBuilder result = new StringBuilder();
//        BufferedReader in = null;
//        try {
//            String urlName;
//            urlName = hasParam ? url + "?" + param : url;
//            URL realUrl = new URL(urlName);
//            URLConnection conn = realUrl.openConnection();
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("cookie", MyApplication.cookie);
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//            conn.setConnectTimeout(30000);
//            conn.setReadTimeout(120000);
//            conn.connect();
//            Map<String, List<String>> map = conn.getHeaderFields();
//            for (String key : map.keySet()) {
//                if (key != null) {
//                    if (key.equals("Set-Cookie")) {
//                        String tempcookie = "";
//                        for (String value : map.get(key)) {
//                            String[] temparray = value.split("; ");
//                            String[] sp = temparray[0].split("=", 2);
//                            MyApplication.cookieMap.put(sp[0], sp[1]);
//                        }
//                        Set<String> ks = MyApplication.cookieMap.keySet();
//                        Iterator<String> it = ks.iterator();
//                        while (it.hasNext()) {
//                            String skey = it.next();
//                            String value = MyApplication.cookieMap.get(skey);
//                            tempcookie += skey + "=" + value + ";";
//                        }
//                        MyApplication.cookie = tempcookie;
//                    }
//                }
//            }
//            in = new BufferedReader(
//                    new InputStreamReader(conn.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                result.append("/n" + line);
//            }
//        } catch (Exception e) {
//            result.append("failed");
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return result.toString();
//    }
//
//    /**
//     * 将Json对象转换成Map
//     *
//     * @param jsonObject
//     *            json对象
//     * @return Map对象
//     * @throws JSONException
//     */
//    public static Map<String,String> toMap(String jsonString) throws JSONException {
//        JSONObject jsonObject = new JSONObject(jsonString);
//        ConcurrentHashMap<String,String> result = new ConcurrentHashMap<>();
//        Iterator<?> iterator = jsonObject.keys();
//        String key = null;
//        String value = null;
//        while (iterator.hasNext()) {
//            key = String.valueOf(iterator.next());
//            value = String.valueOf(jsonObject.get(key));
//            result.put(key, value);
//        }
//        return result;
//    }
//
//    /**
//     * map拼接成字符串
//     * @param map
//     * @return
//     */
//      public static String mapToString(Map<String,String> map){
//          StringBuffer stringBuffer=new StringBuffer();
//          for (String el:map.keySet()) {
//               if (stringBuffer.length()>0){
//                   stringBuffer.append("&");
//               }
//              stringBuffer.append(el+"="+map.get(el));
//          }
//          return stringBuffer.toString();
//      }
//
//
//    public static String sendPost(String url, String param, String cookies) {
//
//        PrintWriter out = null;
//        BufferedReader in = null;
//        StringBuilder sb = new StringBuilder();
//        try {
//            URL realUrl = new URL(url);
//            URLConnection conn = realUrl.openConnection();
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("cookie", cookies);
//            conn.setRequestProperty("Referer", "https://" + MyApplication.getUserBean().getDomain() + "/");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//            conn.setConnectTimeout(30000);
//            conn.setReadTimeout(120000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            out = new PrintWriter(conn.getOutputStream());
//            out.print(param);
//            out.flush();
//            conn.connect();
//            Map<String, List<String>> map = conn.getHeaderFields();
//            for (String key : map.keySet()) {
//                if (key != null) {
//                    if (key.equals("Set-Cookie")) {
//                        String tempcookie = "";
//                        for (String value : map.get(key)) {
//                            String[] temparray = value.split("; ");
//                            String[] sp = temparray[0].split("=", 2);
//                            MyApplication.cookieMap.put(sp[0], sp[1]);
//                        }
//                        Set<String> ks = MyApplication.cookieMap.keySet();
//                        Iterator<String> it = ks.iterator();
//                        while (it.hasNext()) {
//                            String skey = it.next();
//                            String value = MyApplication.cookieMap.get(skey);
//                            tempcookie += skey + "=" + value + ";";
//                        }
//                        MyApplication.cookie = tempcookie;
//                    } else {
//                        MyApplication.headMap.put(key, map.get(key).toString());
//                    }
//                }
//            }
//            try {
//                in = new BufferedReader(
//                        new InputStreamReader(conn.getInputStream()));
//            } catch (IOException e) {
//
//            }
//            String line;
//            while ((line = in.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (Exception e) {
//            LogUtils.e("发送 POST 请求出现异常！" + e.getMessage());
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return sb.toString();
//    }
//
//
//    public static String sendPost(String url, String param) {
//
//        PrintWriter out = null;
//        BufferedReader in = null;
//        StringBuilder sb = new StringBuilder();
//        LogUtils.e(param);
//        try {
//            URL realUrl = new URL(url);
//            URLConnection conn = realUrl.openConnection();
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            out = new PrintWriter(conn.getOutputStream());
//            out.print(param);
//            out.flush();
//            conn.connect();
//            try {
//                in = new BufferedReader(
//                        new InputStreamReader(conn.getInputStream()));
//            } catch (IOException e) {
//
//            }
//            String line;
//            while ((line = in.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (Exception e) {
//            LogUtils.e("发送 POST 请求出现异常！" + e.getMessage());
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return sb.toString();
//    }
//
//    public static Bitmap getBitmapFromBase64Str(String str) {
//        Bitmap bitmap = null;
//        try {
//            byte[] bitmapArray;
//            bitmapArray = Base64.decode(str, Base64.DEFAULT);
//            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    public static Bitmap getBitmapFromURL(String src) {
//        LogUtils.e(src);
//        InputStream input = null;
//        try {
//            URL url = new URL(src);
//            trustAllHosts();
//            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("cookie", MyApplication.cookie);
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//            connection.connect();
//            input = connection.getInputStream();
//            return BitmapFactory.decodeStream(input);
//        } catch (IOException e) {
//            e.printStackTrace();
//            LogUtils.i(e.getMessage());
//            return null;
//        } finally {
//            try {
//                if (input != null) {
//                    input.close();
//                }
//            } catch (IOException ex) {
//                LogUtils.i(ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
//    }
//
//
//    public static boolean getFile(String src, String saveFile) {
//        InputStream input = null;
//        OutputStream output = null;
//        try {
//            URL url = new URL(src);
//            trustAllHosts();
//            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("Accept-Encoding", "identity;q=1, *;q=0");
//            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
//            connection.setRequestProperty("Host", MyApplication.getUserBean().getDomain());
//            connection.setRequestProperty("Range", "bytes=0-");
//            connection.setRequestProperty("Referer", "https://" + MyApplication.getUserBean().getDomain() + "/");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("cookie", MyApplication.cookie);
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//            connection.connect();
//            input = connection.getInputStream();
//            output = new FileOutputStream(saveFile);
//            int len;
//            byte[] buffer = new byte[1024];
//            LogUtils.e("byte[] buffer=new byte[1024];");
//            while ((len = input.read(buffer)) != -1) {
//                output.write(buffer, 0, len);
//            }
//            output.flush();
//            return true;
//        } catch (IOException e) {
//            LogUtils.e("getFile" + e.getMessage());
//            return false;
//        } finally {
//            try {
//                if (output != null) {
//                    output.close();
//                }
//                if (input != null) {
//                    input.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//
//    /**
//     * Trust every server - dont check for any certificate
//     */
//    private static void trustAllHosts() {
//        final String TAG = "trustAllHosts";
//        // Create a trust manager that does not validate certificate chains
//        TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return new X509Certificate[]{};
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                        Log.i(TAG, "checkClientTrusted");
//                    }
//
//                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                        Log.i(TAG, "checkServerTrusted");
//                    }
//                }
//
//        };
//        // Install the all-trusting trust manager
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static  Handler handler;
//    public static void SendTextMsg(final Context context, String TargetUser, String Content) {
//        LogUtils.i("https://" + MyApplication.getUserBean().getDomain() + "/cgi-bin/mmwebwx-bin/webwxsendmsg?lang=zh_CN&pass_ticket=" + MyApplication.getUserBean().getPass_ticket());
//        LogUtils.i("{\"BaseRequest\":{\"Uin\":" + MyApplication.getUserBean().getWxuin() + ",\"Sid\":\"" + MyApplication.getUserBean().getWxsid() + "\",\"Skey\":\"" + MyApplication.getUserBean().getSkey() + "\",\"DeviceID\":\"e8" + MethodUtils.getRandomNumberString(14) + "\"},\"Msg\":{\"Type\":2,\"Content\":\"" + Content + "\",\"FromUserName\":\"" + MyApplication.getUserBean().getUserName() + "\",\"ToUserName\":\"" + TargetUser + "\",\"LocalID\":\"" + MethodUtils.getRandomNumberString(17) + "\",\"ClientMsgId\":\"" + MethodUtils.getRandomNumberString(17) + "");
//        String s = sendPost("https://" + MyApplication.getUserBean().getDomain() + "/cgi-bin/mmwebwx-bin/webwxsendmsg?lang=zh_CN&pass_ticket=" + MyApplication.getUserBean().getPass_ticket(), "{\"BaseRequest\":{\"Uin\":" + MyApplication.getUserBean().getWxuin() + ",\"Sid\":\"" + MyApplication.getUserBean().getWxsid() + "\",\"Skey\":\"" + MyApplication.getUserBean().getSkey() + "\",\"DeviceID\":\"e8" + MethodUtils.getRandomNumberString(14) + "\"},\"Msg\":{\"Type\":2,\"Content\":\"" + Content + "\",\"FromUserName\":\"" + MyApplication.getUserBean().getUserName() + "\",\"ToUserName\":\"" + TargetUser + "\",\"LocalID\":\"" + MethodUtils.getRandomNumberString(17) + "\",\"ClientMsgId\":\"" + MethodUtils.getRandomNumberString(17) + "\"}}", MyApplication.cookie);
//
//        handler=new Handler(context.getMainLooper()){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                final  SpeechUtils speechUtils=new SpeechUtils();
//                speechUtils.iat(context, MyApplication.info.getUserName());
//                MyApplication.isOpen=true;
//                speechUtils.speechText(SpeechUtils.concatString(context,
//                        "消息发送成功","如需继续发送,请说继续发送",true),"1#3#");
//            }
//        };
//        JSONObject objs = null;
//        try {
//            JSONObject jsonmsg = new JSONObject(s);
//            objs = jsonmsg.getJSONObject("BaseResponse");
//
//            String Ret = objs.get("Ret") + "";
//            if (TextUtils.equals("0", Ret)) {
//                //停止播报消息
//                try {
//                    //继续发送或者
//                   handler.sendEmptyMessage(0);
//                }catch (Exception ex){
//                ex.printStackTrace();                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        LogUtils.i("发送结果="+s);
//
//    }
//}
