//package com.fun.crawl.utils;
//
//import com.okl.wechathelper.MyApplication;
//import com.okl.wechathelper.common.Constant;
//import com.okl.wechathelper.entity.UserBean;
//import com.okl.wechathelper.entity.UserEvent;
//
//import de.greenrobot.event.EventBus;
//
///**
// * 项目名: WechatHelper
// * 包  名: com.okl.wechathelper.utils
// * 作  者: LD
// * 日  期: 2017年04月05日
// * 描  述:
// */
//
//public class LoginThread extends Thread {
//    private UserBean userBean;
//    private boolean isLoop = true;
//
//    public LoginThread() {
//        userBean = new UserBean();
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        while (isLoop) {
//            String re = WebUtil.sendGet("https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login", "loginicon=true&uuid=" + MyApplication.uuid + "&tip=1&r=471115730&lang=zh_CN", true);
//            LogUtils.e("re===" + re);
//            if (re.contains("window.userAvatar"))
//                userBean.setUserIcon(re.split(",")[1]);
//            if (re.equals("failed")) {
//                EventBus.getDefault().post(new UserEvent(Constant.LOGIN_FAILED));
//                isLoop = false;
//            } else if (re.contains("window.redirect_uri")) {
//                //表示登录成功了
//                userBean.setTokenUrl(MethodUtils.getcentertext(re, "window.redirect_uri=\"", "\""));
//                //获取参数
//                LogUtils.i("登录成功");
//                getParama(userBean);
//                MyApplication.isNoQuit = true;
//                MyApplication.isStopShow = false;
//                MyApplication.isPlayShow = false;
//                MyApplication.ispauseShow = false;
//                isLoop = false;
//            } else {
//                try {
//                    Thread.sleep(400);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private static void getParama(UserBean userBean) {
//        String re = WebUtil.sendGet(userBean.getTokenUrl() + "&fun=new", "", false);
//        userBean.setSkey(MethodUtils.getcentertext(re, "<skey>", "</skey>"));
//        userBean.setWxsid(MethodUtils.getcentertext(re, "<wxsid>", "</wxsid>"));
//        userBean.setWxuin(MethodUtils.getcentertext(re, "<wxuin>", "</wxuin>"));
//        userBean.setPass_ticket(MethodUtils.getcentertext(re, "<pass_ticket>", "</pass_ticket>"));
//        //登录成功了
//        MyApplication.setUserBean(userBean);
//        EventBus.getDefault().post(new UserEvent(Constant.LOGIN_SUSSCE));
//    }
//}