package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AllEventsList {

    public static void reloadActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);

    }


    public static void hourEventRepeating(MyDatabaseHelper myDB)
    {ArrayList<HourEvent> eventsDB = new ArrayList<>();
        ArrayList<Event> eventTest = new ArrayList<>();
        ArrayList<Repeat> repeatTest = new ArrayList<>();
        ArrayList<RepeatEvents> repeatEventsTest = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Cursor cursor = myDB.readAllData();
        Cursor cursorRepeat = myDB.readAllRepeat();

        while (cursor.moveToNext())
        {LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
            LocalTime timeDB = LocalTime.parse(cursor.getString(4));
            String titleDB = cursor.getString(1);
            String commentDB = cursor.getString(2);
            String id_event = cursor.getString(0);
            String alarm = cursor.getString(5);
            String repeat = cursor.getString(6);
            Event eventTestDB = new Event(id_event, titleDB, commentDB, dateDB, timeDB, alarm,repeat);
            eventTest.add(eventTestDB);
        }

        while (cursorRepeat.moveToNext())
        {
            try {
                String id = cursorRepeat.getString(0);
                String eventId = cursorRepeat.getString(1);
                Date repeatDate = format.parse(cursorRepeat.getString(2));

                Repeat repeatTestDB = new Repeat(id,eventId,repeatDate);
                repeatTest.add(repeatTestDB);
            }catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        for (int e=0; e<eventTest.size(); e++)
        {
            for (int r=0; r<repeatTest.size(); r++)
            {
                if (eventTest.get(e).getId().equals(repeatTest.get(r).getEvent_id()))
                {
                    String id = eventTest.get(e).getId();
                    String tittle = eventTest.get(e).getName();
                    String comment = eventTest.get(e).getComment();
                    LocalDate localDate = eventTest.get(e).getDate();
                    LocalTime localTime = eventTest.get(e).getTime();
                    String alarm = eventTest.get(e).getAlarm();
                    String repeat = eventTest.get(e).getRepeat();
                    Date repeatDate = repeatTest.get(r).getRepeatDate();
                    RepeatEvents RE = new RepeatEvents(id,tittle,comment,localDate,localTime,alarm,repeat,repeatDate);
                    repeatEventsTest.add(RE);
                }
            }
        }
                repeatEventsTest.size();
    }
    @NonNull
    public static ArrayList<HourEvent> hourEventListFromDatabase(Context context, MyDatabaseHelper myDB) {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllData();

        hourEventRepeating(myDB);
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


}
