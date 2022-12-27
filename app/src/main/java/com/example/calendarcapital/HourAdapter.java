package com.example.calendarcapital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    Context context = this.getContext();
    MyDatabaseHelper myDB = new MyDatabaseHelper(context);

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents) {
        super(context, 0, hourEvents);

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);


        compareAndGetValuesFromDB(convertView,event.time);


        setEvents(convertView, event.events);


        return convertView;
    }




            private void compareAndGetValuesFromDB(View convertView, LocalTime time)
             { Cursor cursor = myDB.readAllData();


                 if (cursor.getCount() == 0)
                 {
                     try{

                             setHour(convertView,time);

                         Toast.makeText(context, "No data to present.", Toast.LENGTH_SHORT).show();
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }else
                 {
                     while (cursor.moveToNext())
                     {
                         if (LocalTime.parse(cursor.getString(4)).equals(time))
                         {
                             Event eventDB = new Event(cursor.getString(0),cursor.getString(1),cursor.getString(2),
                                     CalendarUtils.stringToLocalDate(cursor.getString(3)),LocalTime.parse(cursor.getString(4)));
                             ArrayList<Event> eventArrayDB = new ArrayList<>();
                             eventArrayDB.add(eventDB);
                             HourEvent event = new HourEvent(LocalTime.parse(cursor.getString(4)),eventArrayDB);
                             event.setEvents(eventArrayDB);
                             event.setTime(LocalTime.parse(cursor.getString(4)));
                             //Καλείτε η setHour και δίνω στην λίστα τις τιμές της ώρες από την βάση δεδομένων και το αντίστοιχο event
                             setHour(convertView,time);



                         }

                     }
                 }



                }



    private void setHour(View convertView, LocalTime time) {


        TextView timeTv = convertView.findViewById(R.id.timeTV);
        TextView dayofmonthTV = convertView.findViewById(R.id.dateTV);


        timeTv.setText(CalendarUtils.formattedShortTime(time));
        dayofmonthTV.setText(CalendarUtils.DailyViewFormattedDate(CalendarUtils.selectedDate));

    }
    private void setEvents(View convertView, ArrayList<Event> events) {
        TextView event1 = convertView.findViewById(R.id.event1);
        TextView event2 = convertView.findViewById(R.id.event2);
        TextView event3 = convertView.findViewById(R.id.event3);
        TextView comment1 = convertView.findViewById(R.id.comment1);
        TextView comment2 = convertView.findViewById(R.id.comment2);
        TextView comment3 = convertView.findViewById(R.id.coment3);

        if (events.size() == 0)
        {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
            hideEvent(comment1);
            hideEvent(comment2);
            hideEvent(comment3);
        }else if (events.size() == 1)
        {
            setEvent(event1, events.get(0));
            setComment(comment1, events.get(0));
            hideEvent(comment2);
            hideEvent(comment3);

            hideEvent(event2);
            hideEvent(event3);

        }
        else if (events.size() == 2)
        {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            hideEvent(comment3);
            hideEvent(event3);


        }
        else if (events.size() == 3)
        {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            setComment(comment3, events.get(2));
        }
        else
        {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(events.size() - 2);
            eventsNotShown += " More Events";
            event3.setText(eventsNotShown);

            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            comment3.setVisibility(View.VISIBLE);
            String commentsNotShown = String.valueOf(events.size() -2);
            commentsNotShown += " More Comments";
            comment3.setText(commentsNotShown);
        }



    }

    private void setEvent(TextView textView, Event event) {

        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);

    }

    private void setComment(TextView textView, Event event){

        textView.setText(event.getComment());
        textView.setVisibility(View.VISIBLE);

    }



    private void hideEvent(TextView tv)
    {
        tv.setVisibility(View.INVISIBLE);
    }
}
