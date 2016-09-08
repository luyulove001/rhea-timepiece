package net.tatans.rhea.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
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
    @ViewInject(id = R.id.btn_isMassage)
    ImageView btnIsMassage;
    @ViewInject(id = R.id.join_massage_set, click = "onClick")
    RelativeLayout lytMassageToggle;
    private CountDownBean bean;
    private TatansDb tatansDb;
    private CountDownTimerActivity c;
    private Preferences p;
    private boolean isMassage;

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
        isMassage = true;
        String str = c.showTime(p.getLong("countDownTime", Const.TIME_30));
        time_countdown.setText(str);
        lyt_countdown_set.setContentDescription("倒计时设置，" + str + "。按钮");
        str = p.getInt("intervalTime") + "分钟";
        time_interval.setText(str);
        lyt_interval_set.setContentDescription("时间间隔设置，" + str + "。按钮");
        lyt_model_set.setContentDescription("播放模式设置。按钮");
        lytMassageToggle.setContentDescription(getString(R.string.join_massage_set) + "。已开启");
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
                saveBean();
                break;
            case R.id.join_massage_set:
                isMassageToggle();
                break;
            default:
                break;
        }
    }

    /**
     * 保存倒计时bean到数据库并结束界面
     */
    private void saveBean() {
        bean = new CountDownBean();
        bean.setIntervalTime(p.getInt("intervalTime"));
        bean.setCountDownTime((int) (p.getLong("countDownTime", Const.TIME_30) / Const.TIME_1));
        bean.setIsVibrate(p.getBoolean("isVibrate", true));
        bean.setIsSpeaking(p.getBoolean("isSpeaking", true));
        bean.setIsRinging(p.getBoolean("isRinging", true));
        bean.setMassage(isMassage);
        bean.setId(tatansDb.findAll(CountDownBean.class).size());
        tatansDb.save(bean);
        p.putBoolean("isFirst", true);
        finish();
    }

    private void isMassageToggle() {
        if (isMassage) {
            btnIsMassage.setImageResource(R.mipmap.close_icon);
            lytMassageToggle.setContentDescription(getString(R.string.join_massage_set) + "。已关闭");
            TatansToast.showAndCancel("关闭");
            isMassage = false;
        } else {
            btnIsMassage.setImageResource(R.mipmap.open_icon);
            lytMassageToggle.setContentDescription(getString(R.string.join_massage_set) + "。已开启");
            TatansToast.showAndCancel("开启");
            isMassage = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
