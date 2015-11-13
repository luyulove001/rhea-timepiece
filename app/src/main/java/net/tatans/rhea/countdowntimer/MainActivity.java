package net.tatans.rhea.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Const;
import net.tatans.rhea.utils.Preferences;


public class MainActivity extends TatansActivity implements OnClickListener {
    @ViewInject(id = R.id.start_time, click = "onClick")
    LinearLayout start_time;
    @ViewInject(id = R.id.setting, click = "onClick")
    LinearLayout setting;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 计算计时时间
     */
    private void initView() {
        preferences = new Preferences(this);
        long time = preferences.getLong("countDownTime", Const.TIME_30);
        CountDownTimerActivity cdt = new CountDownTimerActivity();
        start_time.setContentDescription("开始计时," + cdt.showTime(time));
        if (preferences.getInt("intervalTime") == 0){
            preferences.putInt("intervalTime", 1);
        }
        if(preferences.getBoolean("isFirst", false)){
            preferences.putBoolean("isRinging", true);
            preferences.putBoolean("isVibrate", true);
            preferences.putBoolean("isSpeaking", true);
            preferences.putBoolean("isFirst", true);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.start_time:
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
