package net.tatans.rhea.countdowntimer;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Const;
import net.tatans.rhea.utils.Preferences;

/**
 * Created by Administrator on 2015/10/28.
 */
public class CustomActivity extends TatansActivity implements View.OnHoverListener, View.OnClickListener {
    @ViewInject(id = R.id.title_bar)
    LinearLayout lyt_title_bar;
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;
    @ViewInject(id = R.id.tv_hour)
    TextView tv_hour;
    @ViewInject(id = R.id.tv_minute)
    TextView tv_minute;
    @ViewInject(id = R.id.lyt_custom_set)
    LinearLayout lyt_custom_set;
    @ViewInject(id = R.id.tv_confirm)
    TextView tv_confirm;
    @ViewInject(id = R.id.lyt_confirm, click = "onClick")
    LinearLayout lyt_confirm;
    @ViewInject(id = R.id.title_bar)
    LinearLayout title_bar;
    private GestureDetector mGD;//手势管理
    private boolean isHour, isMinute;
    private Speaker speaker;
    private int hour = 0, minute = 0;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_time);
        setTitle("自定义时间设置");
        tv_title.setText("自定义时间设置");
        mGD = new GestureDetector(this, new MyGesture());
        speaker = Speaker.getInstance(this);
        tv_hour.setOnHoverListener(this);
        tv_minute.setOnHoverListener(this);
        title_bar.setOnHoverListener(this);
        preferences = new Preferences(this);
        tv_hour.setContentDescription("0小时");
        tv_minute.setContentDescription("0分");
        lyt_title_bar.setContentDescription("选中时间后，双指向上推增加一小时，下划减少一小时，左划减少十小时，右划增加十小时。");
    }

    @Override
    protected void onResume() {
        super.onResume();
        lyt_custom_set.setAccessibilityDelegate(new FocusChange());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        switch (event.getAction()) {
            // 手指离开view
            case MotionEvent.ACTION_HOVER_EXIT:
                if (event.getX() > 0 && event.getX() < v.getWidth()
                        && event.getY() > 0 && event.getY() < v.getHeight()) {
                    getTimeAction(v);
                }
                break;
        }
        return false;
    }

    /**
     * 当手指抬起或者双击时间设置为true
     */
    private void getTimeAction(View v) {
        switch (v.getId()) {
            case R.id.tv_hour:
                isHour = true;
                break;
            case R.id.tv_minute:
                isMinute = true;
                break;
            default:
                isHour = false;
                isMinute = false;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_confirm:
                preferences.putLong("countDownTime", (hour * 60 + minute) * Const.TIME_1);
                finish();
                break;
        }
    }

    /**
     * 焦点发生变化时处理事件
     */
    private class FocusChange extends View.AccessibilityDelegate {
        @Override
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            switch (event.getEventType()) {
                case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                    isHour = false;
                    isMinute = false;
                    break;
            }
            return super.onRequestSendAccessibilityEvent(host, child, event);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGD != null) {
            if (mGD.onTouchEvent(ev))
                // If the gestureDetector handles the event, a swipe has been
                // executed and no more needs to be done.
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private class MyGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // e1为向量的起点，e2为向量的终点 , velocityX在X轴上面的速度 xx像素/s, velocityY在Y轴上面的滑动速度
            float xlength = e1.getX() - e2.getX();
            float ylength = e1.getY() - e2.getY();
            // 向左向右
            if (Math.abs(xlength) > Math.abs(ylength)) {
                if (xlength < -120) {
                    calculateTime(10);
                }
                if (xlength > 120) {
                    calculateTime(-10);
                }
            } else {
                if (ylength > 120) {
                    calculateTime(1);
                }
                if (ylength < -120) {
                    calculateTime(-1);
                }
            }
            String sec = "0" + hour;
            sec = sec.substring(sec.length() - 2, sec.length());
            tv_hour.setText(sec);
            tv_hour.setContentDescription(hour + "小时");
            sec = "0" + minute;
            sec = sec.substring(sec.length() - 2, sec.length());
            tv_minute.setText(sec);
            tv_minute.setContentDescription(minute + "分");
            speaker.speech(hour + "小时" + minute + "分");
            return false;
        }

        /**
         * 手势操作，时间的计算
         *
         * @param time
         */
        private void calculateTime(int time) {
            if (isHour)
                hour += time;
            else if (isMinute)
                minute += time;
            if (hour < 0)
                hour += 24;
            else if (hour >= 24)
                hour -= 24;
            if (minute < 0) {
                minute += 60;
                hour -= 1;
                if (hour < 0)
                    hour += 24;
            } else if (minute >= 60) {
                minute -= 60;
                hour += 1;
                if (hour >= 24)
                    hour -= 24;
            }
        }
    }
}
