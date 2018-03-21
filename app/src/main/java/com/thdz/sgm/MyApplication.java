package com.thdz.sgm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.thdz.sgm.bean.AlarmEvent;
import com.thdz.sgm.util.DataUtils;
import com.thdz.sgm.util.FileUtil;
import com.thdz.sgm.util.Finals;
import com.thdz.sgm.util.LogUtils;
import com.thdz.sgm.util.NotifyUtil;
import com.thdz.sgm.util.SMSUtil;
import com.thdz.sgm.util.SpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Application<br/>
 * ----------------<br/>
 * 1 心跳检测： 每次收到短信时，关闭并重新打开计时器<br/>
 * 2 主动发送接收检测：每次发送短信时，关闭并重新打开计时器<br/>
 */
public class MyApplication extends Application {

    private static final String TAG = "SGM APP";

    private SoundPool soundPool; // 告警音

    private int soundId; // 加载声音文件 返回的sound ID

    private Map<Integer, Integer> soundIDs = new HashMap<Integer, Integer>();

    public static int notyId = 1; // 通知的index

    private List<Activity> activityList;       // 全部activity集合
    private static MyApplication application; // 程序全局对象

    public static long lastSendTime = System.currentTimeMillis();// 最后一次发送短信的时间
    public static long lastRecvTime = System.currentTimeMillis();// 最后一次接收短信的时间


    AlarmShowInterface alarmshow;
    LogShowInterface logShow;


    public static MyApplication getInstance() {
        if (null == application) {
            application = new MyApplication();
        }
        return application;
    }


    public void setAlarmShowInterface(AlarmShowInterface asif) {
        this.alarmshow = asif;
    }

    public void setLogShowInterface(LogShowInterface lsif) {
        this.logShow = lsif;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "MyApplication实例： " + this.toString());

        application = this;

        FileUtil.createDirectory(Finals.FilePath);
        FileUtil.createFile(Finals.LogPath);

