package net.tatans.rhea.countdowntimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Const;
import net.tatans.rhea.utils.Preferences;


/**
 * Created by Administrator on 2015/10/26.
 */
public class CountDownTimerActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.tv_time)
    TextView tv_time;
    @ViewInject(id = R.id.btn_pause_resume)
    TextView btn_pause_resume;
    @ViewInject(id = R.id.layout_pause_resume, click = "onClick")
    LinearLayout layout_pause_resume;
    @ViewInject(id = R.id.layout_stop, click = "onClick")
    LinearLayout layout_stop;
    @ViewInject(id = R.id.btn_stop)
    TextView btn_stop;

    private MyCountDownTimer countDownTimer;//自定义倒计时类
    private boolean isPause = false, isStop = false;
    private long pauseTime;//保存暂停时的剩余时间
    private long mCountDownInterval = 1000, mMillisInFuture = Const.TIME_30;
    private Preferences preferences;
    private Vibrator vibrator;//震动
    private long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启 震动模式
    private static int count;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_time);
        preferences = new Preferences(this);
        mMillisInFuture = preferences.getLong("countDownTime", mMillisInFuture);
        countDownTimer = new MyCountDownTimer(mMillisInFuture, mCountDownInterval);
        countDownTimer.start();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        tv_time.setText(showTimeCount(mMillisInFuture));
        acquireWakeLock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause){
            setTitle("计时已暂停");
        }else {
            setTitle("还剩" + showTime(showTimeMillis(tv_time.getText().toString())));
        }
        tv_time.setContentDescription(showTime(showTimeMillis(tv_time.getText().toString())));
    }

    private PowerManager.WakeLock wakeLock = null;

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
        stopCountDown();
        releaseWakeLock();
    }

    /**
     * 停止计时
     */
    private void stopCountDown() {
        Speaker.getInstance(CountDownApplication.getInstance()).speech("倒计时结束");
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            isStop = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_pause_resume:
                if (isStop)
                    return;
                if (!isPause) {
                    //继续计时
                    pauseTime = showTimeMillis(tv_time.getText().toString());
                    countDownTimer.cancel();
                    btn_pause_resume.setText(R.string.resume);
                    btn_pause_resume.setContentDescription("继续");
                    layout_pause_resume.setContentDescription("继续");
                    isPause = true;
                } else {
                    //暂停计时
                    countDownTimer = new MyCountDownTimer(pauseTime, mCountDownInterval);
                    countDownTimer.start();
                    btn_pause_resume.setText(R.string.pause);
                    btn_pause_resume.setContentDescription("暂停");
                    layout_pause_resume.setContentDescription("暂停");
                    isPause = false;
                }
                CountDownApplication.setPause(isPause);
                break;
            case R.id.layout_stop:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 自定义倒计时类
     */
    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 倒计时每秒回调
         *
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            tv_time.setText(showTimeCount(millisUntilFinished));
            tv_time.setContentDescription(showTime(millisUntilFinished));
            if ((millisUntilFinished / 1000) % (preferences.getInt("intervalTime") * 60) == 0) {
                model(millisUntilFinished, false);
            }
        }

        /**
         * 倒计时停止时回调
         */
        @Override
        public void onFinish() {
            tv_time.setText("00:00:00");
            model(0, true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);

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

//    private void isOnTime() {
//        switch (preferences.getInt("intervalTime")) {
//            case 1:
//                TatansLog.d(count + "");
//                if (count % 60 == 0)
//                    model();
//                break;
//            case 5:
//                if (count % (5 * 60) == 0)
//                    model();
//                break;
//            case 10:
//                if (count % (10 * 60) == 0)
//                    model();
//                break;
//            case 15:
//                if (count % (15 * 60) == 0)
//                    model();
//                break;
//            case 30:
//                if (count % (30 * 60) == 0)
//                    model();
//                break;
//        }
//    }

    /**
     * 判断震动、铃声、语音
     */
    private void model(long millisUntilFinished, boolean isStop) {
        if (preferences.getBoolean("isRinging", false) && !isStop) {
//                mediaPlayer.start();
            CountDownApplication.playMusic(R.raw.beep);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CountDownApplication.stopPlay();
                }
            }, 1800);
        }else{
            CountDownApplication.playMusic(R.raw.terminationn);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CountDownApplication.stopPlay();
                }
            }, 1800);
        }
        if (preferences.getBoolean("isSpeaking", false) && !isStop) {
            Speaker.getInstance(CountDownApplication.getInstance()).speech("还剩" + showTime(millisUntilFinished));
        }else{
            Speaker.getInstance(CountDownApplication.getInstance()).speech("倒计时结束");
        }
        if (preferences.getBoolean("isVibrate", false))
            vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
