package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import android.content.Context;
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


        compareAndGetValuesFromDBWithId(convertView, event.getId(), event.getTime());


        setHour(convertView,event.time,selectedDate);
        setEvents(convertView, event.events);

        return convertView;
    }



    private void compareAndGetValuesFromDBWithId(View convertView, String id, LocalTime time) {
        Cursor cursor = myDB.readAllEvents();


        if (cursor.getCount() == 0) {
            try {


                Toast.makeText(context, "No data to present.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            while (cursor.moveToNext()) {

                if (cursor.getString(0).equals(id)) {
                    Event eventDB = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)),
                            cursor.getString(5), cursor.getString(6),cursor.getString(7));
                    ArrayList<Event> eventArrayDB = new ArrayList<>();


                    LocalTime cursorTime = LocalTime.parse(cursor.getString(4));

                    String cursorId = cursor.getString(0);
                    eventArrayDB.add(eventDB);

                    HourEvent event = new HourEvent(cursorTime, eventArrayDB, cursorId);

                    event.setEvents(eventArrayDB);
                    event.setTime(cursorTime);


                }

            }
        }

        cursor.close();
        myDB.close();
    }





    private  void setHour(View convertView, LocalTime time, LocalDate date) {


        TextView timeTv = convertView.findViewById(R.id.timeTV);
        TextView dayofmonthTV = convertView.findViewById(R.id.dateTV);


        timeTv.setText(CalendarUtils.formattedShortTime(time));
        dayofmonthTV.setText(CalendarUtils.formattedDate(date));

    }


    private void setEvents(View convertView, ArrayList<Event> events) {
        TextView event1 = convertView.findViewById(R.id.event1);
        TextView event2 = convertView.findViewById(R.id.event2);
        TextView event3 = convertView.findViewById(R.id.event3);
        TextView comment1 = convertView.findViewById(R.id.comment1);
        TextView comment2 = convertView.findViewById(R.id.comment2);
        TextView comment3 = convertView.findViewById(R.id.coment3);

        if (events.size() == 0) {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
            hideEvent(comment1);
            hideEvent(comment2);
            hideEvent(comment3);
        } else if (events.size() == 1) {
            setEvent(event1, events.get(0));
            setComment(comment1, events.get(0));
            hideEvent(comment2);
            hideEvent(comment3);

            hideEvent(event2);
            hideEvent(event3);

        } else if (events.size() == 2) {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            hideEvent(comment3);
            hideEvent(event3);


        } else if (events.size() == 3) {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            setComment(comment3, events.get(2));
        } else {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(events.size() - 2);
            eventsNotShown += " More Events";
            event3.setText(eventsNotShown);

            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            comment3.setVisibility(View.VISIBLE);
            String commentsNotShown = String.valueOf(events.size() - 2);
            commentsNotShown += " More Comments";
            comment3.setText(commentsNotShown);
        }

        if (comment1.getText().toString().isEmpty()) {
            comment1.setVisibility(View.GONE);
        }


    }

    private void setEvent(TextView textView, Event event) {

        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);

    }

    private void setComment(TextView textView, Event event) {

        textView.setText(event.getComment());
        textView.setVisibility(View.VISIBLE);

    }


    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }
}
