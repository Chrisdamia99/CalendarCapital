package com.example.calendarcapital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class MonthAdapter extends ArrayAdapter<MonthEvent> {


    public MonthAdapter(@NonNull Context context, List<MonthEvent> monthEvent) {
        super(context, 0, monthEvent);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MonthEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.month_cell, parent, false);

        setMonth(convertView,event.date);
        setEvents(convertView, event.events);

        return convertView;
    }



    private void setMonth(View convertView, LocalDate date) {

        TextView monthTv = convertView.findViewById(R.id.month_cell_TV);
        monthTv.setText(CalendarUtils.formattedDate(date));
    }
    private void setEvents(View convertView, ArrayList<Event> events) {
        TextView event1 = convertView.findViewById(R.id.month_event1);
        TextView event2 = convertView.findViewById(R.id.month_event2);
        TextView event3 = convertView.findViewById(R.id.month_event3);

        if (events.size() == 0)
        {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
        }else if (events.size() == 1)
        {
            setEvent(event1, events.get(0));
            hideEvent(event2);
            hideEvent(event3);

        }
        else if (events.size() == 2)
        {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            hideEvent(event3);


        }
        else if (events.size() == 3)
        {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
        }
        else
        {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(events.size() - 2);
            eventsNotShown += " More Events";
            event3.setText(eventsNotShown);
        }



    }

    private void setEvent(TextView textView, Event event) {
        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);

    }

    private void hideEvent(TextView tv)
    {
        tv.setVisibility(View.INVISIBLE);
    }

}
