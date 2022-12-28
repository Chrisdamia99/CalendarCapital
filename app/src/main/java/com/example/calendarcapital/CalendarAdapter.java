package com.example.calendarcapital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> //Extends the recyclerview that
                                                                       // do adapt from class calendarviewholder
{ private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private  MyDatabaseHelper myDB ;


    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener,Context context)
    {
        this.days = days;
        this.onItemListener = onItemListener;
        this.myDB = new MyDatabaseHelper(context.getApplicationContext());
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Calendar cell take shape and inflated

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();



        if(days.size() > 15) //month view
        {
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        }
        else // week view
            layoutParams.height = (int) parent.getHeight();



        return new CalendarViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        Cursor cursor = myDB.readAllData();

        final LocalDate date = days.get(position);

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if (date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY);
        if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.BLACK);
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);

        while (cursor.moveToNext()) {
            if (date.equals(CalendarUtils.stringToLocalDate(cursor.getString(3)))) {

                holder.dayOfMonth.setBackgroundColor(Color.LTGRAY);
            }

        }


    }



    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }
}