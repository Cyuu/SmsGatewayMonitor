package com.thdz.sgm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * desc:    短信发送到对方后，对返回的对方接受状态的处理逻辑
 * author:  Administrator
 * date:    2018/3/12  8:28
 */
public class SmsDeliveryStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "短信发送对方回执";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.d(TAG, "Activity.RESULT_OK");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.d(TAG, "RESULT_ERROR_GENERIC_FAILURE");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.d(TAG, "RESULT_ERROR_NO_SERVICE");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.d(TAG, "RESULT_ERROR_NULL_PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.d(TAG, "RESULT_ERROR_RADIO_OFF");
                break;
            default:
                break;
        }
    }
}
