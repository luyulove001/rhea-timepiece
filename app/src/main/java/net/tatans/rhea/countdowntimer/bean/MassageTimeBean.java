package net.tatans.rhea.countdowntimer.bean;

import net.tatans.coeus.annotation.sqlite.Table;

/**
 * Created by cly on 2016/9/1.
 */
@Table(name = "MassageTime")
public class MassageTimeBean {
    private String id;//yyyyMMddHHmm
    private int year;
    private int month;
    private int day;
    private String startTime;//用 HH:mm 的int型
    private int duration;//持续时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MassageTimeBean{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
