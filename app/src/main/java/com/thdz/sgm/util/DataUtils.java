package com.thdz.sgm.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.thdz.sgm.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * desc:    数据验证和处理
 * author:  Administrator
 * date:    2018/3/9  17:37
 */
public class DataUtils {

    private static final String TAG = "DataUtils";

    /**
     * 验证输入的手机号是否符合规则 , 不包含各种服务号
     *
     * @param s 手机号
     * @return 验证结果
     */
    public static boolean IsMobileNum(String s) {
        //使用正则表达式初步匹配手机号码是否正确
        String num = "[1][345678]\\d{9}";
        return (!TextUtils.isEmpty(s) && s.matches(num))
                || s.equals("10086") || s.equals("10010") || s.equals("10000");
    }

    /**
     * 静默接收短信网关的短信，最后1次接收短信网关中心短信的时间距今的时间间隔 是否大于规定的时间间隔
     * 如果true， 需要发送短信给短信网关，
     */
    public static boolean isTimeoutDuringTwoRecv() {
        int duringRecv = (int) ((System.currentTimeMillis() - MyApplication.lastRecvTime) / 1000 / 60);
        Log.i(TAG, "最后一次接收网关的短信的时间距今时间间隔：" + duringRecv + "秒");
        int intervalDefault = SpUtil.getIntData(MyApplication.getInstance().getApplicationContext(), Finals.SP_HEART, Finals.INTERVAL_HEART_DEFAULT);

        return duringRecv > intervalDefault;
    }

    /**
     * 主动发送短信给断网网关后，是否在规定时间内接收到回复短信<br/>
     * 这个应该是判断现在距离上次发送短信的时间间隔，
     * 因为只要收到短信就是连接状态
     */
    public static boolean isTimeoutDuringSendRecv() {
        int interval = (int) ((System.currentTimeMillis() - MyApplication.lastSendTime) / 1000 / 60);
        Log.i(TAG, "主动轮询发送和接收信息的 时间间隔：" + interval + "秒");
        int intervalDefault = SpUtil.getIntData(MyApplication.getInstance().getApplicationContext(), Finals.SP_SEND_RECV, Finals.INTERVAL_SEND_RECV_DEFAULT);

        return interval > intervalDefault;
    }


    public static void goActivity(Context context,  Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 返回今天的时间格式化值
     */
    public static String getFormatToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        return str;
    }


    /**
     * 返回今天的时间格式化值
     */
    public static String getTodayCnStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Date date = new Date();
        String str = sdf.format(date);
        return str;
    }


//    public static void saveLog(String msg) {
//        OutputStream out = null;
//        Writer writer = null;
//        try {
//            out = MyApplication.getInstance().openFileOutput(Finals.LogPath, Context.MODE_PRIVATE);
//        /* 参数一: 文件名。
//         * 如果文件不存在，Android会自动创建它。创建的文件保存在/data/data/<package name>/files目录下
//         * 参数二: 文件操作模式参数。代表该文件是私有数据，只能被应用本身访问。
//         * */
//            writer = new OutputStreamWriter(out);
//            writer.write(msg);
//        }catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (writer != null) {
//                    writer.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public static String loadLog(){
//        BufferedReader reader = null;
//        StringBuilder data = new StringBuilder();
//        try {
//            InputStream in = MyApplication.getInstance().openFileInput(Finals.LogPath);
//            Log.i(TAG, "读取日志内容："+in.toString());
//            reader = new BufferedReader(new InputStreamReader(in));
//            String line = new String();
//            while ((line = reader.readLine()) != null) {
//                data.append(line);
//            }
//            return data.toString();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        return null;
//    }

}
