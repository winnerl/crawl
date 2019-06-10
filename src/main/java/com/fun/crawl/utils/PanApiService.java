package com.fun.crawl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.utils.dto.Garbageguid;
import com.fun.crawl.utils.dto.RecentRopotDto;
import com.fun.crawl.utils.dto.ShareDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//@Component
public class PanApiService {
    //    https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-SkOwJ8vE4bPxcLQwikRzZ0PiXhc%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2663604430278993193&dp-callid=0&dstime=1556194700&r=862685583&vip=0&ext=.mp3
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
        String jsonStr = PanCoreUtil.visit(PANHOST, "/api/list", parmsMap, "GET", cookie, null);
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            //请求失败再次请求下
            Integer errno = jsonObject.getInteger("errno");
            if (errno != null && errno.intValue() == 1) {
                jsonStr = PanCoreUtil.visit(PANHOST, "/api/list", parmsMap, "GET", cookie, null);
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
     * @return
     */
    public static String getVideoAdToken(String bdsToken, String path, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("path", path);
        parmsMap.put("type", "M3U8_FLV_264_480");
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");
        parmsMap.put("startLogTime", "");
        parmsMap.put("vip", "1");
        String res = PanCoreUtil.visit(PANHOST, "/api/streaming", parmsMap, "GET", cookie, null);
        JSONObject jsonObject = JSONObject.parseObject(res);
        String adtoken = jsonObject.getString("adToken");
        return adtoken;
    }

    /**
     * 创建分享链接
     * <p>
     * period 有效期  0 代表永久 其他数字代表天数
     *
     * @return
     */
    public static ShareDto shrioUrl(String bdsToken, int period, List<Long> fidlist, String cookie) {
        String pwd = PanCoreUtil.makePrivatePassword();
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");

        String getString = PanCoreUtil.mapToGetString(parmsMap, false);

        Map<String, String> postMap = new HashMap<>();
        postMap.put("schannel", "4");
        postMap.put("channel_list", JSON.toJSONString(new ArrayList<>()));
        postMap.put("fid_list", JSON.toJSONString(fidlist));
        postMap.put("period", "" + period);
        postMap.put("pwd", pwd);

        Map<String, String> Headmap = PanCoreUtil.xmlHttpHead();
        Headmap.put("Host", "pan.baidu.com");
        Headmap.put("Referer", "https://pan.baidu.com/disk/home");
        Headmap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        Headmap.put("Accept", "application/json, text/javascript, */*; q=0.01");
        Headmap.put("Accept-Language", "zh-CN,zh;q=0.9");
        Headmap.put("X-Requested-With", "XMLHttpRequest");
        Headmap.put("Accept-Encoding", "gzip, deflate, br");
        Headmap.put("Connection", "keep-alive");
        Headmap.put("Origin", "https://pan.baidu.com");
        Headmap.put("cache-control", "no-cache");
        Headmap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        String res = PanCoreUtil.visit(PANHOST, "/share/set" + getString, postMap, "POST_PARM", cookie, null);
        JSONObject jsonObject = JSONObject.parseObject(res);
        String link = jsonObject.getString("link");
        int expiredType = jsonObject.getIntValue("expiredType");
        ShareDto shareDto = new ShareDto();
        shareDto.setLink(link);
        shareDto.setPwd(pwd);
        shareDto.setExpiredType(expiredType);
        return shareDto;
    }

    /**
     * 获取视频播放流....
     *
     * @return
     */
    public static String getVideoStream(String adToken, String path, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("path", path);
        parmsMap.put("type", "M3U8_FLV_264_480");
        parmsMap.put("app_id", app_id);
        parmsMap.put("adToken", adToken);
        parmsMap.put("clienttype", "0");
        parmsMap.put("vip", "1");
        String res = PanCoreUtil.visit(PANHOST, "/api/streaming", parmsMap, "GET", cookie, null);
        return res;
    }


    /**
     * 获取下载前验证MD5 文件
     * 此时的sign必须要和时间相匹配才能获取
     *
     * @return
     */
    public static String garbageguid(String bdsToken, List<Garbageguid> fileList, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");

        Map<String, String> postMap = new HashMap<>();
        postMap.put("filelist", JSON.toJSONString(fileList));
        String getString = PanCoreUtil.mapToGetString(parmsMap, true);
        String res = PanCoreUtil.visit(PANHOST, "/api/garbageguid" + getString, postMap, "POST_PARM", cookie, null);
        return res;
    }

    /**
     * 获取下载路径
     * 此时的sign必须要和时间相匹配才能获取
     *
     * @return
     */
    public static String apiDownload(String bdsToken, String sign, List<Long> fidlist, String timestamp, String cookie) {

        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");
        parmsMap.put("startLogTime", timestamp + "000");

        Map<String, String> postMap = new HashMap<>();
        postMap.put("fidlist", JSON.toJSONString(fidlist));
        postMap.put("sign", sign);
        postMap.put("timestamp", timestamp);
        postMap.put("type", "dlink");
        postMap.put("vip", "1");
        String getString = PanCoreUtil.mapToGetString(parmsMap, true);
        String res = PanCoreUtil.visit(PANHOST, "/api/download" + getString, postMap, "POST_PARM", cookie, null);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray dlinks = jsonObject.getJSONArray("dlink");
        JSONObject dlink = (JSONObject) dlinks.get(0);
        String dlinkUrl = dlink.getString("dlink");

//        String replace = dlinkUrl.replace("d.pcs.baidu.com", "nj02cm01.baidupcs.com");
        Map<String, String> mainHeader = new HashMap<>();
        mainHeader.put("Host", "d.pcs.baidu.com");
        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        mainHeader.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        mainHeader.put("Accept-Encoding", "gzip, deflate, br");
        mainHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        mainHeader.put("Referer", "https://pan.baidu.com/disk/home?");
        mainHeader.put("Connection", "keep-alive");
        mainHeader.put("Range", "bytes=0-");
        mainHeader.put("Upgrade-Insecure-Requests", "1");

        Map<String, String> mapa = new HashMap<>();
        String[] split1 = cookie.split(";");
        for (int i = 0; i < split1.length; i++) {
            String s1 = split1[i];
            String[] split2 = s1.split("=");
            if (split2[0].contains("BDUSS")) {
                mapa.put(split2[0], split2[1]);
            }
            if (split2[0].contains("BAIDUID")) {
                mapa.put(split2[0], split2[1]);
            }
        }

        String tempcookie = "";
        Set<String> ks = mapa.keySet();
        Iterator<String> it = ks.iterator();
        while (it.hasNext()) {
            String skey = it.next();
            String value = mapa.get(skey);
            tempcookie += skey + "=" + value + ";";
        }


        Request request = new Request.Builder()
                .url(dlinkUrl).get()
                .addHeader("Host", "d.pcs.baidu.com")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")
                .addHeader("Cookie", tempcookie)
//                .addHeader("Cookie", "BAIDUID=3BC9C99DEBC6D4D057FA432088582AC4:FG=1; BDUSS=1NDOWpBNWU3QUdqRkNqWlY1ODV6RjRteG1CWG9kdlp5NFB4c0VjVkl1a1BSfnRjSVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-601wPutNcb")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            Response priorResponse = response.priorResponse();
            if (priorResponse == null) {
                return "";
            }
            String location = priorResponse.header("Location");
            return location;
        } catch (IOException e) {

        }

        return "";
    }

