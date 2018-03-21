package com.thdz.sgm.util;

/**
 * desc:    日告工具类，用于写日志，读日志，
 * author:  Administrator
 * date:    2018/3/10  15:43
 * see:     http://blog.csdn.net/aicpzl/article/details/51451984
 */
public class LogUtils {

    /**
     * 将日志写入本地
     */
    public static void write2SD(String log) {
        // DataUtils.saveLog(log);
        FileUtil.writeFile(Finals.LogPath, log);
    }

//    /**
//     * 将日志写入本地
//     */
//    public static void write2SD(String gatewayno, String time, String desc) {
//
//    }

    /**
     * 读取日志
     */
    public static String readLog(){
        // return DataUtils.loadLog();
        return FileUtil.readFile(Finals.LogPath);
    }


}
