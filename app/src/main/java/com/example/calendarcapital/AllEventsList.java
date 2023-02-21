package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class AllEventsList {

    public static void reloadActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);

    }


    @NonNull
    public static ArrayList<HourEvent> hourEventListFromDatabase(Context context, MyDatabaseHelper myDB) {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllData();


        while (cursor.moveToNext()) {
            LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
            LocalTime timeDB = LocalTime.parse(cursor.getString(4));
            String titleDB = cursor.getString(1);
            String commentDB = cursor.getString(2);
            String id_event = cursor.getString(0);
            String alarm = cursor.getString(5);
            String repeat = cursor.getString(6);
            Event eventDB = new Event(id_event, titleDB, commentDB, dateDB, timeDB, alarm,repeat);

            if (CalendarUtils.formattedDate(selectedDate).equals(CalendarUtils.formattedDate(dateDB))) {
                ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                eventarrayDB.add(eventDB);
                HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB);

                eventsDB.add(hourEventDB);

                Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

            }


            Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));


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

        } else {
            while (cursor.moveToNext()) {
                LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
                LocalTime timeDB = LocalTime.parse(cursor.getString(4));
                String titleDB = cursor.getString(1);
                String commentDB = cursor.getString(2);
                String id_event = cursor.getString(0);
                String alarm = cursor.getString(5);
                String repeat = cursor.getString(6);
                Event eventDB = new Event(id_event, titleDB, commentDB, dateDB, timeDB, alarm,repeat);


                ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                eventarrayDB.add(eventDB);
                HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB);

                eventsDB.add(hourEventDB);


                Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

            }
            Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

        }


        Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));


        return eventsDB;
    }


}
