package com.example.calendarcapital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> //Extends the recyclerview that
        // do adapt from class calendarviewholder
{
    private final ArrayList<LocalDate> days;
    private final ArrayList<Date> daysForRepeat;
    Calendar calendar = Calendar.getInstance();
    Date repeatDate;
    private final OnItemListener onItemListener;
    private MyDatabaseHelper myDB;
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

//        while (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
//            daysForRepeat.add(calendar.getTime());
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
//        }



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
        Cursor cursorRepeat = myDB.readAllRepeat();
        holder.eventDayText.setVisibility(View.GONE);
        holder.eventDayText2.setVisibility(View.GONE);
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventRepeatText1.setVisibility(View.GONE);
        holder.eventRepeatText2.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);



        final LocalDate date = days.get(position);
        repeatDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        for (LocalDate day:days)
        {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth());
            calendar1.set(Calendar.HOUR_OF_DAY, 8);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);
            Date date2 = calendar1.getTime();
            daysForRepeat.add(date2);
        }

        final Date myRepeatDate = daysForRepeat.get(position);



        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if (date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY);
        if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.BLACK);
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
        ArrayList<Event> myEvents = new ArrayList<>();
        ArrayList<Repeat> myRepeats = new ArrayList<>();
        ArrayList<Repeat> myRepeatsSameDate = new ArrayList<>();
        ArrayList<Repeat> dayOfCalendarRepeat = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");


        while (cursor.moveToNext()) {
            Event eventDB = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)),
                    cursor.getString(5),cursor.getString(6));

            myEvents.add(eventDB);


        }
        cursorRepeat.moveToPosition(-1);
        while (cursorRepeat.moveToNext())
        {
            try {
                Repeat repeatDB = new Repeat(cursorRepeat.getString(0),cursorRepeat.getString(1),format.parse(cursorRepeat.getString(2)));
                myRepeats.add(repeatDB);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.v("test",e.toString());
            }


        }

for (int i=0; i< myRepeats.size(); i++)
{
    Repeat rTest = myRepeats.get(i);
    boolean isDubl = false;

    for (int j= i+1; j<myRepeats.size(); j++)
    {
        if (rTest.getRepeatDate().equals(myRepeats.get(j).getRepeatDate()))
        {
            isDubl = true;
            break;
        }
    }
    if (isDubl)
    {
        if (!myRepeatsSameDate.contains(rTest))
        {
            myRepeatsSameDate.add(rTest);
        }
    }
}


        myRepeats.size();
        myRepeatsSameDate.size();






        myEvents.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        myEvents.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));


        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {

            List<String> eventNames = new ArrayList<>();
            for (Event event : myEvents) {
                if (event.getDate().equals(date)) {
                    eventNames.add(event.getName());
                }
            }

            int numEvents = eventNames.size();
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



        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {

            List<String> eventNamesForRepeats = new ArrayList<>();
            for (Event event : myEvents) {
                for (Repeat repeat : myRepeats) {
                    if (repeat.getRepeatDate().equals(myRepeatDate)) {
                        eventNamesForRepeats.add(event.getName());
                    }
                }
            }

            int numEvents = eventNamesForRepeats.size();
            if (numEvents >= 1) {
                holder.eventRepeatText1.setVisibility(View.VISIBLE);
                holder.eventRepeatText1.setText(eventNamesForRepeats.get(0));
                holder.eventRepeatText1.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                holder.eventRepeatText1.setBackgroundResource(R.drawable.rounded_corner);
            }

            if (numEvents >= 2) {
                holder.eventRepeatText2.setVisibility(View.VISIBLE);
                holder.eventRepeatText2.setText(eventNamesForRepeats.get(1));
                holder.eventRepeatText2.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                holder.eventRepeatText2.setBackgroundResource(R.drawable.rounded_corner);
            }

            if (numEvents >= 3) {
                holder.eventRepeatText3.setVisibility(View.VISIBLE);
                holder.eventRepeatText3.setText(eventNamesForRepeats.get(2));
                holder.eventRepeatText3.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                holder.eventRepeatText3.setBackgroundResource(R.drawable.rounded_corner);
            }



        }
        cursor.close();
        myDB.close();



    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}