package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
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
    public static ArrayList<HourEvent> hourEventListFromDatabase( MyDatabaseHelper myDB) {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();

        Cursor cursor = myDB.readAllEvents();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");



        while (cursor.moveToNext()) {
            String myDateTest = cursor.getString(3);
            LocalDate dateDB = CalendarUtils.stringToLocalDate(myDateTest);
            LocalTime timeDB = LocalTime.parse(cursor.getString(4));
            String titleDB = cursor.getString(1);
            String commentDB = cursor.getString(2);
            String id_event = cursor.getString(0);
            String alarm = cursor.getString(5);
            String repeat = cursor.getString(6);
            String parent_id = cursor.getString(7);
            Event eventDB = new Event(id_event, titleDB, commentDB, dateDB, timeDB, alarm,repeat,parent_id);

            if (CalendarUtils.formattedDate(selectedDate).equals(CalendarUtils.formattedDate(dateDB)) ) {
                ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                eventarrayDB.add(eventDB);
                HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB,id_event);

                eventsDB.add(hourEventDB);

                Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));


            }

            Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));


        }


    cursor.close();
        myDB.close();

        Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));
        return eventsDB;
    }


    @NonNull
    public static ArrayList<HourEvent> hourEventListFromDatabaseToShowAllEvents(Context context, MyDatabaseHelper myDB) {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllEvents();


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
                String parent_id = cursor.getString(7);
                Event eventDB = new Event(id_event, titleDB, commentDB, dateDB, timeDB, alarm,repeat,parent_id);


                ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                eventarrayDB.add(eventDB);
                HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB,id_event);

                eventsDB.add(hourEventDB);


                Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

            }
            Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

        }


        Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

        cursor.close();
        myDB.close();
        return eventsDB;
    }

    public static ArrayList<HourEvent> hourEventListONLYRepeating(Context context, MyDatabaseHelper myDB)
    {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllEvents();


        if (cursor.getCount() == 0) {
            Toast.makeText(context, "Error read data failed", Toast.LENGTH_SHORT).show();

        } else {
            while (cursor.moveToNext()) {
                if (!(cursor.getString(7) == null)) {
                    LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
                    LocalTime timeDB = LocalTime.parse(cursor.getString(4));
                    String titleDB = cursor.getString(1);
                    String commentDB = cursor.getString(2);
                    String id_event = cursor.getString(0);
                    String alarm = cursor.getString(5);
                    String repeat = cursor.getString(6);
                    String parent_id = cursor.getString(7);
                    Event eventDB = new Event(id_event, titleDB, commentDB, dateDB, timeDB, alarm, repeat, parent_id);


                    ArrayList<Event> eventarrayDB = Event.eventsForDateAndTime(selectedDate, timeDB);

                    eventarrayDB.add(eventDB);
                    HourEvent hourEventDB = new HourEvent(timeDB, eventarrayDB, id_event);

                    eventsDB.add(hourEventDB);


                    Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));
                }
            }
            Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

        }


        Collections.sort(eventsDB, (a, b) -> a.events.get(0).getDate().compareTo(b.events.get(0).getDate()));

        cursor.close();
        myDB.close();
        return eventsDB;
    }

}
