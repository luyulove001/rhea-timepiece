package net.tatans.rhea.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Preferences;

import net.tatans.rhea.countdowntimer.bean.CountDownBean;

/**
 * Created by Administrator on 2015/10/26.
 */
public class SettingActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.layout_countdown_set, click = "onClick")
    RelativeLayout lyt_countdown_set;
    @ViewInject(id = R.id.layout_interval_set, click = "onClick")
    RelativeLayout lyt_interval_set;
    @ViewInject(id = R.id.layout_model_set, click = "onClick")
    RelativeLayout lyt_model_set;
    @ViewInject(id = R.id.time_countdown)
    TextView time_countdown;
    @ViewInject(id = R.id.time_interval)
    TextView time_interval;
    @ViewInject(id = R.id.lyt_confirm, click = "onClick")
    LinearLayout lyt_confirm;
    private CountDownBean bean;
    private TatansDb tatansDb;
    private CountDownTimerActivity c;
    private Preferences p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);
        setTitle("设置");
        setResult(RESULT_OK);
        tatansDb = TatansDb.create(Const.CountDownDB);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        c = new CountDownTimerActivity();
        p = new Preferences(this);
        String str = c.showTime(p.getLong("countDownTime", Const.TIME_30));
        time_countdown.setText(str);
        lyt_countdown_set.setContentDescription("倒计时设置，" + str + "。按钮");
        str = p.getInt("intervalTime") + "分钟";
        time_interval.setText(str);
        lyt_interval_set.setContentDescription("时间间隔设置，" + str + "。按钮");
        lyt_model_set.setContentDescription("播放模式设置。按钮");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.layout_countdown_set:
                intent.setClass(this, CountDownSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_interval_set:
                intent.setClass(this, IntervalSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_model_set:
                intent.setClass(this, ModelActivity.class);
                startActivity(intent);
                break;
            case R.id.lyt_confirm:
                bean = new CountDownBean();
                bean.setIntervalTime(p.getInt("intervalTime"));
                bean.setCountDownTime((int) (p.getLong("countDownTime", Const.TIME_30) / Const.TIME_1));
                bean.setIsVibrate(p.getBoolean("isVibrate", true));
                bean.setIsSpeaking(p.getBoolean("isSpeaking", true));
                bean.setIsRinging(p.getBoolean("isRinging", true));
                bean.setId(tatansDb.findAll(CountDownBean.class).size());
                tatansDb.save(bean);
                p.putBoolean("isFirst", true);
                finish();
                break;
            default:
                break;
        }
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
