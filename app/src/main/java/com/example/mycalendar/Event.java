package com.example.mycalendar;


import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {






    private String id;
    private String name, comment;
    private LocalDate date;
    private LocalTime time;
    private String alarm;
    private String parent_id;
    private String color;
    private String location;

    public Event(String id, String name, String comment, LocalDate date, LocalTime time, String alarm,String repeat,String parent_id,String color,String location) {
        this.id = id;
        this.comment = comment;
        this.name = name;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.parent_id = parent_id;
        this.color = color;
        this.location = location;


    }

    public Event(String id, String name, String comment, LocalDate date, LocalTime time, String alarm, String parent_id,String location) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.parent_id = parent_id;
        this.location=location;
    }

    public Event(String name, String comment, LocalDate date, LocalTime time) {
        this.comment = comment;
        this.name = name;
        this.date = date;
        this.time = time;

    }


    @NonNull
    @Override
    public String toString() {
        return name + "\n" + comment + "\n" + date + "\n" + CalendarUtils.formattedShortTime(time);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getParent_id() {
        return parent_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

}
