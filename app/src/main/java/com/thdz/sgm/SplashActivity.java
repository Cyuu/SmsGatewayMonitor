package com.thdz.sgm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * desc:    启动页， 解决启动白屏/黑屏的问题
 * 参考：http://blog.csdn.net/yanzhenjie1003/article/details/52201896
 * author:  Administrator
 * date:    2018/3/13  17:08
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MonitorActivity.class));
        finish();
    }

}
