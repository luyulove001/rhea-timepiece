package bean;

import net.tatans.coeus.annotation.sqlite.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/24.
 */
@Table(name = "CountDownTime")
public class CountDownBean implements Serializable {
    private int id;
    private int countDownTime;
    private int intervalTime;
    private boolean isSpeaking;
    private boolean isRinging;
    private boolean isVibrate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(int countDownTime) {
        this.countDownTime = countDownTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setIsSpeaking(boolean isSpeaking) {
        this.isSpeaking = isSpeaking;
    }

    public boolean isRinging() {
        return isRinging;
    }

    public void setIsRinging(boolean isRinging) {
        this.isRinging = isRinging;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setIsVibrate(boolean isVibrate) {
        this.isVibrate = isVibrate;
    }

    @Override
    public String toString() {
        return "CountDownBean{" +
                "id=" + id +
                ", countDownTime=" + countDownTime +
                ", intervalTime=" + intervalTime +
                ", isSpeaking=" + isSpeaking +
                ", isRinging=" + isRinging +
                ", isVibrate=" + isVibrate +
                '}';
    }
}
