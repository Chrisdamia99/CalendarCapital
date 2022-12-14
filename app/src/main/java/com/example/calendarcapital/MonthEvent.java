package com.example.calendarcapital;

import java.time.LocalDate;
import java.util.ArrayList;

public class MonthEvent {

    LocalDate date;
    ArrayList<Event> events;

    public MonthEvent(LocalDate date, ArrayList<Event> events) {
        this.date = date;
        this.events = events;
    }

    public LocalDate getTime() {
        return date;
    }

    public void setTime(LocalDate time) {
        this.date = time;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
