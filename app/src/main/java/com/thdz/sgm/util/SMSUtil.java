package com.thdz.sgm.util;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.thdz.sgm.MyApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 短信工具类
 */
public class SMSUtil {

    private static final String TAG = "SMSUtil";

    private static void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    public static String getPhoneno(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String tel = tm.getLine1Number();
            return tel;
        } else {
            toast("请开启'读取手机状态'权限！");
        }
        return "";
    }

    /**
     * iTelephony.getActivePhoneType(); 1=GSM, 2=CDMA
     */
    public static void sendMultipartTextMessage(String content, String phone, Object phoneType) {
        try {
            Class<?> smsManagerClass = null;
            Class[] divideMessagePamas = {String.class};
            Class[] sendMultipartTextMessagePamas = {String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class, int.class};
            Method divideMessage = null;
            Method sendMultipartTextMessage = null;
            smsManagerClass = Class.forName("android.telephony.SmsManager");
            Method method = smsManagerClass.getMethod("getDefault", new Class[]{});
            Object smsManager = method.invoke(smsManagerClass, new Object[]{});
            divideMessage = smsManagerClass.getMethod("divideMessage", divideMessagePamas);
            sendMultipartTextMessage = smsManagerClass.getMethod("sendMultipartTextMessage", sendMultipartTextMessagePamas);
            ArrayList<String> magArray = (ArrayList<String>) divideMessage.invoke(smsManager, content);
            sendMultipartTextMessage.invoke(smsManager, phone, "", magArray, null, null, phoneType);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断是否是双卡
     */
    public static void initIsDoubleTelephone(Context context) {
        boolean isDouble = true;
        Method method = null;
        Object result_0 = null;
        Object result_1 = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            // 只要在反射getSimStateGemini这个函数时报错了就是单卡手机(这是帖子作者的经验，不一定全对)
            method = TelephonyManager.class.getMethod("getSimStateGemini", new Class[]{int.class});
            // 获取sim卡1
            result_0 = method.invoke(tm, new Object[]{new Integer(0)});
            // 获取sim卡2
            result_1 = method.invoke(tm, new Object[]{new Integer(1)});
        } catch (SecurityException e) {
            isDouble = false;
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            isDouble = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            isDouble = false;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            isDouble = false;
            e.printStackTrace();
        }
        SpUtil.saveBoolean(context.getApplicationContext(), Finals.SP_IsDouble, isDouble);

        if (isDouble) {
            toast("双卡手机");
        } else {
            toast("单卡手机");
        }

    }


    public static void SendMsgOth(String message, String phone) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> list = smsManager.divideMessage(message);
        for (String text:list) {
            smsManager.sendTextMessage(phone, null, text, null, null);
        }
    }

    /**
     * 发送信息 - 调用系统功能直接发短信
     */
    public static void sendMsg(Context context, String toNo, String message) {
        // sendIntent: 发送短信结果状态信号(是否成功发送), 操作系统接收到信号后将广播这个Intent.此过程为异步.
        PendingIntent sendIntent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        // deliveryIntent: 对方接收状态信号(是否已成功接收)
        PendingIntent deliveryIntent = null;
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(toNo, null, message, sendIntent, deliveryIntent);

    }

    /**
     * 发送信息 - 调起系统功能发短信
     */
    public void gotoSmsDefaultApp(Context context, String phoneNumber,String message){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        }
    }


//    /**
//     * 删除制定联系人的短信
//     * ------------
//     * 相关参数：
//     * address 对方号码 content://sms/ （content://sms/sent 和 content://sms/draft 会被报异常）
//     * type，类型，1 接收，2 发送
//     */
//    public static void deleteMsgByToNo(Context context, String toNo) {
//
//        ContentResolver resolver = context.getContentResolver();
//        // 提示开启权限
////        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
////            toast("请开启'读写短信'权限！");
////        }
//
//        // 2 删除系统短信 content://sms/ content://sms/sent  draft
//        resolver.delete(Uri.parse("content://sms"), "address in (?, ?) and type in (?)",
//                new String[]{toNo, "+86" + toNo, "2"});
//
//    }

}
