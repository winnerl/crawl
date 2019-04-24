package com.fun.crawl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.FileExtend;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
            if (errno!=null&&errno.intValue()==1){
                jsonStr = PanCoreUtil.visit(PANHOST, "/api/list", parmsMap, "GET", cookie);
                jsonObject = JSONObject.parseObject(jsonStr);
            }

            List<FileExtend> jsStr = JSON.parseArray(jsonObject.getString("list"), FileExtend.class); //将字符串{“id”：1}
            return jsStr;
        }catch (Exception e){
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
        String bdstoken = "762f47ee51eae09de19580bedfdbc3f8";
//{"flag":"1","vip_end_time":"null","file_list":"null","task_time":"1556110658","sign2":"function s(j,r){var a=[];var p=[];var o=\"\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};","sign1":"65d557bc9673bfd5fad2ac7b3931c92e1fd055ba","sign3":"d76e889b6aafd3087ac3bd56f4d4053a","vol_autoup":"0","uk":"3754657732","is_auto_svip":"0","is_evip":"0","bdstoken":"762f47ee51eae09de19580bedfdbc3f8","timestamp":"1556110658","is_svip":"0","activity_end_time":"0","sharedir":"0","pansuk":"3fXeHIPvSh8uUIsgkcDMmg","is_vip":"0","loginstate":"1","sampling":"{\"expvar\":[\"company_pub\",\"sampling_test\",\"disk_center_change\",\"video_high_speed\",\"disk_timeline\"]}","need_tips":"null","photo":"https:\/\/ss0.bdstatic.com\/7Ls0a8Sm1A5BphGlnYG\/sys\/portrait\/item\/c9df1bcf.jpg","timeline_status":"1","face_status":"0","curr_activity_code":"0","urlparam":"[]","token":"06a4Dtu0Ck6K8xHuLzwohiURe6tzF5wj1y6n+TzyM5wEKEVLagvKABeHN8USiYx422Z\/ryh2Tuxbmk6yVuPzo\/REMqP343Mw0k+9lNIUbQ1YOiPGF8e2mW4n9o32XTMK9VrTGGTgM\/qS9fmZc0uoHo\/OY3OgdnzVBz4zFKmOAk4PeYGp2aLUP\/hsLPtLHsNucE4yRhK80jkMloAD9twrKrG5NGJxm\/07BZDnPmE8KiBCd63AEQRs7c6P75YCmSPiL22sY30TeDjR9PYnDIlMz8B0Ep14cUiuqqk","XDUSS":"pansec_DCb740ccc5511e5e8fedcff06b081203-Gv1eI772krMQbWySOWOa0%2BFsurt3zL6Dx1wk0CBXK3QCkIL9FF62z41ncvkHLJoYceM5BBCQ7eDsTRoccbEfTZFou80Ib4vzu24Mcnrn0zZoSKESVoLoXFIKICroEtukV12447ksb9SW5uF5qars1WRhUCFCYz5q5JYQkWHUEP3cBER5hT8FRsPuChiE%2BHDtr8sKFpjc%2B2aqRWlfC9JKeNxQGvKYD1sJumncv8oEukTz%2BmwwFEmpte7tuDUE3ZzTkgKHTqweYfwfEb%2Bc9py8pg%3D%3D","third":"0","show_vip_ad":"0","task_key":"896a8c4d6b2f441558d5749501275db3cc1defb3","applystatus":"1","bt_paths":"null","source_entry_tip_message":"海量音频免费听","srv_ts":"1556110658","is_year_vip":"0","activity_status":"0","username":"回忆太多不好理"}
//        String cookie = "STOKEN=94033d6dbb41b30916bcb7bfdbfedbd332da10ce6c9d4d070a9ccce8bbc92725;BDUSS=2cyV2dRdHo3MDFIVk1IN1JHU2xxeklXbjRMb0g4WW1TaEJqZFZQMmYwSUtCLWhjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAp6wFwKesBcN;PTOKEN=d7744d44ad3cb778ba3cde6f2fc1946e;PASSID=rVKWGK;pan_login_way=1;SCRC=710b862f7b679cf0837c9c4f0ffa1586;PANWEB=1;BAIDUID=B53094540A6B275D066B042AAF8BB803:FG=1;UBI=fi_PncwhpxZ%7ETaJc3TP4RRUaOGXUSMc%7EW-m;PANPSC=3597290008676799715%3AQsaf43VL%2Ft4Nqu6Hm%2FZfKJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2Bcpe3QKXhtMijDaTuwfy3xNR028b7i%2B2HmldUB8t5cdskwB6OGEKN0fEZy5xVYxZqLvxjdeGWe16DTmdSEuVx3i%2B37N4rR16QY8uG8AM%2BY0Ih6uZoP3DwQ3ePlzJEAU4t4oRCM5jrTJ0BDChpkEtqiw%3D%3D; ";
                String cookie = "pcsett=; STOKEN=; BAIDUID=:FG=1; BDUSS=W94WmJ4Vnl-LXJ6c1BTRTUzMH5ETGttaHFzWHN0MDFJRDdsVzhBWDlSTmRBdWhjRVFBQUFBJCQAAAAAAAAAAAEAAADJ3xvPu9jS5MyrtuCyu7rDwO0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF11wFxddcBcV; cflag=13%3A3; BIDUPSID=; PSTM=1556112058; ; delPer=0; PSINO=7; H_PS_PSSID=; BDORZ=" +
                "";
        PanCoreUtil.standard_cookie = cookie;
        Map<String, String> mainHeader = PanCoreUtil.getMainHeader();
        mainHeader.put("Host","d.pcs.baidu.com");
        mainHeader.put("Cookie",cookie);
        mainHeader.put("Referer","https://pan.baidu.com/box-static/base/thirdParty/music/_nomd5_nomod/dist/muplayer_mp3.swf?t=1556109560601");
        mainHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        mainHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
        mainHeader.put("Upgrade-Insecure-Requests", "1");

        Response request = PanCoreUtil.getRequest("https://d.pcs.baidu.com/file/c6dfef6ba983b4efebc82091b76453cb?fid=3754657732-250528-526056080704766&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-nsaRaczEaRWqpT%2Bzv72VRgvot4M%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=2640748955180820523&dp-callid=0&dstime=1556109556&r=429654111&vip=0&ext=.mp3"
                , "", null, mainHeader
        );
        System.out.println(request.headers());
    }


}