    /**
     * 获取下载路径第1部
     * 此时的sign必须要和时间相匹配才能获取
     *
     * @return
     */
    public static String apiDownloadMp3(String bdsToken, String sign, List<Long> fidlist, String timestamp, String cookie) {
//
//        String sss=System.currentTimeMillis()/1000+"";
//        Map<String, String> asMap = new HashMap<>();
//        asMap.put("channel", "chunlei");
//        asMap.put("method", "listhost");
//        asMap.put("t", System.currentTimeMillis()/1000+"");
//        asMap.put("_", sss);
//        asMap.put("clienttype", "0");
//        asMap.put("callback", "jQuery112403285207257949123_" + sss);
//        String aString = PanCoreUtil.mapToGetString(asMap, true);
//
//        Map<String, String> mapaa = new HashMap<>();
//        String[] AA = cookie.split(";");
//        for (int i = 0; i < AA.length; i++) {
//            String s1 = AA[i];
//            String[] split2 = s1.split("=");
//            if (!split2.equals("STOKEN")) {
//                if (split2[0].contains("FILE_")) {
//                    mapaa.put(split2[0].replace("FILE_", ""), split2[1]);
//                } else {
//                    mapaa.put(split2[0], split2[1]);
//                }
//            }
//        }
//        String tempAAAcookie = "";
//        Set<String> kAs = mapaa.keySet();
//        Iterator<String> iAt = kAs.iterator();
//        while (iAt.hasNext()) {
//            String skey = iAt.next();
//            String value = mapaa.get(skey);
//            tempAAAcookie += skey + "=" + value + ";";
//        }
//
//                Map<String, String> mainaaHeader = new HashMap<>();
//        mainaaHeader.put("Host", "d.pcs.baidu.com");
//        mainaaHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//        mainaaHeader.put("Accept", " */*");
//        mainaaHeader.put("Accept-Encoding", "gzip, deflate, br");
//        mainaaHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
//        mainaaHeader.put("Referer", "https://pan.baidu.com/disk/home?");
//        mainaaHeader.put("Connection", "keep-alive");
//
//        String get = PanCoreUtil.visit("https://d.pcs.baidu.com", "/rest/2.0/pcs/manage" + aString, null, "GET", tempAAAcookie, mainaaHeader);
//        System.out.println(get);
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");
        parmsMap.put("startLogTime", timestamp + "000");

        Map<String, String> postMap = new HashMap<>();
        postMap.put("fidlist", JSON.toJSONString(fidlist));
        postMap.put("sign", sign);
        postMap.put("timestamp", timestamp);
        postMap.put("type", "dlink");
        postMap.put("vip", "1");
        String getString = PanCoreUtil.mapToGetString(parmsMap, true);
        String res = PanCoreUtil.visit(PANHOST, "/api/download" + getString, postMap, "POST_PARM", cookie, null);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray dlinks = jsonObject.getJSONArray("dlink");
        JSONObject dlink = (JSONObject) dlinks.get(0);
        String dlinkUrl = dlink.getString("dlink");

        String replace = dlinkUrl.replace("d.pcs.baidu.com", "nj02cm01.baidupcs.com");
//        Map<String, String> mainHeader = new HashMap<>();
//        mainHeader.put("chrome-proxy", "frfr");
//        mainHeader.put("Host", "d.pcs.baidu.com");
//        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//        mainHeader.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        mainHeader.put("Accept-Encoding", "gzip, deflate, br");
//        mainHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
//        mainHeader.put("Referer", "https://pan.baidu.com/disk/home?");
//        mainHeader.put("Connection", "keep-alive");
//        mainHeader.put("Range", "bytes=0-");
//        mainHeader.put("Upgrade-Insecure-Requests", "1");
//
//        Map<String, String> mapa = new HashMap<>();
//        String[] split1 = cookie.split(";");
//        for (int i = 0; i < split1.length; i++) {
//            String s1 = split1[i];
//            String[] split2 = s1.split("=");
//            if (!split2.equals("STOKEN")) {
//                if (split2[0].contains("FILE_")) {
//                    mapa.put(split2[0].replace("FILE_", ""), "");
//                } else {
//                    mapa.put(split2[0], split2[1]);
//                }
//            }
//        }
//        String tempcookie = "";
//        Set<String> ks = mapa.keySet();
//        Iterator<String> it = ks.iterator();
//        while (it.hasNext()) {
//            String skey = it.next();
//            String value = mapa.get(skey);
//            tempcookie += skey + "=" + value + ";";
//        }
//
//
//        Request request = new Request.Builder()
//                .url(dlinkUrl).get()
//                .addHeader("Host", "d.pcs.baidu.com")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0")
//                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
//                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
//                .addHeader("Accept-Encoding", "gzip, deflate, br")
//                .addHeader("Referer", "https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=")
//                .addHeader("Connection", "keep-alive")
////                .addHeader("Cookie", tempcookie
//                .addHeader("Cookie", "BDUSS=WN5dEtCTExlbzY3SUlKc3Z-NVNaUUZ1U353V1MtQzUwQ1Z2YW9Yb1RlLW0tLXRjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKZuxFymbsRcY;")
//
//                .addHeader("Upgrade-Insecure-Requests", "1")
//                .build();
//        try {
//            OkHttpClient client = new OkHttpClient();
//            Response response = client.newCall(request).execute();
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//        }

        return replace;
    }

