package com.fun.crawl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.utils.dto.RecentRopotDto;
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
     * 获取下载路径第1部
     * 此时的sign必须要和时间相匹配才能获取
     *
     * @return
     */
    public static String apiDownloadURL(String bdsToken, String sign, List<Long> fidlist, String timestamp, String cookie) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("channel", "chunlei");
        parmsMap.put("web", "1");
        parmsMap.put("app_id", app_id);
        parmsMap.put("bdstoken", bdsToken);
        parmsMap.put("logid", "");
        parmsMap.put("clienttype", "0");
        parmsMap.put("startLogTime", timestamp + "000");
        parmsMap.put("fidlist", JSON.toJSONString(fidlist));
        parmsMap.put("sign", sign);
        parmsMap.put("timestamp", timestamp);
        parmsMap.put("type", "dlink");
        parmsMap.put("vip", "1");
        String getString = PanCoreUtil.mapToGetString(parmsMap, true);
        String res = PanCoreUtil.visit(PANHOST, "/api/download" + getString, null, "POST_STRING", cookie);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray dlinks = jsonObject.getJSONArray("dlink");
        JSONObject dlink = (JSONObject) dlinks.get(0);
        String dlinkUrl = dlink.getString("dlink");

        Map<String, String> mainHeader = new HashMap<>();

        mainHeader.put("Host","d.pcs.baidu.com");
        mainHeader.put("Accept","*/*");
        mainHeader.put("Accept-Encoding","identity;q=1, *;q=0");
        mainHeader.put("Range","bytes=0-");
        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        mainHeader.put("Cookie",cookie);
        mainHeader.put("Referer","https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=");
        mainHeader.put("Connection","keep-alive");
         dlinkUrl=dlinkUrl+"";

        String replace = dlinkUrl.replace("d.pcs.baidu.com", "nj02cm01.baidupcs.com");
        return replace;
    }

    /**
     * [{"type":3,"path":"/我的资源/刘惜君 - 我很快乐.mp3","fs_id":526056080704766,"category":2,"op_time":1556193152}]
     * 获取下载路径
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
        parmsMap.put("detail", JSON.toJSONString(detail));
        String getString = PanCoreUtil.mapToGetString(parmsMap, true);
        String res = PanCoreUtil.visit(PANHOST, "/recent/report" + getString, null, "POST_STRING", cookie);
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
        String bdstoken = "4801b9f16a5d7e22125671f960f5c0cc";
        //        {"flag":"1","vip_end_time":"null","file_list":"null","task_time":"1556194020","sign2":"function s(j,r){var a=[];var p=[];var o=\"\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};","sign1":"c77a725a26afb4e548d9d2492f95afe4c7f16778","sign3":"d76e889b6aafd3087ac3bd56f4d4053a","vol_autoup":"0","uk":"3754657732","is_auto_svip":"0","is_evip":"0","bdstoken":"4801b9f16a5d7e22125671f960f5c0cc","timestamp":"1556194020","is_svip":"0","activity_end_time":"0","sharedir":"0","pansuk":"3fXeHIPvSh8uUIsgkcDMmg","is_vip":"0","loginstate":"1","sampling":"{\"expvar\":[\"sampling_test\",\"disk_center_change\",\"video_high_speed\",\"disk_timeline\"]}","need_tips":"null","photo":"https:\/\/ss0.bdstatic.com\/7Ls0a8Sm1A5BphGlnYG\/sys\/portrait\/item\/c9df1bcf.jpg","timeline_status":"1","face_status":"0","curr_activity_code":"0","urlparam":"[]","token":"1fbbXehLBBgD8JBFHOeptL0MXtOAaln35YMHG3+01r61lDt9qWb3QXATmGqiJimw0mN06apMgJf8pZtI2bi66R8u2hHsfyS272XlUns9KnPl+7osMn5D9mxeohfrBsbGHIOB\/BFXBMtuiUzaoTecjcnQP651kdWoZT2mu4ifplUdrI\/4CznxRRPTorR2rd\/nyQ45r1Ul2wKWA9SmnMQG8UCZn+sjWE7pMzV1j+KUSHZSK5gK7cVoZH\/ysEQfyGemYj3L8p5y5y+Frzhtie7DfqHC2O6SyNbqQoA","XDUSS":"pansec_DCb740ccc5511e5e8fedcff06b081203-QYWOJ%2BB0lzSQp6EBXcDqs%2BYFsOLmOVSvThl0tOZQKYgHzUZKeMZ78IzebzNrwnn3MpogGBSFSQoC%2BdsuNd7L%2FYk%2F37eCEp7EfNHKA%2F%2BP35GnD3YRjYhtGKTx9NAXXbA5SM5rK3S1qHqxEK%2BhGy1jg%2B1a6p0e8CYXOU14hQf49EuIC41uVXayiU4ES4W9irV7jrW%2FUKz2bVN3Vzf77g2pwj9TjnQ9%2BVhpPDBe2VSY%2FXVVRXLJlkcVtOAqEg2KNKB1j3hTsdmU2pSiW0sLjEqq7Q%3D%3D","third":"0","show_vip_ad":"0","task_key":"f3bf6d1d907a3b3177a136ac369bdc0bf82f2a66","applystatus":"1","bt_paths":"null","source_entry_tip_message":"海量音频免费听","srv_ts":"1556194020","is_year_vip":"0","activity_status":"0","username":"回忆太多不好理"}
        String cookie = "STOKEN=e6ab0f2e6942525d623ebc31fedc055cacd9141d54087eac08f424e6e2137ed5;BDUSS=n45MUlmT1ZrQ1JOZzBQWkdlRlhhMnUxbjMxaTNFWnF2QlFPbmQwTVhKd0NUZWxjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALAwVwCwMFcd;PTOKEN=8da147ac8773d0b413c6a5f8009210b5;PASSID=zeq4WX;pan_login_way=1;SCRC=0062d30010af8818a94738f115ed385d;PANWEB=1;BAIDUID=F4E172B40F39C023475D90E8AFB7A2BC:FG=1;UBI=fi_PncwhpxZ%7ETaJc31S9Uf-Ii7QL9swGPny;PANPSC=14747608222208384837%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdv6NcxiVdG39CA9YKyMd6JELvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D;";

        PanCoreUtil.standard_cookie = cookie;
//        Map<String, String> mainHeader = PanCoreUtil.getMainHeader();
//        mainHeader.put("Host", "d.pcs.baidu.com");
//        mainHeader.put("Cookie", cookie);
//        mainHeader.put("Referer", "https://pan.baidu.com/box-static/base/thirdParty/music/_nomd5_nomod/dist/muplayer_mp3.swf?t=1556109560601");
//        mainHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
//        mainHeader.put("Upgrade-Insecure-Requests", "1");

        String sign = PanCoreUtil.getDownloadSign("d76e889b6aafd3087ac3bd56f4d4053a", "1718891bc06ee199cb68235eb326e29c8ce71a71");
        System.out.println(sign);
        List<RecentRopotDto> detail = new ArrayList<>();
        RecentRopotDto dto = new RecentRopotDto();
        //detail	[{"type":3,"path":"/我的资源/刘惜君 - 我很快乐.mp3","fs_id":526056080704766,"category":2,"op_time":1556190572}]
        dto.setFs_id(526056080704766L)
                .setPath("/我的资源/850.720p.mp4")
                .setCategory(1)
                .setType(3)
                .setOp_time(System.currentTimeMillis() / 1000);

        detail.add(dto);
        List<Long> fidles = new ArrayList<>();
        fidles.add(477589954217336l);
//        String timst = apiDownloadRecentReport(bdstoken, detail, cookie1);
        String a = "1556193143";
        String s = apiDownloadURL(bdstoken, sign, fidles, 1556201475 + "", cookie);
//        fs_id=
        System.out.println(s);

//        Response request = PanCoreUtil.getRequest("https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-nsaRaczEaRWqpT%2Bzv72VRgvot4M%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2640748955180820523&dp-callid=0&dstime=1556109556&r=429654111&vip=0&ext=.mp3"
//                , "", null, mainHeader
//        );

//        System.out.println(jsonStr);

    }
//    https://nj02cm01.baidupcs.com/file/fb455e5b8696b63e1840344fc5206ca6?fid=3754657732-250528-469577228126982&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-dqp7aidKrDglAKwHLXF6JH8Pd9I%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2665192954560875166&dp-callid=0&dstime=1556200617&r=792513992&vip=1&ext=.mp3
}
