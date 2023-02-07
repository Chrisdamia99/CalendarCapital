package com.example.calendarcapital;


import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.ArrayList;

public class HourEvent {


    LocalTime time;
    ArrayList<Event> events;

    public HourEvent(LocalTime time, ArrayList<Event> events) {
        this.time = time;
        this.events = events;
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
