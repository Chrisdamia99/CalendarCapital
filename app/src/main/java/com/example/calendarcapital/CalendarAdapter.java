package com.example.calendarcapital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> //Extends the recyclerview that
        // do adapt from class calendarviewholder
{
    private final ArrayList<LocalDate> days;

    private final OnItemListener onItemListener;
    private MyDatabaseHelper myDB;
    Context context;


    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, Context context) {
        this.days = days;
        this.onItemListener = onItemListener;
        this.myDB = new MyDatabaseHelper(context.getApplicationContext());
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Calendar cell take shape and inflated

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();


        if (days.size() > 15) //month view
        {
//            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
            layoutParams.height = (int) (parent.getHeight() * 0.2);

        } else // week view
            layoutParams.height = (int) parent.getHeight();


        return new CalendarViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        Cursor cursor = myDB.readAllData();
        holder.eventDayText.setVisibility(View.GONE);
        holder.eventDayText2.setVisibility(View.GONE);


        final LocalDate date = days.get(position);

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if (date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY);
        if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.BLACK);
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
        ArrayList<Event> myEvents = new ArrayList<>();
        while (cursor.moveToNext()) {
            Event eventDB = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)));
            myEvents.add(eventDB);
        }


        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {






            if (myEvents.size()<=2)
            {
                if (myEvents.size()<2) {
                    if (myEvents.get(0).getDate().equals(date) && LocalTime.parse(cursor.getString(4)).equals(myEvents.get(0).getTime())) {
                        holder.eventDayText.setVisibility(View.VISIBLE);
                        holder.eventDayText.setText(myEvents.get(0).getName());
                        holder.eventDayText.setTextColor(Color.BLUE);
                        holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);
                    }
                }else {
                for (int i = myEvents.size()-1; i >= 0; i--) {
                    if (myEvents.get(i).getDate().equals(date) && LocalTime.parse(cursor.getString(4)).equals(myEvents.get(i).getTime())) {


                        holder.eventDayText.setVisibility(View.VISIBLE);
                        holder.eventDayText.setText(myEvents.get(0).getName());
                        holder.eventDayText.setTextColor(Color.BLUE);
                        holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);

                        holder.eventDayText2.setVisibility(View.VISIBLE);
                        holder.eventDayText2.setText(myEvents.get(1).getName());
                        holder.eventDayText2.setTextColor(Color.BLUE);
                        holder.eventDayText2.setBackgroundResource(R.drawable.rounded_corner);

                    }


                    }


                }
            }


            for (int i = 0; i < myEvents.size(); i++) {
                if (myEvents.get(i).getDate().equals(date) && LocalTime.parse(cursor.getString(4)).equals(myEvents.get(i).getTime())) {

                    holder.eventDayText.setVisibility(View.VISIBLE);
                    holder.eventDayText.setText(myEvents.get(i).getName());
                    holder.eventDayText.setTextColor(Color.BLUE);
                    holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);



                    for (int j = myEvents.size()-1; j >= 0; j--) {
                        if (myEvents.get(i).getDate().equals(myEvents.get(j).getDate()) && i < myEvents.size() ) {
                            if (myEvents.get(j).getName().equals(myEvents.get(i).getName())) {
                                holder.eventDayText2.setVisibility(View.GONE);

                            } else {
                                holder.eventDayText2.setVisibility(View.VISIBLE);
                                holder.eventDayText2.setText(myEvents.get(j).getName());
                                holder.eventDayText2.setTextColor(Color.BLUE);
                                holder.eventDayText2.setBackgroundResource(R.drawable.rounded_corner);

                            }
                        }

                    }

                }


            }
        }









}


    private void compareAndGetValuesFromDB(View convertView, LocalTime time) {
        Cursor cursor = myDB.readAllData();


        if (cursor.getCount() == 0) {
            try {


                Toast.makeText(context, "No data to present.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            while (cursor.moveToNext()) {
                if (LocalTime.parse(cursor.getString(4)).equals(time)) {
                    Event eventDB = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)));
                    ArrayList<Event> eventArrayDB = new ArrayList<>();
                    eventArrayDB.add(eventDB);
                    HourEvent event = new HourEvent(LocalTime.parse(cursor.getString(4)), eventArrayDB);
                    event.setEvents(eventArrayDB);
                    event.setTime(LocalTime.parse(cursor.getString(4)));
                    //Καλείτε η setHour και δίνω στην λίστα τις τιμές της ώρες από την βάση δεδομένων και το αντίστοιχο event


                }

            }
        }


    }


    @Override
    public int getItemCount() {
        return days.size();
    }

public interface OnItemListener {
    void onItemClick(int position, LocalDate date);
}
}