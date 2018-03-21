package com.thdz.sgm;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thdz.sgm.bean.AlarmEvent;
import com.thdz.sgm.util.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 展示历史日志<br/>
 * 默认日志文件，不包括备份的
 */
public class LogActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LogActivity";
    private Context context = this;
    private TextView tv_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.log_history));
        setSupportActionBar(toolbar);

        // textview的滚动效果不流畅
        tv_log = (TextView) findViewById(R.id.tv_log);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String logStr = LogUtils.readLog();
                tv_log.setText(logStr);
            }
        }).start();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealShowLog(AlarmEvent event) {
        try {
            tv_log.append(event.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "短信接收日志展示失败");
        }
    }

    private void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }
}
