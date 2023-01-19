package com.example.calendarcapital;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

//Extends Recyclerview viewholder which gets the recycler view
//implements the view class onclicklistener
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth,eventDayText,eventDayText2,eventDayText3;

    private final CalendarAdapter.OnItemListener onItemListener;


    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        eventDayText = itemView.findViewById(R.id.eventDayText);
        eventDayText2 = itemView.findViewById(R.id.eventDayText2);
        eventDayText3 = itemView.findViewById(R.id.eventDayText3);

        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;


    }


    @Override
    public void onClick(View view) {

            view.setSelected(true);
            onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));

    }


}