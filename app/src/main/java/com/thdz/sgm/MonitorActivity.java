package com.thdz.sgm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thdz.sgm.bean.AlarmEvent;
import com.thdz.sgm.bean.MsgRecvEvent;
import com.thdz.sgm.util.Finals;
import com.thdz.sgm.util.LogUtils;
import com.thdz.sgm.util.NotifyUtil;
import com.thdz.sgm.util.SpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 不接收如下广播：<br/>
 * 1  短信发送后的发送状态<br/>
 * 2  短信发送到对方后，对返回的对方接受状态<br/>
 * 接收 收到短信的广播， 静态注册，<br/>
 */
public class MonitorActivity extends AppCompatActivity implements View.OnClickListener, MyApplication.AlarmShowInterface {

    private static final String TAG = "MonitorActivity";
    private static final String LOG_NONE_STR = "尚无日志数据";
    private Context context = this;

    private String SMS_SEND_ACTION = "SMS_SEND_ACTION";

    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    private EditText et_gatewayno;// 短信网关号码
    private EditText et_heart;    // “心跳”时间间隔
    private EditText et_send_recv;// “发送-接收”时间间隔
    private FloatingActionButton fab_send;
    private FloatingActionButton fab_alarm;

    private TextView tv_log; // 日志展示
    private TextView btn_clear_log; // 清除日志

    private ImageView iv_edit_no;     // 设置  短信网关号码
    private ImageView iv_edit_heart;  // 设置  “心跳”时间间隔
    private ImageView iv_edit_recv;   // 设置  “发送-接收”时间间隔
    private CardView card_tv_alarm;

    private TextView tv_alarm;

    /**
     * 动态注册接收短信的广播接收器
     */
    private void registRecv() {
        SmsReceiver mSmsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sms_received");
        registerReceiver(mSmsReceiver, intentFilter);
    }


    /**
     * 动态注册发送短信回执的广播接收器
     */
    private void registSendReturn() {
        SmsStatusReceiver mSmsReceiver = new SmsStatusReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SMS_SEND_ACTION);
        registerReceiver(mSmsReceiver, intentFilter);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        MyApplication.getInstance().setAlarmShowInterface(this); // 设置接口

//        if (!EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().register(this);
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name_cn));
        setSupportActionBar(toolbar);

        et_gatewayno = (EditText) findViewById(R.id.et_gatewayno);
        et_heart = (EditText) findViewById(R.id.et_heart);
        et_send_recv = (EditText) findViewById(R.id.et_send_recv);


        iv_edit_no = (ImageView) findViewById(R.id.iv_edit_no);
        iv_edit_heart = (ImageView) findViewById(R.id.iv_edit_heart);
        iv_edit_recv = (ImageView) findViewById(R.id.iv_edit_recv);

        tv_alarm = (TextView) findViewById(R.id.tv_alarm);
        card_tv_alarm = (CardView) findViewById(R.id.card_tv_alarm);
        card_tv_alarm.setOnClickListener(this);

        tv_log = (TextView) findViewById(R.id.tv_log);

        // 主动发送测试短信-- 上线后，不暴漏
        fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(this);
        fab_alarm = (FloatingActionButton) findViewById(R.id.fab_alarm);
        fab_alarm.setOnClickListener(this);

        if (Finals.IS_TEST) {
            fab_send.setVisibility(View.VISIBLE);
            fab_alarm.setVisibility(View.VISIBLE);
        } else {
            fab_send.setVisibility(View.GONE);
            fab_alarm.setVisibility(View.GONE);
        }

        iv_edit_no.setOnClickListener(this);
        iv_edit_heart.setOnClickListener(this);
        iv_edit_recv.setOnClickListener(this);
        card_tv_alarm.setOnClickListener(this);

        // 获取默认网关号码，并展示
        et_gatewayno.setText(SpUtil.getSmsGateWayno());
        toast("短信网关号码：" + SpUtil.getSmsGateWayno());

        // 获取默认"心跳"时间间隔，并展示
        et_heart.setText(SpUtil.getIntervalHeart() + "");

        // 获取默认“发送和接收”的时间间隔，并展示
        et_send_recv.setText(SpUtil.getIntervalSendRecv() + "");

        // textview的滚动效果不流畅
        tv_log.setMovementMethod(new ScrollingMovementMethod());
        String logStr = LogUtils.readLog();
        if (TextUtils.isEmpty(logStr)) {
            logStr = LOG_NONE_STR;
        }
        tv_log.setText(logStr);
//        for (int i = 0; i < 12; i++) {
//            tv_log.append("接下来我们再来学习一下位于Inspector最中间的那个正方形区域，它是用来控制控件大小的。一共有三种模式可选，每种模式都使用了一种不同的符号表示，点击符号即可进行切换。"
//                    + "\n - - - - - - - - - - - - - - - - - - \n");
//        }

        // 清除日志
        btn_clear_log = (TextView) findViewById(R.id.btn_clear_log);
        btn_clear_log.setOnClickListener(this);


        // todo 处理返回的发送状态

        checkPermission();
        registRecv();
        registSendReturn();

        // 隐藏输入法
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    private void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }

        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                toast("请开通相关权限，否则无法正常使用本应用！");
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
//            toast("授权成功！");
            Log.i(TAG, "checkPermission: 已经授权！");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},2);
