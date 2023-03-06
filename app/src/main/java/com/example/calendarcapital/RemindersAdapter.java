package com.example.calendarcapital;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

import org.w3c.dom.Text;

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
        ImageButton cancelReminderImageView = convertView.findViewById(R.id.cancelReminderImageView);
        TextView reminderSetTV = convertView.findViewById(R.id.reminderSetTV);

//        setReminders(myTest  ,reminderSetTV,cancelReminderImageView,remindersLay);
        setRemindersVol2(getDate, reminderSetTV, cancelReminderImageView,  position);



        return convertView;
    }

    public void setRemindersVol2(Date reminder, TextView reminderTV, ImageButton cancelBTN,  int position) {
        String testStr = CalendarUtils.dateForReminder(reminder);
        Cursor remCursor = myDB.readAllReminder();
        Cursor secMyDbCursor = myDB.readAllData();
        Cursor secMyRemCursor = myDB.readAllReminder();
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
                remCursor.close();
                myTest.remove(position);

                secMyDbCursor.moveToPosition(-1);
                secMyRemCursor.moveToPosition(-1);

                while (secMyDbCursor.moveToNext()) {
                    if (secMyRemCursor.getCount() == 0) {
                        myDB.updateAlarmNum(secMyDbCursor.getString(0), "0");

                        RemindersAdapter.this.notifyDataSetChanged();


                    }
                    while (secMyRemCursor.moveToNext()) {

                        if (secMyDbCursor.getString(5).equals("1")) {

                            if (!secMyDbCursor.getString(0).equals(secMyRemCursor.getString(1)) || secMyRemCursor.getCount() == 0) {
                                myDB.updateAlarmNum(secMyDbCursor.getString(0), "0");
                            }
                        }
                    }
                }




                RemindersAdapter.this.notifyDataSetChanged();
                secMyDbCursor.close();
                secMyRemCursor.close();
                myDB.close();


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
