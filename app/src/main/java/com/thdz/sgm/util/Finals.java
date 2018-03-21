package com.thdz.sgm.util;


import android.os.Environment;

/**
 * 静态常量
 */
public class Finals {

    public static final boolean IS_TEST = false;    // 是否测试
    public static final String TAG_ME = "SmsGatewayMonitor";    // tag

    // ----sp----
    public static final String SP_NAME = "SP_SMSGATEWAYNO";
    public static final String SP_SMSGATEWAYNO = "SP_SMSGATEWAYNO";
    public static final String SP_HEART = "SP_HEART";             // 接收短信王冠的短信心跳时间间隔
    public static final String SP_SEND_RECV = "SP_SEND_RECV";      // 主动发送短信和接收短信时间间隔
    public static final String SP_IsDouble = "SP_IsDouble";         // 是否双卡

    public static final String SMS_GATEWAYNO_TEST = "10000";            // 默认短信网关号码
    public static final String SMS_GATEWAYNO_OFFICIAL = "1065845106";   // 短信网关号码 - 张均宁提供的

    public static final String SP_LAST_ALARM_TIME = "SP_LAST_ALARM_TIME";

    public static final int INTERVAL_HEART_DEFAULT = 10;        // 接收短信网关的短信心跳时间间隔
    public static final int INTERVAL_SEND_RECV_DEFAULT = 2;     // 主动发送短信和接收短信时间间隔




    // 文件缓存至磁盘路径
    public static final String FilePath = Environment.getExternalStorageDirectory() + "/sgm/";
    // 日志文件存储路径
    public static final String LogPath = FilePath + "sgm.log";
    public static final String LogPathHead = FilePath + "sgm";

    /**
     * 主动发送短信到短信网关的最大次数
     */
    public static final int MAX_SEND_COUNT = 5;

}
