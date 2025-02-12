package com.example.mycalendar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;



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

//        TextView id_lv_tv = view.findViewById(R.id.id_lv_tv);
        TextView title_lv_tv = view.findViewById(R.id.title_lv_tv);
        TextView comment_lv_tv = view.findViewById(R.id.comment_lv_tv);
        TextView date_lv_tv = view.findViewById(R.id.date_lv_tv);
        TextView time_lv_tv = view.findViewById(R.id.time_lv_tv);
        TextView location_lv_tv = view.findViewById(R.id.location_lv_tv);


        String id_row = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("event_title"));
        String comment = cursor.getString(cursor.getColumnIndexOrThrow("event_comment"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("event_date"));
        String time = cursor.getString(cursor.getColumnIndexOrThrow("event_time"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("event_location"));

//        id_lv_tv.setText(id_row);
        title_lv_tv.setText(title);
        comment_lv_tv.setText(comment);
        date_lv_tv.setText(date);
        time_lv_tv.setText(time);
        location_lv_tv.setText(location.trim());

    }


    public View setAllFields(View view, String id, String title, String comment, String date, String time,String location) {
        String alarmDate;

//        TextView id_lv_tv = view.findViewById(R.id.id_lv_tv);
        TextView title_lv_tv = view.findViewById(R.id.title_lv_tv);
        TextView comment_lv_tv = view.findViewById(R.id.comment_lv_tv);
        TextView date_lv_tv = view.findViewById(R.id.date_lv_tv);
        TextView time_lv_tv = view.findViewById(R.id.time_lv_tv);
        TextView location_lv_tv = view.findViewById(R.id.location_lv_tv);
        TextView locationView = view.findViewById(R.id.locationView);
        LinearLayout lin_lv_dialog_layout = view.findViewById(R.id.lin_lv_dialog_layout);
        ListView existedRemindersListView = view.findViewById(R.id.existedRemindersListView);
        @SuppressLint("InflateParams") View viewInvalidation = LayoutInflater.from(mContext).inflate(R.layout.show_event_from_listview,null);


        EventCursorAdapter.this.notifyDataSetChanged();
//        id_lv_tv.setText(id);
        title_lv_tv.setText(title);
        comment_lv_tv.setText(comment);
        date_lv_tv.setText(date);
        time_lv_tv.setText(time);
        location_lv_tv.setText(location);


        MyDatabaseHelper myDb = new MyDatabaseHelper(mContext);


        Cursor cursor = myDb.readAllEvents();
        Cursor cursorRem = myDb.readAllReminder();

//            existedReminders.sort(Date::compareTo);
        Collections.sort(existedReminders, (item1, item2) -> item1.compareTo(item2));
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(id)) {
                alarmDate = cursor.getString(5);
                if (alarmDate.equals("1") && existedReminders.isEmpty()) {
                    lin_lv_dialog_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    existedRemindersListView.setVisibility(View.GONE);
                    viewInvalidation.invalidate();
                    EventCursorAdapter.this.notifyDataSetChanged();



                    cursorRem.moveToPosition(-1);
                    //----Problem here maybe--------//
                    while (cursorRem.moveToNext()) {

                        if (cursorRem.getString(1).equals(id)) {

//                            long mytestLong = Date.parse(cursorRem.getString(2));
//                            Date lastDate = new Date(mytestLong);
                            String dateString = cursorRem.getString(2);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                            Date lastDate = null;
                            try {
                                lastDate = dateFormat.parse(dateString);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            existedReminders.add(lastDate);


                        }
                    }


                    for (int i = 0; i < existedReminders.size(); i++) {
                        for (int j = i + 1; j < existedReminders.size(); j++) {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTime(existedReminders.get(i));

                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.setTime(existedReminders.get(j));

                            if (isSameDateTime(calendar1, calendar2)) {
                                existedReminders.remove(i);
                                Toast.makeText(mContext, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    }


                    remindersAdapter = new RemindersAdapter(mContext, existedReminders);
                    remindersAdapter.notifyDataSetChanged();
                    EventCursorAdapter.this.notifyDataSetChanged();
                    existedRemindersListView.setVisibility(View.VISIBLE);
                    existedRemindersListView.setAdapter(remindersAdapter);
                } else if (alarmDate.equals("1") && !existedReminders.isEmpty()) {
                    lin_lv_dialog_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    viewInvalidation.invalidate();
                    existedReminders.clear();
                    cursorRem.moveToPosition(-1);
                    //----Problem here maybe--------//
                    while (cursorRem.moveToNext()) {

                        if (cursorRem.getString(1).equals(id)) {

//                            long mytestLong = Date.parse(cursorRem.getString(2));
//                            Date lastDate = new Date(mytestLong);
                            String dateString = cursorRem.getString(2);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                            Date lastDate = null;
                            try {
                                lastDate = dateFormat.parse(dateString);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            existedReminders.add(lastDate);


                        }
                    }

                    remindersAdapter = new RemindersAdapter(mContext, existedReminders);
                    remindersAdapter.notifyDataSetChanged();
                    EventCursorAdapter.this.notifyDataSetChanged();
                    existedRemindersListView.setVisibility(View.VISIBLE);
                    existedRemindersListView.setAdapter(remindersAdapter);
                } else {
                    lin_lv_dialog_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    viewInvalidation.invalidate();
                    existedRemindersListView.setVisibility(View.GONE);
                    break;
                }
            }




        }
        cursor.close();
        cursorRem.close();
        myDb.close();

        locationClickListener(location_lv_tv);

        String location_final = location_lv_tv.getText().toString().trim();
        if (location_final == null || location_final.equals(""))
        {
            location_lv_tv.setVisibility(View.GONE);
            locationView.setVisibility(View.GONE);

        }else {
            location_lv_tv.setVisibility(View.VISIBLE);
            locationView.setVisibility(View.VISIBLE);
        }

        EventCursorAdapter.this.notifyDataSetChanged();

        return view;
    }

    private void locationClickListener(TextView location)
    {
        String location_final = location.getText().toString().trim();
        if (location_final == null || location_final.equals(""))
        {
            location.setVisibility(View.GONE);


        }else
        {
            location.setVisibility(View.VISIBLE);
            location.setOnClickListener(v -> {


                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location_final);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(mapIntent);
//                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
//                        mContext.startActivity(mapIntent);
//                    } else {
//                        Toast.makeText(mContext, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
//                    }
            });
        }
    }
    private boolean isSameDateTime(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
                calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY) &&
                calendar1.get(Calendar.MINUTE) == calendar2.get(Calendar.MINUTE);
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
