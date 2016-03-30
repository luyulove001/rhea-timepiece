package net.tatans.rhea.countdowntimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Preferences;
import net.tatans.rhea.countdowntimer.utils.Util;

/**
 * Created by Administrator on 2015/12/18.
 */
public class LockReceiver extends BroadcastReceiver {
    private Intent service;
    private Preferences preferences;

    public LockReceiver() {
        super();
        service = new Intent(CountDownApplication.getInstance(), CountDownService.class);
        preferences = new Preferences(CountDownApplication.getInstance());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Const.CLICK_START.equals(intent.getAction())) {
            if (!Util.isServiceWork(CountDownApplication.getInstance(), Const.COUNTDOWN_SERVICE)) {
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
