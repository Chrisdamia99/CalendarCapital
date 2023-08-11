package com.example.calendarcapital;


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
        String eventColor = getItem(position).getEvents().get(0).getColor();
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);


        compareAndGetValuesFromDBWithId(event.getId());


//        setHour(convertView,event.time,selectedDate);
        setHour(convertView,event.time,event.getEvents().get(0).getDate());
        setEvents(convertView, event.events,eventColor);

        return convertView;
    }



    private void compareAndGetValuesFromDBWithId(String id) {
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
                    Event eventDB = null;

                        eventDB = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)),
                                cursor.getString(5), cursor.getString(6),
                                cursor.getString(7),cursor.getString(8),cursor.getString(9));

                    ArrayList<Event> eventArrayDB = new ArrayList<>();


                    LocalTime cursorTime = null;

                        cursorTime = LocalTime.parse(cursor.getString(4));


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


private void setEvents(View convertView, ArrayList<Event> events,String color) {
    TextView event1 = convertView.findViewById(R.id.event1);

    TextView comment1 = convertView.findViewById(R.id.comment1);


    if (events.size() == 0) {
        hideEvent(event1);
        hideEvent(comment1);

    } else if (events.size() == 1) {
        setEvent(event1, events.get(0),color);
        setComment(comment1, events.get(0),color);


    }


    if (comment1.getText().toString().isEmpty()) {
        comment1.setVisibility(View.GONE);
    }


}

    private void setEvent(TextView textView, Event event,String color) {

        if (event.getName().equals(""))
        {

            textView.setVisibility(View.GONE);
        }else
        {
            textView.setText(event.getName());
            textView.setVisibility(View.VISIBLE);
        }
        switch (color) {
            case "0":
                textView.setBackgroundResource(R.drawable.rounded_corner);
                break;
            case "1":
                textView.setBackgroundResource(R.drawable.rounded_corner_red);

                break;
            case "2":
                textView.setBackgroundResource(R.drawable.rounded_corner_yellow);

                break;
            case "3":
                textView.setBackgroundResource(R.drawable.rounded_corner_green);

                break;
            case "4":
                textView.setBackgroundResource(R.drawable.rounded_corner_blue);

                break;
            case "5":
                textView.setBackgroundResource(R.drawable.rounded_corner_purple);
                break;
            default:
                textView.setBackgroundResource(R.drawable.rounded_corner);

                break;
        }
        notifyDataSetChanged();

    }

    private void setComment(TextView textView, Event event,String color) {
        if (event.getComment().equals(""))
        {

            textView.setVisibility(View.GONE);
        }else {
            textView.setText(event.getComment());
            textView.setVisibility(View.VISIBLE);
        }

        switch (color) {
            case "0":
                textView.setBackgroundResource(R.drawable.rounded_corner);
                break;
            case "1":
                textView.setBackgroundResource(R.drawable.rounded_corner_red);

                break;
            case "2":
                textView.setBackgroundResource(R.drawable.rounded_corner_yellow);

                break;
            case "3":
                textView.setBackgroundResource(R.drawable.rounded_corner_green);

                break;
            case "4":
                textView.setBackgroundResource(R.drawable.rounded_corner_blue);

                break;
            case "5":
                textView.setBackgroundResource(R.drawable.rounded_corner_purple);
                break;
            default:
                textView.setBackgroundResource(R.drawable.rounded_corner);

                break;
        }
    }


    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }
}
