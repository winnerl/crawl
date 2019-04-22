package com.fun.crawl.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.utils.PanCoreUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
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
        List<FileExtend> jsStr = JSON.parseArray(JSONObject.parseObject(jsonStr).getString("list"), FileExtend.class); //将字符串{“id”：1}
        return jsStr;
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
        int pageSize = 1000;
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

    public List<FileExtend> generciTree(Executor executor, List<FileExtend> fileExtends) {

        for (FileExtend fileExtend : fileExtends) {

            Callable<List> fileExtendsCallables = () -> {


                return null;
            };
            FutureTask<List> fileExtendsListTask = new FutureTask<>(fileExtendsCallables);

            try {
                fileExtendsListTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            executorService.submit(fileExtendsListTask);

        }
        return fileExtends;
    }


    public static List<FileExtend> generciTreeNoThread(List<FileExtend> fileExtends, String pathName, String append, String bdstoken) {
        append = "    " + append;
        for (FileExtend fileExtend : fileExtends) {


            //是否目录  ,且目录不为空
            String name = fileExtend.getServer_filename();
            pathName = fileExtend.getPath();
            System.out.println(append + name);
            if (fileExtend.getIsdir().equals(1L) && fileExtend.getDir_empty().equals(0L)) {
                List<FileExtend> secondlist = new ArrayList<>();
                secondlist = PanApiService.listAll(bdstoken, pathName, "time", 1, 0, PanCoreUtil.standard_cookie);
                if (CollectionUtils.isNotEmpty(secondlist)) {
                    generciTreeNoThread(secondlist, pathName, append, bdstoken);
                }
            }
        }

        return fileExtends;
    }

    public static long id = 1;
    public static List<FileExtend> generciTreeJSON(List<FileExtend> fileExtends, String pathName, long parentId, String bdstoken, List<FileExtend> allTree) {
        for (FileExtend fileExtend : fileExtends) {
            fileExtend.setParent_id(parentId);
            fileExtend.setId(id++);
            //重新添加到新的集合中
            allTree.add(fileExtend);
            String name = fileExtend.getServer_filename();
            String path_Name = fileExtend.getPath();
            if (fileExtend.getIsdir().equals(1L) && fileExtend.getDir_empty().equals(0L)) {
                List<FileExtend> secondlist = new ArrayList<>();
                secondlist = PanApiService.listAll(bdstoken, path_Name, "time", 1, 0, PanCoreUtil.standard_cookie);
                if (CollectionUtils.isNotEmpty(secondlist)) {
                    generciTreeJSON(secondlist, path_Name, fileExtend.getId(), bdstoken, allTree);
                }
            }
        }
        return allTree;
    }

    public static List<FileExtend> generciTreeJSONThread(List<FileExtend> fileExtends, String pathName, long parentId, String bdstoken, List<FileExtend> allTree) {

        for (FileExtend fileExtend : fileExtends) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    fileExtend.setParent_id(parentId);
                    fileExtend.setId(id++);
                    //重新添加到新的集合中
                    allTree.add(fileExtend);
                    String name = fileExtend.getServer_filename();
                    String path_Name = fileExtend.getPath();
                    if (fileExtend.getIsdir().equals(1L) && fileExtend.getDir_empty().equals(0L)) {
                        List<FileExtend> secondlist = new ArrayList<>();
                        secondlist = PanApiService.listAll(bdstoken, path_Name, "time", 1, 0, PanCoreUtil.standard_cookie);
                        if (CollectionUtils.isNotEmpty(secondlist)) {
                            generciTreeJSON(secondlist, path_Name, fileExtend.getId(), bdstoken, allTree);
                        }
                    }
                }
            });
        }
        return allTree;
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
        String bdstoken = "7b6c3dff1531ad59d5432884df8cc254";
        String cookie = "STOKEN=6071f2c1389267d05bdc009b48bc55500edb2a12e4dcfad2785a66a8bba23db9;BDUSS=ozMUMtUk9PNldtT3R3RGlHdVZlcThVRTQtRGdEN0tmNGNDSHZ3MzhZak05T1JjSVFBQUFBJCQAAAAAAAAAAAEAAADI-sctu9jS5MyrtuDIxrK7wcsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMxnvVzMZ71cak;PTOKEN=fdf6c05b2c69578a43931d03f04cf779;PASSID=PkT5p4;pan_login_way=1;SCRC=4f5925e0a9b0514be6abff368130e0bb;PANWEB=1;BAIDUID=4C4F6C65F2F552B1F2C2F3358FBEEDF6:FG=1;UBI=fi_PncwhpxZ%7ETaJc7NYWvbnTO1J7%7EnDxIRq;PANPSC=17568544890574622578%3ACQw3%2BDxyk2BRkgvSzOz9dJgn1M6s6PFhIxDGQGgSurphs5%2FZj17TVSKbQDGpKep%2B9n0Zo7M8Q56fpIvAqVETj9R028b7i%2B2HmldUB8t5cdsrCTCyp51uP3qD0KGUduysGiOBsXB2oU0vuXRe4orLfC78Y3XhlnteBrU3YVu%2FDjscT50Qvhq4xWPLhvADPmNCIermaD9w8EN3j5cyRAFOLeKEQjOY60ydAQwoaZBLaos%3D;";


        PanCoreUtil.standard_cookie = cookie;
        List<FileExtend> time = PanApiService.list(bdstoken, 1, 500, "/", "time", 1, 0, cookie);
        List<FileExtend> listTree = new ArrayList<>();//用来存放数据
        listTree = generciTreeJSON(time, "", 0, bdstoken, listTree);

        Object o = JSON.toJSON(listTree);
        boolean json = creatTxtFile("json");


        boolean b = writeTxtFile(o.toString());
        System.out.println(o.toString());

    }


}
