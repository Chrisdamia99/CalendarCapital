package com.example.calendarcapital;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindersAdapter extends ArrayAdapter<Date> {
    LayoutInflater inflater;
    ArrayList<Date> myTest;
    MyDatabaseHelper myDB = new MyDatabaseHelper(this.getContext());

    public RemindersAdapter(@NonNull Context context, List<Date> reminders) {
        super(context, 0, reminders);
        myTest = (ArrayList<Date>) reminders;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Date getDate = myTest.get(position);


        if (inflater == null) {
            Context context = parent.getContext();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.reminders_for_eventedit, parent, false);
        }
        LinearLayout remindersLay = convertView.findViewById(R.id.reminderLayout);
        ImageButton cancelReminderImageView = convertView.findViewById(R.id.cancelReminderImageView);
        TextView reminderSetTV = convertView.findViewById(R.id.reminderSetTV);

//        setReminders(myTest  ,reminderSetTV,cancelReminderImageView,remindersLay);
        setRemindersVol2(getDate, reminderSetTV, cancelReminderImageView, remindersLay, position);


        return convertView;
    }

    public void setRemindersVol2(Date reminder, TextView reminderTV, ImageButton cancelBTN, LinearLayout reminderLay, int position) {
        String testStr = CalendarUtils.dateForReminder(reminder);
        Cursor myDbCursor = myDB.readAllData();
        Cursor remCursor = myDB.readAllReminder();

        reminderTV.setText(testStr);

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                while (remCursor.moveToNext()) {
                    long mytestLong = Date.parse(remCursor.getString(2));
                    Date lastDate = new Date(mytestLong);
                    if (lastDate.equals(myTest.get(position))) {

                        myDB.deleteOneRowReminder(remCursor.getString(0));
                        String alarmId = remCursor.getString(0);
                        cancelAlarm(Integer.parseInt(alarmId));
                        RemindersAdapter.this.notifyDataSetChanged();
                    }
                }
                myTest.remove(position);

                myDbCursor.moveToPosition(-1);
                remCursor.moveToPosition(-1);
                while (myDbCursor.moveToNext()) {
                    while (remCursor.moveToNext()) {
                        if (myDbCursor.getString(5).equals("1")) {
                            if (!myDbCursor.getString(0).equals(remCursor.getString(1))) {
                                myDB.updateAlarmNum(myDbCursor.getString(0), "0");
                            }
                        }
                    }
                }

                RemindersAdapter.this.notifyDataSetChanged();


            }
        });

    }

    public void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(getContext(), "Alarm Cancelled", Toast.LENGTH_SHORT).show();


    }


}
