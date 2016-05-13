package net.tatans.rhea.countdowntimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.rhea.countdowntimer.bean.CountDownBean;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Preferences;
import net.tatans.rhea.countdowntimer.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/18.
 */
public class LockReceiver extends BroadcastReceiver {
    private Intent service;
    private Preferences preferences;
    private CountDownBean bean = new CountDownBean();
    private TatansDb tdb;
    private List<CountDownBean> al_countDown;

    public LockReceiver() {
        super();
        service = new Intent(CountDownApplication.getInstance(), CountDownService.class);
        preferences = new Preferences(CountDownApplication.getInstance());
        tdb = TatansDb.create(Const.CountDown_DB);
        al_countDown = new ArrayList<>();
        al_countDown = tdb.findAll(CountDownBean.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Const.CLICK_START.equals(intent.getAction())) {
            if (!Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE)) {
                if (al_countDown.size() != 0) {
                    bean = al_countDown.get(0);
                } else {
                    bean.setId(0);
                    bean.setCountDownTime((int) (intent.getLongExtra("countDownTime",
                            preferences.getLong("countDownTime", Const.TIME_30)) / 60000));
                    bean.setIntervalTime(30);
                    bean.setIsRinging(true);
                    bean.setIsSpeaking(true);
                    bean.setIsVibrate(true);
                }
                service.putExtra("countDown_scheme", bean);
                service.putExtra("countDownTime", intent.getLongExtra("countDownTime",
                        preferences.getLong("countDownTime", Const.TIME_30)));
                CountDownApplication.getInstance().startService(service);
            }
        } else if (Const.CLOCK_PAUSE.equals(intent.getAction())) {
            CountDownApplication.getInstance().stopService(service);
        } else if (Const.CLOCK_RESTART.equals(intent.getAction())) {
            CountDownApplication.getInstance().stopService(service);
        }
    }
}
