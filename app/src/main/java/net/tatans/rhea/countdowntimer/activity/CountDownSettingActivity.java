package net.tatans.rhea.countdowntimer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Preferences;

/**
 * Created by Administrator on 2015/10/27.
 */
public class CountDownSettingActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.lyt_time_1, click = "onClick")
    RelativeLayout lyt_time_1;
    @ViewInject(id = R.id.lyt_time_5, click = "onClick")
    RelativeLayout lyt_time_5;
    @ViewInject(id = R.id.lyt_time_10, click = "onClick")
    RelativeLayout lyt_time_10;
    @ViewInject(id = R.id.lyt_time_15, click = "onClick")
    RelativeLayout lyt_time_15;
    @ViewInject(id = R.id.lyt_time_20, click = "onClick")
    RelativeLayout lyt_time_20;
    @ViewInject(id = R.id.lyt_time_30, click = "onClick")
    RelativeLayout lyt_time_30;
    @ViewInject(id = R.id.lyt_time_45, click = "onClick")
    RelativeLayout lyt_time_45;
    @ViewInject(id = R.id.lyt_time_hour_1, click = "onClick")
    RelativeLayout lyt_time_hour_1;
    @ViewInject(id = R.id.lyt_time_hour_1_1, click = "onClick")
    RelativeLayout lyt_time_hour_1_1;
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
    @ViewInject(id = R.id.img_tick_20)
    ImageView img_tick_20;
    @ViewInject(id = R.id.img_tick_30)
    ImageView img_tick_30;
    @ViewInject(id = R.id.img_tick_45)
    ImageView img_tick_45;
    @ViewInject(id = R.id.img_tick_hour_1)
    ImageView img_tick_hour_1;
    @ViewInject(id = R.id.img_tick_hour_1_1)
    ImageView img_tick_hour_1_1;
    @ViewInject(id = R.id.img_tick_hour_2)
    ImageView img_tick_hour_2;
    @ViewInject(id = R.id.img_tick_custom)
    ImageView img_tick_custom;

    private Preferences preferences;//保存选中状态和时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ls_time_set);
        setTitle("倒计时时间设置");
        preferences = new Preferences(this);
        if (preferences.getBoolean("isFirst", true)) {
//            preferences.putBoolean("isFirst", false);
            preferences.putInt("TimeLevel", 30);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        MobclickAgent.onResume(this);
    }

    private void initView() {
        switch (preferences.getInt("TimeLevel")) {
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
            case 20:
                setVisibility(img_tick_20);
                break;
            case 30:
                setVisibility(img_tick_30);
                break;
            case 45:
                setVisibility(img_tick_45);
                break;
            case 60:
                setVisibility(img_tick_hour_1);
                break;
            case 90:
                setVisibility(img_tick_hour_1_1);
                break;
            case 120:
                setVisibility(img_tick_hour_2);
                break;
            case -1:
                setVisibility(img_tick_custom);
                break;
            default:
                setVisibility(img_tick_30);
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
        if (img_tick_20.getVisibility() == View.VISIBLE) {
            lyt_time_20.setContentDescription("20分钟，已选中");
        } else {
            lyt_time_20.setContentDescription("20分钟");
        }
        if (img_tick_30.getVisibility() == View.VISIBLE) {
            lyt_time_30.setContentDescription("30分钟，已选中");
        } else {
            lyt_time_30.setContentDescription("30分钟");
        }
        if (img_tick_45.getVisibility() == View.VISIBLE) {
            lyt_time_45.setContentDescription("45分钟，已选中");
        } else {
            lyt_time_45.setContentDescription("45分钟");
        }
        if (img_tick_hour_1.getVisibility() == View.VISIBLE) {
            lyt_time_hour_1.setContentDescription("一小时，已选中");
        } else {
            lyt_time_hour_1.setContentDescription("一小时");
        }
        if (img_tick_hour_1_1.getVisibility() == View.VISIBLE) {
            lyt_time_hour_1_1.setContentDescription("1.5小时，已选中");
        } else {
            lyt_time_hour_1_1.setContentDescription("1.5小时");
        }
        if (img_tick_hour_2.getVisibility() == View.VISIBLE) {
            lyt_time_hour_2.setContentDescription("两小时，已选中");
        } else {
            lyt_time_hour_2.setContentDescription("两小时");
        }
        if (img_tick_custom.getVisibility() == View.VISIBLE) {
            lyt_custom.setContentDescription("自定义，已选中。按钮");
        } else {
            lyt_custom.setContentDescription("自定义。按钮");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_time_1:
                setVisibility(img_tick_1);
                preferences.putInt("TimeLevel", 1);
                preferences.putLong("countDownTime", Const.TIME_1);
                break;
            case R.id.lyt_time_5:
                setVisibility(img_tick_5);
                preferences.putInt("TimeLevel", 5);
                preferences.putLong("countDownTime", Const.TIME_5);
                break;
            case R.id.lyt_time_10:
                setVisibility(img_tick_10);
                preferences.putInt("TimeLevel", 10);
                preferences.putLong("countDownTime", Const.TIME_10);
                break;
            case R.id.lyt_time_15:
                setVisibility(img_tick_15);
                preferences.putInt("TimeLevel", 15);
                preferences.putLong("countDownTime", Const.TIME_15);
                break;
            case R.id.lyt_time_20:
                setVisibility(img_tick_20);
                preferences.putInt("TimeLevel", 20);
                preferences.putLong("countDownTime", Const.TIME_20);
                break;
            case R.id.lyt_time_30:
                setVisibility(img_tick_30);
                preferences.putInt("TimeLevel", 30);
                preferences.putLong("countDownTime", Const.TIME_30);
                break;
            case R.id.lyt_time_45:
                setVisibility(img_tick_45);
                preferences.putInt("TimeLevel", 45);
                preferences.putLong("countDownTime", Const.TIME_45);
                break;
            case R.id.lyt_time_hour_1:
                setVisibility(img_tick_hour_1);
                preferences.putInt("TimeLevel", 60);
                preferences.putLong("countDownTime", Const.TIME_HOUR_1);
                break;
            case R.id.lyt_time_hour_1_1:
                setVisibility(img_tick_hour_1_1);
                preferences.putInt("TimeLevel", 90);
                preferences.putLong("countDownTime", Const.TIME_HOUR_1_1);
                break;
            case R.id.lyt_time_hour_2:
                setVisibility(img_tick_hour_2);
                preferences.putInt("TimeLevel", 120);
                preferences.putLong("countDownTime", Const.TIME_HOUR_2);
                break;
            case R.id.lyt_custom:
                setVisibility(img_tick_custom);
                preferences.putInt("TimeLevel", -1);
                Intent intent = new Intent();
                intent.setClass(this, CustomActivity.class);
                startActivity(intent);
                break;
        }
//        Intent boardCost = new Intent(Const.COUNTDOWN_TIME);
//        boardCost.putExtra("countDownTime", preferences.getLong("countDownTime", Const.TIME_30));
//        sendBroadcast(boardCost);
        finish();
    }

    private void setVisibility(ImageView tick) {
        img_tick_1.setVisibility(View.GONE);
        img_tick_5.setVisibility(View.GONE);
        img_tick_10.setVisibility(View.GONE);
        img_tick_15.setVisibility(View.GONE);
        img_tick_20.setVisibility(View.GONE);
        img_tick_30.setVisibility(View.GONE);
        img_tick_45.setVisibility(View.GONE);
        img_tick_hour_1.setVisibility(View.GONE);
        img_tick_hour_1_1.setVisibility(View.GONE);
        img_tick_hour_2.setVisibility(View.GONE);
        img_tick_custom.setVisibility(View.GONE);
        tick.setVisibility(View.VISIBLE);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
