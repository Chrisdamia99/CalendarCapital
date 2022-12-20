package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, eventCommentET;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV;
    int hour, min;
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


        changeTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeTime(LocalTime.now().getHour(), LocalTime.now().getMinute());
            }
        });

        changeDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeDate(LocalDate.now().getYear(), LocalDate.now().getMonth().getValue(), LocalDate.now().getDayOfMonth());
            }
        });


    }


    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventCommentET = findViewById(R.id.eventCommentET);
        eventDateTV = findViewById(R.id.eventDateET);
        eventTimeTV = findViewById(R.id.eventTimeET);
        changeTimeTV = findViewById(R.id.changeTimeTV);
        changeDateTV = findViewById(R.id.changeDateTV);


    }

    public void showChangeDate(int year, int month, int dayofmonth) {
        final DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int trueMonth = monthOfYear + 1;

                if (trueMonth < 10 && dayOfMonth>=10) {
                    String str = String.format("%02d", trueMonth);
                    eventDateTV.setText("Date: " + dayOfMonth + " " + str + " " + year);

                }else if (dayOfMonth<10 && trueMonth<10)
                {String strMonth = String.format("%02d", trueMonth);
                    String strDay =String.format("%02d", dayOfMonth);
                    eventDateTV.setText("Date: " + strDay + " " + strMonth + " " + year);
                }else if (dayOfMonth<10 && trueMonth>=10)
                {                    String strDay =String.format("%02d", dayOfMonth);
                    eventDateTV.setText("Date: " + strDay + " " + trueMonth + " " + year);

                }else
                {
                    eventDateTV.setText("Date: " + dayOfMonth + " " + trueMonth + " " + year);
                }
                date = LocalDate.of(year, trueMonth, dayOfMonth);

            }

        }, year, month, dayofmonth);
        StartTime.setTitle("Select Date");
        StartTime.show();

    }


    public void showChangeTime(int hours, int minute) {
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(EventEdit.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                min = selectedMinute;

                if (hour<10 && min<10)
                {   String hourStr = String.format("%02d", hour);
                    String minStr = String.format("%02d",min);
                    String allTime = hourStr + ":" + minStr;

                    eventTimeTV.setText("Time: " + hourStr + ":" + minStr);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);


                }else if(hour<10 && min>=10)
                {
                    String hourStr = String.format("%02d", hour);
                    String allTime = hourStr + ":" + min;

                    eventTimeTV.setText("Time: " + hourStr + ":" + min);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);
                }else if (hour >=10 && min<10)
                {
                    String minStr = String.format("%02d",min);

                    String allTime = hour + ":" + minStr;

                    eventTimeTV.setText("Time: " + hour + ":" + minStr);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);


                }else
                {
                    String allTime = hour + ":" + min;

                    eventTimeTV.setText("Time: " + hour + ":" + min);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);

                }




            }
        }, hours, minute, true);//Yes 24 hour time
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }




    public void saveEventAction(View view) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);


        String eventName = eventNameET.getText().toString();
        String eventComment = eventCommentET.getText().toString();
        myDB.addEvent(eventName, eventComment, date, time);

//        Event newEvent = new Event(eventName, eventComment, date, time);
//        Event.eventsList.add(newEvent);

        Intent i1 = new Intent(EventEdit.this,MainActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i1);
        overridePendingTransition(0, 0);


    }


}