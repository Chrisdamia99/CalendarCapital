package com.example.mycalendar;


import android.view.View;
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
    public final TextView dayOfMonth, eventDayText, eventDayText2, eventDayText3;
    public final TextView  eventRepeatText1, eventRepeatText2, eventRepeatText3,eventsMore;


    private final CalendarAdapter.OnItemListener onItemListener;


    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        eventDayText = itemView.findViewById(R.id.eventDayText);
        eventDayText2 = itemView.findViewById(R.id.eventDayText2);
        eventDayText3 = itemView.findViewById(R.id.eventDayText3);
        eventRepeatText1 = itemView.findViewById(R.id.eventRepeatText1);
        eventRepeatText2= itemView.findViewById(R.id.eventRepeatText2);
        eventRepeatText3= itemView.findViewById(R.id.eventRepeatText3);
        eventsMore = itemView.findViewById(R.id.eventsMore);


        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;


    }


    @Override
    public void onClick(View view) {

        view.setSelected(true);

        int adapterPosition = getAdapterPosition();

        if (adapterPosition<0)
        {

                adapterPosition = CalendarUtils.selectedDate.getDayOfMonth();

        }
        onItemListener.onItemClick(adapterPosition, days.get(adapterPosition));

    }


}