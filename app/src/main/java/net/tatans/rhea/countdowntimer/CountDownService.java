package net.tatans.rhea.countdowntimer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.rhea.countdowntimer.bean.CountDownBean;
import net.tatans.rhea.countdowntimer.bean.MassageTimeBean;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.CountDownTimeWakeLock;
import net.tatans.rhea.countdowntimer.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private CountDownBean bean = new CountDownBean();
    private TatansDb tdb;

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
        tdb = TatansDb.create(Const.CountDownDB);
//        remainder = (int) ((mMillisInFuture / 1000) % (preferences.getInt("intervalTime") * 60));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_NOT_STICKY;
        bean = (CountDownBean) intent.getSerializableExtra("countDown_scheme");
        if (bean == null) {
            List<CountDownBean> timeList = tdb.findAll(CountDownBean.class);
            int count = timeList.size();
            Log.e("antony", count + "");
            if (count == 0) {
                bean.setId(0);
                bean.setCountDownTime((int) (intent.getLongExtra("countDownTime",
                        preferences.getLong("countDownTime", Const.TIME_30)) / 60000));
                bean.setIntervalTime(30);
                bean.setIsRinging(true);
                bean.setIsSpeaking(false);
                bean.setIsVibrate(true);
            } else {
                bean = timeList.get(0);
            }
        }
        try {
            remainder = (int) ((bean.getCountDownTime() * Const.TIME_1 / 1000) % (bean.getIntervalTime() * 60));
            mMillisInFuture = intent.getLongExtra("countDownTime", bean.getCountDownTime() * Const.TIME_1);
            Log.e("antony", "bean.getCountDownTime():" + bean.getCountDownTime() + "---" + mMillisInFuture +
                    "---" + remainder + "--- bean.getIntervalTime():" + bean.getIntervalTime() + "---bean.getCountDownTime():" + bean.getCountDownTime());
            countDownTimer = new MyCountDownTimer(mMillisInFuture, mCountDownInterval);
            countDownTimer.start();
            countDownTimer.setStartTime();
            broadcast = new Intent();
            CountDownTimeWakeLock.acquireCpuWakeLock(CountDownApplication.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            getEndTime(countDownTimer.mMillisInFuture - countDownTimer.mMillisUntilFinished, countDownTimer.startTime);
            Log.e("antony", "mMillisInFuture = " + countDownTimer.mMillisInFuture + " -- mMillisUntilFinished = "
                    + countDownTimer.mMillisUntilFinished + " -- startTime = " + countDownTimer.startTime);
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /**
     * 自定义倒计时类
     */
    private class MyCountDownTimer extends CountDownTimer {
        public long mMillisUntilFinished;
        public String startTime;
        public long mMillisInFuture;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.mMillisInFuture = millisInFuture;
        }

        /**
         * 倒计时每秒回调
         *
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            mMillisUntilFinished = millisUntilFinished;
            if (broadcast == null)
                broadcast = new Intent();
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
            if (broadcast == null)
                broadcast = new Intent();
            broadcast.setAction(Const.CLOCK_STOP);
            sendBroadcast(broadcast);
            getEndTime(mMillisInFuture - mMillisUntilFinished, startTime);
            Log.e("antony", "mMillisInFuture = " + mMillisInFuture + " -- mMillisUntilFinished = "
                    + mMillisUntilFinished + " -- startTime = " + startTime);
        }

        public void setStartTime() {
            Calendar start = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            startTime = sdf.format(start.getTime());
        }
    }

    private MassageTimeBean getEndTime(long useTime, String startTime) {
        MassageTimeBean bean = new MassageTimeBean();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        Log.e("antony", sdf.format(calendar.getTime()));
        String id = sdf.format(calendar.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        bean.setId(id);
        bean.setYear(year);
        bean.setMonth(month + 1);
        bean.setDay(day);
        bean.setStartTime(startTime);
        bean.setDuration((int) useTime / 60000);
        Log.e("antony", useTime / 60000 + "分");
//        if (useTime / 60000 > 0)
            tdb.save(bean);
        return bean;
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
