package com.example.calendarcapital;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, eventCommentET, repeatCounter,locationET;
    private Spinner color_spinner;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV, addAlarmButton, addRepeatButton,locationTV;
    private TextView oneDayBefore, oneHourMinBefore, halfHourMinBefore, fifteenMinBefore, tenMinBefore, fiveMinBefore, customChoice;
    private TextView everyDay, everyWeek, everyMonth, everyYear, customRepeat, repeatCountTv;
    Button btnSave;
    private View oneDayView, oneHourView, halfMinView, fifteenMinView, tenMinView, fiveMinView, aboveAlarmButton;
    RemindersAdapter remindersAdapter;
    ImageButton cancelReminderImageView, eventEditBackButton, eventEditRefreshButton, cancelRepeat;
    LinearLayout reminderLayout;
    ListView remindersListView;
    int hour, min;
    private static LocalDate date,myDD;
    private static LocalTime time;
    private Date oneDayBeforeDate, oneHourBeforeDate, halfHourBeforeDate, fifteenMinBeforeDate, tenMinBeforeDate, fiveMinBeforeDate;
    int alarmState, repeatState;
    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    Calendar cReminder = Calendar.getInstance();
    Calendar cRepeat = Calendar.getInstance();
    Calendar cDatePicker = Calendar.getInstance();
    ArrayList<Date> reminders_list = new ArrayList<>();
    ArrayList<Date> repeats_list = new ArrayList<>();
    ArrayList<String> list_reminders_for_db = new ArrayList<>();
    ArrayList<String> list_repeat_for_db = new ArrayList<>();
    private Activity mCurrentActivity;
    private ScheduledFuture<?> scheduledFuture;
    int repeatCounterInt;
    static String  stackNow;
    private static int color;
   static String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            time = LocalTime.parse(CalendarUtils.formattedShortTime(LocalTime.now()));
        }
        eventTimeTV.setText(CalendarUtils.formattedShortTime(time));
        date = CalendarUtils.selectedDate;

        if (!(date == null)) {
            eventDateTV.setText(date.toString().trim());
        }



        alarmState = 0;
        repeatState = 0;
        mCurrentActivity = this;
        setIntentsFromCustomRepeat();
        updateIfEmptyListView();
        changeColor();

        createNotificationChannel();


        btnSave.setOnClickListener(v -> saveEventAction());

        changeTimeTV.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showChangeTime(LocalTime.now().getHour(), LocalTime.now().getMinute());
            }
        });

        changeDateTV.setOnClickListener(v -> showChangeDate(cDatePicker.get(Calendar.YEAR), cDatePicker.get(Calendar.MONTH), cDatePicker.get(Calendar.DAY_OF_MONTH)));
        eventEditBackButton.setOnClickListener(v -> {

            Intent i = new Intent(EventEdit.this, MainActivity.class);

            i.putExtra("stack", stackNow);

            String myTemp = CalendarUtils.selectedDate.toString();
            i.putExtra("tempDate", myTemp);

            startActivity(i);
        });

        eventEditRefreshButton.setOnClickListener(v -> AllEventsList.reloadActivity(EventEdit.this));


        addAlarmButton.setOnClickListener(v -> addAlarm());

        addRepeatButton.setOnClickListener(v -> addRepeat());

        cancelRepeat.setOnClickListener(v -> {
            repeatState = 0;
            cancelRepeat.setVisibility(View.GONE);
            repeatCountTv.setVisibility(View.GONE);
            addRepeatButton.setText(R.string.repeat_gr);
            CustomRepeatActivity.customDatesToSaveLocalDate.clear();
        });



    }


    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventCommentET = findViewById(R.id.eventCommentET);
        eventDateTV = findViewById(R.id.eventDateET);
        eventTimeTV = findViewById(R.id.eventTimeET);
        changeTimeTV = findViewById(R.id.changeTimeTV);
        changeDateTV = findViewById(R.id.changeDateTV);
        addAlarmButton = findViewById(R.id.addAlarmButton);
        eventEditBackButton = findViewById(R.id.eventEditBackButton);
        reminderLayout = findViewById(R.id.reminderLayout);
        cancelReminderImageView = findViewById(R.id.cancelReminderImageView);
        eventEditRefreshButton = findViewById(R.id.eventEditRefreshButton);
        remindersListView = findViewById(R.id.remindersListView);
        aboveAlarmButton = findViewById(R.id.aboveAddRemView);
        addRepeatButton = findViewById(R.id.addRepeatButton);
        cancelRepeat = findViewById(R.id.cancelRepeat);
        repeatCountTv = findViewById(R.id.repeatCountTv);
        btnSave = findViewById(R.id.btnSave);
        color_spinner = findViewById(R.id.changeColorSpinner);
        locationET = findViewById(R.id.locationET);
        locationTV = findViewById(R.id.locationTV);


    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {



        } else {

        }
    }
    private void setIntentsFromCustomRepeat() {
        if (getIntent().hasExtra("5")) {
            repeatState = 5;
            addRepeatButton.setText(CustomRepeatActivity.textForEventEdit);
            cancelRepeat.setVisibility(View.VISIBLE);
        }

        if (getIntent().hasExtra("tittle")) {
            eventNameET.setText(getIntent().getStringExtra("tittle"));
        }

        if (getIntent().hasExtra("comment")) {
            eventCommentET.setText(getIntent().getStringExtra("comment"));
        }
        if (getIntent().hasExtra("stack")) {
            stackNow = getIntent().getStringExtra("stack");
        }
    }

    private void hideAdReminderDynamically() {
        if (mCurrentActivity instanceof EventEdit) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            scheduledFuture = executorService.scheduleAtFixedRate(() -> runOnUiThread(() -> {
                // update ui here


                for (int i = 0; i < reminders_list.size(); i++) {
                    if (reminders_list.get(i).equals(oneDayBeforeDate)) {
                        oneDayBefore.setVisibility(View.GONE);
                        oneDayView.setVisibility(View.GONE);
                    }

                    if (reminders_list.get(i).equals(oneHourBeforeDate)) {
                        oneHourMinBefore.setVisibility(View.GONE);
                        oneHourView.setVisibility(View.GONE);
                    }

                    if (reminders_list.get(i).equals(halfHourBeforeDate)) {
                        halfHourMinBefore.setVisibility(View.GONE);
                        halfMinView.setVisibility(View.GONE);
                    }

                    if (reminders_list.get(i).equals(fifteenMinBeforeDate)) {
                        fifteenMinBefore.setVisibility(View.GONE);
                        fifteenMinView.setVisibility(View.GONE);
                    }

                    if (reminders_list.get(i).equals(tenMinBeforeDate)) {
                        tenMinBefore.setVisibility(View.GONE);
                        tenMinView.setVisibility(View.GONE);
                    }

                    if (reminders_list.get(i).equals(fiveMinBeforeDate)) {
                        fiveMinBefore.setVisibility(View.GONE);
                        fiveMinView.setVisibility(View.GONE);
                    }
                }
            }), 0, 100, TimeUnit.MILLISECONDS); //runs every 100 milliseconds
        }

    }

    private void updateIfEmptyListView() {
        if (mCurrentActivity instanceof EventEdit) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(() -> runOnUiThread(() -> {
                // update ui here
                if (reminders_list.size() == 5) {
                    addAlarmButton.setVisibility(View.GONE);
                    aboveAlarmButton.setVisibility(View.GONE);
                } else {
                    addAlarmButton.setVisibility(View.VISIBLE);
                    aboveAlarmButton.setVisibility(View.VISIBLE);
                }

                if (reminders_list.isEmpty()) {
                    remindersListView.setVisibility(View.GONE);
                } else if (reminders_list.size() == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        reminders_list.sort(Date::compareTo);
                    }
                    ViewGroup.LayoutParams paramsListView = remindersListView.getLayoutParams();
                    paramsListView.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    remindersListView.setLayoutParams(paramsListView);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        reminders_list.sort(Date::compareTo);
                    }
                    ViewGroup.LayoutParams paramsListView = remindersListView.getLayoutParams();
                    paramsListView.height = 500;
                    remindersListView.setLayoutParams(paramsListView);
                }
            }), 0, 100, TimeUnit.MILLISECONDS);
        }


    }

    private void changeColor()
    {
        List<String> items = Arrays.asList("Προκαθορισμένο","Κόκκινο", "Κίτρινο", "Πράσινο", "Μπλε", "Μωβ");
        List<Drawable> iconsDraw = Arrays.asList(ResourcesCompat.getDrawable(getResources(),R.drawable.default_circle,null),
                ResourcesCompat.getDrawable(getResources(),R.drawable.red_circle,null),
                ResourcesCompat.getDrawable(getResources(),R.drawable.yellow_circle,null),
                ResourcesCompat.getDrawable(getResources(),R.drawable.green_circle,null),
                ResourcesCompat.getDrawable(getResources(),R.drawable.blue_circle,null),
                ResourcesCompat.getDrawable(getResources(),R.drawable.purple_circle,null));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, items, iconsDraw);
        color_spinner.setAdapter(adapter);

        color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            if (position==0)
            {
                color=0;
            }else if (position==1)
            {
                color=1;
            }else if (position==2)
            {
                color=2;
            }else if (position==3)
            {
                color=3;
            }else if (position==4)
            {
                color=4;
            }else if (position==5)
            {
                color=5;
            }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                color=0;
            }
        });
    }


    private void showChangeDate(int year, int month, int dayofmonth) {
        ArrayList<Date> reminder_list_date_changed = new ArrayList<>();

        final DatePickerDialog StartTime = new DatePickerDialog(this, R.style.TimePickerTheme, (view, year1, monthOfYear, dayOfMonth) -> {
            int trueMonth = monthOfYear + 1;




                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myDD = LocalDate.of(year1, trueMonth, dayOfMonth);

                }

                eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = LocalDate.of(year1, trueMonth, dayOfMonth);
            }



            if (!(myDD == null))
            {
                if (!reminders_list.isEmpty()) {

                    for (int i = 0; i < reminders_list.size(); i++) {
                        Calendar cRemChanged = Calendar.getInstance();
                        cRemChanged.clear();
                        Date cRemBefore = reminders_list.get(i);
                        int yearTEST = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            yearTEST = myDD.getYear();
                        }
                        int monthTEST = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            monthTEST = myDD.getMonth().getValue()-1;
                        }
                        int dayTest = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            dayTest = myDD.getDayOfMonth();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            cRemChanged.set(Calendar.YEAR, yearTEST);
                            cRemChanged.set(Calendar.MONTH, monthTEST);
                            cRemChanged.set(Calendar.DAY_OF_MONTH, dayTest);
                            cRemChanged.set(Calendar.HOUR, cRemBefore.getHours());
                            cRemChanged.set(Calendar.MINUTE, cRemBefore.getMinutes());
                            cRemChanged.set(Calendar.SECOND, 0);
                            cRemChanged.set(Calendar.MILLISECOND, 0);
                            Date testD = cRemChanged.getTime();
                            reminder_list_date_changed.add(testD);
                        }


                    }
                    reminders_list.clear();
                    reminders_list.addAll(reminder_list_date_changed);
                    remindersAdapter.notifyDataSetChanged();

                }

            }else
            {
                Toast.makeText(mCurrentActivity, "problem showChangeDate_eventEdit", Toast.LENGTH_SHORT).show();
            }
        }, year, month, dayofmonth);


        StartTime.setTitle("Select Date");
        StartTime.show();
    }

    public void showChangeTime(int hours, int minute) {
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(EventEdit.this, R.style.TimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            min = selectedMinute;

            if (hour < 10 && min < 10) {
                @SuppressLint("DefaultLocale") String hourStr = String.format("%02d", hour);
                @SuppressLint("DefaultLocale") String minStr = String.format("%02d", min);
                String allTime = hourStr + ":" + minStr;

                eventTimeTV.setText(hourStr + ":" + minStr);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);
                }


            } else if (hour < 10 && min >= 10) {
                @SuppressLint("DefaultLocale") String hourStr = String.format("%02d", hour);
                String allTime = hourStr + ":" + min;

                eventTimeTV.setText(hourStr + ":" + min);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);
                }
            } else if (hour >= 10 && min < 10) {
                @SuppressLint("DefaultLocale") String minStr = String.format("%02d", min);

                String allTime = hour + ":" + minStr;

                eventTimeTV.setText(hour + ":" + minStr);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);
                }


            } else {
                String allTime = hour + ":" + min;

                eventTimeTV.setText(hour + ":" + min);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);
                }

            }


        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();


    }

    private void showChangeTimeForReminder(int hours, int minute) {

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(EventEdit.this, R.style.TimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            min = selectedMinute;

            Calendar fixedFromChangeTime;

            if (hour < 10 && min < 10) {


                cReminder.set(Calendar.HOUR_OF_DAY, hour);
                cReminder.set(Calendar.MINUTE, min);
                cReminder.set(Calendar.SECOND, 0);
                cReminder.set(Calendar.MILLISECOND, 0);
                fixedFromChangeTime = cReminder;

                reminders_list.add(fixedFromChangeTime.getTime());


            } else if (hour < 10 && min >= 10) {

                cReminder.set(Calendar.HOUR_OF_DAY, hour);
                cReminder.set(Calendar.MINUTE, min);
                cReminder.set(Calendar.SECOND, 0);
                cReminder.set(Calendar.MILLISECOND, 0);
                fixedFromChangeTime = cReminder;

                reminders_list.add(fixedFromChangeTime.getTime());


            } else if (hour >= 10 && min < 10) {


                cReminder.set(Calendar.HOUR_OF_DAY, hour);
                cReminder.set(Calendar.MINUTE, min);
                cReminder.set(Calendar.SECOND, 0);
                cReminder.set(Calendar.MILLISECOND, 0);
                fixedFromChangeTime = cReminder;
                reminders_list.add(fixedFromChangeTime.getTime());


            } else {

                cReminder.set(Calendar.HOUR_OF_DAY, hour);
                cReminder.set(Calendar.MINUTE, min);
                cReminder.set(Calendar.SECOND, 0);
                cReminder.set(Calendar.MILLISECOND, 0);
                fixedFromChangeTime = cReminder;

                reminders_list.add(fixedFromChangeTime.getTime());


            }


        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.setOnDismissListener(dialog -> {


            for (int i = 0; i < reminders_list.size(); i++) {
                for (int j = i + 1; j < reminders_list.size(); j++) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(reminders_list.get(i));

                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(reminders_list.get(j));

                    if (isSameDateTime(calendar1, calendar2)) {
                        reminders_list.remove(i);
                     Toast.makeText(EventEdit.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            remindersAdapter = new RemindersAdapter(getApplicationContext(), reminders_list);

            remindersListView.setVisibility(View.VISIBLE);
            remindersListView.setAdapter(remindersAdapter);
        });
        timePickerDialog.show();
    }
    private boolean isSameDateTime(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
                calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY) &&
                calendar1.get(Calendar.MINUTE) == calendar2.get(Calendar.MINUTE);
    }

    private void startAlarm(int alarmId, Calendar cc) {


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(EventEdit.this, AlarmReceiver.class);


        intent.removeExtra("title");
        intent.removeExtra("comment");


        String strTitle = eventNameET.getText().toString();
        String strComment = eventCommentET.getText().toString();

        intent.putExtra("title", strTitle);
        intent.putExtra("comment", strComment);
        intent.putExtra("alarmtime", cc.getTimeInMillis());
        intent.putExtra("alarmid", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(EventEdit.this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);
        }


        Toast.makeText(EventEdit.this, "Alarm set at: " + cc.getTime().toString(), Toast.LENGTH_SHORT).show();


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

    private void addRepeat() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cRepeat.set(Calendar.YEAR, date.getYear());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cRepeat.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cRepeat.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        }


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

        dialog.setOnShowListener(dialog1 -> {
            everyDay.setOnClickListener(v -> {

                alertDialogCountRepeat();
                repeatState = 1;
                cancelRepeat.setVisibility(View.VISIBLE);
                addRepeatButton.setText(R.string.every_day_gr);

                dialog1.dismiss();
            });

            everyWeek.setOnClickListener(v -> {
                alertDialogCountRepeat();
                repeatState = 2;
                cancelRepeat.setVisibility(View.VISIBLE);
                addRepeatButton.setText(R.string.every_week_gr);

                dialog1.dismiss();
            });

            everyMonth.setOnClickListener(v -> {
                alertDialogCountRepeat();
                repeatState = 3;
                cancelRepeat.setVisibility(View.VISIBLE);
                addRepeatButton.setText(R.string.every_month_gr);


                dialog1.dismiss();
            });

            everyYear.setOnClickListener(v -> {
                alertDialogCountRepeat();
                repeatState = 4;
                cancelRepeat.setVisibility(View.VISIBLE);
                addRepeatButton.setText(R.string.every_year_gr);

                dialog1.dismiss();
            });

            customRepeat.setOnClickListener(v -> {

                repeatState = 5;

                Intent iCustomRepeat = new Intent(EventEdit.this, CustomRepeatActivity.class);
                String flagForEventEdit = "0";
                iCustomRepeat.putExtra("flagBack", flagForEventEdit);
                iCustomRepeat.putExtra("date", date.toString());
                iCustomRepeat.putExtra("tittle", eventNameET.getText().toString());
                iCustomRepeat.putExtra("comment", eventCommentET.getText().toString());
                iCustomRepeat.putExtra("stack",stackNow);
                startActivity(iCustomRepeat);
                dialog1.dismiss();
            });


        });

        dialog.show();


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
            cReminder.set(Calendar.MINUTE, time.getMinute());
            cReminder.set(Calendar.SECOND, time.getSecond());
        }

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(rowView)
                .setTitle("Επιλογή Υπενθύμισης")
                .create();
        dialog.setOnShowListener(dialog1 -> {

            oneDayBefore.setOnClickListener(v -> {
                alarmState = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    date.minusDays(1);
                    cReminder.setTime(Date.from(date.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
                    cReminder.set(Calendar.MINUTE, time.getMinute());
                    cReminder.set(Calendar.MILLISECOND, 0);
                    oneDayBeforeDate = cReminder.getTime();
                    reminders_list.add(oneDayBeforeDate);
                    dialog1.dismiss();
                }
            });

            oneHourMinBefore.setOnClickListener(v -> {
                alarmState = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cReminder.set(Calendar.HOUR_OF_DAY, time.getHour() - 1);
                }
                cReminder.set(Calendar.MILLISECOND, 0);
                oneHourBeforeDate = cReminder.getTime();
                reminders_list.add(oneHourBeforeDate);

                dialog1.dismiss();
            });

            halfHourMinBefore.setOnClickListener(v -> {
                alarmState = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 30);
                }
                cReminder.set(Calendar.MILLISECOND, 0);
                halfHourBeforeDate = cReminder.getTime();
                reminders_list.add(halfHourBeforeDate);


                dialog1.dismiss();
            });
            fifteenMinBefore.setOnClickListener(v -> {
                alarmState = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 15);
                }
                cReminder.set(Calendar.MILLISECOND, 0);
                fifteenMinBeforeDate = cReminder.getTime();
                reminders_list.add(fifteenMinBeforeDate);


                dialog1.dismiss();
            });

            tenMinBefore.setOnClickListener(v -> {

                alarmState = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 10);
                }
                cReminder.set(Calendar.MILLISECOND, 0);
                tenMinBeforeDate = cReminder.getTime();

                reminders_list.add(tenMinBeforeDate);


                dialog1.dismiss();
            });

            fiveMinBefore.setOnClickListener(v -> {

                alarmState = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 5);
                }
                cReminder.set(Calendar.MILLISECOND, 0);
                fiveMinBeforeDate = cReminder.getTime();
                reminders_list.add(fiveMinBeforeDate);


                dialog1.dismiss();
            });


            customChoice.setOnClickListener(v -> {
                alarmState = 1;
                showChangeTimeForReminder(cReminder.get(Calendar.HOUR_OF_DAY), cReminder.get(Calendar.MINUTE));

                dialog1.dismiss();
            });

        });

        dialog.setOnDismissListener(dialog12 -> {
            for (int i = 0; i < reminders_list.size(); i++) {
                for (int j = i + 1; j < reminders_list.size(); j++) {
                    if (reminders_list.get(i).getMonth() == reminders_list.get(j).getMonth() &&
                            reminders_list.get(i).getYear() == reminders_list.get(j).getYear() &&
                            reminders_list.get(i).getDay() == reminders_list.get(j).getDay() &&
                            reminders_list.get(i).getHours() == reminders_list.get(j).getHours() &&
                            reminders_list.get(i).getMinutes() == reminders_list.get(j).getMinutes()) {
                        reminders_list.remove(i);

                        Toast.makeText(EventEdit.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            remindersAdapter = new RemindersAdapter(getApplicationContext(), reminders_list);

            remindersListView.setVisibility(View.VISIBLE);
            remindersListView.setAdapter(remindersAdapter);


        });
        dialog.show();


    }

    private void alertDialogCountRepeat() {
        final View viewCountRepeat = getLayoutInflater().inflate(R.layout.repeat_counter, null);

        repeatCounter = viewCountRepeat.findViewById(R.id.repeatCounter);

        AlertDialog.Builder builderCountRepeat = new AlertDialog.Builder(EventEdit.this);
        builderCountRepeat.setView(viewCountRepeat).setPositiveButton("Ναι", (dialog, which) -> {

            repeatCounterInt = Integer.parseInt(repeatCounter.getText().toString());
            String strCount = "Ο αριθμός επαναλήψεων είναι: " + repeatCounterInt;
            repeatCountTv.setVisibility(View.VISIBLE);
            repeatCountTv.setText(strCount);

            if (repeatState == 1) {

                for (int i = 0; i < repeatCounterInt; i++) {
                    cRepeat.add(Calendar.DAY_OF_YEAR, 1);
                    repeats_list.add(cRepeat.getTime());

                }
            } else if (repeatState == 2) {

                for (int i = 0; i < repeatCounterInt; i++) {

                    cRepeat.add(Calendar.DAY_OF_YEAR, 7);
                    repeats_list.add(cRepeat.getTime());
                }


            } else if (repeatState == 3) {
                for (int i = 0; i < repeatCounterInt; i++) {

                    cRepeat.add(Calendar.MONTH, 1);
                    repeats_list.add(cRepeat.getTime());
                }

            } else if (repeatState == 4) {

                for (int i = 0; i < repeatCounterInt; i++) {

                    cRepeat.add(Calendar.YEAR, 1);
                    repeats_list.add(cRepeat.getTime());
                }

            }


            dialog.dismiss();
        }).setNegativeButton("Άκυρο", (dialog, which) -> {
            repeatCountTv.setVisibility(View.GONE);
            addRepeatButton.setText("Επανάληψη");
            cancelRepeat.setVisibility(View.GONE);
            addRepeat();
        }).setTitle("Προσθέστε το πλήθος επαναλήψεων.");
        builderCountRepeat.show();
    }


    private void makeAndSaveEvent() {

        String eventName = eventNameET.getText().toString();
        location = locationET.getText().toString().trim();
        if (eventName.equals(""))
        {
            eventName="(Χωρίς τίτλο)";
        }
        String eventComment = eventCommentET.getText().toString();
        myDB.addEvent(eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), null, String.valueOf(color),location);

    }

    private void makeAndSaveReminders() {

        String idForReminder = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            reminders_list.sort(Date::compareTo);
        }
        Cursor cursor = myDB.readAllEvents();
        Cursor cursorRem = myDB.readAllReminder();
        if (reminders_list.size() > 0) {

            while (cursor.moveToNext()) {
                cursor.moveToLast();

                for (int i = 0; i < reminders_list.size(); i++) {
                    idForReminder = cursor.getString(0);
                    myDB.addReminder(idForReminder, reminders_list.get(i));

                }
            }
        }


        cursorRem.moveToPosition(-1);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            while (cursorRem.moveToNext()) {
                if (cursorRem.getString(1).equals(idForReminder)) {
                    list_reminders_for_db.add(cursorRem.getString(0));

                }


            }

        }

        cursor.close();
        cursorRem.close();
        for (int i = 0; i < list_reminders_for_db.size(); i++) {
            startAlarm(Integer.parseInt(list_reminders_for_db.get(i)), CalendarUtils.dateToCalendar(reminders_list.get(i)));
        }

        myDB.close();
    }

    private void makeAndSaveRepeat() {

        Cursor cursor = myDB.readAllEvents();

        String idForRepeat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            repeats_list.sort(Date::compareTo);
        }
        if (repeatState == 5) {
            for (LocalDate localDate : CustomRepeatActivity.customDatesToSaveLocalDate) {
                Calendar cForCustom = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    cForCustom.set(Calendar.YEAR, localDate.getYear());
                    cForCustom.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
                    cForCustom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
                }

                cForCustom.set(Calendar.HOUR_OF_DAY, 8);
                cForCustom.set(Calendar.MINUTE, 0);
                cForCustom.set(Calendar.SECOND, 0);
                Date dateFromCustomRepeat = cForCustom.getTime();
                repeats_list.add(dateFromCustomRepeat);
                if (localDate.equals(date)) {
                    repeats_list.remove(dateFromCustomRepeat);
                }
            }


        }

        if (repeats_list.size() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.moveToLast()) {
                    for (int i = 0; i < repeats_list.size(); i++) {
                        idForRepeat = cursor.getString(0);
                        LocalDate dateDB = CalendarUtils.dateToLocalDate(repeats_list.get(i));
                        LocalTime timeDB = CalendarUtils.dateToLocalTime(repeats_list.get(i));

                        myDB.addEvent(cursor.getString(1), cursor.getString(2), dateDB, timeDB, "0",
                                "0", idForRepeat,String.valueOf(color),location);

                        list_repeat_for_db.add(cursor.getString(0));


                    }
                }
            }
        }

        cursor.close();

        for (int i = 0; i < list_repeat_for_db.size(); i++) {
            startAlarm(Integer.parseInt(list_repeat_for_db.get(i)), CalendarUtils.dateToCalendar(repeats_list.get(i)));

        }

        myDB.close();

    }

    public void saveEventAction() {


        makeAndSaveEvent();
        makeAndSaveReminders();
        makeAndSaveRepeat();

        Intent i1 = new Intent(EventEdit.this, MainActivity.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            i1.putExtra("date", date);
        }

        String myTemp = CalendarUtils.selectedDate.toString();
        i1.putExtra("tempDate", myTemp);
        i1.putExtra("stack", stackNow);
        myDB.removeDuplicateReminders();
        overridePendingTransition(0, 0);
        startActivity(i1);
        overridePendingTransition(0, 0);


    }

}