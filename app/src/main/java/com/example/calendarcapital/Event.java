package com.example.calendarcapital;


import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Event {


    public static ArrayList<Event> eventsList = new ArrayList<>();
    Map<Event, List<Event>> eventData = new LinkedHashMap<>();

    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventsList) {
            if (event.getDate().equals(date))

                events.add(event);
        }
        return events;
    }



    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventsList) {
            int eventHour = event.time.getHour();
            int cellHour = time.getHour();
            if (event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }
        return events;
    }

    private String id;
    private String name, comment;
    private LocalDate date;
    private LocalTime time;
    private String alarm;
    private String repeat;
    private String parent_id;

    public Event(String id, String name, String comment, LocalDate date, LocalTime time, String alarm,String repeat,String parent_id) {
        this.id = id;
        this.comment = comment;
        this.name = name;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.repeat = repeat;
        this.parent_id = parent_id;


    }

    public Event(String id, String name, String comment, LocalDate date, LocalTime time, String alarm, String parent_id) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.parent_id = parent_id;
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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
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

    public void setName(String name) {
        this.name = name;
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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