//        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    toast("实时监控功能需要您开启相应权限");
                }
                break;

            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    toast("实时监控功能需要您开启相应权限");
                }
                break;
            default:
                break;
        }
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        FileUtil.checkBackupLog();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_no:
                toast("设置短信网关");
                String no = et_gatewayno.getText().toString().trim();
                if (TextUtils.isEmpty(no)) {
                    toast("短信网关号码不合理，请重新输入");
                    return;
                }
                SpUtil.saveSmsGateWayno(no);
                break;

            case R.id.iv_edit_heart:
                toast("设置心跳检测间隔");
                try {
                    int val12 = Integer.parseInt(et_heart.getText().toString().trim());
                    if (val12 <= 0) {
                        toast("时间间隔不合理，请重新输入");
                        return;
                    }
                    SpUtil.saveIntervalHeart(val12);
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("时间间隔不合理，请重新输入");
                }
                break;

            case R.id.iv_edit_recv:
                toast("设置主动检测间隔");
                try {
                    int val12 = Integer.parseInt(et_send_recv.getText().toString().trim());
                    if (val12 <= 0) {
                        toast("时间间隔不合理，请重新输入");
                        return;
                    }
                    SpUtil.saveIntervalSendRecv(val12);
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("时间间隔不合理，请重新输入");
                }
                break;

            case R.id.btn_clear_log: // 不清除log文件
                toast("清除日志");
                tv_log.setText("");
                break;

            // 用作测试
            case R.id.fab_send:
                // 获取3个常量并展示
                toast("短信网关号码： " + SpUtil.getSmsGateWayno()        // 网关号码
                        + " \n心跳时间间隔： " + SpUtil.getIntervalHeart()   // "心跳"时间间隔
                        + " \n发送和接收的时间间隔： " + SpUtil.getIntervalSendRecv()); // “发送和接收”的时间间隔
                showSureDialog("确认了啊？");
                break;
            case R.id.fab_alarm:
                if (Finals.IS_TEST) {
                    MyApplication.getInstance().playSound();
                }
                break;
            case R.id.card_tv_alarm:
                if (Finals.IS_TEST) {
                    card_tv_alarm.setVisibility(View.GONE);
                    stopAllAlarmShow();
                }
                break;
            default:
                break;
        }
    }

    private AlertDialog sureDialog = null;
    private TextView dialog_sure_tv = null;

    /**
     * 打开确认对话框，用户手动取消告警
     */
    public void showSureDialog(String tip) {
        if (sureDialog == null) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View mView = layoutInflater.inflate(R.layout.dialog_sure, null);
            dialog_sure_tv = (TextView) mView.findViewById(R.id.dialog_sure_tv);
            dialog_sure_tv.setText(tip);
            mBuilder.setView(mView);
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    card_tv_alarm.setVisibility(View.GONE);
                    stopAllAlarmShow();
                    dialog_sure_tv.setText("");
                    return;
                }
            });

//            mBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog_sure_tv.setText("");
//                    return;
//                }
//            });
            sureDialog = mBuilder.create();
        } else {
            dialog_sure_tv.setText(tip);
        }

        if (!sureDialog.isShowing()) {
            sureDialog.show();
        }

    }


    private void stopAllAlarmShow() {
        // 取消响铃
        MyApplication.getInstance().cancelSound();
        // 取消震动
        MyApplication.getInstance().cancelViber();
        // 清空通知栏
        NotifyUtil.clearNotification();

    }


    @Override
    public void uiAlarm(String msg) {
        toast(msg);
        card_tv_alarm.setVisibility(View.VISIBLE);
        tv_alarm.setText(msg + " (点我关闭)");
    }


    /**
     * 短信网关告警处理
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealAlarmEvent(AlarmEvent event) {
        try {
            showSureDialog(event.getMsg());
            String tmp = tv_log.getText().toString();
            if (tv_log.getText().toString().trim().contains(LOG_NONE_STR)) {
                tmp = tmp.replace(LOG_NONE_STR, "");
            }
            tv_log.setText(tmp + event.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "短信接收日志展示失败");
        }
    }


    /**
     * 收到短信了，处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealMsgRecvEvent(MsgRecvEvent event) {
        try {
            card_tv_alarm.setVisibility(View.GONE);

            String tmp = tv_log.getText().toString();
            if (tv_log.getText().toString().trim().contains(LOG_NONE_STR)) {
                tmp = tmp.replace(LOG_NONE_STR, "");
            }
            tv_log.setText(tmp + event.getMsg());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "清空告警标识失败");
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 如果已通过onCreateOptionsMenu()自定义了含icon的Menu，那这个方法将不再生效
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
//            goActivity(SettingsActivity.class, null);
        } else if (item.getItemId() == R.id.action_mine) {
//            goActivity(LogActivity.class, null);

            toast("最近一次告警时间：" + SpUtil.getData(getApplicationContext(), Finals.SP_LAST_ALARM_TIME));
        }
        return super.onOptionsItemSelected(item);
    }

    public void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 再按一次退出
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                toast("再按一次退出程序");
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//            return true;
//        }

        // 最小化程序
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
