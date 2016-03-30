package net.tatans.rhea.countdowntimer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.countdowntimer.utils.Preferences;

/**
 * Created by Administrator on 2015/11/1.
 */
public class ModelActivity extends TatansActivity implements View.OnClickListener {
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
        preferences = new Preferences(this);
        initView();
    }

    private void initView() {
        if (preferences.getBoolean("isRinging", false)) {
            img_ringing.setBackgroundResource(R.mipmap.bg_tick);
            lyt_ringing.setContentDescription("音效，开启，点按可切换");
        } else {
            lyt_ringing.setContentDescription("音效，关闭，点按可切换");
            img_ringing.setBackgroundResource(R.mipmap.bg_circle);
        }
        if (preferences.getBoolean("isVibrate", false)) {
            img_vibrate.setBackgroundResource(R.mipmap.bg_tick);
            lyt_vibrate.setContentDescription("震动，开启，点按可切换");
        } else {
            img_vibrate.setBackgroundResource(R.mipmap.bg_circle);
            lyt_vibrate.setContentDescription("震动，关闭，点按可切换");
        }
        if (preferences.getBoolean("isSpeaking", false)) {
            img_speak.setBackgroundResource(R.mipmap.bg_tick);
            lyt_speak.setContentDescription("语音，开启，点按可切换");
        } else {
            img_speak.setBackgroundResource(R.mipmap.bg_circle);
            lyt_speak.setContentDescription("语音，关闭，点按可切换");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_ringing:
                if (!preferences.getBoolean("isVibrate", false) && !preferences.getBoolean("isSpeaking", false)) {
                    TatansToast.showAndCancel("必须保留一个");
                } else {
                    if (!preferences.getBoolean("isRinging", false)) {
                        preferences.putBoolean("isRinging", true);
                        img_ringing.setBackgroundResource(R.mipmap.bg_tick);
                        lyt_ringing.setContentDescription("音效，开启，点按可切换");
                        TatansToast.showAndCancel("音效，开启，点按可切换");
                    } else {
                        preferences.putBoolean("isRinging", false);
                        img_ringing.setBackgroundResource(R.mipmap.bg_circle);
                        lyt_ringing.setContentDescription("音效，关闭，点按可切换");
                        TatansToast.showAndCancel("音效，关闭，点按可切换");
                    }
                }
                break;
            case R.id.lyt_vibrate:
                if (!preferences.getBoolean("isRinging", false) && !preferences.getBoolean("isSpeaking", false)) {
                    TatansToast.showAndCancel("必须保留一个");
                } else {
                    if (!preferences.getBoolean("isVibrate", false)) {
                        preferences.putBoolean("isVibrate", true);
                        img_vibrate.setBackgroundResource(R.mipmap.bg_tick);
                        lyt_vibrate.setContentDescription("震动，开启，点按可切换");
                        TatansToast.showAndCancel("震动，开启，点按可切换");
                    } else {
                        preferences.putBoolean("isVibrate", false);
                        img_vibrate.setBackgroundResource(R.mipmap.bg_circle);
                        lyt_vibrate.setContentDescription("震动，关闭，点按可切换");
                        TatansToast.showAndCancel("震动，关闭，点按可切换");
                    }
                }
                break;
            case R.id.lyt_speak:

                if (!preferences.getBoolean("isVibrate", false) && !preferences.getBoolean("isRinging", false)) {
                    TatansToast.showAndCancel("必须保留一个");
                } else {
                    if (!preferences.getBoolean("isSpeaking", false)) {
                        preferences.putBoolean("isSpeaking", true);
                        img_speak.setBackgroundResource(R.mipmap.bg_tick);
                        lyt_speak.setContentDescription("语音，开启，点按可切换");
                        TatansToast.showAndCancel("语音，开启，点按可切换");
                    } else {
                        preferences.putBoolean("isSpeaking", false);
                        img_speak.setBackgroundResource(R.mipmap.bg_circle);
                        lyt_speak.setContentDescription("语音，关闭，点按可切换");
                        TatansToast.showAndCancel("语音，关闭，点按可切换");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!preferences.getBoolean("isSpeaking", false) && !preferences.getBoolean("isVibrate", false)
                && !preferences.getBoolean("isRinging", false))
            TatansToast.showAndCancel("您当前关闭，点按可切换任何播报提示");
    }
}
