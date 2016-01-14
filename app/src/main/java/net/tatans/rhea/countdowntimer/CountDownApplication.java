package net.tatans.rhea.countdowntimer;

import android.media.MediaPlayer;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.CrashHandler;
import net.tatans.coeus.network.tools.TatansApplication;

/**
 * Created by Administrator on 2015/10/29.
 */
public class CountDownApplication extends TatansApplication {
    private static CountDownApplication sInstance;
    private static boolean isPause = true;
    private static MediaPlayer mediaPlayer;
    private static Speaker speaker;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        speaker = Speaker.getInstance(sInstance);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.initTatans("countdown");
    }

    public static Speaker getSpeaker() {
        if (speaker == null)
            speaker = Speaker.getInstance(sInstance);
        return speaker;
    }

    public static CountDownApplication getInstance() {
        return sInstance;
    }

    public static void setPause(boolean isPause) {
        sInstance.isPause = isPause;
    }

    public static boolean getPause() {
        return isPause;
    }

    public static void playMusic(int id) {
        try {
            mediaPlayer = MediaPlayer.create(sInstance, id);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopPlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
