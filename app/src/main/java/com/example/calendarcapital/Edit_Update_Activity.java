package com.example.calendarcapital;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Edit_Update_Activity extends AppCompatActivity {

    private EditText eventNameETUPD, eventCommentETUPD,repeatCounter;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV, addAlarmButtonUPD, addRepeatButtonUPD,repeatCountTvUPD;
    private TextView oneDayBefore, oneHourMinBefore, halfHourMinBefore, fifteenMinBefore, tenMinBefore, fiveMinBefore, customChoice;
    private TextView everyDay, everyWeek, everyMonth, everyYear, customRepeat;
    private View oneDayView, oneHourView, halfMinView, fifteenMinView, tenMinView, fiveMinView, aboveAlarmButtonUPD;
    Button btnUpdate;
    RemindersAdapter remindersAdapter;
    ImageButton cancelReminderImageViewUPD, eventEditBackButtonUPD, eventEditRefreshButtonUPD, cancelRepeatUPD;
    LinearLayout reminderLayoutUPD;
    ListView remindersListViewUPD;
    int hour, min;
    private static LocalDate date;
    private static LocalDate events_date;
    private static LocalTime time;
    int alarmState, repeatState;
    String parent_id;
    public Date oneDayBeforeDate, oneHourBeforeDate, halfHourBeforeDate, fifteenMinBeforeDate, tenMinBeforeDate, fiveMinBeforeDate;
    private Activity mCurrentActivity;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;
    int repeatCounterInt;

    Calendar cReminder = Calendar.getInstance();
    Calendar cRepeat = Calendar.getInstance();
    Calendar cDatePicker = Calendar.getInstance();
    String id_row, title, comment;
    ArrayList<Date> repeats_listUPD = new ArrayList<>();
    ArrayList<String> list_repeat_for_dbUPD = new ArrayList<>();
    ArrayList<Date> reminders_upd_list = new ArrayList<>();
    ArrayList<String> idsOfNewReminders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_update);
        initWidgets();
        getSetIntentData();
        showExistedRemindersFromDB();
        mCurrentActivity = this;
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
                showChangeDate(cDatePicker.get(Calendar.YEAR), cDatePicker.get(Calendar.MONTH), cDatePicker.get(Calendar.DAY_OF_MONTH));
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

        addRepeatButtonUPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRepeat();
            }
        });

        cancelRepeatUPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatState = 0;
                cancelRepeatUPD.setVisibility(View.GONE);
                repeatCountTvUPD.setVisibility(View.GONE);
                addRepeatButtonUPD.setText(R.string.repeat_gr);
            }
        });


    }

    private void getSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("comment") && getIntent().hasExtra("date") && getIntent().hasExtra("time")) {
            id_row = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            comment = getIntent().getStringExtra("comment");
            date = LocalDate.parse(getIntent().getStringExtra("date"));
            time = LocalTime.parse(getIntent().getStringExtra("time"));
            alarmState = Integer.parseInt(getIntent().getStringExtra("alarm"));
            repeatState = Integer.parseInt(getIntent().getStringExtra("repeat"));
            if (getIntent().getStringExtra("parent_id") == null)
            {
                parent_id = null;
            }else {
                parent_id = getIntent().getStringExtra("parent_id");
            }
            setRepeatFromIntent();

            eventNameETUPD.setText(title);
            eventCommentETUPD.setText(comment);
            eventDateTV.setText(CalendarUtils.formattedDateEventEdit(date));
            eventTimeTV.setText(CalendarUtils.formattedShortTime(time));
            events_date = date;

        }
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
        aboveAlarmButtonUPD = findViewById(R.id.aboveAddRemViewUPD);
        addRepeatButtonUPD = findViewById(R.id.addRepeatButtonUPD);
        cancelRepeatUPD = findViewById(R.id.cancelRepeatUPD);
        repeatCountTvUPD = findViewById(R.id.repeatCountTvUPD);

    }

    private void setRepeatFromIntent() {
        if (repeatState == 1) {
            addRepeatButtonUPD.setText(R.string.every_day_gr);
            cancelRepeatUPD.setVisibility(View.VISIBLE);

        } else if (repeatState == 2) {
            addRepeatButtonUPD.setText(R.string.every_week_gr);
            cancelRepeatUPD.setVisibility(View.VISIBLE);
        } else if (repeatState == 3) {
            addRepeatButtonUPD.setText(R.string.every_month_gr);
            cancelRepeatUPD.setVisibility(View.VISIBLE);
        } else if (repeatState == 4) {
            addRepeatButtonUPD.setText(R.string.every_year_gr);
            cancelRepeatUPD.setVisibility(View.VISIBLE);
        } else if (repeatState == 5) {
            addRepeatButtonUPD.setText(R.string.custom_gr);
            cancelRepeatUPD.setVisibility(View.VISIBLE);
        } else {
            addRepeatButtonUPD.setText(R.string.repeat_gr);
            cancelRepeatUPD.setVisibility(View.GONE);
        }
    }

    private void setRemindersForListViaDate() {
        oneDayBeforeDate = CalendarUtils.dateForMinusDay(date, time);
        oneHourBeforeDate = CalendarUtils.dateForOneHourBefore(date, time);
        halfHourBeforeDate = CalendarUtils.dateForHalfHourBefore(date, time);
        fifteenMinBeforeDate = CalendarUtils.dateForFifteenMinBefore(date, time);
        tenMinBeforeDate = CalendarUtils.dateForTenMinBefore(date, time);
        fiveMinBeforeDate = CalendarUtils.dateForFiveMinBefore(date, time);
    }

    private void hideAdReminderDynamically() {

        if (mCurrentActivity instanceof Edit_Update_Activity) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            scheduledFuture = executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update ui here
                            setRemindersForListViaDate();
                            for (int i = 0; i < reminders_upd_list.size(); i++) {
                                if (reminders_upd_list.get(i).equals(oneDayBeforeDate)) {
                                    oneDayBefore.setVisibility(View.GONE);
                                    oneDayView.setVisibility(View.GONE);
                                }

                                if (reminders_upd_list.get(i).equals(oneHourBeforeDate)) {
                                    oneHourMinBefore.setVisibility(View.GONE);
                                    oneHourView.setVisibility(View.GONE);
                                }

                                if (reminders_upd_list.get(i).equals(halfHourBeforeDate)) {
                                    halfHourMinBefore.setVisibility(View.GONE);
                                    halfMinView.setVisibility(View.GONE);
                                }

                                if (reminders_upd_list.get(i).equals(fifteenMinBeforeDate)) {
                                    fifteenMinBefore.setVisibility(View.GONE);
                                    fifteenMinView.setVisibility(View.GONE);
                                }

                                if (reminders_upd_list.get(i).equals(tenMinBeforeDate)) {
                                    tenMinBefore.setVisibility(View.GONE);
                                    tenMinView.setVisibility(View.GONE);
                                }

                                if (reminders_upd_list.get(i).equals(fiveMinBeforeDate)) {
                                    fiveMinBefore.setVisibility(View.GONE);
                                    fiveMinView.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }, 0, 100, TimeUnit.MILLISECONDS); // runs every 100 milliseconds
        }
    }

    public void showExistedRemindersFromDB() {
        String alarmDate;
        MyDatabaseHelper myDb = new MyDatabaseHelper(Edit_Update_Activity.this);


        Cursor cursor = myDb.readAllEvents();
        Cursor cursorRem = myDb.readAllReminder();

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(id_row)) {
                alarmDate = cursor.getString(5);
                if (alarmDate.equals("1") && reminders_upd_list.isEmpty()) {


                    cursorRem.moveToPosition(-1);
                    //----Problem here maybe--------//
                    while (cursorRem.moveToNext()) {

                        if (cursorRem.getString(1).equals(id_row)) {

                            long mytestLong = Date.parse(cursorRem.getString(2));
                            Date lastDate = new Date(mytestLong);
                            lastDate.setSeconds(0);
                            reminders_upd_list.add(lastDate);


                        }
                    }
                    reminders_upd_list.sort((o1, o2) -> o1.compareTo(o2));

                    for (int i = 0; i < reminders_upd_list.size(); i++) {
                        for (int j = i + 1; j < reminders_upd_list.size(); j++) {
                            if (reminders_upd_list.get(i).getMonth() == reminders_upd_list.get(j).getMonth() &&
                                    reminders_upd_list.get(i).getYear() == reminders_upd_list.get(j).getYear() &&
                                    reminders_upd_list.get(i).getDay() == reminders_upd_list.get(j).getDay() &&
                                    reminders_upd_list.get(i).getHours() == reminders_upd_list.get(j).getHours() &&
                                    reminders_upd_list.get(i).getMinutes() == reminders_upd_list.get(j).getMinutes()) {
                                reminders_upd_list.remove(i);

                            }
                        }
                    }

                    for (int i = 0; i < reminders_upd_list.size(); i++) {
                        for (int j = i + 1; j < reminders_upd_list.size(); j++) {
                            if (reminders_upd_list.get(i).getMonth() == reminders_upd_list.get(j).getMonth() &&
                                    reminders_upd_list.get(i).getYear() == reminders_upd_list.get(j).getYear() &&
                                    reminders_upd_list.get(i).getDay() == reminders_upd_list.get(j).getDay() &&
                                    reminders_upd_list.get(i).getHours() == reminders_upd_list.get(j).getHours() &&
                                    reminders_upd_list.get(i).getMinutes() == reminders_upd_list.get(j).getMinutes()) {
                                reminders_upd_list.remove(i);

                            }
                        }
                    }

                    reminders_upd_list.sort((o1, o2) -> o1.compareTo(o2));
                    remindersAdapter = new RemindersAdapter(Edit_Update_Activity.this, reminders_upd_list);

                    remindersListViewUPD.setVisibility(View.VISIBLE);
                    remindersListViewUPD.setAdapter(remindersAdapter);
                } else if (!reminders_upd_list.isEmpty()) {

                    remindersAdapter = new RemindersAdapter(Edit_Update_Activity.this, reminders_upd_list);

                    remindersListViewUPD.setVisibility(View.VISIBLE);
                    remindersListViewUPD.setAdapter(remindersAdapter);
                }

            }


        }
        cursor.close();
        cursorRem.close();
        myDb.close();


    }

    public void updateIfEmptyListView() {
        if (mCurrentActivity instanceof Edit_Update_Activity) {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update ui here
                            if (reminders_upd_list.size() == 5) {
                                addAlarmButtonUPD.setVisibility(View.GONE);
                                aboveAlarmButtonUPD.setVisibility(View.GONE);
                            } else {
                                addAlarmButtonUPD.setVisibility(View.VISIBLE);
                                aboveAlarmButtonUPD.setVisibility(View.VISIBLE);
                            }

                            if (reminders_upd_list.isEmpty()) {
//                            remindersAdapter = new RemindersAdapter(EventEdit.this, ok);
//                            remindersAdapter.notifyDataSetChanged();
                                remindersListViewUPD.setVisibility(View.GONE);
                            } else if (reminders_upd_list.size() == 1) {
                                reminders_upd_list.sort((o1, o2) -> o1.compareTo(o2));
                                ViewGroup.LayoutParams paramsListView = remindersListViewUPD.getLayoutParams();
                                paramsListView.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                remindersListViewUPD.setLayoutParams(paramsListView);
                            } else {
                                reminders_upd_list.sort((o1, o2) -> o1.compareTo(o2));
                                ViewGroup.LayoutParams paramsListView = remindersListViewUPD.getLayoutParams();
                                paramsListView.height = 500;
                                remindersListViewUPD.setLayoutParams(paramsListView);
                            }
                        }
                    });
                }
            }, 0, 100, TimeUnit.MILLISECONDS); // runs every 100 milliseconds
        }
        }


    private void alertDialogCountRepeat()
    {
        final  View viewCountRepeat = getLayoutInflater().inflate(R.layout.repeat_counter, null);

        repeatCounter = viewCountRepeat.findViewById(R.id.repeatCounter);

        AlertDialog.Builder builderCountRepeat = new AlertDialog.Builder(Edit_Update_Activity.this);
        builderCountRepeat.setView(viewCountRepeat).setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatCounterInt = Integer.parseInt(repeatCounter.getText().toString());
                String strCount = "Ο αριθμός επαναλήψεων είναι: " + repeatCounterInt;
                repeatCountTvUPD.setVisibility(View.VISIBLE);
                repeatCountTvUPD.setText(strCount);

                if (repeatState == 1) {

                    for (int i = 0; i < repeatCounterInt; i++) {
                        cRepeat.add(Calendar.DAY_OF_YEAR,1);
                        repeats_listUPD.add(cRepeat.getTime());

                    }
                } else if (repeatState == 2) {

                    for (int i = 0; i < repeatCounterInt; i++) {

                        cRepeat.add(Calendar.DAY_OF_YEAR,7);
                        repeats_listUPD.add(cRepeat.getTime());
                    }


                } else if (repeatState == 3) {
                    for (int i = 0; i < repeatCounterInt; i++) {

                        cRepeat.add(Calendar.MONTH,1);
                        repeats_listUPD.add(cRepeat.getTime());
                    }

                } else if (repeatState == 4) {

                    for (int i = 0; i < repeatCounterInt; i++) {

                        cRepeat.add(Calendar.YEAR,1);
                        repeats_listUPD.add(cRepeat.getTime());
                    }
                } else if (repeatState == 5) {
                }


                dialog.dismiss();
            }
        }).setNegativeButton("Άκυρο", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatCountTvUPD.setVisibility(View.GONE);
                addRepeatButtonUPD.setText("Επανάληψη");
                cancelRepeatUPD.setVisibility(View.GONE);
                addRepeat();
            }
        }).setTitle("Προσθέστε το πλήθος επαναλήψεων.");
        builderCountRepeat.show();
    }

    private void alertDialogCountRepeatIfDateChange(LocalDate date)
    {
        cRepeat.clear();
        cRepeat.set(date.getYear(),date.getMonthValue() - 1, date.getDayOfMonth());
        cRepeat.set(Calendar.HOUR_OF_DAY, 8);
        cRepeat.set(Calendar.MINUTE, 0);
        cRepeat.set(Calendar.SECOND, 0);
        repeats_listUPD.clear();
        if (repeatState == 1) {

            for (int i = 0; i < repeatCounterInt; i++) {
                cRepeat.add(Calendar.DAY_OF_YEAR,1);
                repeats_listUPD.add(cRepeat.getTime());

            }
        } else if (repeatState == 2) {

            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.DAY_OF_YEAR,7);
                repeats_listUPD.add(cRepeat.getTime());
            }


        } else if (repeatState == 3) {
            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.MONTH,1);
                repeats_listUPD.add(cRepeat.getTime());
            }

        } else if (repeatState == 4) {

            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.YEAR,1);
                repeats_listUPD.add(cRepeat.getTime());
            }
        } else if (repeatState == 5) {
        }
    }
    private void showChangeDate(int year, int month, int dayofmonth) {
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


    private void showChangeTime(int hours, int minute) {
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

    private void showChangeTimeForReminder(int hours, int minute) {

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

                    reminders_upd_list.add(fixedFromChangeTime.getTime());


                } else if (hour < 10 && min >= 10) {


                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    reminders_upd_list.add(fixedFromChangeTime.getTime());

                } else if (hour >= 10 && min < 10) {


                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;
                    reminders_upd_list.add(fixedFromChangeTime.getTime());


                } else {

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    reminders_upd_list.add(fixedFromChangeTime.getTime());


                }


            }
        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                for (int i = 0; i < reminders_upd_list.size(); i++) {
                    for (int j = i + 1; j < reminders_upd_list.size(); j++) {
                        if (reminders_upd_list.get(i).getMonth() == reminders_upd_list.get(j).getMonth() &&
                                reminders_upd_list.get(i).getYear() == reminders_upd_list.get(j).getYear() &&
                                reminders_upd_list.get(i).getDay() == reminders_upd_list.get(j).getDay() &&
                                reminders_upd_list.get(i).getHours() == reminders_upd_list.get(j).getHours() &&
                                reminders_upd_list.get(i).getMinutes() == reminders_upd_list.get(j).getMinutes()) {
                            reminders_upd_list.remove(i);

                            Toast.makeText(Edit_Update_Activity.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                remindersAdapter = new RemindersAdapter(getApplicationContext(), reminders_upd_list);

                remindersListViewUPD.setVisibility(View.VISIBLE);
                remindersListViewUPD.setAdapter(remindersAdapter);
            }
        });
        timePickerDialog.show();
    }

    private void startAlarm(int alarmId, Calendar cc) {


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(Edit_Update_Activity.this, AlarmReceiver.class);

        intent.removeExtra("title");
        intent.removeExtra("comment");


        String strTitle = eventNameETUPD.getText().toString();
        String strComment = eventCommentETUPD.getText().toString();

        intent.putExtra("title", strTitle);
        intent.putExtra("comment", strComment);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(Edit_Update_Activity.this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);


        Toast.makeText(Edit_Update_Activity.this, "Alarm set at: " + cc.getTime().toString(), Toast.LENGTH_SHORT).show();


    }





    private void addAlarm() {

        LayoutInflater lf = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = lf.inflate(R.layout.activity_radio_button_for_reminder, null);
        oneDayBefore = rowView.findViewById(R.id.oneDayBefore);
        oneHourMinBefore = rowView.findViewById(R.id.oneHourMinBefore);
        halfHourMinBefore = rowView.findViewById(R.id.halfHourMinBefore);
        fifteenMinBefore = rowView.findViewById(R.id.fifteenMinBefore);
        tenMinBefore = rowView.findViewById(R.id.tenMinBefore);
        fiveMinBefore = rowView.findViewById(R.id.fiveMinBefore);
        customChoice = rowView.findViewById(R.id.customChoice);
        oneDayView = (View) rowView.findViewById(R.id.oneDayView);
        oneHourView = (View) rowView.findViewById(R.id.oneHourView);
        halfMinView = (View) rowView.findViewById(R.id.halfMinView);
        fifteenMinView = (View) rowView.findViewById(R.id.fifteenMinView);
        tenMinView = (View) rowView.findViewById(R.id.tenMinView);
        fiveMinView = (View) rowView.findViewById(R.id.fiveMinView);

        hideAdReminderDynamically();


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

                oneDayBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
                        date.minusDays(1);
                        cReminder.setTime(Date.from(date.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
                        cReminder.set(Calendar.MINUTE, time.getMinute());
                        cReminder.set(Calendar.MILLISECOND, 0);
                        oneDayBeforeDate = cReminder.getTime();
                        reminders_upd_list.add(oneDayBeforeDate);
                        dialog.dismiss();

                    }
                });

                oneHourMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
                        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour() - 1);
                        cReminder.set(Calendar.MILLISECOND, 0);
                        oneHourBeforeDate = cReminder.getTime();
                        reminders_upd_list.add(oneHourBeforeDate);

                        dialog.dismiss();
                    }
                });

                halfHourMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 30);
                        cReminder.set(Calendar.MILLISECOND, 0);
                        halfHourBeforeDate = cReminder.getTime();
                        reminders_upd_list.add(halfHourBeforeDate);


                        dialog.dismiss();
                    }
                });

                fifteenMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 15);
                        cReminder.set(Calendar.MILLISECOND, 0);
                        fifteenMinBeforeDate = cReminder.getTime();
                        reminders_upd_list.add(fifteenMinBeforeDate);


                        dialog.dismiss();
                    }
                });
                tenMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 10);
                        cReminder.set(Calendar.MILLISECOND, 0);

                        tenMinBeforeDate = cReminder.getTime();
                        reminders_upd_list.add(tenMinBeforeDate);

                        dialog.dismiss();
                    }
                });

                fiveMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 5);
                        cReminder.set(Calendar.MILLISECOND, 0);
                        fiveMinBeforeDate = cReminder.getTime();
                        reminders_upd_list.add(fiveMinBeforeDate);


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
                for (int i = 0; i < reminders_upd_list.size(); i++) {
                    for (int j = i + 1; j < reminders_upd_list.size(); j++) {
                        if (reminders_upd_list.get(i).getMonth() == reminders_upd_list.get(j).getMonth() &&
                                reminders_upd_list.get(i).getYear() == reminders_upd_list.get(j).getYear() &&
                                reminders_upd_list.get(i).getDay() == reminders_upd_list.get(j).getDay() &&
                                reminders_upd_list.get(i).getHours() == reminders_upd_list.get(j).getHours() &&
                                reminders_upd_list.get(i).getMinutes() == reminders_upd_list.get(j).getMinutes()) {
                            reminders_upd_list.remove(i);

                            Toast.makeText(Edit_Update_Activity.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                remindersAdapter = new RemindersAdapter(getApplicationContext(), reminders_upd_list);

                remindersListViewUPD.setVisibility(View.VISIBLE);
                remindersListViewUPD.setAdapter(remindersAdapter);


            }
        });

        dialog.show();


    }

    private void addRepeat() {

        cRepeat.set(Calendar.YEAR, date.getYear());
        cRepeat.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cRepeat.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cRepeat.set(Calendar.HOUR_OF_DAY, 8);
        cRepeat.set(Calendar.MINUTE, 0);
        cRepeat.set(Calendar.SECOND, 0);


        LayoutInflater lf = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = lf.inflate(R.layout.repeat_alert_dialog, null);

        everyDay = rowView.findViewById(R.id.everyDay);
        everyWeek = rowView.findViewById(R.id.everyWeek);
        everyMonth = rowView.findViewById(R.id.everyMonth);
        everyYear = rowView.findViewById(R.id.everyYear);
        customRepeat = rowView.findViewById(R.id.customRepeat);

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(rowView)
                .setTitle("Επιλογή Επανάληψης")
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                everyDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 1;
                        cancelRepeatUPD.setVisibility(View.VISIBLE);
                        addRepeatButtonUPD.setText(R.string.every_day_gr);


                        dialog.dismiss();
                    }
                });

                everyWeek.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 2;
                        cancelRepeatUPD.setVisibility(View.VISIBLE);
                        addRepeatButtonUPD.setText(R.string.every_week_gr);

                        dialog.dismiss();
                    }
                });

                everyMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 3;
                        cancelRepeatUPD.setVisibility(View.VISIBLE);
                        addRepeatButtonUPD.setText(R.string.every_month_gr);


                        dialog.dismiss();
                    }
                });

                everyYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 4;
                        cancelRepeatUPD.setVisibility(View.VISIBLE);
                        addRepeatButtonUPD.setText(R.string.every_year_gr);

                        dialog.dismiss();
                    }
                });

                customRepeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 5;
                        dialog.dismiss();
                    }
                });


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


