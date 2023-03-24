package com.example.calendarcapital;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class RepeatEvents {

    private String id;
    private String name, comment;
    private LocalDate date;
    private LocalTime time;
    private String alarm;
    private String repeat;
    private Date repeatDate;

    public RepeatEvents(String id, String name, String comment, LocalDate date, LocalTime time, String alarm, String repeat, Date repeatDate) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.repeat = repeat;
        this.repeatDate = repeatDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Date getRepeatDate() {
        return repeatDate;
    }

    public void setRepeatDate(Date repeatDate) {
        this.repeatDate = repeatDate;
    }
}
