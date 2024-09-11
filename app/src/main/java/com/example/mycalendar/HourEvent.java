package com.example.mycalendar;


import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.ArrayList;

public class HourEvent {

    String id;
    LocalTime time;
    ArrayList<Event> events;


    public HourEvent(LocalTime time, ArrayList<Event> events, String id) {
        this.time = time;
        this.events = events;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public String toString() {
        return events.toString();
    }
}
