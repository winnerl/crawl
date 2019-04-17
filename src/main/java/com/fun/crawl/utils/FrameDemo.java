package com.fun.crawl.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import static com.sun.glass.ui.Cursor.setVisible;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

class FrameDemo{
    //定义该图形中所需的组件引用
    private Frame f;
    private Button but;
    FrameDemo(){
        init();
    }
    //初始化组件
    public void init()   {
        //对frame进行基本设置
        f = new Frame("my frame");
        f.setBounds(500,300,500,500);
        f.setLayout(new FlowLayout());

        but = new Button("button");
        //将组件添加进窗体
//        f.add(but);
        ImageIcon imageIcon = null;
        try {

            new URL("https://passport.baidu.com/channel/unicast?channel_id=ae221bf5b34a87c256960f641559dc39&tpl=netdisk&gid=E2CB08F-B63E-4E07-9FF9-DACFFDD98976&callback=tangram_guid_1555514307408&apiver=v3&tt=1555514307772&_=1555514307773");
            imageIcon = new ImageIcon(new URL("https://passport.baidu.com/v2/api/qrcode?sign=ae221bf5b34a87c256960f641559dc39&uaonly=&client_id=&lp=pc&client=&qrloginfrom=pc&wechat=&traceid="));
            System.out.println( imageIcon.getIconHeight());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        f.add(imageIcon);
        JLabel label1 = new JLabel(imageIcon);
        label1.setSize(500,500);
         f.add(label1);
        //加载一下窗体上事件
        myEvent();
        //显示窗体
        f.setVisible(true);
    }

    //窗体事件
    private void myEvent(){
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }});
        //让按钮具备退出程序的功能
        but.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("button 关闭的窗口");
                System.exit(0);
            }});
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

    public static void main(String[] args)
    {
        new FrameDemo();
    }

}