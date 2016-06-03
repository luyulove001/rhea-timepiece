package net.tatans.rhea.countdowntimer;

import android.media.MediaPlayer;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.CrashHandler;
import net.tatans.coeus.network.tools.SoundPoolUtil;
import net.tatans.coeus.network.tools.TatansApplication;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/10/29.
 */
public class CountDownApplication extends TatansApplication {
    private static CountDownApplication sInstance;
    private static boolean isPause = true;
    private static MediaPlayer mediaPlayer;
    private static Speaker speaker;
    private static SoundPoolUtil soundPool;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        speaker = Speaker.getInstance(sInstance);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.initTatans("countdown");
        HashMap<Integer,Integer> map = new HashMap<>();
        map.put(0, R.raw.silence);
        soundPool= new SoundPoolUtil(map);
    }

    public static void soundPlay(){
        stopAll();
        soundPool.soundPlay(0, -1);
    }

    public static void stopAll(){
        soundPool.stopAll();
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
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
        }
    }

}
