package com.example.mycalendar;

import static com.example.mycalendar.CalendarUtils.daysInMonthArray;
import static com.example.mycalendar.CalendarUtils.daysInWeekArray;
import static com.example.mycalendar.CalendarUtils.selectedDate;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;


public class RecyclerViewActions {




    public static void removeExtraDateIntent(Activity activity)
    {
        if (activity.getIntent().hasExtra("date")) {
            Bundle b = activity.getIntent().getExtras();

                selectedDate = (LocalDate) b.get("date");

            activity.getIntent().removeExtra("date");

        }
    }


    public static void setCalendarRecyclerViewMonth(CalendarAdapter.OnItemListener activity,
                                                    Context context,RecyclerView calendarRecyclerView)
    {
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, activity, context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 7);


        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarRecyclerView.setAdapter(calendarAdapter);
        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) calendarRecyclerView.getLayoutParams();

        params.height = 1700;
        marginParams.bottomMargin = 100;


        calendarRecyclerView.setLayoutParams(params);
        calendarRecyclerView.setLayoutParams(marginParams);
    }




    public static void setCalendarRecyclerViewWeek(CalendarAdapter.OnItemListener activity, Context context,RecyclerView calendarRecyclerView)
    {
        ArrayList<LocalDate> days = daysInWeekArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, activity, context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) calendarRecyclerView.getLayoutParams();

        params.height = 320;
        marginParams.bottomMargin = 0;

        calendarRecyclerView.setLayoutParams(params);
        calendarRecyclerView.setLayoutParams(marginParams);
    }

}
