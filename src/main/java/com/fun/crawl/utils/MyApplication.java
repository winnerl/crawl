//package com.fun.crawl.utils;
//
//import android.app.Application;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Message;
//import android.support.multidex.MultiDex;
//
//import com.iflytek.cloud.SpeechUtility;
//import com.okl.wechathelper.common.Constant;
//import com.okl.wechathelper.entity.DeliverData;
//import com.okl.wechathelper.entity.Member;
//import com.okl.wechathelper.entity.UserBean;
//import com.okl.wechathelper.service.MyMediaService;
//import com.okl.wechathelper.utils.LogUtils;
//import com.okl.wechathelper.utils.MethodUtils;
//import com.okl.wechathelper.utils.MyExceptionHandler;
//import com.okl.wechathelper.utils.MyWakeUp;
//import com.okl.wechathelper.utils.SharePreUtils;
//import com.tencent.bugly.Bugly;
//import com.tencent.bugly.crashreport.CrashReport;
//
//import java.util.HashSet;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 项目名: WechatHelper
// * 包  名: com.okl.wechathelper
// * 作  者: LD
// * 日  期: 2017年03月01日
// * 描  述:
// */
//
//public class MyApplication extends Application {
//    public static  String OSSURL = "";
//    public static String cookie = "";
//    public static String uuid = "";
//    public static ConcurrentHashMap<String, String> cookieMap = new ConcurrentHashMap<>();
//    public static ConcurrentHashMap<String, String> headMap = new ConcurrentHashMap<>();
//    public static HashSet<String> processedMsg = new HashSet<>();
//    public static String[] key = new String[4];
//    public static UserBean userBean;
//    public static boolean isNoQuit = true;//微信是否没有退出（手机端）
//    public static boolean isStopShow = false;//是否停止播报
//    public static boolean isPlayShow = false;//是否正在进行播放语音（语音合成和语音文件播放）
//    public static boolean ispauseShow=false;//语音回复的时候是否停止播报
//    public static boolean ispauseShow1=false;//语音回复的时候是否停止播报
//    public static boolean isStopShowIng=false;//判断应用是否暂停处理消息,在语音交互的时候
//    public static boolean isWakeUp=false;
//
//    public static boolean isRecieve=true;//是否收到消息
//    public static boolean isListening=true;//是否一直再听消息
//    public static boolean isInit=true;
//    public static  boolean isOff = true;
//
//    public static boolean isInit1=true;
//
//    public static   boolean isTwice=false;
//
//    public static boolean isOpen=false;
//    public static  long timeStamp;
//
//    public static boolean isInit2=true;
//    public static Member currentMember=new Member();
//    public static String path;
//    public static String uploadPath="";
//
//    /**
//     * 消息流程
//     */
//    public static  StringBuffer  stringBufferEd=new StringBuffer();
//
//    public static Member info=new Member();
//
//    public static  boolean isCurrentChatPage=false;//是否为当前聊天页面
//    public static MyWakeUp myWakeUp;
//
//    public static String OSCREAT="";
//    public static String OSCRKEY="";
//    public static Context context;
//    public static String fcontent;
//    public static String fowardContent;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//
//        if (SharePreUtils.getBoolean(getApplicationContext(),"isKill",false)){
//            SharePreUtils.putBoolean(getApplicationContext(),"isKill",false);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            Intent intent = getBaseContext().getPackageManager()
//                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//
//        }
//        SharePreUtils.putBoolean(getApplicationContext(),"isKill",true);
//
//        if (isInit1) {
//
//
//            MultiDex.install(this);
//            myWakeUp = new MyWakeUp(getApplicationContext());
//            try {
//                //全局捕获崩溃异常
//                //Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler.getInstance());
//                SpeechUtility.createUtility(getApplicationContext(), "appid=590c3bb9");
//                Bugly.init(getApplicationContext(), "aca6fdb97e", true);
//                LogUtils.e(MethodUtils.getTime() + "********************");
//                LogUtils.e("初始化成功");
//                MyApplication.ispauseShow = SharePreUtils.getBoolean(getApplicationContext(), "ispauseShow", false);
//                CrashReport.initCrashReport(getApplicationContext(), "aca6fdb97e", true);
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            isInit1=false;
//        }
//        context=this.getApplicationContext();
//}
//
//    public static android.os.Handler handler=new android.os.Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            DeliverData obj = (DeliverData) msg.obj;
//
//            Intent intent = new Intent();
//            intent.putExtra("MSG", Constant.PLAY_MAG);
//            intent.putExtra("URI", obj.savePath);
//            intent.putExtra("USERNAME", obj.userName);
//            intent.putExtra("NICKNAME", obj.nickName);
//            intent.putExtra("PATH", obj.savePath);
//            intent.putExtra("isDel",false);
//            intent.setClass(obj.context, MyMediaService.class);
//            obj.context.startService(intent);
//        }
//    };
//public static UserBean getUserBean() {
//        return userBean;
//        }
//
//    public static void setUserBean(UserBean userBean) {
//        MyApplication.userBean = userBean;
//    }
//
//
//
//
//    //应用关闭时擦除数据
//    public static void cleanData() {
//        MyApplication.isNoQuit = false;//微信网页端退出（手机端）
//        MyApplication.isStopShow = true;//微信手机端退出网页版登陆，则停止播报
//        setUserBean(null);
//        MyApplication.uuid = null;
//        MyApplication.isPlayShow=false;
//        MyApplication.isOpen=false;
//        MyApplication.isRecieve=true;
//        MyApplication.isInit1=true;
//        System.exit(0);
//
//    }
//
//}
