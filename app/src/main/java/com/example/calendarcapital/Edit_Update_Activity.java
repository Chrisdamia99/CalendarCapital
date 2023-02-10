package com.example.calendarcapital;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Edit_Update_Activity extends AppCompatActivity {

    private EditText eventNameETUPD, eventCommentETUPD;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV, addAlarmButtonUPD;
    Button btnUpdate;
    RemindersAdapter remindersAdapter;
    ImageButton cancelReminderImageViewUPD, eventEditBackButtonUPD, eventEditRefreshButtonUPD;
    LinearLayout reminderLayoutUPD;
    ListView remindersListViewUPD;
    int hour, min;
    private static LocalDate date;
    private static LocalTime time;
    int alarmState;
    Calendar cReminder = Calendar.getInstance();

    String id_row, title, comment;

    ArrayList<Date> ok = new ArrayList<>();
    ArrayList<String> testRemUpd = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_update);
        initWidgets();
        getSetIntentData();
        showExistedRemindersFromDB();


        time = LocalTime.parse(CalendarUtils.formattedShortTime(LocalTime.now()));
        eventTimeTV.setText(CalendarUtils.formattedShortTime(time));
        date = CalendarUtils.selectedDate;
        eventDateTV.setText(CalendarUtils.formattedDateEventEdit(date));
        btnUpdate = findViewById(R.id.btnUpdate);
        createNotificationChannel();
        updateIfEmptyListView();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updEventAction();
            }
        });
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

        eventEditBackButtonUPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Edit_Update_Activity.this, MainActivity.class);
                Boolean myBool = true;
                i.putExtra("bool", myBool);

                startActivity(i);
            }
        });

        eventEditRefreshButtonUPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllEventsList.reloadActivity(Edit_Update_Activity.this);

            }
        });


        addAlarmButtonUPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();

            }
        });


    }

    private void initWidgets() {
        eventNameETUPD = findViewById(R.id.eventNameETupd);
        eventCommentETUPD = findViewById(R.id.eventCommentETupd);
        eventDateTV = findViewById(R.id.eventDateETupd);
        eventTimeTV = findViewById(R.id.eventTimeETupd);
        changeTimeTV = findViewById(R.id.changeTimeTVupd);
        changeDateTV = findViewById(R.id.changeDateTVupd);
        addAlarmButtonUPD = findViewById(R.id.addAlarmButtonUPD);
        eventEditBackButtonUPD = findViewById(R.id.eventEditBackButtonUPD);
        reminderLayoutUPD = findViewById(R.id.reminderLayoutUPD);
        cancelReminderImageViewUPD = findViewById(R.id.cancelReminderImageView);
        eventEditRefreshButtonUPD = findViewById(R.id.eventEditRefreshButtonUPD);
        remindersListViewUPD = findViewById(R.id.remindersListViewUPD);


    }

    public void showExistedRemindersFromDB() {
        String alarmDate;
        MyDatabaseHelper myDb = new MyDatabaseHelper(Edit_Update_Activity.this);


        Cursor cursor = myDb.readAllData();
        Cursor cursorRem = myDb.readAllReminder();

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(id_row)) {
                alarmDate = cursor.getString(5);
                if (alarmDate.equals("1") && ok.isEmpty()) {


                    cursorRem.moveToPosition(-1);
                    //----Problem here maybe--------//
                    while (cursorRem.moveToNext()) {

                        if (cursorRem.getString(1).equals(id_row)) {

                            long mytestLong = Date.parse(cursorRem.getString(2));
                            Date lastDate = new Date(mytestLong);
                            lastDate.setSeconds(0);
                            ok.add(lastDate);


                        }
                    }
                    ok.sort((o1, o2) -> o1.compareTo(o2));

                    for (int i = 0; i < ok.size(); i++) {
                        for (int j = i + 1; j < ok.size(); j++) {
                            if (ok.get(i).getMonth() == ok.get(j).getMonth() &&
                                    ok.get(i).getYear() == ok.get(j).getYear() &&
                                    ok.get(i).getDay() == ok.get(j).getDay() &&
                                    ok.get(i).getHours() == ok.get(j).getHours() &&
                                    ok.get(i).getMinutes() == ok.get(j).getMinutes()) {
                                ok.remove(i);

                            }
                        }
                    }

                    for (int i = 0; i < ok.size(); i++) {
                        for (int j = i + 1; j < ok.size(); j++) {
                            if (ok.get(i).getMonth() == ok.get(j).getMonth() &&
                                    ok.get(i).getYear() == ok.get(j).getYear() &&
                                    ok.get(i).getDay() == ok.get(j).getDay() &&
                                    ok.get(i).getHours() == ok.get(j).getHours() &&
                                    ok.get(i).getMinutes() == ok.get(j).getMinutes()) {
                                ok.remove(i);

                            }
                        }
                    }

                    ok.sort((o1, o2) -> o1.compareTo(o2));
                    remindersAdapter = new RemindersAdapter(Edit_Update_Activity.this, ok);

                    remindersListViewUPD.setVisibility(View.VISIBLE);
                    remindersListViewUPD.setAdapter(remindersAdapter);
                } else if (!ok.isEmpty()) {

                    remindersAdapter = new RemindersAdapter(Edit_Update_Activity.this, ok);

                    remindersListViewUPD.setVisibility(View.VISIBLE);
                    remindersListViewUPD.setAdapter(remindersAdapter);
                }

            }


        }


    }

    public void updateIfEmptyListView() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // update ui here
                                if (ok.isEmpty()) {
//                            remindersAdapter = new RemindersAdapter(EventEdit.this, ok);
//                            remindersAdapter.notifyDataSetChanged();
                                    remindersListViewUPD.setVisibility(View.GONE);

                                } else if (ok.size() == 1) {
                                    ok.sort((o1, o2) -> o1.compareTo(o2));

                                    ViewGroup.LayoutParams paramsListView = remindersListViewUPD.getLayoutParams();
                                    paramsListView.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                                    remindersListViewUPD.setLayoutParams(paramsListView);
                                } else {
                                    ok.sort((o1, o2) -> o1.compareTo(o2));
                                    ViewGroup.LayoutParams paramsListView = remindersListViewUPD.getLayoutParams();
                                    paramsListView.height = 500;

                                    remindersListViewUPD.setLayoutParams(paramsListView);
                                }
                            }
                        });

                    }
                }, 0, 100); //runs every three seconds


    }

    public void showChangeDate(int year, int month, int dayofmonth) {
        final DatePickerDialog StartTime = new DatePickerDialog(this, R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int trueMonth = monthOfYear + 1;

                if (trueMonth < 10 && dayOfMonth >= 10) {


                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);

                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));

                } else if (dayOfMonth < 10 && trueMonth < 10) {


                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));
                } else if (dayOfMonth < 10 && trueMonth >= 10) {

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);

                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));

                } else {

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));

                }
                date = LocalDate.of(year, trueMonth, dayOfMonth);


            }


        }, year, month, dayofmonth);


        StartTime.setTitle("Select Date");
        StartTime.show();

    }


    public void showChangeTime(int hours, int minute) {
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(Edit_Update_Activity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                min = selectedMinute;

                if (hour < 10 && min < 10) {
                    String hourStr = String.format("%02d", hour);
                    String minStr = String.format("%02d", min);
                    String allTime = hourStr + ":" + minStr;

                    eventTimeTV.setText(hourStr + ":" + minStr);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);


                } else if (hour < 10 && min >= 10) {
                    String hourStr = String.format("%02d", hour);
                    String allTime = hourStr + ":" + min;

                    eventTimeTV.setText(hourStr + ":" + min);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);
                } else if (hour >= 10 && min < 10) {
                    String minStr = String.format("%02d", min);

                    String allTime = hour + ":" + minStr;

                    eventTimeTV.setText(hour + ":" + minStr);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);


                } else {
                    String allTime = hour + ":" + min;

                    eventTimeTV.setText(hour + ":" + min);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);

                }


            }
        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }

    public void showChangeTimeForReminder(int hours, int minute) {

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(Edit_Update_Activity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                min = selectedMinute;

                Calendar fixedFromChangeTime;

                if (hour < 10 && min < 10) {


                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    ok.add(fixedFromChangeTime.getTime());


                } else if (hour < 10 && min >= 10) {


                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    ok.add(fixedFromChangeTime.getTime());

                } else if (hour >= 10 && min < 10) {


                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;
                    ok.add(fixedFromChangeTime.getTime());


                } else {

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    ok.add(fixedFromChangeTime.getTime());


                }


            }
        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                for (int i = 0; i < ok.size(); i++) {
                    for (int j = i + 1; j < ok.size(); j++) {
                        if (ok.get(i).getMonth() == ok.get(j).getMonth() &&
                                ok.get(i).getYear() == ok.get(j).getYear() &&
                                ok.get(i).getDay() == ok.get(j).getDay() &&
                                ok.get(i).getHours() == ok.get(j).getHours() &&
                                ok.get(i).getMinutes() == ok.get(j).getMinutes()) {
                            ok.remove(i);

                            Toast.makeText(Edit_Update_Activity.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                remindersAdapter = new RemindersAdapter(getApplicationContext(), ok);

                remindersListViewUPD.setVisibility(View.VISIBLE);
                remindersListViewUPD.setAdapter(remindersAdapter);
            }
        });
        timePickerDialog.show();
    }

    public void startAlarm(int alarmId, Calendar cc) {


        Date myTime = cc.getTime();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(Edit_Update_Activity.this, AlarmReceiver.class);

        intent.removeExtra("title");
        intent.removeExtra("comment");
        intent.removeExtra("calendar");


        String strTitle = eventNameETUPD.getText().toString();
        String strComment = eventCommentETUPD.getText().toString();

        intent.putExtra("title", strTitle);
        intent.putExtra("comment", strComment);
        intent.putExtra("calendar", myTime);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(Edit_Update_Activity.this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);


        Toast.makeText(Edit_Update_Activity.this, "Alarm set at: " + cc.getTime().toString(), Toast.LENGTH_SHORT).show();


    }


    public void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


    }


    void getSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("comment") && getIntent().hasExtra("date") && getIntent().hasExtra("time")) {
            id_row = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            comment = getIntent().getStringExtra("comment");
            date = LocalDate.parse(getIntent().getStringExtra("date"));
            time = LocalTime.parse(getIntent().getStringExtra("time"));
            alarmState = Integer.parseInt(getIntent().getStringExtra("alarm"));
            eventNameETUPD.setText(title);
            eventCommentETUPD.setText(comment);
            eventDateTV.setText(CalendarUtils.formattedDateEventEdit(date));
            eventTimeTV.setText(CalendarUtils.formattedShortTime(time));


        }
    }


    public void addAlarm() {


        LayoutInflater lf = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = lf.inflate(R.layout.activity_radio_button_for_reminder, null);
        TextView tenMinBefore = rowView.findViewById(R.id.tenMinBefore);
        TextView fiveMinBefore = rowView.findViewById(R.id.fiveMinBefore);
        TextView eventsTimeReminder = rowView.findViewById(R.id.eventsTimeReminder);
        TextView fiveMinLater = rowView.findViewById(R.id.fiveMinLater);
        TextView tenMinLater = rowView.findViewById(R.id.tenMinLater);
        TextView customChoice = rowView.findViewById(R.id.customChoice);


        cReminder.set(Calendar.YEAR, date.getYear());
        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());


        final AlertDialog dialog = new AlertDialog.Builder(this).setView(rowView)
                .setTitle("Επιλογή Υπενθύμισης")
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                tenMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 10);
                        cReminder.set(Calendar.MILLISECOND,0);
                        ok.add(cReminder.getTime());

                        dialog.dismiss();
                    }
                });

                fiveMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 5);
                        cReminder.set(Calendar.MILLISECOND,0);
                        ok.add(cReminder.getTime());


                        dialog.dismiss();
                    }
                });
                eventsTimeReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
                        cReminder.set(Calendar.MILLISECOND,0);
                        ok.add(cReminder.getTime());

                        dialog.dismiss();
                    }
                });
                fiveMinLater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() + 5);

                        cReminder.set(Calendar.MILLISECOND,0);
                        ok.add(cReminder.getTime());



                        dialog.dismiss();
                    }
                });
                tenMinLater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() + 10);


                        cReminder.set(Calendar.MILLISECOND,0);
                        ok.add(cReminder.getTime());


                        dialog.dismiss();
                    }
                });

                customChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
                        showChangeTimeForReminder(cReminder.get(Calendar.HOUR_OF_DAY), cReminder.get(Calendar.MINUTE));

                        dialog.dismiss();
                    }
                });

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (int i = 0; i < ok.size(); i++) {
                    for (int j = i + 1; j < ok.size(); j++) {
                        if (ok.get(i).getMonth() == ok.get(j).getMonth() &&
                                ok.get(i).getYear() == ok.get(j).getYear() &&
                                ok.get(i).getDay() == ok.get(j).getDay() &&
                                ok.get(i).getHours() == ok.get(j).getHours() &&
                                ok.get(i).getMinutes() == ok.get(j).getMinutes()) {
                            ok.remove(i);

                            Toast.makeText(Edit_Update_Activity.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                remindersAdapter = new RemindersAdapter(getApplicationContext(), ok);

                remindersListViewUPD.setVisibility(View.VISIBLE);
                remindersListViewUPD.setAdapter(remindersAdapter);


            }
        });
        dialog.show();


    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myandroidReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("myandroid", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void updEventAction() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(Edit_Update_Activity.this);
        String eventName = eventNameETUPD.getText().toString();
        String eventComment = eventCommentETUPD.getText().toString();


        Cursor cursorRem = myDB.readAllReminder();
        ok.sort((o1, o2) -> o1.compareTo(o2));


        if (ok.size() > 0) {
            alarmState = 1;
            myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState));

            cursorRem.moveToPosition(-1);


                for (int i = 0; i < ok.size(); i++) {
                    while (cursorRem.moveToNext()) {
                        long mytestLong = Date.parse(cursorRem.getString(2));
                        Date lastDate = new Date(mytestLong);
                    for (int j=0; j<ok.size(); j++)
                    {
                        if (ok.get(j).equals(lastDate)) {
                            ok.remove(j);
                        }


                    }

                    }

                    if (ok.size() > 0) {
                        myDB.addReminder(id_row, ok.get(i));


                    }
                }



        } else {
            alarmState = 0;
            myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState));
            ok.size();
        }


        cursorRem.close();




        Cursor secondRem = myDB.readAllReminder();

        secondRem.moveToPosition(-1);

                while (secondRem.moveToNext()) {

                    for (int i=0; i<ok.size(); i++) {

                        long mytestLongUpd = Date.parse(secondRem.getString(2));
                    Date lastDateUpd = new Date(mytestLongUpd);
                    if (secondRem.getString(1).equals(id_row) ) {
                      if (lastDateUpd.equals(ok.get(i))) {
                          testRemUpd.add(secondRem.getString(0));
                      }
                    }


                }

            }


            secondRem.close();




        for (int i=0; i<testRemUpd.size(); i++) {
            startAlarm(Integer.parseInt(testRemUpd.get(i)),CalendarUtils.dateToCalendar(ok.get(i)));
        }



        Intent i1 = new Intent(Edit_Update_Activity.this, MainActivity.class);
        Boolean myBool = true;
        i1.putExtra("date", date);


        i1.putExtra("bool", myBool);


        overridePendingTransition(0, 0);
        startActivity(i1);
        overridePendingTransition(0, 0);


    }
}