    /**
     * [{"type":3,"path":"/我的资源/刘惜君 - 我很快乐.mp3","fs_id":526056080704766,"category":2,"op_time":1556193152}]
     *
     * @return
     */
    public static String apiDownloadRecentReport(String bdsToken, List<RecentRopotDto> detail, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");

        Map<String, String> postMap = new HashMap<>();
        postMap.put("detail", JSON.toJSONString(detail));
        String getString = PanCoreUtil.mapToGetString(parmsMap, true);
        String res = PanCoreUtil.visit(PANHOST, "/recent/report" + getString, postMap, "POST_PARM", cookie, null);
        System.out.println(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        return System.currentTimeMillis() / 1000 + "";
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
        String bdstoken = "c728d3ca6e611595dea2482b565e7419";
//        {"flag":"1","vip_end_time":"null","file_list":"null","task_time":"1556454381","sign2":"function s(j,r){var a=[];var p=[];var o=\"\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};","sign1":"000fcaf8ddc345d9701a675f1af6d825db420c12","sign3":"d76e889b6aafd3087ac3bd56f4d4053a","vol_autoup":"0","uk":"3754657732","is_auto_svip":"0","is_evip":"0","bdstoken":"c728d3ca6e611595dea2482b565e7419","timestamp":"1556454381","is_svip":"0","activity_end_time":"0","sharedir":"1","pansuk":"3fXeHIPvSh8uUIsgkcDMmg","is_vip":"0","loginstate":"1","sampling":"{\"expvar\":[\"sampling_test\",\"disk_center_change\",\"video_high_speed\",\"disk_timeline\"]}","need_tips":"null","photo":"https:\/\/ss0.bdstatic.com\/7Ls0a8Sm1A5BphGlnYG\/sys\/portrait\/item\/c9df1bcf.jpg","timeline_status":"1","face_status":"0","curr_activity_code":"0","urlparam":"[]","token":"63cc4VT\/nys5zFXDlvKjhXe\/AyFcQYOp0RxgKJeMJ9DiActmYxu\/OQiQNS+M2SlZyrHyTD85DUo2vS4CV3IQ+qVl6qkNh6iyUGe+c85\/oKMwEGjMEsa2X5Ud7wDWXumrrlCwxLrdx16VY5iapIPxZP0j1zwgVjRZGQvC4KJstGj+6YvpbUSrWkTHqYlbQmecBzsScgxh7GXJxJmuhDh5TDvpZnfMpH22WOxLQ5qurQ+MHVlh+iYDCpRKNRZTwvrvrwleDTM+b3bUtaAkGElChPAikp\/vsRhh","XDUSS":"pansec_DCb740ccc5511e5e8fedcff06b081203-EMSYx%2FtAuBSKIp6WfL6WPPsERLnln0THbB9kGqyU4sqppCC2MP4TcU%2BDAMW4yk69q3%2FwPQil01ajqN%2BMZ4J71GkS4lVpCzcEfUIxxizbI2r6tdNbVkalH45SQ14synys0WTOJScpV58DpC6D77ulYMzgDfWkMHWJNqW4IDVXyT%2FUPEsyEqGLz1reFvYVNbVLR9OfzSDNmzzqZVaX09GFK1I9uGxrC5ZPM6Hz2qSM7L2D1aPYXgvtyEmibas4MLXnLyGAdtJIzUZ7A1F9fESDPw%3D%3D","third":"0","show_vip_ad":"0","task_key":"cc9ecab7dc492082bb170c0ddf0a8bee7ebb0d28","applystatus":"1","bt_paths":"null","source_entry_tip_message":"海量音频免费听","srv_ts":"1556454381","is_year_vip":"0","activity_status":"0","username":"回忆太多不好理"}


        String cookie = "STOKEN=2a102e6500383e74fd2adea79851f948a41a12b967344726f69530ab73a9670b;PTOKEN=2f03059afaca086c3fffd37ecde9eb47;pcsett=1556540781-1c7c305e486f45218166b32058405a94;pan_login_way=1;SCRC=e2f47b2c1dca501d31097b32c374593f;PANWEB=1;BDUSS=DJzVWRYV3RELVI0OWF3c2YwNm5hMEFRVWI5elc0VjljQWRnd2ZNMXRETHJLTzFjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOubxVzrm8VcU;PASSID=2P72DJ;FILE_STOKEN=2a102e6500383e74fd2adea79851f948a41a12b967344726f69530ab73a9670b;BAIDUID=1AB7DE19E7F69710207B142820B1662D:FG=1;UBI=fi_PncwhpxZ%7ETaJc5TOqqRxC6QC785vNWh4;PANPSC=7630149378413235739%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdtpvcNeNLk7Pb%2FiiUOqky53LvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D;";


        PanCoreUtil.standard_cookie = cookie;
//        Map<String, String> mainHeader = PanCoreUtil.getMainHeader();
//        mainHeader.put("Host", "d.pcs.baidu.com");
//        mainHeader.put("Cookie", cookie);
//        mainHeader.put("Referer", "https://pan.baidu.com/box-static/base/thirdParty/music/_nomd5_nomod/dist/muplayer_mp3.swf?t=1556109560601");
//        mainHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
//        mainHeader.put("Upgrade-Insecure-Requests", "1");

        String sign = PanCoreUtil.getDownloadSign("d76e889b6aafd3087ac3bd56f4d4053a", "000fcaf8ddc345d9701a675f1af6d825db420c12");
        System.out.println(sign);
        //detail	[{"type":3,"path":"/我的资源/刘惜君 - 我很快乐.mp3","fs_id":526056080704766,"category":2,"op_time":1556190572}]
        List<Long> fidles = new ArrayList<>();
//        fidles.add(422493994346459L);//[422493994346459,526056080704766]
        fidles.add(526056080704766L);//[422493994346459,526056080704766]
//        String timst = apiDownloadRecentReport(bdstoken, detail, cookie1);

        String xduss = "pansec_DCb740ccc5511e5e8fedcff06b081203-EMSYx%2FtAuBSKIp6WfL6WPPsERLnln0THbB9kGqyU4sqppCC2MP4TcU%2BDAMW4yk69q3%2FwPQil01ajqN%2BMZ4J71GkS4lVpCzcEfUIxxizbI2r6tdNbVkalH45SQ14synys0WTOJScpV58DpC6D77ulYMzgDfWkMHWJNqW4IDVXyT%2FUPEsyEqGLz1reFvYVNbVLR9OfzSDNmzzqZVaX09GFK1I9uGxrC5ZPM6Hz2qSM7L2D1aPYXgvtyEmibas4MLXnLyGAdtJIzUZ7A1F9fESDPw%3D%3D";
        PanCoreUtil.XDUSS(xduss, null);
        String a = "1556454381";
        String s = apiDownloadMp3(bdstoken, sign, fidles, 1556454381 + "", cookie);
//        fs_id=
        System.out.println(s);
        String path = "/我的资源/34.720p.mp4";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
//                .url("https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-SEOxdVdftgYI6OEhI2o9JGG58Sk%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2712727659714700561&dp-callid=0&dstime=1556377698&r=636903735&vip=0").get()
                .url(s)
                .addHeader("Host", "d.pcs.baidu.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Referer", "https://pan.baidu.com/disk/home?")
                .addHeader("Connection", "keep-alive")
//                .addHeader("Cookie", cookie)
                //STOKEN=6d09a826797af1cfdce2d9484e70afa4e28643a07317a795429477bd233fe338;BDUSS=pMOFNpS1RadndFYnVkT0p0NTZ0TTJRcGstaWR5VVdFQXJ4b3FHN2hRTWhET3hjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACF~xFwhf8RcWV;BAIDUID=8CF4DFF293BBC0AC80C84A70AF32AF75:FG;
                .addHeader("Cookie", "BDUSS=WN5dEtCTExlbzY3SUlKc3Z-NVNaUUZ1U353V1MtQzUwQ1Z2YW9Yb1RlLW0tLXRjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKZuxFymbsRcY;")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "7439d2ea-49c6-45c2-a145-71aef8e6e05a")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response);

//        String videoAdToken = getVideoAdToken(bdstoken, path, cookie);

//        String videoStream = getVideoStream(videoAdToken, path, cookie);
//        ShareDto shareDto = shrioUrl(bdstoken, 0, fidles, cookie);
//        System.out.println(shareDto);
//        System.out.printf(videoStream);
//        Response request = PanCoreUtil.getRequest("https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-nsaRaczEaRWqpT%2Bzv72VRgvot4M%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2640748955180820523&dp-callid=0&dstime=1556109556&r=429654111&vip=0&ext=.mp3"
//                , "", null, mainHeader
//        );

//        System.out.println(jsonStr);

    }
//    https://nj02cm01.baidupcs.com/file/fb455e5b8696b63e1840344fc52
//
//
//
//
//
//
//
//
// 06ca6?fid=3754657732-250528-469577228126982&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-dqp7aidKrDglAKwHLXF6JH8Pd9I%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2665192954560875166&dp-callid=0&dstime=1556200617&r=792513992&vip=1&ext=.mp3

}
