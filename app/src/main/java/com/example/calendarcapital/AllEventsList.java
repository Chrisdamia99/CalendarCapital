package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class AllEventsList {


    @NonNull
    public static ArrayList<HourEvent> hourEventListFromDatabase(Context context, MyDatabaseHelper myDB) {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllData();


        if (cursor.getCount() == 0) {
            Toast.makeText(context, "Error read data failed", Toast.LENGTH_SHORT).show();
            hourEventList();
        } else {
            while (cursor.moveToNext()) {
                LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
                LocalTime timeDB = LocalTime.parse(cursor.getString(4));
                String titleDB = cursor.getString(1);
                String commentDB = cursor.getString(2);
                String id_event = cursor.getString(0);
                Event eventDB = new Event(id_event,titleDB, commentDB, dateDB, timeDB);

                if (CalendarUtils.monthDayFromDate(selectedDate).equals(CalendarUtils.monthDayFromDate(dateDB)))
                {
                    ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                    eventarrayDB.add(eventDB);
                    HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB);

                    eventsDB.add(hourEventDB);

                    Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

                }



                Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));




            }
        }




        Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));
        return eventsDB;
    }


    @NonNull
    public static ArrayList<HourEvent> hourEventListFromDatabaseToShowAllEvents(Context context, MyDatabaseHelper myDB) {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllData();


        if (cursor.getCount() == 0) {
            Toast.makeText(context, "Error read data failed", Toast.LENGTH_SHORT).show();
            hourEventList();
        } else {
            while (cursor.moveToNext()) {
                LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
                LocalTime timeDB = LocalTime.parse(cursor.getString(4));
                String titleDB = cursor.getString(1);
                String commentDB = cursor.getString(2);
                String id_event = cursor.getString(0);
                Event eventDB = new Event(id_event,titleDB, commentDB, dateDB, timeDB);



                    ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                    eventarrayDB.add(eventDB);
                    HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB);

                    eventsDB.add(hourEventDB);







            }
        }



        Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));


        return eventsDB;
    }


    public static ArrayList<HourEvent> hourEventList() {

        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }
        return list;
    }


    private ArrayList<MonthEvent> monthEventList() {
        ArrayList<MonthEvent> list = new ArrayList<>();

        for (int month = 0; month < 42; month++) {
            LocalDate date = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDayOfMonth());
            ArrayList<Event> events = Event.eventsForDate(date);
            MonthEvent monthEvent = new MonthEvent(date, events);
            list.add(monthEvent);
        }
        return list;

    }
}
