package net.tatans.rhea.countdowntimer;

import android.app.Application;
import android.media.MediaPlayer;

/**
 * Created by Administrator on 2015/10/29.
 */
public class CountDownApplication extends Application {
    private static CountDownApplication sInstance;
    private static boolean isPause = true;
    private static MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
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
