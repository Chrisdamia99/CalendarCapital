package com.example.calendarcapital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindersAdapter extends ArrayAdapter<Date> {
    LayoutInflater inflater;
    ArrayList<Date> myTest;


    public RemindersAdapter(@NonNull Context context, List<Date> reminders) {
        super(context, 0,reminders);
        myTest = (ArrayList<Date>) reminders;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Date getDate = myTest.get(position);



        if(inflater == null){
            Context context = parent.getContext();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.reminders_for_eventedit, parent, false);
        }
        LinearLayout remindersLay = convertView.findViewById(R.id.reminderLayout);
        ImageButton cancelReminderImageView = convertView.findViewById(R.id.cancelReminderImageView);
        TextView reminderSetTV =convertView.findViewById(R.id.reminderSetTV);

//        setReminders(myTest  ,reminderSetTV,cancelReminderImageView,remindersLay);
        setRemindersVol2(getDate,reminderSetTV,cancelReminderImageView,remindersLay,position);


        return convertView;
    }

    public void setRemindersVol2(Date reminder,TextView reminderTV,ImageButton cancelBTN,LinearLayout reminderLay,int position)
    {  String testStr = CalendarUtils.dateForReminder(reminder);

        reminderTV.setText(testStr);

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myTest.remove(position);
                notifyDataSetChanged();
            }
        });

    }



}
