package com.example.calendarcapital;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Handler;
import android.service.autofill.OnClickAction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class EventEdit extends AppCompatActivity {

    private EditText eventNameET, eventCommentET;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV, addAlarmButton;
    Button btnSave;
    RemindersAdapter remindersAdapter;
    ImageButton cancelReminderImageView, eventEditBackButton, eventEditRefreshButton;
    LinearLayout reminderLayout;
    ListView remindersListView;
    int hour, min;
    private static LocalDate date;
    private static LocalTime time;

    int alarmState;
    Calendar cReminder = Calendar.getInstance();

    ArrayList<Date> ok = new ArrayList<>();


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
        createNotificationChannel();
        updateIfEmptyListView();


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
                                    remindersListView.setVisibility(View.GONE);

                                } else if (ok.size() == 1) {
                                    ok.sort((o1, o2) -> o1.compareTo(o2));
                                    ViewGroup.LayoutParams paramsListView = remindersListView.getLayoutParams();
                                    paramsListView.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                                    remindersListView.setLayoutParams(paramsListView);
                                } else {
                                    ok.sort((o1, o2) -> o1.compareTo(o2));
                                    ViewGroup.LayoutParams paramsListView = remindersListView.getLayoutParams();
                                    paramsListView.height = 500;

                                    remindersListView.setLayoutParams(paramsListView);
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
                    String str = String.format("%02d", trueMonth);

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);

                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));

//                    eventDateTV.setText("Date: " + dayOfMonth + " " + str + " " + year);

                } else if (dayOfMonth < 10 && trueMonth < 10) {
                    String strMonth = String.format("%02d", trueMonth);
                    String strDay = String.format("%02d", dayOfMonth);

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));
//                    eventDateTV.setText("Date: " + strDay + " " + strMonth + " " + year);
                } else if (dayOfMonth < 10 && trueMonth >= 10) {
                    String strDay = String.format("%02d", dayOfMonth);

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);

                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));


//                    eventDateTV.setText("Date: " + strDay + " " + trueMonth + " " + year);

                } else {

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));

//                    eventDateTV.setText("Date: " + dayOfMonth + " " + trueMonth + " " + year);
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

    public void showChangeTimeForReminder(int hours, int minute) {

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
                    String allTime = hourStr + ":" + minStr;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
//                    reminderLayout.setVisibility(View.VISIBLE);
                    fixedFromChangeTime = cReminder;

                    ok.add(fixedFromChangeTime.getTime());
//                    reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());


                } else if (hour < 10 && min >= 10) {
                    String hourStr = String.format("%02d", hour);
                    String allTime = hourStr + ":" + min;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    fixedFromChangeTime = cReminder;

                    ok.add(fixedFromChangeTime.getTime());

//                    reminderLayout.setVisibility(View.VISIBLE);


//                    reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());
                } else if (hour >= 10 && min < 10) {
                    String minStr = String.format("%02d", min);

                    String allTime = hour + ":" + minStr;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    fixedFromChangeTime = cReminder;
                    ok.add(fixedFromChangeTime.getTime());

//                    reminderLayout.setVisibility(View.VISIBLE);
//
//
//                    reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());


                } else {
                    String allTime = hour + ":" + min;

                    cReminder.set(Calendar.HOUR_OF_DAY, hour);
                    cReminder.set(Calendar.MINUTE, min);
                    fixedFromChangeTime = cReminder;

                    ok.add(fixedFromChangeTime.getTime());

//                    reminderLayout.setVisibility(View.VISIBLE);
//
//                    reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());


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

                            Toast.makeText(EventEdit.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                remindersAdapter = new RemindersAdapter(getApplicationContext(), ok);

                remindersListView.setVisibility(View.VISIBLE);
                remindersListView.setAdapter(remindersAdapter);
            }
        });
        timePickerDialog.show();
    }


    public void startAlarm(int alarmId, Calendar cc) {


        Date myTime = cc.getTime();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(EventEdit.this, AlarmReceiver.class);

        intent.removeExtra("title");
        intent.removeExtra("comment");
        intent.removeExtra("calendar");


        String strTitle = eventNameET.getText().toString();
        String strComment = eventCommentET.getText().toString();

        intent.putExtra("title", strTitle);
        intent.putExtra("comment", strComment);
        intent.putExtra("calendar", myTime);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(EventEdit.this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);


        Toast.makeText(EventEdit.this, "Alarm set at: " + cc.getTime().toString(), Toast.LENGTH_SHORT).show();


    }


    public void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


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

                        Calendar tenminutesBeforeFixed;
                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 10);

                        ok.add(cReminder.getTime());
                        tenminutesBeforeFixed = cReminder;


//                        reminderLayout.setVisibility(View.VISIBLE);
//                        reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());

                        dialog.dismiss();
                    }
                });

                fiveMinBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar fiveminutesBeforeFixed;

                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() - 5);

                        fiveminutesBeforeFixed = cReminder;
                        ok.add(cReminder.getTime());

//                        reminderLayout.setVisibility(View.VISIBLE);
//                        reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());

                        dialog.dismiss();
                    }
                });
                eventsTimeReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmState = 1;
//                        reminderLayout.setVisibility(View.VISIBLE);
//                        reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());

                        ok.add(cReminder.getTime());

                        dialog.dismiss();
                    }
                });
                fiveMinLater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Calendar fiveminutesLaterFixed;
                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() + 5);

                        fiveminutesLaterFixed = cReminder;

                        ok.add(cReminder.getTime());


//                        reminderLayout.setVisibility(View.VISIBLE);
//                        reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());

                        dialog.dismiss();
                    }
                });
                tenMinLater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar tenminutesLaterFixed;
                        alarmState = 1;
                        cReminder.set(Calendar.MINUTE, time.getMinute() + 10);

                        tenminutesLaterFixed = cReminder;

                        ok.add(cReminder.getTime());

//                        reminderLayout.setVisibility(View.VISIBLE);
//                        reminderInfoTV.setText("Η υπενθύμιση ορίστηκε στις: " + cReminder.getTime().toString().trim());

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

                            Toast.makeText(EventEdit.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                remindersAdapter = new RemindersAdapter(getApplicationContext(), ok);

                remindersListView.setVisibility(View.VISIBLE);
                remindersListView.setAdapter(remindersAdapter);


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

    public void saveEventAction(View view) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(EventEdit.this);
        String idForReminder = "";
        Cursor cursor = myDB.readAllData();
        String eventName = eventNameET.getText().toString();
        String eventComment = eventCommentET.getText().toString();
        ok.sort((o1, o2) -> o1.compareTo(o2));


        myDB.addEvent(eventName, eventComment, date, time, String.valueOf(alarmState));
        if (ok.size() > 0) {
            while (cursor.moveToNext()) {
                cursor.moveToLast();
                for (int i = 0; i < ok.size(); i++) {
                    idForReminder = cursor.getString(0);
                    myDB.addReminder(idForReminder, ok.get(i));
                }
            }
        }


        Intent i1 = new Intent(EventEdit.this, MainActivity.class);


        Boolean myBool = true;
        i1.putExtra("date", date);


        i1.putExtra("bool", myBool);

        overridePendingTransition(0, 0);
        startActivity(i1);

        overridePendingTransition(0, 0);


    }


}