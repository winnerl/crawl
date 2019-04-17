package com.fun.crawl.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
public class Demo4 extends JFrame {
    MyJPanel mp;
    int index;
    ImageIcon[] imgs;

    {
        try {
            imgs = new ImageIcon[]{
                        new ImageIcon(new URL("http://tp1.sinaimg.cn/3223061260/180/5659068018/1"))
                };
        } catch (MalformedURLException e) {
        }
    }

    public Demo4() {
        mp = new MyJPanel();
        this.add(mp);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("窗口");
        this.setVisible(true);
        Timer timer = new Timer(500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mp.repaint();
            }
        });
        timer.start();
    }
    public static void main(String[] args) {
        new Demo4();
    }
    class MyJPanel extends JPanel{
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(imgs[index%imgs.length].getImage(), 0, 0,this);
            index++;
        }
    }
}