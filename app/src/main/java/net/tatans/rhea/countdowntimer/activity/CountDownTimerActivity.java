package net.tatans.rhea.countdowntimer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.countdowntimer.CountDownApplication;
import net.tatans.rhea.countdowntimer.CountDownService;
import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.bean.CountDownBean;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Preferences;
import net.tatans.rhea.countdowntimer.utils.Util;


/**
 * Created by Administrator on 2015/10/26.
 */
public class CountDownTimerActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.tv_time)
    TextView tv_time;
    @ViewInject(id = R.id.lyt_time, click = "onClick")
    LinearLayout lyt_time;
    @ViewInject(id = R.id.btn_pause_resume)
    TextView btn_pause_resume;
    @ViewInject(id = R.id.layout_pause_resume, click = "onClick")
    LinearLayout layout_pause_resume;
    @ViewInject(id = R.id.layout_stop, click = "onClick")
    LinearLayout layout_stop;

    private MyBroadcastReceiver myBroadcastReceiver;//自定义倒计时类
    private boolean isPause = false, isStop = false;
    private long pauseTime;//保存暂停时的剩余时间
    private long mMillisInFuture;
    private Preferences preferences;
    private Handler handler = new Handler();
    private PowerManager.WakeLock wakeLock;//唤醒锁
    private Intent service;
    //当前音量
    private int currentVolume;
    //控制音量的对象
    public AudioManager mAudioManager;
    //确保关闭程序后，停止线程
    private boolean isDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_time);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.CLOCK_TICK);
        intentFilter.addAction(Const.CLOCK_STOP);
        intentFilter.addAction(Const.CLOCK_RESTART);
        registerReceiver(myBroadcastReceiver, intentFilter);
        service = new Intent(CountDownApplication.getInstance(), CountDownService.class);
        isDestroy = false;
        // 获得AudioManager对象
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//音乐音量,如果要监听铃声音量变化，则改为AudioManager.STREAM_RING
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        CountDownBean bean = (CountDownBean) getIntent().getSerializableExtra("countDown_scheme");
        if (bean == null) return;
        mMillisInFuture = bean.getCountDownTime() * Const.TIME_1;
        service.putExtra("countDown_scheme", bean);
        if (!Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE))
            startService(service);
        tv_time.setText(showTimeCount(mMillisInFuture));
        acquireWakeLock();
        onVolumeChangeListener();
        CountDownApplication.soundPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (!Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE)) {
            setTitle("计时已暂停");
            btn_pause_resume.setBackgroundResource(R.mipmap.btn_resume);
            btn_pause_resume.setContentDescription("继续。按钮");
            layout_pause_resume.setContentDescription("继续。按钮");
        } else {
            setTitle("还剩" + showTime(showTimeMillis(tv_time.getText().toString())));
            btn_pause_resume.setBackgroundResource(R.mipmap.btn_pause);
            btn_pause_resume.setContentDescription("暂停。按钮");
            layout_pause_resume.setContentDescription("暂停。按钮");
        }
        lyt_time.setContentDescription(showTime(showTimeMillis(tv_time.getText().toString())));
        tv_time.setContentDescription(showTime(showTimeMillis(tv_time.getText().toString())));
    }

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     */
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    /**
     * 释放设备电源锁
     */
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        stopService(service);
        releaseWakeLock();
        unregisterReceiver(myBroadcastReceiver);
        CountDownApplication.stopAll();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Const.CLOCK_TICK.equals(intent.getAction())) {
                setTitle("还剩" + showTime(showTimeMillis(tv_time.getText().toString())));
                tv_time.setText(showTimeCount(intent.getLongExtra("countDownTime", 0)));
                tv_time.setContentDescription(showTime(intent.getLongExtra("countDownTime", 0)));
                lyt_time.setContentDescription(showTime(intent.getLongExtra("countDownTime", 0)));
            } else if (Const.CLOCK_STOP.equals(intent.getAction())) {
                tv_time.setText("00:00:00");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopService(service);
                        finish();
                    }
                }, 1000);
            } else if (Const.CLOCK_RESTART.equals(intent.getAction())){
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_pause_resume:
                if (isStop)
                    return;
                if (Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE)) {
                    //继续计时
                    pauseTime = showTimeMillis(tv_time.getText().toString());
                    stopService(service);
                    btn_pause_resume.setBackgroundResource(R.mipmap.btn_resume);
                    btn_pause_resume.setContentDescription("继续。按钮");
                    layout_pause_resume.setContentDescription("继续。按钮");
                    TatansToast.showAndCancel("倒计时已暂停");
                    isPause = true;
                    Intent lock = new Intent("net.tatans.rhea.lock.countDownTime.pauseTime");
                    lock.putExtra("pauseTime", pauseTime);
                    sendBroadcast(lock);
                } else {
                    //暂停计时
                    pauseTime = showTimeMillis(tv_time.getText().toString());
                    service.putExtra("countDownTime", pauseTime);
                    startService(service);
                    btn_pause_resume.setBackgroundResource(R.mipmap.btn_pause);
                    btn_pause_resume.setContentDescription("暂停。按钮");
                    layout_pause_resume.setContentDescription("暂停。按钮");
                    TatansToast.showAndCancel("倒计时继续");
                    isPause = false;
                }
                CountDownApplication.setPause(isPause);
                break;
            case R.id.layout_stop:
                TatansToast.showAndCancel("倒计时结束");
                Intent stop = new Intent(Const.CLOCK_STOP);
                sendBroadcast(stop);
                stopService(new Intent(CountDownApplication.getInstance(), CountDownService.class));
                finish();
                break;
            case R.id.lyt_time:
                break;
            default:
                break;
        }
    }


    /**
     * 时间long值转换为字符串
     *
     * @param time
     * @return
     */
    public String showTimeCount(long time) {
        if (time >= 360000000) {
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time / 3600000;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length() - 2, hour.length());

        long minuec = (time - hourc * 3600000) / (60000);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length() - 2, minue.length());

        long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
        String sec = "0" + secc;
        sec = sec.substring(sec.length() - 2, sec.length());
        timeCount = hour + ":" + minue + ":" + sec;
        return timeCount;
    }

    /**
     * 返回时间字符串,用于时间设置显示和开始时播报
     *
     * @param time
     */
    public String showTime(long time) {
        String[] times = showTimeCount(time).split(":");
        String str = new String();
        if (!times[0].equals("00")) {
            str += Integer.valueOf(times[0]) + "小时";
        }
        if (!times[1].equals("00")) {
            str += Integer.valueOf(times[1]) + "分钟";
        }
        if (!times[2].equals("00")) {
            str += Integer.valueOf(times[2]) + "秒";
        }
        return str;
    }

    /**
     * 时间字符串转换为long值
     *
     * @param time
     * @return
     */
    public long showTimeMillis(String time) {
        String[] times = time.split(":");
        long millis = Long.valueOf(times[0]) * 3600 * 1000 + Long.valueOf(times[1]) * 60000 + Long.valueOf(times[2]) * 1000;
        return millis;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 监听音量按键的线程
     */
    private Thread volumeChangeThread;

    /**
     * 持续监听音量变化 说明： 当前音量改变时，将音量值重置为最大值减2
     */
    public void onVolumeChangeListener() {

        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeChangeThread = new Thread() {
            public void run() {
                while (!isDestroy) {
                    // 监听的时间间隔
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        System.out.println("error in onVolumeChangeListener Thread.sleep(20) " + e.getMessage());
                    }

                    if (currentVolume < mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                            || currentVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {
                        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        System.out.println("按下了音量+");
                        CountDownApplication.getSpeaker().speech("还剩" + lyt_time.getContentDescription());
                    }
                }
            }

            ;
        };
        volumeChangeThread.start();
        TatansLog.e("antony", "volumeChangeThread.start()=_=");
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
