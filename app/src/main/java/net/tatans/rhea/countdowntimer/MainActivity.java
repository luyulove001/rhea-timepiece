package net.tatans.rhea.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Const;
import net.tatans.rhea.utils.Preferences;
import net.tatans.rhea.utils.Util;

import java.util.ArrayList;
import java.util.List;

import adapter.CountDownAdapter;
import bean.CountDownBean;


public class MainActivity extends TatansActivity implements OnClickListener {
    @ViewInject(id = R.id.start_time, click = "onClick")
    LinearLayout start_time;
    @ViewInject(id = R.id.setting, click = "onClick")
    LinearLayout setting;
    @ViewInject(id = R.id.lv_countdown_time)
    ListView lv_countdown_time;
    private Preferences preferences;
    private List<CountDownBean> al_countDown = new ArrayList<>();
    private CountDownAdapter countDownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setTitle("首页");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        isServiceAlive();
    }

    /**
     * 计算计时时间
     */
    private void initView() {
        preferences = new Preferences(this);
        long time = preferences.getLong("countDownTime", Const.TIME_30);
        CountDownTimerActivity cdt = new CountDownTimerActivity();
        start_time.setContentDescription("开始计时," + cdt.showTime(time) + "。按钮");
        setting.setContentDescription("设置。按钮");
        if (preferences.getInt("intervalTime") == 0){
            preferences.putInt("intervalTime", 1);
        }
        if(!preferences.getBoolean("isFirst", false)){
            preferences.putBoolean("isRinging", true);
            preferences.putBoolean("isVibrate", true);
            preferences.putBoolean("isSpeaking", true);
            preferences.putBoolean("isFirst", true);
            initCountDownData();
        }
    }

    private void initCountDownData() {
        TatansDb tdb = TatansDb.create(Const.CountDown_DB);
        CountDownBean countDownBean = new CountDownBean();
        int[] countDownTime = new int[] {30, 60, 120};
        for (int i = 0; i < countDownTime.length; i++) {
            countDownBean.setId(i);
            countDownBean.setCountDownTime(countDownTime[i]);
            countDownBean.setIntervalTime(1);
            countDownBean.setIsRinging(true);
            countDownBean.setIsSpeaking(true);
            countDownBean.setIsVibrate(true);
            tdb.save(countDownBean);
        }
        al_countDown = tdb.findAll(CountDownBean.class);
//        countDownAdapter = new CountDownAdapter(this);
    }

    private void isServiceAlive(){
        if (Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE)){
            Intent intent = new Intent(this, CountDownTimerActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.start_time:
                TatansToast.showAndCancel("计时开始");
                intent.setClass(this, CountDownTimerActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
