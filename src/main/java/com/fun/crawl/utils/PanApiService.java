package com.fun.crawl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.FileExtend;
import com.mzlion.core.lang.ArrayUtils;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import static com.fun.crawl.utils.PanCoreUtil.streamToStr;

//@Component
public class PanApiService {

    public static final String PANHOST = "https://pan.baidu.com";//访问订单系统接口的地址
    public static final String app_id = "250528";//访问订单系统接口的地址
    public static final String channel = "chunlei";//访问订单系统接口的地址

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    /**
     * 获取用户百度网盘 文件列表
     *
     * @param parmsMap
     * @return
     */


    /**
     * @param bdsToken
     * @param page
     * @param pageSize
     * @param dir
     * @param order     默认 time时间 size  大小  name 名称
     * @param desc      1  降序
     * @param showempty 0
     * @return
     */
    public static List<FileExtend> list(String bdsToken, int page, int pageSize, String dir, String order, int desc, int showempty, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("order", order);
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
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            //请求失败再次请求下
            Integer errno = jsonObject.getInteger("errno");
            if (errno != null && errno.intValue() == 1) {
                jsonStr = PanCoreUtil.visit(PANHOST, "/api/list", parmsMap, "GET", cookie);
                jsonObject = JSONObject.parseObject(jsonStr);
            }

            List<FileExtend> jsStr = JSON.parseArray(jsonObject.getString("list"), FileExtend.class); //将字符串{“id”：1}
            return jsStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取目录下所有的数据
     *
     * @param bdsToken
     * @param dir
     * @param order     默认 time时间 size  大小  name 名称
     * @param desc      1  降序
     * @param showempty 0
     * @return
     */
    public static List<FileExtend> listAll(String bdsToken, String dir, String order, int desc, int showempty, String cookie) {
        Boolean isall = true;
        int page = 1;
        int pageSize = 500;
        List<FileExtend> allList = new ArrayList<>();
        while (isall) {

            List<FileExtend> templist = PanApiService.list(bdsToken, page, pageSize, dir, order, desc, showempty, PanCoreUtil.standard_cookie);
            if (CollectionUtils.isNotEmpty(templist)) {
                if (templist.size() < pageSize) {
                    isall = false;
                } else {
                    page = page + 1;
                }
                allList.addAll(templist);
            } else {
                isall = false;
            }
        }
        return allList;
    }


    /**
     * 获取下载路径
     *
     * @return
     */
    public static String apiDownload(String bdsToken, String sign, List<Long> fidlist,  String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");
        parmsMap.put("startLogTime", "1556182337061");
        parmsMap.put("fidlist", "[" + ArrayUtils.toString(fidlist.toArray()) + "]");
        parmsMap.put("sign", sign);
        parmsMap.put("timestamp",  "1556181933");
        parmsMap.put("type", "dlink");
        parmsMap.put("vip", "0");

        String getString = PanCoreUtil.mapToGetString(parmsMap, true);


        String res = PanCoreUtil.visit(PANHOST, "/api/download"+getString, null, "POST_FORM", cookie);
        return res;
    }


    private static String path = "D:/";


    private static String filenameTemp;

    /**
     * 创建文件
     *
     * @throws IOException
     */
    public static boolean creatTxtFile(String name) throws IOException {
        boolean flag = false;
        filenameTemp = path + name + ".json";
        File filename = new File(filenameTemp);
        if (!filename.exists()) {
            filename.createNewFile();
            flag = true;
        }
        return flag;
    }

    /**
     * 写文件
     *
     * @param newStr 新内容
     * @throws IOException
     */
    public static boolean writeTxtFile(String newStr) throws IOException {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        String filein = newStr + "\r\n";
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(filenameTemp);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            // TODO 自动生成 catch 块
            throw e1;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }


    public static void main(String[] args) throws IOException {
        String bdstoken = "fd55b89df4a36046fc12c39fd5086a1b";
//        {"flag":"1","vip_end_time":"null","file_list":"null","task_time":"1556178871","sign2":"function s(j,r){var a=[];var p=[];var o=\"\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};","sign1":"1f40cabf1024572b56cf4af74adad7444c8e85bd","sign3":"e8c7d729eea7b54551aa594f942decbe","vol_autoup":"0","uk":"3754657732","is_auto_svip":"0","is_evip":"0","bdstoken":"d552710630269a2e14032e9a20882206","timestamp":"1556178871","is_svip":"0","activity_end_time":"0","sharedir":"0","pansuk":"3fXeHIPvSh8uUIsgkcDMmg","is_vip":"0","loginstate":"1","sampling":"{\"expvar\":[\"sampling_test\",\"disk_center_change\",\"video_high_speed\",\"disk_timeline\"]}","need_tips":"null","photo":"https:\/\/ss0.bdstatic.com\/7Ls0a8Sm1A5BphGlnYG\/sys\/portrait\/item\/c9df1bcf.jpg","timeline_status":"1","face_status":"0","curr_activity_code":"0","urlparam":"[]","token":"75d2z3yUBB3kqPgLgTn0p9jnJL6uDpA3iPkj79AOUQCB7RRMVLe5uNTsn2Pln1V+9QetrO8fD5cSlFmxJT1O4aqXyAkVS2UjTPNhXzRzpVU\/qAmHs5k1NNDvwdlHMda6iGqwjIxWOdasgzXSzdprIZQHPoue3GNvRGoA78thUqpe0zVNQ5FkfLRwdchQhxWsDnOv7R6DIixJSO9+robf9IqfXY50dBk8HJRaGVDwwk7hsR0DZvwGYkRhEEf6DylpzL8TeqxuJ3TR2w6rzX+RNZ0MmLECsg","XDUSS":"pansec_DCb740ccc5511e5e8fedcff06b081203-gN7h3dSvDkYIkAxISEz%2FiCuyP08YvMWUHskI6hrnx98i0viZTlid2YjcrJIUcefni%2BnBWt5TGps2LL3HDfI9PxGM31rmGAHTaZSpDqBUYi2HJN%2FKONKi1DWkJX7BfUw9uuiRnTmPRl7Gpj7ORoXsOxoJydbAYICgCjbIXaTYxiy%2FREVMKWKpNgrcbVaH13gOin7olGt2rsnfuBl1P1DRTxlImcWaofPARcuhnivWCbl9tclUf3VRK91%2FK5Y%2Bo6tkQS%2BmVaVVpsoi69NbOiOpHg%3D%3D","third":"0","show_vip_ad":"0","task_key":"7bb1fbe6f17d267316e50b7b5a667cadf7322668","applystatus":"1","bt_paths":"null","source_entry_tip_message":"海量音频免费听","srv_ts":"1556178871","is_year_vip":"0","activity_status":"0","username":"回忆太多不好理"}
        String cookie1 = "STOKEN=565de320319f1b13fc719e3a1d2799c022df0b1c936d7a4185b538908b988c54;BDUSS=V4QzdBbnp-Q0MzWkkwRGhZc0o1Y0l4aU5-bWJyci1oVUo2b1RDVk9wTzI5T2hjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALZnwVy2Z8FcWT;PTOKEN=80e55275dc34e8f7e0a2f926edec4bc1;PASSID=hM23Ip;pan_login_way=1;SCRC=030f6b0cbc186f30564c7e13544261c9;PANWEB=1;BAIDUID=8ED1CE4C62C0E06EE24F2C48C19282CD:FG=1;UBI=fi_PncwhpxZ%7ETaJc8g7xRjRlup7Fiad8Hdy;PANPSC=7922364247481486854%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdubeobUk09rIr%2FiiUOqky53LvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D;";
        String cookie = "BAIDUID=D207C291E67971801E7FE9D08F63AE6B:FG=1; pan_login_way=1; PANWEB=1; PSTM=1555658433; BIDUPSID=0B722C79E86D9EF7813210F2E70E45BB; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; cflag=13%3A3; BDUSS=FVanUtOXlGeXF4Z1BYeDlySmtLNXl0eDE1cFNKcWdWYU5VOFNZd3NWR0xzT2hjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIsjwVyLI8FcS3; STOKEN=d7323e0e23dae5e11badee8d0660434b19dd94497780ed729b3e2bcaed9f5201; SCRC=4c58a08e1ca91618e71ef706ed5cde28; Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1555920882,1556161377,1556173198,1556181561; Hm_lpvt_7a3960b6f067eb0085b7f96ff5e660b0=1556181933; PANPSC=14812002289066230854%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdvqtMlO6xgkESA9YKyMd6JELvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D";

        String cookieLocal = "pcsett=; STOKEN=; BAIDUID=:FG=1; BDUSS=W94WmJ4Vnl-LXJ6c1BTRTUzMH5ETGttaHFzWHN0MDFJRDdsVzhBWDlSTmRBdWhjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF11wFxddcBcV; cflag=13%3A3; BIDUPSID=; PSTM=1556112058; ; delPer=0; PSINO=7; H_PS_PSSID=; BDORZ=" +
                "";


        PanCoreUtil.standard_cookie = cookie;
//        Map<String, String> mainHeader = PanCoreUtil.getMainHeader();
//        mainHeader.put("Host", "d.pcs.baidu.com");
//        mainHeader.put("Cookie", cookie);
//        mainHeader.put("Referer", "https://pan.baidu.com/box-static/base/thirdParty/music/_nomd5_nomod/dist/muplayer_mp3.swf?t=1556109560601");
//        mainHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
//        mainHeader.put("Upgrade-Insecure-Requests", "1");


        String sign = PanCoreUtil.getDownloadSign("d76e889b6aafd3087ac3bd56f4d4053a", "846e0770ccd5c85c893b30cf1a37beb730f6489e");
        List<Long> fidles=new ArrayList<>();
        fidles.add(526056080704766L);
        System.out.println(sign);
        String s = apiDownload(bdstoken, "elR4RXqKvqYEuZY9iZb1rXvRvmr7LpDZagYrOplk5RBLACFbrAe5Nw==", fidles, cookie1);
//        System.out.println(s);

//        Response request = PanCoreUtil.getRequest("https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-nsaRaczEaRWqpT%2Bzv72VRgvot4M%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2640748955180820523&dp-callid=0&dstime=1556109556&r=429654111&vip=0&ext=.mp3"
//                , "", null, mainHeader
//        );


        System.out.println(s);
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        Request request = new Request.Builder()
                .url("https://pan.baidu.com/api/download?channel=chunlei&web=1&app_id=250528&bdstoken="+bdstoken+"&logid=MTU1NjE4MjMzNzA2MTAuNjkxOTY3NTcwNTk1NjYzOA==&clienttype=0&startLogTime=&sign="+sign+"&timestamp=1556181933&fidlist=%5B526056080704766%5D&type=dlink&vip=0")
                .post(builder.build())
                .addHeader("Host", "pan.baidu.com")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("Origin", "https://pan.baidu.com")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.108 Safari/537.36")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Referer", "https://pan.baidu.com/disk/home")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Cookie", cookie1)
//                .addHeader("Cookie", "BAIDUID=D207C291E67971801E7FE9D08F63AE6B:FG=1; pan_login_way=1; PANWEB=1; PSTM=1555658433; BIDUPSID=0B722C79E86D9EF7813210F2E70E45BB; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; cflag=13%3A3; BDUSS=FVanUtOXlGeXF4Z1BYeDlySmtLNXl0eDE1cFNKcWdWYU5VOFNZd3NWR0xzT2hjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIsjwVyLI8FcS3; STOKEN=d7323e0e23dae5e11badee8d0660434b19dd94497780ed729b3e2bcaed9f5201; SCRC=4c58a08e1ca91618e71ef706ed5cde28; Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1555920882,1556161377,1556173198,1556181561; Hm_lpvt_7a3960b6f067eb0085b7f96ff5e660b0=1556181933; PANPSC=14812002289066230854%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdvqtMlO6xgkESA9YKyMd6JELvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "9a1b12f4-a1f6-46d6-83a5-dafcec0a5aa5")
                .build();

        Response response = client.newCall(request).execute();
        Headers respronseHeader = response.headers();
        boolean isGzip = false;
        //Content-Encoding: gzip
        for (String value : respronseHeader.values("Content-Encoding")) {
            if (value.equals("gzip")) {
                isGzip = true;
            }
        }
        String jsonStr="";
        if (isGzip) {
            InputStream is = response.body().byteStream();
            //gzip 解压数据
            GZIPInputStream gzipIn = new GZIPInputStream(is);
            jsonStr = streamToStr(gzipIn, "UTF-8");
        } else {
            jsonStr = response.body().string();
        }

//        System.out.println(jsonStr);
    }


}
