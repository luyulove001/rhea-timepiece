package net.tatans.rhea.countdowntimer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Preferences;

/**
 * Created by Administrator on 2015/11/1.
 */
public class ModelActivity extends TatansActivity implements View.OnClickListener {
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;
    @ViewInject(id = R.id.lyt_ringing)
    RelativeLayout lyt_ringing;
    @ViewInject(id = R.id.lyt_vibrate)
    RelativeLayout lyt_vibrate;
    @ViewInject(id = R.id.lyt_speak)
    RelativeLayout lyt_speak;
    @ViewInject(id = R.id.img_ringing)
    ImageView img_ringing;
    @ViewInject(id = R.id.img_speak)
    ImageView img_speak;
    @ViewInject(id = R.id.img_vibrate)
    ImageView img_vibrate;

    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        setTitle("播放模式设置");
        tv_title.setText("播放模式设置");
        preferences = new Preferences(this);
        initView();
    }

    private void initView() {
        if (preferences.getBoolean("isRinging", false)) {
            img_ringing.setBackgroundResource(R.drawable.bg_tick);
            lyt_ringing.setContentDescription("音效，已选中");
        } else {
            lyt_ringing.setContentDescription("音效，未选中");
            img_ringing.setBackgroundResource(R.drawable.bg_circle);
        }
        if (preferences.getBoolean("isVibrate", false)) {
            img_vibrate.setBackgroundResource(R.drawable.bg_tick);
            lyt_vibrate.setContentDescription("震动，已选中");
        } else {
            img_vibrate.setBackgroundResource(R.drawable.bg_circle);
            lyt_vibrate.setContentDescription("震动，未选中");
        }
        if (preferences.getBoolean("isSpeaking", false)) {
            img_speak.setBackgroundResource(R.drawable.bg_tick);
            lyt_speak.setContentDescription("语音，已选中");
        } else {
            img_speak.setBackgroundResource(R.drawable.bg_circle);
            lyt_speak.setContentDescription("语音，未选中");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_ringing:
                if (!preferences.getBoolean("isRinging", false)) {
                    preferences.putBoolean("isRinging", true);
                    img_ringing.setBackgroundResource(R.drawable.bg_tick);
                    lyt_ringing.setContentDescription("音效，已选中");
                    Toast.makeText(this, "音效，已选中", Toast.LENGTH_SHORT).show();
                } else {
                    preferences.putBoolean("isRinging", false);
                    img_ringing.setBackgroundResource(R.drawable.bg_circle);
                    lyt_ringing.setContentDescription("音效，未选中");
                    Toast.makeText(this, "音效，未选中", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lyt_vibrate:
                if (!preferences.getBoolean("isVibrate", false)) {
                    preferences.putBoolean("isVibrate", true);
                    img_vibrate.setBackgroundResource(R.drawable.bg_tick);
                    lyt_vibrate.setContentDescription("震动，已选中");
                    Toast.makeText(this, "震动，已选中", Toast.LENGTH_SHORT).show();
                } else {
                    preferences.putBoolean("isVibrate", false);
                    img_vibrate.setBackgroundResource(R.drawable.bg_circle);
                    lyt_vibrate.setContentDescription("震动，未选中");
                    Toast.makeText(this, "震动，未选中", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lyt_speak:
                if (!preferences.getBoolean("isSpeaking", false)) {
                    preferences.putBoolean("isSpeaking", true);
                    img_speak.setBackgroundResource(R.drawable.bg_tick);
                    lyt_speak.setContentDescription("语音，已选中");
                    Toast.makeText(this, "语音，已选中", Toast.LENGTH_SHORT).show();
                } else {
                    preferences.putBoolean("isSpeaking", false);
                    img_speak.setBackgroundResource(R.drawable.bg_circle);
                    lyt_speak.setContentDescription("语音，未选中");
                    Toast.makeText(this, "语音，未选中", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}