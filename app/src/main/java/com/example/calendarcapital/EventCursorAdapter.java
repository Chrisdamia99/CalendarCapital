package com.example.calendarcapital;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;


public class EventCursorAdapter extends CursorAdapter {

    private Context mContext;
    Cursor mCursor;
    RemindersAdapter remindersAdapter;
    ArrayList<Date> existedReminders = new ArrayList<>();


    public EventCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

        this.mCursor = c;
        this.mContext = context;


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        return LayoutInflater.from(context).inflate(R.layout.show_event_from_listview, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView id_lv_tv = view.findViewById(R.id.id_lv_tv);
        TextView title_lv_tv = view.findViewById(R.id.title_lv_tv);
        TextView comment_lv_tv = view.findViewById(R.id.comment_lv_tv);
        TextView date_lv_tv = view.findViewById(R.id.date_lv_tv);
        TextView time_lv_tv = view.findViewById(R.id.time_lv_tv);


        String id_row = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("event_title"));
        String comment = cursor.getString(cursor.getColumnIndexOrThrow("event_comment"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("event_date"));
        String time = cursor.getString(cursor.getColumnIndexOrThrow("event_time"));

        id_lv_tv.setText(id_row);
        title_lv_tv.setText(title);
        comment_lv_tv.setText(comment);
        date_lv_tv.setText(date);
        time_lv_tv.setText(time);

    }


    public View setAllFields(View view, String id, String title, String comment, String date, String time) {
        String alarmDate;

        TextView id_lv_tv = view.findViewById(R.id.id_lv_tv);
        TextView title_lv_tv = view.findViewById(R.id.title_lv_tv);
        TextView comment_lv_tv = view.findViewById(R.id.comment_lv_tv);
        TextView date_lv_tv = view.findViewById(R.id.date_lv_tv);
        TextView time_lv_tv = view.findViewById(R.id.time_lv_tv);
        TextView alarmInfo = view.findViewById(R.id.alarmInfo);
//        ImageButton cancelReminderFromList = view.findViewById(R.id.cancelReminderFromList);
        ListView existedRemindersListView = view.findViewById(R.id.existedRemindersListView);


        id_lv_tv.setText(id);
        title_lv_tv.setText(title);
        comment_lv_tv.setText(comment);
        date_lv_tv.setText(date);
        time_lv_tv.setText(time);


        MyDatabaseHelper myDb = new MyDatabaseHelper(mContext);


        Cursor cursor = myDb.readAllData();
        Cursor cursorRem = myDb.readAllReminder();

        existedReminders.sort((o1, o2) -> o1.compareTo(o2));
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(id)) {
                alarmDate = cursor.getString(5);
                if (alarmDate.equals("1") && existedReminders.isEmpty()) {
                    alarmInfo.setVisibility(View.GONE);

                    cursorRem.moveToPosition(-1);
                    //----Problem here maybe--------//
                    while (cursorRem.moveToNext()) {

                        if (cursorRem.getString(1).equals(id)) {

                            long mytestLong = Date.parse(cursorRem.getString(2));
                            Date lastDate = new Date(mytestLong);

                            existedReminders.add(lastDate);


                        }
                    }


                    for (int i = 0; i < existedReminders.size(); i++) {
                        for (int j = i + 1; j < existedReminders.size(); j++) {
                            if (existedReminders.get(i).getMonth() == existedReminders.get(j).getMonth() &&
                                    existedReminders.get(i).getYear() == existedReminders.get(j).getYear() &&
                                    existedReminders.get(i).getDay() == existedReminders.get(j).getDay() &&
                                    existedReminders.get(i).getHours() == existedReminders.get(j).getHours() &&
                                    existedReminders.get(i).getMinutes() == existedReminders.get(j).getMinutes()) {
                                existedReminders.remove(i);

                            }
                        }
                    }

                    for (int i = 0; i < existedReminders.size(); i++) {
                        for (int j = i + 1; j < existedReminders.size(); j++) {
                            if (existedReminders.get(i).getMonth() == existedReminders.get(j).getMonth() &&
                                    existedReminders.get(i).getYear() == existedReminders.get(j).getYear() &&
                                    existedReminders.get(i).getDay() == existedReminders.get(j).getDay() &&
                                    existedReminders.get(i).getHours() == existedReminders.get(j).getHours() &&
                                    existedReminders.get(i).getMinutes() == existedReminders.get(j).getMinutes()) {
                                existedReminders.remove(i);

                            }
                        }
                    }
                    remindersAdapter = new RemindersAdapter(mContext, existedReminders);

                    existedRemindersListView.setVisibility(View.VISIBLE);
                    existedRemindersListView.setAdapter(remindersAdapter);
                } else if (!existedReminders.isEmpty()) {
                    alarmInfo.setVisibility(View.GONE);
                    remindersAdapter = new RemindersAdapter(mContext, existedReminders);

                    existedRemindersListView.setVisibility(View.VISIBLE);
                    existedRemindersListView.setAdapter(remindersAdapter);
                } else {
                    alarmInfo.setText("Καμία υπενθύμιση.");
//                    cancelReminderFromList.setVisibility(View.GONE);
                    break;
                }
            }


        }


        EventCursorAdapter.this.notifyDataSetChanged();

        return view;
    }

    private void startAlarm(int alarmId, Calendar c, String title, String comment) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(mContext);
        Cursor cursor = myDB.readAllData();
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(String.valueOf(alarmId))) {
                myDB.updateData(cursor.getString(0), cursor.getString(1), cursor.getString(2), LocalDate.parse(cursor.getString(3)),
                        LocalTime.parse(cursor.getString(4)), "true");


            }
        }
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);

        intent.putExtra("calendar", c.getTime());
        intent.putExtra("title", title);
        intent.putExtra("comment", comment);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        notifyDataSetChanged();

    }


    private void cancelAlarmCursorAdapter(int alarmId) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(mContext);
        Cursor cursor = myDB.readAllData();
        cursor.moveToPosition(-1);
        Calendar DBdate;
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(String.valueOf(alarmId))) {
                myDB.updateData(cursor.getString(0), cursor.getString(1), cursor.getString(2), LocalDate.parse(cursor.getString(3)),
                        LocalTime.parse(cursor.getString(4)), "false");

            }
        }


        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, intent, 0);
        Toast.makeText(mContext, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


        alarmManager.cancel(pendingIntent);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return super.getView(position, convertView, parent);
    }
}
