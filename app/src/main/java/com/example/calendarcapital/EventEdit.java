package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, eventCommentET;
    private TextView eventDateTV, eventTimeTV,changeTimeTV;
    int hour,min;
    private LocalDate date;
    private static LocalTime time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
        date = CalendarUtils.selectedDate;
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(date));





    }


    private void initWidgets() {
    eventNameET = findViewById(R.id.eventNameET);
    eventCommentET = findViewById(R.id.eventCommentET);
    eventDateTV = findViewById(R.id.eventDateET);
    eventTimeTV = findViewById(R.id.eventTimeET);
    changeTimeTV= findViewById(R.id.changeTimeTV);


    }

    void showTime(int hours, int minute)
    {

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(EventEdit.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
               eventTimeTV.setText( selectedHour + ":" + selectedMinute);
                hour=selectedHour;
                min=selectedMinute;
//                time = LocalTime.parse(selectedHour + ":" + selectedMinute);

            }
        }, hours, minute, false);//Yes 24 hour time
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }

    public void saveEventAction(View view) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);

        for (int i=0; i<Event.eventsList.size(); i++)
        {

        }

        String eventName = eventNameET.getText().toString();
        String eventComment = eventCommentET.getText().toString();
        myDB.addEvent(eventName,eventComment,date,time);

        Event newEvent = new Event(eventName,eventComment, date, time);
        Event.eventsList.add(newEvent);

        finish();
    }



}