// ------------------------Events------------------------------------

    private void updateAndSaveEvent()
    {
        MyDatabaseHelper myDB = new MyDatabaseHelper(Edit_Update_Activity.this);
        String eventName = eventNameETUPD.getText().toString();
        String eventComment = eventCommentETUPD.getText().toString();


        Cursor cursorRem = myDB.readAllReminder();
        reminders_upd_list.sort((o1, o2) -> o1.compareTo(o2));


        if (reminders_upd_list.size() > 0) {
            alarmState = 1;
            if (repeatState==0) {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), parent_id);
            }else
            {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), null);

            }
            cursorRem.moveToPosition(-1);


            for (int i = 0; i < reminders_upd_list.size(); i++) {
                while (cursorRem.moveToNext()) {
                    long mytestLong = Date.parse(cursorRem.getString(2));
                    Date lastDate = new Date(mytestLong);
                    for (int j = 0; j < reminders_upd_list.size(); j++) {
                        if (reminders_upd_list.get(j).equals(lastDate)) {
                            reminders_upd_list.remove(j);
                        }


                    }

                }

                if (reminders_upd_list.size() > 0) {
                    myDB.addReminder(id_row, reminders_upd_list.get(i));


                }
            }


        } else {
            alarmState = 0;
            if (repeatState==0) {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), parent_id);
            }else
            {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), null);

            }
        }


        cursorRem.close();
        myDB.close();
    }

    private void giveIdToStartAlarmsEvent()
    {        MyDatabaseHelper myDB = new MyDatabaseHelper(Edit_Update_Activity.this);

        Cursor secondRem = myDB.readAllReminder();

        secondRem.moveToPosition(-1);

        while (secondRem.moveToNext()) {

            for (int i = 0; i < reminders_upd_list.size(); i++) {

                long mytestLongUpd = Date.parse(secondRem.getString(2));
                Date lastDateUpd = new Date(mytestLongUpd);
                if (secondRem.getString(1).equals(id_row)) {
                    if (lastDateUpd.equals(reminders_upd_list.get(i))) {
                        idsOfNewReminders.add(secondRem.getString(0));
                    }
                }


            }

        }


        secondRem.close();
        myDB.close();

        for (int i = 0; i < idsOfNewReminders.size(); i++) {
            startAlarm(Integer.parseInt(idsOfNewReminders.get(i)), CalendarUtils.dateToCalendar(reminders_upd_list.get(i)));
        }

    }

    private void makeAndSaveRepeatUPD()
    {        MyDatabaseHelper myDB = new MyDatabaseHelper(Edit_Update_Activity.this);

        Cursor cursor = myDB.readAllEvents();


        repeats_listUPD.sort((o1, o2) -> o1.compareTo(o2));

        if (repeats_listUPD.size()>0)
        {
            while (cursor.moveToNext())
            {
                if (cursor.getString(0).equals(id_row)) {
                    for (int i = 0; i < repeats_listUPD.size(); i++) {

                        LocalDate dateDB = CalendarUtils.dateToLocalDate(repeats_listUPD.get(i));
                        LocalTime timeDB = CalendarUtils.dateToLocalTime(repeats_listUPD.get(i));

                        myDB.addEvent(cursor.getString(1),cursor.getString(2),dateDB,timeDB,"0",
                                "0",id_row);

                        list_repeat_for_dbUPD.add(cursor.getString(0));


                    }
                }
            }
        }

        cursor.close();

        for (int i = 0; i < list_repeat_for_dbUPD.size(); i++) {
            startAlarm(Integer.parseInt(list_repeat_for_dbUPD.get(i)), CalendarUtils.dateToCalendar(repeats_listUPD.get(i)));
        }

        myDB.close();

    }
//----------------------------------------------------------------------


    private void updEventAction() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(getApplicationContext());


        Cursor eventCursor = myDB.readAllEvents();

        eventCursor.moveToPosition(-1);
        while(eventCursor.moveToNext())
        {
            if (eventCursor.getString(0).equals(id_row) &&
                    events_date.equals(CalendarUtils.stringToLocalDate(eventCursor.getString(3))))
            {
                updateAndSaveEvent();
                giveIdToStartAlarmsEvent();
                if (date.equals(CalendarUtils.selectedDate) )
                {
                    makeAndSaveRepeatUPD();
                }else
                {
                    alertDialogCountRepeatIfDateChange(date);
                    makeAndSaveRepeatUPD();
                }


                break;
            }
        }




        eventCursor.close();
        myDB.close();

        Intent i1 = new Intent(Edit_Update_Activity.this, MainActivity.class);
        Boolean myBool = true;

        i1.putExtra("date", date);
        i1.putExtra("bool", myBool);


        overridePendingTransition(0, 0);
        startActivity(i1);
        overridePendingTransition(0, 0);


    }
}
