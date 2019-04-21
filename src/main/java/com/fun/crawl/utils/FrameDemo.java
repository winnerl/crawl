package com.fun.crawl.utils;

import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.model.Thumbs;
import com.fun.crawl.service.PanApiService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.fun.crawl.utils.PanCoreUtil.toMap;

class FrameDemo {
    //定义该图形中所需的组件引用
    private Frame f;
    private Button but;

    FrameDemo(Map<String, String> map) {
        init(map);
    }

    //初始化组件
    public void init(Map<String, String> map) {
        //对frame进行基本设置
        f = new Frame("my frame");
        f.setBounds(500, 300, 500, 500);
        f.setLayout(new FlowLayout());

        but = new Button("button");
        //将组件添加进窗体
//        f.add(but);
        ImageIcon imageIcon = null;


        try {
            imageIcon = new ImageIcon(new URL("https://" + map.get("imgurl")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(imageIcon.getIconHeight());

//        f.add(imageIcon);
        JLabel label1 = new JLabel(imageIcon);
        label1.setSize(500, 500);
        f.add(label1);
        //加载一下窗体上事件
        myEvent();
        //显示窗体
        f.setVisible(true);
    }

    //窗体事件
    private void myEvent() {
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //让按钮具备退出程序的功能
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("button 关闭的窗口");
                System.exit(0);
            }
        });
    }

//    public void SwingTestImg(){
//        try{
//
//            setVisible(true);
//            //A：网路URL图片
//			icon = new ImageIcon(new URL("http://tp1.sinaimg.cn/3223061260/180/5659068018/1"));
//            //B：项目目录下图片
////			InputStream is = SwingTestImg.class.getResourceAsStream("twodimensioncode.gif");
////			ByteArrayOutputStream baos = new ByteArrayOutputStream();
////			byte [] buff = new byte[100];
////			int readCount = 0;
////			while((readCount = is.read(buff,0,100)) > 0){
////				baos.write(buff,0,readCount);
////			}
////			byte [] inbyte = baos.toByteArray();
////			icon =  new ImageIcon(inbyte);
////			//C：本地磁盘图片，图片太大，会导致空白显示
////			image =  new ImageIcon("D:/1.png").getImage();
//            //D：代码生成的BufferedImage二维码图片
//
////		label.setIcon(new ImageIcon(image));
//    }

    public static void main(String[] args) {
        Map<String, String> codeSignAndCodeURL = PanCoreUtil.getCodeSignAndCodeURL(null);
        new FrameDemo(codeSignAndCodeURL);
        boolean isLoop = true;
        String sign = codeSignAndCodeURL.get("sign");
        String v3Bduss = "";
        //最好多线程
        while (isLoop) {
            try {
                Map<String, String> map = PanCoreUtil.vertifyCodeUnicast(sign);
                if (map.containsKey("channel_v")) {
                    String channel_v = map.get("channel_v");
                    Map<String, String> v3map = toMap(channel_v);
                    if ("0".equals(v3map.get("status"))) {
                        v3Bduss = v3map.get("v");
                        isLoop = false;
                    }
                }
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, String> v3map = PanCoreUtil.v3Login(v3Bduss, null);
        System.out.println(v3map);
        System.out.println("------cookeMap-----1");
        System.out.println(PanCoreUtil.standard_cookieMap);
//        System.out.println("------cookie-----");
//        System.out.println(PanCoreUtil.standard_cookie);


//        System.out.println("------------PAN----STOKEN-----");
        String URL = PanCoreUtil.v3LoginAuthGetToken(null);
        PanCoreUtil.diskHome();
//        System.out.println(stoken);
        System.out.println(PanCoreUtil.standard_cookieMap);


//
        PanCoreUtil.sendTodiskHomeOne(URL);
        System.out.println(PanCoreUtil.standard_cookieMap);
        Map<String, String> map = PanCoreUtil.sendTodiskHomeTwo();


        System.out.println(PanCoreUtil.standard_cookieMap);
//        System.out.println("---- HOU--cookeMap-----");
//        System.out.println(PanCoreUtil.standard_cookieMap);
        String bdstoken = map.get("bdstoken");
        List<FileExtend> time = PanApiService.list(bdstoken, 1, 1000, "/", "time", 1, 0, PanCoreUtil.standard_cookie);

    }

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);



    public  List<FileExtend> generciTree(Executor executor, List<FileExtend> fileExtends ){

        for (FileExtend fileExtend : fileExtends) {

            Callable<List> fileExtendsCallables=()->{


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



        return  fileExtends;

    }

}