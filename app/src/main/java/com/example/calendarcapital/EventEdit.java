package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, eventCommentET;
    private TextView eventDateTV, eventTimeTV;

    private LocalDate date;
    private LocalTime time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
        date = CalendarUtils.selectedDate;


    }


    private void initWidgets() {
    eventNameET = findViewById(R.id.eventNameET);
    eventCommentET = findViewById(R.id.eventCommentET);
    eventDateTV = findViewById(R.id.eventDateET);
    eventTimeTV = findViewById(R.id.eventTimeET);


    }

    public void saveEventAction(View view) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);



        String eventName = eventNameET.getText().toString();
        String eventComment = eventCommentET.getText().toString();
        myDB.addEvent(eventName,eventComment,date,time);

        Event newEvent = new Event(eventName,eventComment, date, time);
        Event.eventsList.add(newEvent);

        finish();
    }


}