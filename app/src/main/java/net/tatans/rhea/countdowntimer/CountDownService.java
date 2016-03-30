package net.tatans.rhea.countdowntimer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.CountDownTimeWakeLock;
import net.tatans.rhea.countdowntimer.utils.Preferences;

import net.tatans.rhea.countdowntimer.bean.CountDownBean;

/**
 * Created by Administrator on 2015/11/24.
 */
public class CountDownService extends Service {

    private MyCountDownTimer countDownTimer;//自定义倒计时类
    private long pauseTime;//保存暂停时的剩余时间
    private long mCountDownInterval = 1000, mMillisInFuture = Const.TIME_30;
    private Preferences preferences;
    private Vibrator vibrator;//震动
    private long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启 震动模式
    private Handler handler = new Handler();
    private int remainder;
    private Intent broadcast;
    private CountDownBean bean;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(this);
//        mMillisInFuture = preferences.getLong("countDownTime", mMillisInFuture);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        remainder = (int) ((mMillisInFuture / 1000) % (preferences.getInt("intervalTime") * 60));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_NOT_STICKY;
        bean = (CountDownBean) intent.getSerializableExtra("countDown_scheme");
        remainder = (int) ((bean.getCountDownTime() * Const.TIME_1 / 1000) % (bean.getIntervalTime() * 60));
        mMillisInFuture = intent.getLongExtra("countDownTime", bean.getCountDownTime() * Const.TIME_1);
        Log.e("antony", "bean.getCountDownTime():" + bean.getCountDownTime() + "---" + mMillisInFuture +
                "---" + remainder + "--- bean.getIntervalTime():" + bean.getIntervalTime() + "---bean.getCountDownTime():" + bean.getCountDownTime());
        countDownTimer = new MyCountDownTimer(mMillisInFuture, mCountDownInterval);
        countDownTimer.start();
        broadcast = new Intent();
        CountDownTimeWakeLock.acquireCpuWakeLock(CountDownApplication.getInstance());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCountDown();
        CountDownTimeWakeLock.releaseCpuLock();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
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
            broadcast.setAction(Const.CLOCK_TICK);
            broadcast.putExtra("countDownTime", millisUntilFinished);
            sendBroadcast(broadcast);
            if ((millisUntilFinished / 1000) % (bean.getIntervalTime() * 60) == remainder && (millisUntilFinished / 1000) != 5 * 60 && (millisUntilFinished / 1000) != 60) {
                model(millisUntilFinished, false);
            }
            if ((millisUntilFinished / 1000) == 5 * 60 || (millisUntilFinished / 1000) == 60 || (millisUntilFinished / 1000) == 30) {
                model(millisUntilFinished, false);
            }
        }

        /**
         * 倒计时停止时回调
         */
        @Override
        public void onFinish() {
            model(0, true);
            broadcast.setAction(Const.CLOCK_STOP);
            sendBroadcast(broadcast);
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

    private void model(long millisUntilFinished, boolean isStop) {
        if (bean.isRinging()) {
            if (isStop) {
                CountDownApplication.playMusic(R.raw.terminationn);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CountDownApplication.stopPlay();
                    }
                }, 1800);
            } else {
                CountDownApplication.playMusic(R.raw.warning_tone);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CountDownApplication.stopPlay();
                    }
                }, 1800);
            }
        }

        if (bean.isSpeaking()) {
            if (isStop)
                CountDownApplication.getSpeaker().speech("倒计时结束");
            else
                CountDownApplication.getSpeaker().speech("还剩" + showTime(millisUntilFinished));
        }
        if (bean.isVibrate()) {
            if (isStop && !preferences.getBoolean("isSpeaking", true) && !preferences.getBoolean("isRinging", true))
                vibrator.vibrate(3000);           //重复两次上面的pattern 如果只想震动一次，index设为-1
            else
                vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
        }
    }
}
