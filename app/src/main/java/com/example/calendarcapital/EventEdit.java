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


public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, eventCommentET,repeatCounter;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV, addAlarmButton, addRepeatButton;
    private TextView oneDayBefore, oneHourMinBefore, halfHourMinBefore, fifteenMinBefore, tenMinBefore, fiveMinBefore, customChoice;
    private TextView everyDay, everyWeek, everyMonth, everyYear, customRepeat,repeatCountTv;
    Button btnSave;
    private View oneDayView, oneHourView, halfMinView, fifteenMinView, tenMinView, fiveMinView, aboveAlarmButton;
    RemindersAdapter remindersAdapter;
    ImageButton cancelReminderImageView, eventEditBackButton, eventEditRefreshButton, cancelRepeat;
    LinearLayout reminderLayout;
    ListView remindersListView;
    int hour, min;
    private static LocalDate date;
    private static LocalTime time;
    private Date oneDayBeforeDate, oneHourBeforeDate, halfHourBeforeDate, fifteenMinBeforeDate, tenMinBeforeDate, fiveMinBeforeDate;
    int alarmState, repeatState;
    Calendar cReminder = Calendar.getInstance();
    Calendar cRepeat = Calendar.getInstance();
    ArrayList<Date> reminders_list = new ArrayList<>();
    ArrayList<Date> repeats_list = new ArrayList<>();
    ArrayList<String> list_reminders_for_db = new ArrayList<>();
    ArrayList<String> list_repeat_for_db = new ArrayList<>();
    private Activity mCurrentActivity;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;
    int repeatCounterInt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.parse(CalendarUtils.formattedShortTime(LocalTime.now()));
        eventTimeTV.setText(CalendarUtils.formattedShortTime(time));
        date = CalendarUtils.selectedDate;
        eventDateTV.setText(CalendarUtils.formattedDateEventEdit(date));
        btnSave = findViewById(R.id.btnSave);
        alarmState = 0;
        repeatState = 0;
        mCurrentActivity = this;
        updateIfEmptyListView();

        createNotificationChannel();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventAction(v);
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
        eventEditBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(EventEdit.this, MainActivity.class);
                Boolean myBool = true;
                i.putExtra("bool", myBool);

                startActivity(i);
            }
        });

        eventEditRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllEventsList.reloadActivity(EventEdit.this);

            }
        });


        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();

            }
        });

        addRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRepeat();
            }
        });

        cancelRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatState = 0;
                cancelRepeat.setVisibility(View.GONE);
                repeatCountTv.setVisibility(View.GONE);
                addRepeatButton.setText(R.string.repeat_gr);
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



    }

    private void hideAdReminderDynamically() {
        if (mCurrentActivity instanceof EventEdit) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            scheduledFuture = executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                        }
                    });
                }
            }, 0, 100, TimeUnit.MILLISECONDS); //runs every 100 milliseconds
        }

    }

    private void updateIfEmptyListView() {
        if (mCurrentActivity instanceof EventEdit) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                                reminders_list.sort((o1, o2) -> o1.compareTo(o2));
                                ViewGroup.LayoutParams paramsListView = remindersListView.getLayoutParams();
                                paramsListView.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                remindersListView.setLayoutParams(paramsListView);
                            } else {
                                reminders_list.sort((o1, o2) -> o1.compareTo(o2));
                                ViewGroup.LayoutParams paramsListView = remindersListView.getLayoutParams();
                                paramsListView.height = 500;
                                remindersListView.setLayoutParams(paramsListView);
                            }
                        }
                    });
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
        }


    }


    private void showChangeDate(int year, int month, int dayofmonth) {
        final DatePickerDialog StartTime = new DatePickerDialog(this, R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int trueMonth = monthOfYear + 1;

                if (trueMonth < 10 && dayOfMonth >= 10) {
                    String str = String.format("%02d", trueMonth);

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);

                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));


                } else if (dayOfMonth < 10 && trueMonth < 10) {
                    String strMonth = String.format("%02d", trueMonth);
                    String strDay = String.format("%02d", dayOfMonth);

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));
                } else if (dayOfMonth < 10 && trueMonth >= 10) {
                    String strDay = String.format("%02d", dayOfMonth);

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
        timePickerDialog = new TimePickerDialog(EventEdit.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
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
        timePickerDialog = new TimePickerDialog(EventEdit.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                min = selectedMinute;

                Calendar fixedFromChangeTime;

                if (hour < 10 && min < 10) {
                    String hourStr = String.format("%02d", hour);
                    String minStr = String.format("%02d", min);


                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    reminders_list.add(fixedFromChangeTime.getTime());


                } else if (hour < 10 && min >= 10) {
                    String hourStr = String.format("%02d", hour);
                    String allTime = hourStr + ":" + min;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    reminders_list.add(fixedFromChangeTime.getTime());


                } else if (hour >= 10 && min < 10) {
                    String minStr = String.format("%02d", min);

                    String allTime = hour + ":" + minStr;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;
                    reminders_list.add(fixedFromChangeTime.getTime());




                } else {
                    String allTime = hour + ":" + min;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    cReminder.set(Calendar.SECOND, 0);
                    cReminder.set(Calendar.MILLISECOND, 0);
                    fixedFromChangeTime = cReminder;

                    reminders_list.add(fixedFromChangeTime.getTime());




                }


            }
        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

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
            }
        });
        timePickerDialog.show();
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



        PendingIntent pendingIntent = PendingIntent.getBroadcast(EventEdit.this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);


        Toast.makeText(EventEdit.this, "Alarm set at: " + cc.getTime().toString(), Toast.LENGTH_SHORT).show();


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
                        cancelRepeat.setVisibility(View.VISIBLE);
                        addRepeatButton.setText(R.string.every_day_gr);

                        dialog.dismiss();
                    }
                });

                everyWeek.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 2;
                        cancelRepeat.setVisibility(View.VISIBLE);
                        addRepeatButton.setText(R.string.every_week_gr);

                        dialog.dismiss();
                    }
                });

                everyMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 3;
                        cancelRepeat.setVisibility(View.VISIBLE);
                        addRepeatButton.setText(R.string.every_month_gr);


                        dialog.dismiss();
                    }
                });

                everyYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCountRepeat();
                        repeatState = 4;
                        cancelRepeat.setVisibility(View.VISIBLE);
                        addRepeatButton.setText(R.string.every_year_gr);

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
                        reminders_list.add(oneDayBeforeDate);
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
                        reminders_list.add(oneHourBeforeDate);

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
                        reminders_list.add(halfHourBeforeDate);


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
                        reminders_list.add(fifteenMinBeforeDate);


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

                        reminders_list.add(tenMinBeforeDate);


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
                        reminders_list.add(fiveMinBeforeDate);


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


            }
        });
        dialog.show();


    }

    private void alertDialogCountRepeat()
    {
        final  View viewCountRepeat = getLayoutInflater().inflate(R.layout.repeat_counter, null);

        repeatCounter = viewCountRepeat.findViewById(R.id.repeatCounter);

        AlertDialog.Builder builderCountRepeat = new AlertDialog.Builder(EventEdit.this);
        builderCountRepeat.setView(viewCountRepeat).setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                repeatCounterInt = Integer.parseInt(repeatCounter.getText().toString());
                String strCount = "Ο αριθμός επαναλήψεων είναι: " + repeatCounterInt;
                repeatCountTv.setVisibility(View.VISIBLE);
                repeatCountTv.setText(strCount);

                if (repeatState == 1) {

                    for (int i = 0; i < repeatCounterInt; i++) {
                        cRepeat.add(Calendar.DAY_OF_YEAR,1);
                        repeats_list.add(cRepeat.getTime());

                    }
                } else if (repeatState == 2) {

                    for (int i = 0; i < repeatCounterInt; i++) {

                        cRepeat.add(Calendar.DAY_OF_YEAR,7);
                        repeats_list.add(cRepeat.getTime());
                    }


                } else if (repeatState == 3) {
                    for (int i = 0; i < repeatCounterInt; i++) {

                        cRepeat.add(Calendar.MONTH,1);
                        repeats_list.add(cRepeat.getTime());
                    }

                } else if (repeatState == 4) {

                    for (int i = 0; i < repeatCounterInt; i++) {

                        cRepeat.add(Calendar.YEAR,1);
                        repeats_list.add(cRepeat.getTime());
                    }
                } else if (repeatState == 5) {
                }


                dialog.dismiss();
            }
        }).setNegativeButton("Άκυρο", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatCountTv.setVisibility(View.GONE);
                addRepeatButton.setText("Επανάληψη");
                cancelRepeat.setVisibility(View.GONE);
                addRepeat();
            }
        }).setTitle("Προσθέστε το πλήθος επαναλήψεων.");
        builderCountRepeat.show();
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



    private void makeAndSaveEvent()
    {
        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);
        String eventName = eventNameET.getText().toString();
        String eventComment = eventCommentET.getText().toString();
        myDB.addEvent(eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState),null);

    }

    private void makeAndSaveReminders()
    {
        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);
        String idForReminder = "";
        reminders_list.sort((o1, o2) -> o1.compareTo(o2));
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

    private void makeAndSaveRepeat()
    {        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);

        Cursor cursor = myDB.readAllEvents();

        String idForRepeat = "";
        repeats_list.sort((o1, o2) -> o1.compareTo(o2));

        if (repeats_list.size()>0)
        {
            while (cursor.moveToNext())
            {
                if (cursor.moveToLast()) {
                    for (int i = 0; i < repeats_list.size(); i++) {
                        idForRepeat = cursor.getString(0);
                        LocalDate dateDB = CalendarUtils.dateToLocalDate(repeats_list.get(i));
                        LocalTime timeDB = CalendarUtils.dateToLocalTime(repeats_list.get(i));

                        myDB.addEvent(cursor.getString(1),cursor.getString(2),dateDB,timeDB,"0",
                                "0",idForRepeat);

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
    public void saveEventAction(View view) {



        makeAndSaveEvent();
        makeAndSaveReminders();
        makeAndSaveRepeat();

        Intent i1 = new Intent(EventEdit.this, MainActivity.class);


        Boolean myBool = true;
        i1.putExtra("date", date);


        i1.putExtra("bool", myBool);

        overridePendingTransition(0, 0);
        startActivity(i1);

        overridePendingTransition(0, 0);


    }


}