package com.thdz.sgm.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.thdz.sgm.MyApplication;

import java.util.Map;
import java.util.Set;

public class SpUtil {

    /**
     * 取sp数据
     *
     * @param context
     * @param key
     */
    public static String getData(Context context, String key) {
        SharedPreferences sp = context
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 取sp数据
     */
    public static String getDataSingle(String key) {
        return getData(MyApplication.getInstance().getApplicationContext(),key);
    }


    /**
     * 取sp数据
     *
     * @param context
     * @param key
     */
    public static int getIntData(Context context, String key, int defaultVal) {
        SharedPreferences sp = context
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultVal);
    }


    /**
     * 取sp数据
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

//    /**
//     * 取uid数据
//     */
//    public static String getUid(Context context) {
//        String value = "";
//        SharedPreferences sp = context.getApplicationContext()
//                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
//        value = sp.getString(Finals.SP_UID, "");
//        return value;
//    }


    /**
     * 保存sp数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void save(Context context, String key, String value) {
        new SpSaveThread(context, key, value).run();
    }

    /**
     * 保存sp数据
     */
    public static void saveData(String key, String value) {
        new SpSaveThread(MyApplication.getInstance().getApplicationContext(), key, value).run();
    }


    /**
     * 保存sp数据 - int
     */
    public static void saveInt(Context context, String key, int value) {
        new SpSaveIntThread(context, key, value).run();
    }

    /**
     * 保存sp数据 - boolean
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        new SpSaveBooleanThread(context, key, value).run();
    }

    /**
     * 保存sp数据
     *
     * @param context
     * @param spMap
     */
    public static void save(Context context, Map<String, String> spMap) {
        Set<String> keySet = spMap.keySet();
        for (String key : keySet) {
            new SpSaveThread(context, key,
                    spMap.get(key)).run();
        }
    }

    /**
     * 保存字符串
     */
    public static class SpSaveThread extends Thread {
        public Context context;
        public String key;
        public String data;

        public SpSaveThread(Context context, String key, String data) {
            super();
            this.context = context;
            this.key = key;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            SharedPreferences sp = context.getSharedPreferences(Finals.SP_NAME,
                    Context.MODE_PRIVATE);
            sp.edit().putString(key, data).commit();
        }
    }

    /**
     * 保存字符串
     */
    public static class SpSaveIntThread extends Thread {
        public Context context;
        public String key;
        public int data;

        public SpSaveIntThread(Context context, String key, int data) {
            super();
            this.context = context;
            this.key = key;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            SharedPreferences sp = context.getSharedPreferences(Finals.SP_NAME,
                    Context.MODE_PRIVATE);
            sp.edit().putInt(key, data).commit();
        }
    }

    /**
     * 保存字符串
     */
    public static class SpSaveBooleanThread extends Thread {
        public Context context;
        public String key;
        public boolean data;

        public SpSaveBooleanThread(Context context, String key, boolean data) {
            super();
            this.context = context;
            this.key = key;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            SharedPreferences sp = context.getSharedPreferences(Finals.SP_NAME,
                    Context.MODE_PRIVATE);
            sp.edit().putBoolean(key, data).commit();
        }
    }


    /**
     * 接收短信网关的心跳检测时间间隔
     */
    public static int getIntervalHeart () {
        return SpUtil.getIntData(MyApplication.getInstance().getApplicationContext(), Finals.SP_HEART, Finals.INTERVAL_HEART_DEFAULT);
    }

    /**
     * 接收短信网关的心跳检测时间间隔
     */
    public static int getIntervalSendRecv () {
        return SpUtil.getIntData(MyApplication.getInstance().getApplicationContext(), Finals.SP_SEND_RECV, Finals.INTERVAL_SEND_RECV_DEFAULT);
    }

    /**
     * 获取默认短信网关号码
     */
    public static String getSmsGateWayno() {
        return  SpUtil.getData(MyApplication.getInstance().getApplicationContext(), Finals.SP_SMSGATEWAYNO);
    }

    /**
     * 设置短信网关的心跳检测时间间隔
     */
    public static void saveIntervalHeart (int value) {
        SpUtil.saveInt(MyApplication.getInstance().getApplicationContext(), Finals.SP_HEART, value);
    }

    /**
     * 设置短信网关的心跳检测时间间隔
     */
    public static void saveIntervalSendRecv (int value) {
        SpUtil.saveInt(MyApplication.getInstance().getApplicationContext(), Finals.SP_SEND_RECV, value);
    }

    /**s
     * 设置默认短信网关号码
     */
    public static void saveSmsGateWayno(String value) {
        SpUtil.saveData(Finals.SP_SMSGATEWAYNO, value);
    }

}
