package net.tatans.rhea.countdowntimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Preferences;
import net.tatans.rhea.countdowntimer.utils.Util;

import net.tatans.rhea.countdowntimer.bean.CountDownBean;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_time);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.CLOCK_TICK);
        intentFilter.addAction(Const.CLOCK_STOP);
        registerReceiver(myBroadcastReceiver, intentFilter);
        service = new Intent(CountDownApplication.getInstance(), CountDownService.class);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
//        preferences = new Preferences(this);
//        mMillisInFuture = preferences.getLong("countDownTime", mMillisInFuture);
        CountDownBean bean = (CountDownBean)getIntent().getSerializableExtra("countDown_scheme");
        if (bean == null) return;
        mMillisInFuture = bean.getCountDownTime() * Const.TIME_1;
        service.putExtra("countDown_scheme", bean);
        if (!Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE))
            startService(service);
        tv_time.setText(showTimeCount(mMillisInFuture));
        acquireWakeLock();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(service);
        releaseWakeLock();
        unregisterReceiver(myBroadcastReceiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Const.CLOCK_TICK.equals(intent.getAction())) {
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
                } else {
                    //暂停计时
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
//                CountDownApplication.getSpeaker().speech("倒计时结束");
                TatansToast.showAndCancel("倒计时结束");
                stopService(new Intent(CountDownApplication.getInstance(), CountDownService.class));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
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
}
