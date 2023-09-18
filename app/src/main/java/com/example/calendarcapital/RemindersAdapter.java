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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemindersAdapter extends ArrayAdapter<Date>  {
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

        setRemindersVol2(getDate, reminderSetTV, cancelReminderImageView,  position);



        return convertView;
    }

    public void setRemindersVol2(Date reminder, TextView reminderTV, ImageButton cancelBTN,  int position) {
        String testStr = CalendarUtils.dateForReminder(reminder);

        Cursor remCursor = myDB.readAllReminder();

        Cursor secMyDbCursor = myDB.readAllEvents();
        Cursor secMyRemCursor = myDB.readAllReminder();

        Cursor thirdCursorEvents = myDB.readAllEvents();
        Cursor thirdCursorReminders = myDB.readAllReminder();
        reminderTV.setText(testStr);

        cancelBTN.setOnClickListener(v -> {

            while (remCursor.moveToNext()) {
//                long mytestLong = Date.parse(remCursor.getString(2));
//                Date lastDate = new Date(mytestLong);
                String dateString = remCursor.getString(2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                Date lastDate = null;
                try {
                    lastDate = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                assert lastDate != null;
                if (lastDate.equals(myTest.get(position))) {

                    myDB.deleteOneRowReminder(remCursor.getString(0));
                    String alarmId = remCursor.getString(0);
                    cancelAlarm(Integer.parseInt(alarmId));
                    RemindersAdapter.this.notifyDataSetChanged();
                }
                RemindersAdapter.this.notifyDataSetChanged();
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
                        RemindersAdapter.this.notifyDataSetChanged();
                    }
                }
                RemindersAdapter.this.notifyDataSetChanged();
            }
            secMyDbCursor.close();
            secMyRemCursor.close();

            while (thirdCursorEvents.moveToNext())
            {
                String cursor_event_id = thirdCursorEvents.getString(0);
                thirdCursorReminders.moveToPosition(-1);
                while(thirdCursorReminders.moveToNext())
                {
                    if (cursor_event_id.equals(thirdCursorReminders.getString(1)))
                    {
                        myDB.updateEventAlarm(cursor_event_id,"1");
                        break;
                    }else
                    {
                        myDB.updateEventAlarm(cursor_event_id,"0");

                    }



                }
            }

            thirdCursorEvents.close();
            thirdCursorReminders.close();


            notifyDataSetChanged();
            RemindersAdapter.this.notifyDataSetChanged();

            myDB.close();


        });

    }

    public void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);


    }


}