package com.example.calendarcapital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> //Extends the recyclerview that
        // do adapt from class calendarviewholder
{
    private final ArrayList<LocalDate> days;
    private final ArrayList<Date> daysForRepeat;
    Calendar calendar = Calendar.getInstance();
    private final OnItemListener onItemListener;
    private MyDatabaseHelper myDB;
    static int numEvents;
    static  int numRepeatingEvents;
    Context context;


    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, Context context) {
        this.days = days;
        this.onItemListener = onItemListener;
        this.myDB = new MyDatabaseHelper(context.getApplicationContext());
        this.context = context;
        this.daysForRepeat = new ArrayList<>();

    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Calendar cell take shape and inflated

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        calendar.setTime(new Date());
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);


        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);
        calendar.set(Calendar.YEAR, currentYear);

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);



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


        Cursor cursor = myDB.readAllEvents();
        holder.eventDayText.setVisibility(View.GONE);
        holder.eventDayText2.setVisibility(View.GONE);
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventRepeatText1.setVisibility(View.GONE);
        holder.eventRepeatText2.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.GONE);


        final LocalDate date = days.get(position);

        for (LocalDate day : days) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth());
            calendar1.set(Calendar.HOUR_OF_DAY, 8);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);
            Date date2 = calendar1.getTime();
            daysForRepeat.add(date2);
        }



        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if (date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY);
        if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.BLACK);
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
        ArrayList<Event> myEvents = new ArrayList<>();


        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            Event eventDB = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)),
                    cursor.getString(5), cursor.getString(6),cursor.getString(7));

            myEvents.add(eventDB);


        }



//        myEvents.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
//
//        myEvents.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
//
        numEvents=0;
        numRepeatingEvents=0;
        myEvents.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        cursor.moveToPosition(-1);
        for (int i=0; i<days.size(); i++)
        {


            List<String> eventNames = new ArrayList<>();
            for (Event event : myEvents) {

                if (event.getDate().equals(date) && event.getParent_id()==null) {
                        eventNames.add(event.getName());

                }
            }

            Collections.sort(eventNames);

             numEvents = eventNames.size();

            if (numEvents >= 1) {
                holder.eventDayText.setVisibility(View.VISIBLE);
                holder.eventDayText.setText(eventNames.get(0));
                holder.eventDayText.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);
            }

            if (numEvents >= 2) {
                holder.eventDayText2.setVisibility(View.VISIBLE);
                holder.eventDayText2.setText(eventNames.get(1));
                holder.eventDayText2.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                holder.eventDayText2.setBackgroundResource(R.drawable.rounded_corner);
            }

            if (numEvents >= 3) {
                holder.eventDayText3.setVisibility(View.VISIBLE);
                holder.eventDayText3.setText(eventNames.get(2));
                holder.eventDayText3.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                holder.eventDayText3.setBackgroundResource(R.drawable.rounded_corner);
            }

            if (date.toString().equals("2023-05-11"))
            {
                System.out.println();
            }
            checkForMore( holder);

        }
//        myEvents.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
//
//        myEvents.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
//        myEvents.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));


        cursor.moveToPosition(-1);
        for (int i=0; i<days.size(); i++) {

                List<String> eventNamesRepeating = new ArrayList<>();
                for (Event event : myEvents) {
                    if (event.getDate().equals(date) && !(event.getParent_id()==null)) {
                        eventNamesRepeating.add(event.getName());
                    }
                }

            Collections.sort(eventNamesRepeating);


                 numRepeatingEvents = eventNamesRepeating.size();
                if (numRepeatingEvents >= 1 ) {
                    holder.eventRepeatText1.setVisibility(View.VISIBLE);
                    holder.eventRepeatText1.setText(eventNamesRepeating.get(0));
                    holder.eventRepeatText1.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                    holder.eventRepeatText1.setBackgroundResource(R.drawable.rounded_corner);
                }

                if (numRepeatingEvents >= 2) {
                    holder.eventRepeatText2.setVisibility(View.VISIBLE);
                    holder.eventRepeatText2.setText(eventNamesRepeating.get(1));
                    holder.eventRepeatText2.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                    holder.eventRepeatText2.setBackgroundResource(R.drawable.rounded_corner);
                }

                if (numRepeatingEvents >= 3) {
                    holder.eventRepeatText3.setVisibility(View.VISIBLE);
                    holder.eventRepeatText3.setText(eventNamesRepeating.get(2));
                    holder.eventRepeatText3.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                    holder.eventRepeatText3.setBackgroundResource(R.drawable.rounded_corner);
                }

if (date.toString().equals("2023-05-11"))
{
    System.out.println();
}
            checkForMore( holder);
        }




        cursor.close();
        myDB.close();


    }

public void checkForMore(@NonNull CalendarViewHolder holder)
{
    if (numEvents>=3 && numRepeatingEvents>=1)
    {
        holder.eventRepeatText1.setVisibility(View.GONE);
        holder.eventRepeatText2.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);

    }

    if (numRepeatingEvents>=3 && numEvents>1)
    {
        holder.eventDayText.setVisibility(View.GONE);
        holder.eventDayText2.setVisibility(View.GONE);
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);
    }

    if (numRepeatingEvents==2 && numEvents>1)
    {
        holder.eventDayText2.setVisibility(View.GONE);
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);
    }

    if (numEvents==2 && numRepeatingEvents>1)
    {
        holder.eventRepeatText2.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);

    }

    if (numEvents>3 && numRepeatingEvents==0)
    {
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);
    }

    if (numRepeatingEvents>3 && numEvents==0)
    {
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);
    }
    if (numEvents>=3 && numRepeatingEvents>0)
    {
        holder.eventRepeatText1.setVisibility(View.GONE);
        holder.eventRepeatText2.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);
    }

    if (numRepeatingEvents>=3 && numEvents>0)
    {
        holder.eventDayText.setVisibility(View.GONE);
        holder.eventDayText2.setVisibility(View.GONE);
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventsMore.setVisibility(View.VISIBLE);
        holder.eventsMore.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
        holder.eventsMore.setBackgroundResource(R.drawable.rounded_corner);
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