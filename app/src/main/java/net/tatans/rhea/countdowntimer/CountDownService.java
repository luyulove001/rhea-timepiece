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
 * 倒计时计时核心类，用于计时及播报
 * Created by cly on 2015/11/24.
 */
public class CountDownService extends Service {

    private MyCountDownTimer countDownTimer;//自定义倒计时类
    private Preferences preferences;
    private Vibrator vibrator;//震动
    private long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启 震动模式
    private Handler handler = new Handler();
    private int remainder;
    private Intent broadcast;
    private CountDownBean bean = new CountDownBean();
    private TatansDb tdb;
    private String countDownTime = "countDownTime";
    private String tag = "antony";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        tdb = TatansDb.create(Const.CountDownDB);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_NOT_STICKY;
        bean = (CountDownBean) intent.getSerializableExtra("countDown_scheme");
        if (bean == null) {
            List<CountDownBean> timeList = tdb.findAll(CountDownBean.class);
            int count = timeList.size();
            Log.e(tag, count + "1");
            if (count == 0) {
                bean = new CountDownBean();
                bean.setId(0);
                bean.setCountDownTime((int) (intent.getLongExtra(countDownTime,
                        preferences.getLong(countDownTime, Const.TIME_30)) / 60000));
                bean.setIntervalTime(30);
                bean.setIsRinging(true);
                bean.setIsSpeaking(false);
                bean.setIsVibrate(true);
                bean.setMassage(true);
            } else {
                bean = timeList.get(0);
            }
        }
        remainder = (int) ((bean.getCountDownTime() * Const.TIME_1 / 1000) % (bean.getIntervalTime() * 60));
        long millisInFuture = intent.getLongExtra(countDownTime, bean.getCountDownTime() * Const.TIME_1);
        countDownTimer = new MyCountDownTimer(millisInFuture, 1000);
        countDownTimer.start();
        countDownTimer.setStartTime();
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
            getEndTime(countDownTimer.mMillisInFuture - countDownTimer.mMillisUntilFinished, countDownTimer.startTime);
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /**
     * 自定义倒计时类
     */
    private class MyCountDownTimer extends CountDownTimer {
        long mMillisUntilFinished;
        String startTime;
        long mMillisInFuture;

        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.mMillisInFuture = millisInFuture;
        }

        /**
         * 倒计时每秒回调
         *
         * @param millisUntilFinished 毫秒数
         */
        @Override
        public void onTick(long millisUntilFinished) {
            mMillisUntilFinished = millisUntilFinished;
            if (broadcast == null)
                broadcast = new Intent();
            broadcast.setAction(Const.CLOCK_TICK);
            broadcast.putExtra(countDownTime, millisUntilFinished);
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
            Log.e(tag, "mMillisInFuture = " + mMillisInFuture + " -- mMillisUntilFinished = "
                    + mMillisUntilFinished + " -- startTime = " + startTime);
        }

        void setStartTime() {
            Calendar start = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            startTime = sdf.format(start.getTime());
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

        /**
         * 返回时间字符串,用于时间设置显示和开始时播报
         *
         * @param time 毫秒数
         */
        private String showTime(long time) {
            String[] times = showTimeCount(time).split(":");
            String str = "";
            if (!"00".equals(times[0])) {
                str += Integer.valueOf(times[0]) + "小时";
            }
            if (!"00".equals(times[1])) {
                str += Integer.valueOf(times[1]) + "分钟";
            }
            if (!"00".equals(times[2])) {
                str += Integer.valueOf(times[2]) + "秒";
            }
            return str;
        }

        /**
         * 时间long值转换为字符串
         *
         * @param time 毫秒数
         * @return timeCount
         */
        private String showTimeCount(long time) {
            if (time >= 360000000) {
                return "00:00:00";
            }
            String timeCount;
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
    }

    private MassageTimeBean getEndTime(long useTime, String startTime) {
        MassageTimeBean massageTimeBean = new MassageTimeBean();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        Log.e(tag, sdf.format(calendar.getTime()));
        String id = sdf.format(calendar.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        massageTimeBean.setId(id);
        massageTimeBean.setYear(year);
        massageTimeBean.setMonth(month + 1);
        massageTimeBean.setDay(day);
        massageTimeBean.setStartTime(startTime);
        massageTimeBean.setDuration((int) useTime / 60000);
        Log.e(tag, useTime / 60000 + "分");
        if (useTime / 60000 > 0 && this.bean.isMassage())
            tdb.save(massageTimeBean);
        return massageTimeBean;
    }

}
