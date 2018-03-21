package com.thdz.sgm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.thdz.sgm.bean.MsgRecvEvent;
import com.thdz.sgm.util.DataUtils;
import com.thdz.sgm.util.Finals;
import com.thdz.sgm.util.NotifyUtil;
import com.thdz.sgm.util.SpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * desc: 接收短信的管听广播接收器<br/>
 * 当系统接收到一个新消息时候系统发送一条action="android.provider.Telephony.SMS_RECEIVED"的广播。
 * 这是一条有序广播可能被截断，所以这个广播方式只对新短信有效，同时未必有效，可能被系统切断了就收不到了有新短信的广播。
 * --------------
 * 业务： 只要收到短信，就关闭定时器<br/>
 * author:  Administrator
 * date:    2018/3/12  8:32
 */

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // 获取到这个intent的action是不是android.provider.Telephony.SMS_RECEIVED
            Log.i(TAG, "action: " + intent.getAction());

            if (SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                StringBuilder builder = new StringBuilder();// 用于存储短信内容
                String sender = null;
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");// 根据pdus key获取短信消息mesg数组，
                    //数组每一个元素表示一个SMS消息。将每个pdu字节数组转化成SmsMessage对象，调用SmsMessage.createFromPdu，传入每个字节数组
                    for (Object object : pdus) {
                        SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                        sender = message.getOriginatingAddress();

                        Log.i(TAG, "短信来源: " + sender + "  ");
                        String gatewayno = SpUtil.getData(MyApplication.getInstance().getApplicationContext(), Finals.SP_SMSGATEWAYNO);
                        // 只需判断号码，不用判断内容
                        if (gatewayno.equals(sender)) {
                            Log.i(TAG, "收到短信网关短信");
                            // 1 重新计时 ,清空次数
                            MyApplication.lastRecvTime = System.currentTimeMillis();
                            MyApplication.getInstance().isTaskOpened = false;
                            MyApplication.getInstance().count4Send = 0;

                            String logStr = DataUtils.getFormatToday() + "  收到来自短信网关" + gatewayno + "的短信"+ "\r\n";
                            // 2 记录日志
//                            LogUtils.write2SD(logStr); // 不写收到短信的日志

                            // 3 主页面  展示到主页面textview， 并取消告警展示， 不展示对话框
                            MsgRecvEvent recvEvent = new MsgRecvEvent(logStr,"");
                            EventBus.getDefault().post(recvEvent);

                            // todo 4 终止各种告警展示
                            NotifyUtil.clearNotification();

                            Vibrator vibrator = (Vibrator)MyApplication.getInstance().getSystemService(Activity.VIBRATOR_SERVICE);
                            vibrator.cancel();

                            break;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            toast("短消息解析失败");
        }
    }

}
