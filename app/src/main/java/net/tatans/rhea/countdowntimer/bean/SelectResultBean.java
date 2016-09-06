package net.tatans.rhea.countdowntimer.bean;

/**
 * 用于保存sql语句查询出来的结果
 * select id, year, month, day, startTime, sum(duration) as duration from MassageTime group by year, month(, day)
 * Created by cly on 2016/9/5.
 */
public class SelectResultBean {
    private String time;//时间，如 2016年9月 或 2016年9月5日
    private int duration;//按摩总时长

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