        startTask4Heart();
        startTask4SendRecv();

    }


    public void addActivity(Activity activity) {
        if (activityList == null) {
            activityList = new LinkedList<Activity>();
        }
        activityList.add(activity);
    }

    /**
     * 移除Activity到容器中
     */
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    private void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }

        activityList.clear();
        activityList = null;

        System.exit(0);
    }


    //////////////////  接收短信网关短信的心跳任务  //////////////////////
    /**
     * 轮询任务，开启应用后开启定时器，然后不许再停止，使用静态标识阻断定时器即可。
     */

    private Timer timer_heart = null;  // 定时器
    private TimerTask task_heart = null;
    private long period4Heart = 1000 * 60; // 定时任务的轮询间隔， 1分钟

    /**
     * 开启计时器，轮询心跳是否超时
     */
    public void startTask4Heart() {
        if (timer_heart == null) {
            timer_heart = new Timer();
        }
        if (task_heart == null) {
            task_heart = new TimerTask() {
                @Override
                public void run() {
                    taskHandler4Heart.sendMessage(new Message());
                }
            };
        }

        if (timer_heart != null && task_heart != null) {
            timer_heart.schedule(task_heart, 100, period4Heart);
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler taskHandler4Heart = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DataUtils.isTimeoutDuringTwoRecv()) {
                Log.i(TAG, "短信网关连接超时，开启主动轮询任务");
                isTaskOpened = true;
            } else {// 正常时间间隔内收到短信，不做处理
                Log.i(TAG, "短信网关连接中...停止主动轮询任务");
                isTaskOpened = false;
            }
        }
    };


    /**
     * 停止计时器
     */
    public void stopTask4Heart() {
        if (timer_heart != null) {
            timer_heart.cancel();
            timer_heart = null;
        }
        if (task_heart != null) {
            task_heart.cancel();
            task_heart = null;
        }
    }

    ///////////////////////  主动发送接收信息的广播 任务  //////////////////////

    public int count4Send = 0;

    private Timer timer_sendrecv = null;  // 定时器
    private TimerTask task_sendrecv = null;
    public boolean isTaskOpened;  // 主动发送信息轮询检测标识， true,继续轮询； false, 停止轮询
    private long periodSendRecv = 500 * 60; // 半分钟

    /**
     * 开启计时器，轮询心跳是否超时
     */
    public void startTask4SendRecv() {
        if (timer_sendrecv == null) {
            timer_sendrecv = new Timer();
        }

        if (task_sendrecv == null) {
            task_sendrecv = new TimerTask() {
                @Override
                public void run() {
                    if (isTaskOpened) {
                        taskHandler4SendRecv.sendMessage(new Message());
                    }
                }
            };
        }

        if (timer_sendrecv != null && task_sendrecv != null) {
            timer_sendrecv.schedule(task_sendrecv, 100, periodSendRecv);
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler taskHandler4SendRecv = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 超时了就发信息，未超时就继续轮询
            if (DataUtils.isTimeoutDuringSendRecv()) {
                if (count4Send < Finals.MAX_SEND_COUNT + 1) { // 发送主动轮询短信, todo 为了使轮询间隔成立，需要+1
                    Log.i(TAG, "发送主动轮询短信超时，发送次数：" + count4Send + ", 继续发送主动轮询短信。");
                    count4Send++;
                    SMSUtil.sendMsg(MyApplication.getInstance().getApplicationContext(), SpUtil.getSmsGateWayno(), "0");
                    // MyApplication.lastSendTime = System.currentTimeMillis(); // 移至 发送信息的广播里设置
                } else { // 没有短信回复，发送系统告警---通知栏，铃声，震动，页面也发告警
                    Log.i(TAG, "发送主动轮询短信超时，已达到最大发送次数：5次，展示告警状态。");
                    showAlarm();
                    // 已触发告警， 1 不重置时间, 2 重置次数，3 停止轮询发送短信任务
                    count4Send = 0;
                    isTaskOpened = false;

                    /**
                     * 触发告警后的处理逻辑：
                     */
                    // 1 告警之后，继续主动发送消息的轮询任务
                    // 不需要代码
                    // 2 告警之后，停止主动发送短消息轮询任务，重置 接收时间lastSendTime和发送时间lastRecvTime为当前时间
                    lastSendTime = System.currentTimeMillis();// 最后一次发送短信的时间
                    lastRecvTime = System.currentTimeMillis();// 最后一次接收短信的时间
                }
//            } else { // 有发送，有回复，且没有超时，停止轮询发送短信的任务，
//                Log.i(TAG, "发送主动轮询短信未超时，停止发送主动轮询短消息");
//                isTaskOpened = false;
            }
        }
    };

    /**
     * 停止计时器
     */
    public void stopTask4SendRecv() {
        if (timer_sendrecv != null) {
            timer_sendrecv.cancel();
            timer_sendrecv = null;
        }
        if (task_sendrecv != null) {
            task_sendrecv.cancel();
            task_sendrecv = null;
        }
    }


    /**
     * 没有短信回复，发送系统告警---通知栏，铃声，震动，页面也发告警
     */
    public void showAlarm() {
        String alarmContent = DataUtils.getFormatToday() + "  短信网关掉线告警 \r\n";
        // 0 保存日志打本地日志文件
        LogUtils.write2SD(alarmContent);
        SpUtil.saveData(Finals.SP_LAST_ALARM_TIME, DataUtils.getTodayCnStr());
        // 1 通知栏
        // MyApplication.notyId++;// +1，通知栏消息不会覆盖
        Intent intent = new Intent(MyApplication.getInstance(), MonitorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotifyUtil.showNotification(MyApplication.getInstance(),
                "短信网关掉线告警",
                "失联时间：" + DataUtils.getFormatToday(),
                intent,
                MyApplication.notyId);

        // 2 铃声, 不做处理
        playSound();

        // 3 震动
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        long[] patter = {1000, 1000, 2000, 50};
        vibrator.vibrate(patter, -1); // -1 : 不循环；  0 ： 一直循环
        // vibrator.cancel(); // 取消循环

        // 4 UI页面(主页面的最上方显示一个textview，提示告警) -- 通过接口
        alarmshow.uiAlarm(alarmContent);

        /**
         * 5 UI页面，展示告警日志
         * （ 主页面：将告警日志打印在主页面最下方的textview
         *   历史日志页面： 将告警日志追加展示）
         */
        AlarmEvent event = new AlarmEvent(alarmContent, "");
        EventBus.getDefault().post(event);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    Vibrator vibrator = null;
    MediaPlayer mMediaPlayer = null;
    AudioManager mAudioManager = null;

    private void doRing() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            // 要释放资源，不然会打开很多个MediaPlayer
            mMediaPlayer.release();
        }
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


    }

    public void playSound() {
        Log.e("ee", "正在响铃");
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        // 使用来电铃声的铃声路径
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                mMediaPlayer.reset();
            }

//            if (mMediaPlayer != null) {
//                mMediaPlayer.prepare();
//                if (mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.stop();
//                    mMediaPlayer.release();
//                }
//            }

            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.setLooping(false); //循环播放
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 用户确认后，停止响铃
     */
    public void cancelSound() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelViber() {
        try {
            if (vibrator != null) {
                vibrator.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface AlarmShowInterface {
        void uiAlarm(String msg);

    }

    public interface LogShowInterface {
        void uiLog(String log);
    }

}
