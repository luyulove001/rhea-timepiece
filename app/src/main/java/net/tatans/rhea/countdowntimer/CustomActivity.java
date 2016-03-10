package net.tatans.rhea.countdowntimer;

import android.content.Intent;
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
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Const;
import net.tatans.rhea.utils.Preferences;

/**
 * Created by Administrator on 2015/10/28.
 */
public class CustomActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.lyt_custom_set)
    LinearLayout lyt_custom_set;
    @ViewInject(id = R.id.tv_confirm, click = "onClick")
    TextView tv_confirm;
    @ViewInject(id = R.id.add_one_minute, click = "onClick")
    TextView add_one_minute;
    @ViewInject(id = R.id.minus_one_minute, click = "onClick")
    TextView minus_one_minute;
    @ViewInject(id = R.id.add_ten_minute, click = "onClick")
    TextView add_ten_minute;
    @ViewInject(id = R.id.minus_ten_minute, click = "onClick")
    TextView minus_ten_minute;
    @ViewInject(id = R.id.add_one_hour, click = "onClick")
    TextView add_one_hour;
    @ViewInject(id = R.id.minus_one_hour, click = "onClick")
    TextView minus_one_hour;
    private long mMillisInFuture = Const.TIME_30;
    private boolean isMinute;
    private Speaker speaker;
    private int hour = 0, minute = 0;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_time);
        setTitle("自定义时间设置");
        initView();
    }

    private void initView() {
        speaker = Speaker.getInstance(this);
        preferences = new Preferences(this);
        mMillisInFuture = preferences.getLong("countDownTime", mMillisInFuture);
        hour = (int) (mMillisInFuture / Const.TIME_1 / 60);
        minute = (int) (mMillisInFuture / Const.TIME_1 % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (hour == 0 && minute == 0) {
                    TatansToast.showAndCancel("时间不能为0小时0分");
                } else {
                    preferences.putLong("countDownTime", (hour * 60 + minute) * Const.TIME_1);
                    Intent boardCost = new Intent(Const.COUNTDOWN_TIME);
                    boardCost.putExtra("countDownTime", preferences.getLong("countDownTime", Const.TIME_30));
                    sendBroadcast(boardCost);
                    finish();
                }
                break;
            case R.id.add_one_minute:
                isMinute = true;
                calculateTime(1);
                break;
            case R.id.add_ten_minute:
                isMinute = true;
                calculateTime(10);
                break;
            case R.id.minus_one_minute:
                isMinute = true;
                calculateTime(-1);
                break;
            case R.id.minus_ten_minute:
                isMinute = true;
                calculateTime(-10);
                break;
            case R.id.add_one_hour:
                isMinute = false;
                calculateTime(1);
                break;
            case R.id.minus_one_hour:
                isMinute = false;
                calculateTime(-1);
                break;
        }
        TatansToast.showAndCancel(hour + "小时" + minute + "分");
    }

    /**
     * 手势操作，时间的计算
     *
     * @param time
     */
    private void calculateTime(int time) {
        if (!isMinute)
            hour += time;
        else
            minute += time;
        if (hour < 0)
            hour = 0;
        else if (hour >= 99)
            hour = 99;
        if (minute < 0) {
            if (hour <= 0) {
                minute = 0;
                hour = 0;
            } else {
                minute += 60;
                hour -= 1;
            }
        } else if (minute >= 60) {
            if (hour >= 99) {
                hour = 99;
                minute = 59;
            } else {
                minute -= 60;
                hour += 1;
            }
        }
    }
}
