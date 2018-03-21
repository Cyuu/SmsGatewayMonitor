package com.thdz.sgm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thdz.sgm.util.SMSUtil;
import com.thdz.sgm.util.SpUtil;

/**
 * 接收如下广播：<br/>
 *  1  短信发送后的发送状态<br/>
 *  2  短信发送到对方后，对返回的对方接受状态<br/>
 *  3  收到短信的广播， 静态注册，<br/>
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;

    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    private EditText et_gatewayno;// 短信网关号码
    private EditText et_heart;    // “心跳”时间间隔
    private EditText et_send_recv;// “发送-接收”时间间隔
    private FloatingActionButton fab_send;

    private TextView tv_log; // 日志展示
    private TextView btn_clear_log; // 清除日志

    private ImageView iv_edit_no;     // 设置  短信网关号码
    private ImageView iv_edit_heart;  // 设置  “心跳”时间间隔
    private ImageView iv_edit_recv;   // 设置  “发送-接收”时间间隔


    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name_cn));
        setSupportActionBar(toolbar);

        et_gatewayno = (EditText) findViewById(R.id.et_gatewayno);
        et_heart = (EditText) findViewById(R.id.et_heart);
        et_send_recv = (EditText) findViewById(R.id.et_send_recv);


        iv_edit_no = (ImageView) findViewById(R.id.iv_edit_no);
        iv_edit_heart = (ImageView) findViewById(R.id.iv_edit_heart);
        iv_edit_recv = (ImageView) findViewById(R.id.iv_edit_recv);

        iv_edit_no.setOnClickListener(this);
        iv_edit_heart.setOnClickListener(this);
        iv_edit_recv.setOnClickListener(this);

        // 获取默认网关号码，并展示
        et_gatewayno.setText(SpUtil.getSmsGateWayno());
        toast("短信网关号码：" + SpUtil.getSmsGateWayno());

        // 获取默认"心跳"时间间隔，并展示
        et_heart.setText(SpUtil.getIntervalHeart() + "");

        // 获取默认“发送和接收”的时间间隔，并展示
        et_send_recv.setText(SpUtil.getIntervalSendRecv() + "");

        // 主动发送测试短信-- 上线后，不暴漏
        fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(this);

        tv_log = (TextView) findViewById(R.id.tv_log);
        // textview的滚动效果不流畅
        tv_log.setMovementMethod(new ScrollingMovementMethod());
        for (int i = 0; i < 12; i++) {
            tv_log.append("接下来我们再来学习一下位于Inspector最中间的那个正方形区域，它是用来控制控件大小的。一共有三种模式可选，每种模式都使用了一种不同的符号表示，点击符号即可进行切换。"
                    + "\n - - - - - - - - - - - - - - - - - - \n");
        }

        // 清除日志
        btn_clear_log = (TextView) findViewById(R.id.btn_clear_log);
        btn_clear_log.setOnClickListener(this);


        // 处理返回的发送状态
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                if (_intent.getAction().equals("android.provider.Telephony.ACTION_CHANGE_DEFAULT")) {
//                    showToast("发送成功");
                } else if (_intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//                    toast("发送成功");
//                    SMSUtil.deleteMsgByToNo(context, str_phone);
                }
            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            //  展示手机号，默认
        }

        String sent_action = "android.provider.Telephony.ACTION_CHANGE_DEFAULT";
        registerReceiver(broadcastReceiver, new IntentFilter(sent_action));

        bookSmsSendOK();

    }

    private void bookSmsSendOK() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, filter);
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
//            goActivity(MineActivity.class, null);
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

    private void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},2);
//        }
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    Toast.makeText(this, "你没启动权限", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_no:
                toast("设置短信网关");
                break;
            case R.id.iv_edit_heart:
                toast("设置心跳间隔");
                break;
            case R.id.iv_edit_recv:
                toast("设置主动检测间隔");
                break;

            case R.id.btn_clear_log:
                toast("清除日志");
                tv_log.setText("");
                break;

            // 用作测试
            case R.id.fab_send:
                String gateno = et_gatewayno.getText().toString().trim();
                if (!TextUtils.isEmpty(gateno)) {
//                    SMSUtil.sendMsg(getApplicationContext(), gateno, "0");
                    SMSUtil.sendMsg(getApplicationContext(), gateno, "5011");
                }
                break;
            default:
                break;
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 再按一次退出
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        // // 最小化程序
        // if (keyCode == KeyEvent.KEYCODE_BACK) {
        // Intent intent = new Intent(Intent.ACTION_MAIN);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.addCategory(Intent.CATEGORY_HOME);
        // startActivity(intent);
        // return true;
        // }

        return super.onKeyDown(keyCode, event);
    }

}
