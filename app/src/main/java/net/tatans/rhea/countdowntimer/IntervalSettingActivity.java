package net.tatans.rhea.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Preferences;

/**
 * Created by Administrator on 2015/10/27.
 */
public class IntervalSettingActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.lyt_time_1, click = "onClick")
    RelativeLayout lyt_time_1;
    @ViewInject(id = R.id.lyt_time_5, click = "onClick")
    RelativeLayout lyt_time_5;
    @ViewInject(id = R.id.lyt_time_10, click = "onClick")
    RelativeLayout lyt_time_10;
    @ViewInject(id = R.id.lyt_time_15, click = "onClick")
    RelativeLayout lyt_time_15;
    @ViewInject(id = R.id.lyt_time_30, click = "onClick")
    RelativeLayout lyt_time_30;
    @ViewInject(id = R.id.lyt_time_45, click = "onClick")
    RelativeLayout lyt_time_45;
    @ViewInject(id = R.id.lyt_time_hour_1, click = "onClick")
    RelativeLayout lyt_time_hour_1;
    @ViewInject(id = R.id.lyt_time_hour_2, click = "onClick")
    RelativeLayout lyt_time_hour_2;
    @ViewInject(id = R.id.lyt_custom, click = "onClick")
    RelativeLayout lyt_custom;
    @ViewInject(id = R.id.img_tick_1)
    ImageView img_tick_1;
    @ViewInject(id = R.id.img_tick_5)
    ImageView img_tick_5;
    @ViewInject(id = R.id.img_tick_10)
    ImageView img_tick_10;
    @ViewInject(id = R.id.img_tick_15)
    ImageView img_tick_15;
    @ViewInject(id = R.id.img_tick_30)
    ImageView img_tick_30;
    @ViewInject(id = R.id.img_time_30)
    ImageView img_time_30;

    private Preferences preferences;//保存选中状态和时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ls_time_set);
        setTitle("时间间隔设置");
        lyt_time_45.setVisibility(View.GONE);
        lyt_time_hour_1.setVisibility(View.GONE);
        lyt_time_hour_2.setVisibility(View.GONE);
        lyt_custom.setVisibility(View.GONE);
        img_time_30.setVisibility(View.GONE);
        preferences = new Preferences(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        switch (preferences.getInt("intervalTime")) {
            case 1:
                setVisibility(img_tick_1);
                break;
            case 5:
                setVisibility(img_tick_5);
                break;
            case 10:
                setVisibility(img_tick_10);
                break;
            case 15:
                setVisibility(img_tick_15);
                break;
            case 30:
                setVisibility(img_tick_30);
                break;
            default:
                setVisibility(img_tick_1);
                preferences.putInt("intervalTime", 1);
                break;
        }
        setContentDescription();
    }

    private void setContentDescription() {
        if (img_tick_1.getVisibility() == View.VISIBLE) {
            lyt_time_1.setContentDescription("1分钟，已选中");
        } else {
            lyt_time_1.setContentDescription("1分钟");
        }
        if (img_tick_5.getVisibility() == View.VISIBLE) {
            lyt_time_5.setContentDescription("5分钟，已选中");
        } else {
            lyt_time_5.setContentDescription("5分钟");
        }
        if (img_tick_10.getVisibility() == View.VISIBLE) {
            lyt_time_10.setContentDescription("10分钟，已选中");
        } else {
            lyt_time_10.setContentDescription("10分钟");
        }
        if (img_tick_15.getVisibility() == View.VISIBLE) {
            lyt_time_15.setContentDescription("15分钟，已选中");
        } else {
            lyt_time_15.setContentDescription("15分钟");
        }
        if (img_tick_30.getVisibility() == View.VISIBLE) {
            lyt_time_30.setContentDescription("30分钟，已选中");
        } else {
            lyt_time_30.setContentDescription("30分钟");
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        switch (v.getId()) {
            case R.id.lyt_time_1:
//                setAlarm(1);
                preferences.putInt("intervalTime", 1);
                break;
            case R.id.lyt_time_5:
//                setAlarm(5);
                preferences.putInt("intervalTime", 5);
                break;
            case R.id.lyt_time_10:
//                setAlarm(10);
                preferences.putInt("intervalTime", 10);
                break;
            case R.id.lyt_time_15:
//                setAlarm(15);
                preferences.putInt("intervalTime", 15);
                break;
            case R.id.lyt_time_30:
//                setAlarm(30);
                preferences.putInt("intervalTime", 30);
                break;
        }
        startActivity(intent);
    }

//    private void setAlarm(int minute) {
//        Intent intent = new Intent(this,AlarmReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
//        //设置闹钟
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.MINUTE, minute);
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(), minute * Const.TIME_1, sender);
//    }

    private void setVisibility(ImageView tick) {
        img_tick_1.setVisibility(View.GONE);
        img_tick_5.setVisibility(View.GONE);
        img_tick_10.setVisibility(View.GONE);
        img_tick_15.setVisibility(View.GONE);
        img_tick_30.setVisibility(View.GONE);
        tick.setVisibility(View.VISIBLE);
    }
}
