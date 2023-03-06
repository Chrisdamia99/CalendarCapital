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
import java.util.Objects;


class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> //Extends the recyclerview that
        // do adapt from class calendarviewholder
{
    private final ArrayList<LocalDate> days;
    private final ArrayList<LocalDate> daysForRepeat;

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

        int year = CalendarUtils.selectedDate.getYear();

        // Loop through all 365 or 366 days of the year
//        for (int i = 1; i <= (year % 4 == 0 && year % 100 != 0 || year % 400 == 0 ? 366 : 365); i++) {
//            // Add the local date to the ArrayList
//            daysForRepeat.add(LocalDate.ofYearDay(year, i));
//        }
        int numDaysInYear = year % 4 == 0 && year % 100 != 0 || year % 400 == 0 ? 366 : 365;
        for (int i = 1; i <= numDaysInYear; i++) {
            // Add the local date to the ArrayList
            daysForRepeat.add(LocalDate.ofYearDay(year, i));
        }


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
        holder.eventDayText3.setVisibility(View.GONE);
        holder.eventRepeatText1.setVisibility(View.GONE);
        holder.eventRepeatText2.setVisibility(View.GONE);
        holder.eventRepeatText3.setVisibility(View.GONE);



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
                    CalendarUtils.stringToLocalDate(cursor.getString(3)), LocalTime.parse(cursor.getString(4)),
                    cursor.getString(5),cursor.getString(6));
            myEvents.add(eventDB);


        }



        myEvents.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        myEvents.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));

//        for (int i = LocalDate.now().getDayOfYear()-1; i < daysForRepeat.size(); i+=7) {
//            for (int j = 0; j < myEvents.size(); j++) {
//
//                Event event = myEvents.get(j);
//                if (event.getDate().equals(daysForRepeat.get(i))) {
//                    if (event.getRepeat().equals("2")) { // check if the event is repeating every 7 days
//                        LocalDate startDate = event.getDate();
//                        int daysBetween = (int) startDate.until(date.plusDays(7)).getDays(); // calculate the number of days between the start date and the current date
//
//                        if (daysBetween % 7 == 0 && daysBetween >= 0)
//                        { // check if the current date is a multiple of 7 days after the start date
//                            holder.eventRepeatText2.setVisibility(View.VISIBLE);
//                            holder.eventRepeatText2.setText(event.getName());
//                        }
//                    }
//                }
//            }
//
//
//        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {


            if (myEvents.size() <= 2) {
                if (myEvents.size() < 2) {
                    if (myEvents.get(0).getDate().equals(date) && LocalTime.parse(cursor.getString(4)).equals(myEvents.get(0).getTime())) {
                        holder.eventDayText.setVisibility(View.VISIBLE);
                        holder.eventDayText.setText(myEvents.get(0).getName());
                        holder.eventDayText.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                        holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);


                    }


                } else {
                    for (int i = 0; i < myEvents.size() - 1; i++) {

                        if (myEvents.get(i).getDate().equals(date) && !myEvents.get(i).getId().equals(myEvents.get(i + 1).getId())
                                && myEvents.get(i + 1).getDate().equals(date)) {


                            holder.eventDayText.setVisibility(View.VISIBLE);
                            holder.eventDayText.setText(myEvents.get(0).getName());
                            holder.eventDayText.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                            holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);

                            holder.eventDayText2.setVisibility(View.VISIBLE);
                            holder.eventDayText2.setText(myEvents.get(1).getName());
                            holder.eventDayText2.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                            holder.eventDayText2.setBackgroundResource(R.drawable.rounded_corner);

                        }




                    }


                }
            }

            for (int i = 0; i < myEvents.size(); i++) {
                if (myEvents.get(i).getDate().equals(date)) {

                    holder.eventDayText.setVisibility(View.VISIBLE);
                    holder.eventDayText.setText(myEvents.get(i).getName());
                    holder.eventDayText.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                    holder.eventDayText.setBackgroundResource(R.drawable.rounded_corner);

                    for (int j = 0; j < myEvents.size(); j++) {

                        if (myEvents.get(j).getDate().equals(date) && i < myEvents.size()
                                && myEvents.get(i).getId() != myEvents.get(j).getId()) {

                            if (myEvents.get(j).getName().equals(myEvents.get(i).getName())) {
                                holder.eventDayText2.setVisibility(View.GONE);

                            } else {
                                holder.eventDayText2.setVisibility(View.VISIBLE);
                                holder.eventDayText2.setText(myEvents.get(j).getName());
                                holder.eventDayText2.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                                holder.eventDayText2.setBackgroundResource(R.drawable.rounded_corner);


                                for (int k = 0; k < myEvents.size(); k++) {
                                    if (myEvents.get(i).getDate().equals(myEvents.get(k).getDate()) && i < myEvents.size()

                                            && !Objects.equals(myEvents.get(i).getId(), myEvents.get(k).getId())
                                            && !Objects.equals(myEvents.get(j).getId(), myEvents.get(k).getId())) {
                                        holder.eventDayText3.setVisibility(View.VISIBLE);
                                        holder.eventDayText3.setText(myEvents.get(k).getName());
//                                        holder.eventDayText3.setText(myEvents.get(k).getName());
                                        holder.eventDayText3.setTextColor(ContextCompat.getColor(context, R.color.primaryLightTirquiso));
                                        holder.eventDayText3.setBackgroundResource(R.drawable.rounded_corner);


                                    }

                                }
                            }
                        }

                    }

                }


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