package com.thdz.sgm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thdz.sgm.util.DataUtils;
import com.thdz.sgm.util.LogUtils;
import com.thdz.sgm.util.SpUtil;

/**
 * desc:    短信发送后的发送状态广播接收器，
 * author:  Administrator
 * date:    2018/3/12  8:31
 */

public class SmsStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    MyApplication.lastSendTime = System.currentTimeMillis();
                    Log.i(TAG, "短信发送成功, 更新最近发送时间");
                    // 2 记录日志, 不写 发送短信成功的日志
//                    String logStr = DataUtils.getFormatToday() + "  短信发送到短信网关"+ SpUtil.getSmsGateWayno() +"成功"+ "\r\n";
//                    LogUtils.write2SD(logStr);
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(TAG, "短信发送失败");
                    break;
            }
        } catch (Exception e) {
            Log.i(TAG, "短信发送异常 ");
        }

    }
}
