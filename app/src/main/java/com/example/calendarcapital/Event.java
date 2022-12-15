package com.example.calendarcapital;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {



    public static ArrayList<Event> eventsList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if (event.getDate().equals(date))

                events.add(event);
        }
        return events;
    }

    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            int eventHour = event.time.getHour();
            int cellHour = time.getHour();
            if (event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }
        return events;
    }

    private String name,comment;
    private LocalDate date;
    private LocalTime time;

    public Event(String name,String comment, LocalDate date, LocalTime time) {
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
}
