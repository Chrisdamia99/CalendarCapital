package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;
import static com.example.calendarcapital.CalendarUtils.stringToLocalDate;

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
import android.content.DialogInterface;
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

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Edit_Update_Activity extends AppCompatActivity {

    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);

    private EditText eventNameETUPD, eventCommentETUPD, repeatCounter,locationETUPD;
    private Spinner color_spinnerUPD;
    private TextView eventDateTV, eventTimeTV, changeTimeTV, changeDateTV, addAlarmButtonUPD, addRepeatButtonUPD, repeatCountTvUPD,locationTVUPD;
    private TextView oneDayBefore, oneHourMinBefore, halfHourMinBefore, fifteenMinBefore, tenMinBefore, fiveMinBefore, customChoice;
    private TextView everyDay, everyWeek, everyMonth, everyYear, customRepeat;
    private View oneDayView, oneHourView, halfMinView, fifteenMinView, tenMinView, fiveMinView, aboveAlarmButtonUPD;
    Button btnUpdate;
    RemindersAdapter remindersAdapter;
    ImageButton cancelReminderImageViewUPD, eventEditBackButtonUPD, eventEditRefreshButtonUPD, cancelRepeatUPD;
    LinearLayout reminderLayoutUPD;
    ListView remindersListViewUPD;
    int hour, min;
    private static LocalDate date,myDD;
    private static LocalDate events_date;
    private static LocalTime time;
    static int alarmState, repeatState;
    String parent_id;
    public Date oneDayBeforeDate, oneHourBeforeDate, halfHourBeforeDate, fifteenMinBeforeDate, tenMinBeforeDate, fiveMinBeforeDate;
    private Activity mCurrentActivity;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;
    int repeatCounterInt;
    static String head_id;
    private static long daysBetween;
    Calendar cReminder = Calendar.getInstance();
    Calendar cRepeat = Calendar.getInstance();
    Calendar cDatePicker = Calendar.getInstance();

    Calendar cAllEventsReminder = Calendar.getInstance();
    String id_row, title, comment;
    ArrayList<Date> repeats_listUPD = new ArrayList<>();
    ArrayList<String> list_repeat_for_dbUPD = new ArrayList<>();
    ArrayList<Date> reminders_upd_list = new ArrayList<>();

    ArrayList<Date> reminders_upd_list_all_events = new ArrayList<>();
    ArrayList<String> idsOfNewReminders = new ArrayList<>();

    static ArrayList<String> editAllArray = new ArrayList<>();
    static ArrayList<String> editFutureArray = new ArrayList<>();
    static String stackNow;
    private static int color;
    static String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_update);
        AndroidThreeTen.init(this);
        initWidgets();
        getSetIntentData();
        showExistedRemindersFromDB();
        mCurrentActivity = this;
        btnUpdate = findViewById(R.id.btnUpdate);
        createNotificationChannel();
        updateIfEmptyListView();
        setIntentsFromCustomRepeat();
        if(getIntent().hasExtra("stack"))
        {
            stackNow = getIntent().getStringExtra("stack");
        }
        retrievetLocation();
        btnUpdate.setOnClickListener(v -> {


            if (parent_id == null && !myDB.checkNextRowHasParentId(Integer.parseInt(id_row))) {
                editAllArray.clear();
                editFutureArray.clear();
                updEventAction();

            } else {
                if (!(editAllArray.isEmpty())) {
                    editAllArray.clear();

                }
                if (!(editFutureArray.isEmpty())) {
                    editFutureArray.clear();

                }
                LayoutInflater lf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View editView = lf.inflate(R.layout.repeating_events_edit_alert_dialog, null);
                TextView editAll = editView.findViewById(R.id.editAll);
                TextView editOne = editView.findViewById(R.id.editOne);
                TextView editFuture = editView.findViewById(R.id.editFuture);
                AlertDialog builderRepeatingEdit = new AlertDialog.Builder(Edit_Update_Activity.this).setView(editView).setTitle("Επεξεργασία συμβάντος").create();

                if (!date.equals(selectedDate) || (!(repeatState == 0) && !(parent_id==null))) {
                    editAll.setVisibility(View.GONE);
                } else {
                    editAll.setVisibility(View.VISIBLE);
                }


                editIfRepeating(builderRepeatingEdit, editAll, editOne, editFuture);
                builderRepeatingEdit.show();


            }
        });
        changeTimeTV.setOnClickListener(v -> {
                showChangeTime(LocalTime.now().getHour(), LocalTime.now().getMinute());

        });

        changeDateTV.setOnClickListener(v -> showChangeDate(cDatePicker.get(Calendar.YEAR), cDatePicker.get(Calendar.MONTH), cDatePicker.get(Calendar.DAY_OF_MONTH)));

        eventEditBackButtonUPD.setOnClickListener(v -> {

            Intent i = new Intent(Edit_Update_Activity.this, MainActivity.class);
            i.putExtra("stack",stackNow);

            String myTemp = CalendarUtils.selectedDate.toString();
            i.putExtra("tempDate",myTemp);
            startActivity(i);
        });

        eventEditRefreshButtonUPD.setOnClickListener(v -> AllEventsList.reloadActivity(Edit_Update_Activity.this));


        addAlarmButtonUPD.setOnClickListener(v -> addAlarm());

        addRepeatButtonUPD.setOnClickListener(v -> addRepeat());

        cancelRepeatUPD.setOnClickListener(v -> {
            repeatState = 0;
            cancelRepeatUPD.setVisibility(View.GONE);
            repeatCountTvUPD.setVisibility(View.GONE);
            addRepeatButtonUPD.setText(R.string.repeat_gr);

            if (!(CustomRepeatActivity.customDatesToSaveLocalDate == null))
            {
                CustomRepeatActivity.customDatesToSaveLocalDate.clear();
            }

        });
        changeColor();


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
        color_spinnerUPD = findViewById(R.id.changeColorSpinnerUPD);
        locationTVUPD = findViewById(R.id.locationTVUPD);
        locationETUPD = findViewById(R.id.locationETUPD);

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
        color_spinnerUPD.setAdapter(adapter);

        if (color==0)
        {
            color_spinnerUPD.setSelection(0);
        }else if (color==1)
        {
            color_spinnerUPD.setSelection(1);
        } else if (color==2) {

            color_spinnerUPD.setSelection(2);
        } else if (color==3) {
            color_spinnerUPD.setSelection(3);
        }else if (color==4) {
            color_spinnerUPD.setSelection(4);
        }else if (color==5) {
            color_spinnerUPD.setSelection(5);
        }

        color_spinnerUPD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

            }
        });
    }

    private void retrievetLocation()
    {
        Cursor cursor = myDB.readAllEvents();

        while (cursor.moveToNext())
        {
            if (cursor.getString(0).equals(id_row))
            {
                if (!(cursor.getString(9).equals(null)))
                {
                    location = cursor.getString(9);
                    locationETUPD.setText(location.trim());

                }else
                {
                    location = null;
                    locationETUPD.setText("");
                }

                break;
            }
        }

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
            addRepeatButtonUPD.setText(CustomRepeatActivity.textForEventEdit);
            cancelRepeatUPD.setVisibility(View.VISIBLE);
        }

        if (getIntent().hasExtra("tittle")) {
            eventNameETUPD.setText(getIntent().getStringExtra("tittle"));
        }

        if (getIntent().hasExtra("comment")) {
            eventCommentETUPD.setText(getIntent().getStringExtra("comment"));
        }

    }
    private void editIfRepeating(AlertDialog builderRepeating, TextView editAll, TextView editOne, TextView editFuture) {

        builderRepeating.setOnShowListener(dialog -> {
            editAll.setOnClickListener(v -> {

                editAllArray = UniStatic.getAllEventsArray(myDB,parent_id,id_row);
                editAllArray.size();
                updEventAction();
                dialog.dismiss();
            });

            editFuture.setOnClickListener(v -> {




                editFutureArray = UniStatic.getFutureEventsArray(myDB,parent_id,id_row,selectedDate);
                editFutureArray.size();
                updEventAction();

                dialog.dismiss();
            });

            editOne.setOnClickListener(v -> {

                updEventAction();

                dialog.dismiss();
            });
        });

    }

    private void getSetIntentData() {


        Cursor eventCursor = myDB.readAllEvents();

        if (getIntent().hasExtra("id") ) {
            id_row = getIntent().getStringExtra("id");

            eventCursor.moveToPosition(-1);
            while (eventCursor.moveToNext()) {
                if (eventCursor.getString(0).equals(id_row)) {
                    title = eventCursor.getString(1);
                    comment = eventCursor.getString(2);
                    date = stringToLocalDate(eventCursor.getString(3));
                        time = LocalTime.parse(eventCursor.getString(4));

                    alarmState = Integer.parseInt(eventCursor.getString(5));
                    repeatState = Integer.parseInt(eventCursor.getString(6));
                    if (eventCursor.getString(7) == null) {
                        parent_id = null;
                    } else {
                        parent_id = eventCursor.getString(7);
                    }
                    color = Integer.parseInt(eventCursor.getString(8));
                }
            }

        }

        setRepeatFromIntent();

        eventNameETUPD.setText(title);
        eventCommentETUPD.setText(comment);
        eventDateTV.setText(date.toString().trim());

        eventTimeTV.setText(CalendarUtils.formattedShortTime(time));
        events_date = date;

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
            scheduledFuture = executorService.scheduleAtFixedRate(() -> runOnUiThread(() -> {
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
            }), 0, 100, TimeUnit.MILLISECONDS); // runs every 100 milliseconds
        }
    }

    public void showExistedRemindersFromDB() {
        String alarmDate;


        Cursor cursor = myDB.readAllEvents();
        Cursor cursorRem = myDB.readAllReminder();

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
//                    reminders_upd_list.sort(Date::compareTo);
                    Collections.sort(reminders_upd_list, new Comparator<Date>() {
                        @Override
                        public int compare(Date item1, Date item2) {
                            return item1.compareTo(item2);
                        }
                    });



                    for (int i = 0; i < reminders_upd_list.size(); i++) {
                        for (int j = i + 1; j < reminders_upd_list.size(); j++) {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTime(reminders_upd_list.get(i));

                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.setTime(reminders_upd_list.get(j));

                            if (isSameDateTime(calendar1, calendar2)) {
                                reminders_upd_list.remove(i);

                            }
                        }
                    }

//                        reminders_upd_list.sort(Date::compareTo);
                    Collections.sort(reminders_upd_list, new Comparator<Date>() {
                        @Override
                        public int compare(Date item1, Date item2) {
                            return item1.compareTo(item2);
                        }
                    });
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
        myDB.close();


    }
    private boolean isSameDateTime(@NonNull Calendar calendar1, @NonNull Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
                calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY) &&
                calendar1.get(Calendar.MINUTE) == calendar2.get(Calendar.MINUTE);
    }

    public void updateIfEmptyListView() {
        if (mCurrentActivity instanceof Edit_Update_Activity) {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(() -> runOnUiThread(() -> {
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
//                        reminders_upd_list.sort(Date::compareTo);
                    Collections.sort(reminders_upd_list, new Comparator<Date>() {
                        @Override
                        public int compare(Date item1, Date item2) {
                            return item1.compareTo(item2);
                        }
                    });
                    ViewGroup.LayoutParams paramsListView = remindersListViewUPD.getLayoutParams();
                    paramsListView.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    remindersListViewUPD.setLayoutParams(paramsListView);
                } else {
//                        reminders_upd_list.sort(Date::compareTo);
                    Collections.sort(reminders_upd_list, new Comparator<Date>() {
                        @Override
                        public int compare(Date item1, Date item2) {
                            return item1.compareTo(item2);
                        }
                    });
                    ViewGroup.LayoutParams paramsListView = remindersListViewUPD.getLayoutParams();
                    paramsListView.height = 500;
                    remindersListViewUPD.setLayoutParams(paramsListView);
                }
            }), 0, 100, TimeUnit.MILLISECONDS); // runs every 100 milliseconds
        }
    }


    private void alertDialogCountRepeat() {
        final View viewCountRepeat = getLayoutInflater().inflate(R.layout.repeat_counter, null);

        repeatCounter = viewCountRepeat.findViewById(R.id.repeatCounter);

        AlertDialog.Builder builderCountRepeat = new AlertDialog.Builder(Edit_Update_Activity.this);
        builderCountRepeat.setView(viewCountRepeat).setPositiveButton("Ναι", (dialog, which) -> {
            repeatCounterInt = Integer.parseInt(repeatCounter.getText().toString());
            String strCount = "Ο αριθμός επαναλήψεων είναι: " + repeatCounterInt;
            repeatCountTvUPD.setVisibility(View.VISIBLE);
            repeatCountTvUPD.setText(strCount);

            if (repeatState == 1) {

                for (int i = 0; i < repeatCounterInt; i++) {
                    cRepeat.add(Calendar.DAY_OF_YEAR, 1);
                    repeats_listUPD.add(cRepeat.getTime());

                }
            } else if (repeatState == 2) {

                for (int i = 0; i < repeatCounterInt; i++) {

                    cRepeat.add(Calendar.DAY_OF_YEAR, 7);
                    repeats_listUPD.add(cRepeat.getTime());
                }


            } else if (repeatState == 3) {
                for (int i = 0; i < repeatCounterInt; i++) {

                    cRepeat.add(Calendar.MONTH, 1);
                    repeats_listUPD.add(cRepeat.getTime());
                }

            } else if (repeatState == 4) {

                for (int i = 0; i < repeatCounterInt; i++) {

                    cRepeat.add(Calendar.YEAR, 1);
                    repeats_listUPD.add(cRepeat.getTime());
                }
            }


            dialog.dismiss();
        }).setNegativeButton("Άκυρο", (dialog, which) -> {
            repeatCountTvUPD.setVisibility(View.GONE);
            addRepeatButtonUPD.setText("Επανάληψη");
            cancelRepeatUPD.setVisibility(View.GONE);
            addRepeat();
        }).setTitle("Προσθέστε το πλήθος επαναλήψεων.");
        builderCountRepeat.show();
    }

    private void alertDialogCountRepeatIfDateChange(LocalDate date) {
        cRepeat.clear();
            cRepeat.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

        cRepeat.set(Calendar.HOUR_OF_DAY, 8);
        cRepeat.set(Calendar.MINUTE, 0);
        cRepeat.set(Calendar.SECOND, 0);
        repeats_listUPD.clear();
        if (repeatState == 1) {

            for (int i = 0; i < repeatCounterInt; i++) {
                cRepeat.add(Calendar.DAY_OF_YEAR, 1);
                repeats_listUPD.add(cRepeat.getTime());

            }
        } else if (repeatState == 2) {

            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.DAY_OF_YEAR, 7);
                repeats_listUPD.add(cRepeat.getTime());
            }


        } else if (repeatState == 3) {
            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.MONTH, 1);
                repeats_listUPD.add(cRepeat.getTime());
            }

        } else if (repeatState == 4) {

            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.YEAR, 1);
                repeats_listUPD.add(cRepeat.getTime());
            }
        }
    }

    private void showChangeDate(int year, int month, int dayofmonth) {
        ArrayList<Date> reminder_list_date_changed = new ArrayList<>();
        final DatePickerDialog StartTime = new DatePickerDialog(this, R.style.TimePickerTheme, (view, year1, monthOfYear, dayOfMonth) -> {
            int trueMonth = monthOfYear + 1;




                myDD = LocalDate.of(year1, trueMonth, dayOfMonth);



            eventDateTV.setText(CalendarUtils.formattedDateEventEdit(myDD));


                date = LocalDate.of(year1, trueMonth, dayOfMonth);


            if (!(myDD == null))
            {
                if (!reminders_upd_list.isEmpty()) {

                    for (int i = 0; i < reminders_upd_list.size(); i++) {
                        Calendar cRemChanged = Calendar.getInstance();
                        cRemChanged.clear();
                        Date cRemBefore = reminders_upd_list.get(i);
                        int yearTEST = 0;
                            yearTEST = myDD.getYear();

                        int monthTEST = 0;
                            monthTEST = myDD.getMonth().getValue()-1;

                        int dayTest = 0;
                            dayTest = myDD.getDayOfMonth();


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
                    reminders_upd_list.clear();
                    reminders_upd_list.addAll(reminder_list_date_changed);
                    remindersAdapter.notifyDataSetChanged();

                }

            }else
            {
                Toast.makeText(mCurrentActivity, "problem showChangeDate_eventEdit_UPDATE", Toast.LENGTH_SHORT).show();
            }


        }, year, month, dayofmonth);


        StartTime.setTitle("Select Date");
        StartTime.show();

    }


    @SuppressLint("SetTextI18n")
    private void showChangeTime(int hours, int minute) {
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(Edit_Update_Activity.this, R.style.TimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            min = selectedMinute;

            if (hour < 10 && min < 10) {
                @SuppressLint("DefaultLocale") String hourStr = String.format("%02d", hour);
                @SuppressLint("DefaultLocale") String minStr = String.format("%02d", min);
                String allTime = hourStr + ":" + minStr;

                eventTimeTV.setText(hourStr + ":" + minStr);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);



            } else if (hour < 10 && min >= 10) {
                @SuppressLint("DefaultLocale") String hourStr = String.format("%02d", hour);
                String allTime = hourStr + ":" + min;

                eventTimeTV.setText(hourStr + ":" + min);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);

            } else if (hour >= 10 && min < 10) {
                @SuppressLint("DefaultLocale") String minStr = String.format("%02d", min);

                String allTime = hour + ":" + minStr;

                eventTimeTV.setText(hour + ":" + minStr);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);



            } else {
                String allTime = hour + ":" + min;

                eventTimeTV.setText(hour + ":" + min);
                    time = LocalTime.parse(allTime, DateTimeFormatter.ISO_TIME);


            }


        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }

    private void showChangeTimeForReminder(int hours, int minute) {

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(Edit_Update_Activity.this, R.style.TimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
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


        }, hours, minute, true);//Yes 24 hour time


        timePickerDialog.setTitle("Select Time");
        timePickerDialog.setOnDismissListener(dialog -> {


            for (int i = 0; i < reminders_upd_list.size(); i++) {
                for (int j = i + 1; j < reminders_upd_list.size(); j++) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(reminders_upd_list.get(i));

                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(reminders_upd_list.get(j));

                    if (isSameDateTime(calendar1, calendar2)) {
                        reminders_upd_list.remove(i);
                        Toast.makeText(Edit_Update_Activity.this, "Σφάλμα, η υπενθύμιση υπάρχει.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            remindersAdapter = new RemindersAdapter(getApplicationContext(), reminders_upd_list);

            remindersListViewUPD.setVisibility(View.VISIBLE);
            remindersListViewUPD.setAdapter(remindersAdapter);
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
        cc.set(Calendar.HOUR_OF_DAY, 8);
        cc.set(Calendar.MINUTE, 0);
        cc.set(Calendar.SECOND, 0);
        cc.set(Calendar.MILLISECOND, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Edit_Update_Activity.this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);
        }else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(), pendingIntent);

        }


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
        dialog.setOnShowListener(dialog1 -> {

            oneDayBefore.setOnClickListener(v -> {
                alarmState = 1;

                    date.minusDays(1);
                    cReminder.setTime(Date.from(date.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
                    cReminder.set(Calendar.MINUTE, time.getMinute());

                cReminder.set(Calendar.MILLISECOND, 0);
                oneDayBeforeDate = cReminder.getTime();
                reminders_upd_list.add(oneDayBeforeDate);
                dialog1.dismiss();

            });

            oneHourMinBefore.setOnClickListener(v -> {
                alarmState = 1;
                    cReminder.set(Calendar.HOUR_OF_DAY, time.getHour() - 1);

                cReminder.set(Calendar.MILLISECOND, 0);
                oneHourBeforeDate = cReminder.getTime();
                reminders_upd_list.add(oneHourBeforeDate);

                dialog1.dismiss();
            });

            halfHourMinBefore.setOnClickListener(v -> {
                alarmState = 1;
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 30);

                cReminder.set(Calendar.MILLISECOND, 0);
                halfHourBeforeDate = cReminder.getTime();
                reminders_upd_list.add(halfHourBeforeDate);


                dialog1.dismiss();
            });

            fifteenMinBefore.setOnClickListener(v -> {
                alarmState = 1;
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 15);

                cReminder.set(Calendar.MILLISECOND, 0);
                fifteenMinBeforeDate = cReminder.getTime();
                reminders_upd_list.add(fifteenMinBeforeDate);


                dialog1.dismiss();
            });
            tenMinBefore.setOnClickListener(v -> {


                alarmState = 1;
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 10);

                cReminder.set(Calendar.MILLISECOND, 0);

                tenMinBeforeDate = cReminder.getTime();
                reminders_upd_list.add(tenMinBeforeDate);

                dialog1.dismiss();
            });

            fiveMinBefore.setOnClickListener(v -> {


                alarmState = 1;
                    cReminder.set(Calendar.MINUTE, time.getMinute() - 5);

                cReminder.set(Calendar.MILLISECOND, 0);
                fiveMinBeforeDate = cReminder.getTime();
                reminders_upd_list.add(fiveMinBeforeDate);


                dialog1.dismiss();
            });


            customChoice.setOnClickListener(v -> {
                alarmState = 1;
                showChangeTimeForReminder(cReminder.get(Calendar.HOUR_OF_DAY), cReminder.get(Calendar.MINUTE));

                dialog1.dismiss();
            });

        });

        dialog.setOnDismissListener(dialog12 -> {
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
                everyDay.setOnClickListener(v -> {
                    alertDialogCountRepeat();
                    repeatState = 1;
                    cancelRepeatUPD.setVisibility(View.VISIBLE);
                    addRepeatButtonUPD.setText(R.string.every_day_gr);


                    dialog.dismiss();
                });

                everyWeek.setOnClickListener(v -> {
                    alertDialogCountRepeat();
                    repeatState = 2;
                    cancelRepeatUPD.setVisibility(View.VISIBLE);
                    addRepeatButtonUPD.setText(R.string.every_week_gr);

                    dialog.dismiss();
                });

                everyMonth.setOnClickListener(v -> {
                    alertDialogCountRepeat();
                    repeatState = 3;
                    cancelRepeatUPD.setVisibility(View.VISIBLE);
                    addRepeatButtonUPD.setText(R.string.every_month_gr);


                    dialog.dismiss();
                });

                everyYear.setOnClickListener(v -> {
                    alertDialogCountRepeat();
                    repeatState = 4;
                    cancelRepeatUPD.setVisibility(View.VISIBLE);
                    addRepeatButtonUPD.setText(R.string.every_year_gr);

                    dialog.dismiss();
                });

                customRepeat.setOnClickListener(v -> {

                    repeatState = 5;
                    Intent iCustomRepeat = new Intent(Edit_Update_Activity.this, CustomRepeatActivity.class);
                    String flagForEditUpdate = "1";
                    if (!(id_row.equals(null)))
                    {
                        iCustomRepeat.putExtra("id",id_row);
                    }
                    iCustomRepeat.putExtra("flagBack",flagForEditUpdate);
                    iCustomRepeat.putExtra("date",date.toString());
                    iCustomRepeat.putExtra("tittle", eventNameETUPD.getText().toString());
                    iCustomRepeat.putExtra("comment", eventCommentETUPD.getText().toString());
                    iCustomRepeat.putExtra("stack",stackNow);
                    startActivity(iCustomRepeat);
                    dialog.dismiss();
                });


            }
        });

        dialog.show();


    }


    private void createNotificationChannel() {
            CharSequence name = "myandroidReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("myandroid", name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
        }

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

    }


    // ------------------------Events------------------------------------
    //IF EDIT ONE_EVENT
    private void updateAndSaveEvent() {
        String eventName = eventNameETUPD.getText().toString();
        String eventComment = eventCommentETUPD.getText().toString();
        location = locationETUPD.getText().toString().trim();
        if (eventName.equals(""))
        {
            eventName="(Χώρις τίτλο)";
        }
        Cursor cursorRem = myDB.readAllReminder();
//            reminders_upd_list.sort(Date::compareTo);
        Collections.sort(reminders_upd_list, new Comparator<Date>() {
            @Override
            public int compare(Date item1, Date item2) {
                return item1.compareTo(item2);
            }
        });



        if (reminders_upd_list.size() > 0) {
            alarmState = 1;
            if (repeatState == 0) {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), parent_id,String.valueOf(color),location);
            } else {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), null,String.valueOf(color),location);

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
            if (repeatState == 0) {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), parent_id,String.valueOf(color),location);
            } else {
                myDB.updateData(id_row, eventName, eventComment, date, time, String.valueOf(alarmState), String.valueOf(repeatState), null,String.valueOf(color),location);

            }
        }


        cursorRem.close();
        myDB.close();
    }

    private void giveIdToStartAlarmsEvent() {

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

    private void makeAndSaveRepeatUPD() {

        Cursor cursor = myDB.readAllEvents();


//            repeats_listUPD.sort(Date::compareTo);
        Collections.sort(repeats_listUPD, new Comparator<Date>() {
            @Override
            public int compare(Date element, Date t1) {
                return element.compareTo(t1);
            }
        });
        if (repeatState == 5) {
            for (LocalDate localDate : CustomRepeatActivity.customDatesToSaveLocalDate) {
                Calendar cForCustom = Calendar.getInstance();

                    cForCustom.set(Calendar.YEAR, localDate.getYear());
                    cForCustom.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
                    cForCustom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());


                cForCustom.set(Calendar.HOUR_OF_DAY, 8);
                cForCustom.set(Calendar.MINUTE, 0);
                cForCustom.set(Calendar.SECOND, 0);
                Date dateFromCustomRepeat = cForCustom.getTime();
                repeats_listUPD.add(dateFromCustomRepeat);
                if (localDate.equals(date)) {
                    repeats_listUPD.remove(dateFromCustomRepeat);
                }
            }


        }

        if (repeats_listUPD.size() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(id_row)) {
                    for (int i = 0; i < repeats_listUPD.size(); i++) {

                        LocalDate dateDB = CalendarUtils.dateToLocalDate(repeats_listUPD.get(i));
                        LocalTime timeDB = CalendarUtils.dateToLocalTime(repeats_listUPD.get(i));

                        myDB.addEvent(cursor.getString(1), cursor.getString(2), dateDB, timeDB, "0",
                                "0", id_row, String.valueOf(color),location);

                        list_repeat_for_dbUPD.add(cursor.getString(0));


                    }
                }
            }
        }

        cursor.close();

        for (int j = 0; j < repeats_listUPD.size(); j++) {
            for (int i = 0; i < list_repeat_for_dbUPD.size(); i++) {
                startAlarm(Integer.parseInt(list_repeat_for_dbUPD.get(i)), CalendarUtils.dateToCalendar(repeats_listUPD.get(j)));
            }
        }
        myDB.close();

    }


    //----------------------------------------------------------------------
    //IF EDIT ALL_EVENTS

    private void updateAndSaveTittleCommentsAllEvents() {
        Cursor cursorEvent = myDB.readAllEvents();
        String eventTitle = eventNameETUPD.getText().toString();
        String eventComment = eventCommentETUPD.getText().toString();
        location = locationETUPD.getText().toString().trim();
        if (eventTitle.equals(""))
        {
            eventTitle="(Χώρις τίτλο)";
        }
        if (!editAllArray.isEmpty()) {
            for (int i = 0; i < editAllArray.size(); i++) {
                cursorEvent.moveToPosition(-1);
                while (cursorEvent.moveToNext()) {

                    if (cursorEvent.getString(0).equals(editAllArray.get(i))) {
                        myDB.updateEventTitleComment(editAllArray.get(i), eventTitle, eventComment);

                    }

                }
            }
        } else {
            Toast.makeText(mCurrentActivity, "ERROR updateAndSaveTittleCommentsFutureEvents", Toast.LENGTH_SHORT).show();
        }

        cursorEvent.close();
        myDB.close();
    }



    private void updateAndSaveReminderAllEvents() {
        Cursor cursorEvent = myDB.readAllEvents();
        Cursor cursorRem = myDB.readAllReminder();
//            reminders_upd_list.sort(Date::compareTo);
        Collections.sort(reminders_upd_list, new Comparator<Date>() {
            @Override
            public int compare(Date item1, Date item2) {
                return item1.compareTo(item2);
            }
        });


        if (!(editAllArray.isEmpty())) {
            if (reminders_upd_list.size() > 0) {

                cursorEvent.moveToPosition(-1);
                while (cursorEvent.moveToNext()) {
                    if (cursorEvent.getString(0).equals(id_row)) {
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
                            myDB.addReminder(id_row, reminders_upd_list.get(i));
                            myDB.updateEventAlarm(id_row,"1");
                        }
                    }

                    if (!(cursorEvent.getString(0).equals(id_row)) && (parent_id==null && (!(cursorEvent.getString(7)==null) && cursorEvent.getString(7).equals(id_row))))
                    {reminders_upd_list_all_events.clear();
                        LocalDate cursorLocalDate = stringToLocalDate(cursorEvent.getString(3));
                        Date cursorDate = null;
                            cursorDate = Date.from(cursorLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

                        cAllEventsReminder.setTime(cursorDate);
                        for (int i = 0; i < reminders_upd_list.size(); i++) {
                            Date getRemindersValues = reminders_upd_list.get(i);
                            LocalDateTime localDateTime = getRemindersValues.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalTime localTime = localDateTime.toLocalTime();
                            int hour = localTime.getHour();
                            int minute = localTime.getMinute();
                            int second = localTime.getSecond();


                            cAllEventsReminder.set(Calendar.HOUR_OF_DAY, hour);
                            cAllEventsReminder.set(Calendar.MINUTE, minute);
                            cAllEventsReminder.set(Calendar.SECOND, second);
                            cAllEventsReminder.set(Calendar.MILLISECOND, 0);

                            reminders_upd_list_all_events.add(cAllEventsReminder.getTime());
                        }

                        for (int j = 0; j < reminders_upd_list_all_events.size(); j++) {


                            while (cursorRem.moveToNext()) {
                                long mytestLong = Date.parse(cursorRem.getString(2));
                                Date lastDate = new Date(mytestLong);
                                for (int i = 0; i < reminders_upd_list_all_events.size(); i++) {
                                    if (reminders_upd_list_all_events.get(i).equals(lastDate)) {
                                        reminders_upd_list_all_events.remove(i);
                                    }


                                }

                            }
                            myDB.addReminder(cursorEvent.getString(0), reminders_upd_list_all_events.get(j));
                            myDB.updateEventAlarm(cursorEvent.getString(0),"1");
                            giveIdToStartAlarmsAllEvents(reminders_upd_list_all_events);
                            reminders_upd_list_all_events.size();

                        }
                    }else if (!(cursorEvent.getString(0).equals(id_row)) && (!(parent_id==null) && (!(cursorEvent.getString(7)==null) && cursorEvent.getString(7).equals(parent_id)) || cursorEvent.getString(0).equals(parent_id))) {
                        reminders_upd_list_all_events.clear();
                        LocalDate cursorLocalDate = stringToLocalDate(cursorEvent.getString(3));
                        Date cursorDate = null;
                            cursorDate = Date.from(cursorLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

                        cAllEventsReminder.setTime(cursorDate);
                        for (int i = 0; i < reminders_upd_list.size(); i++) {
                            Date getRemindersValues = reminders_upd_list.get(i);
                            LocalDateTime localDateTime = getRemindersValues.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalTime localTime = localDateTime.toLocalTime();
                            int hour = localTime.getHour();
                            int minute = localTime.getMinute();
                            int second = localTime.getSecond();


                            cAllEventsReminder.set(Calendar.HOUR_OF_DAY, hour);
                            cAllEventsReminder.set(Calendar.MINUTE, minute);
                            cAllEventsReminder.set(Calendar.SECOND, second);
                            cAllEventsReminder.set(Calendar.MILLISECOND, 0);

                            reminders_upd_list_all_events.add(cAllEventsReminder.getTime());

                        }

                            for (int j = 0; j < reminders_upd_list_all_events.size(); j++) {


                                while (cursorRem.moveToNext()) {
                                    long mytestLong = Date.parse(cursorRem.getString(2));
                                    Date lastDate = new Date(mytestLong);
                                    for (int i = 0; i < reminders_upd_list_all_events.size(); i++) {
                                        if (reminders_upd_list_all_events.get(i).equals(lastDate)) {
                                            reminders_upd_list_all_events.remove(i);
                                        }


                                    }

                                }
                                if (reminders_upd_list_all_events.size() > 0) {
                                    myDB.addReminder(cursorEvent.getString(0), reminders_upd_list_all_events.get(j));
                                    myDB.updateEventAlarm(cursorEvent.getString(0), "1");
                                    giveIdToStartAlarmsAllEvents(reminders_upd_list_all_events);
                                    reminders_upd_list_all_events.size();
                                }

                            }


                    }

                    reminders_upd_list_all_events.size();
                }
            }
        }


    }


    private void updateAndSaveRepeatAllEvents() {

        Cursor cursor = myDB.readAllEvents();


//            repeats_listUPD.sort(Date::compareTo);
        Collections.sort(repeats_listUPD, new Comparator<Date>() {
            @Override
            public int compare(Date item1, Date item2) {
                return item1.compareTo(item2);
            }
        });

        if (repeatState == 5) {
            for (LocalDate localDate : CustomRepeatActivity.customDatesToSaveLocalDate) {
                Calendar cForCustom = Calendar.getInstance();

                    cForCustom.set(Calendar.YEAR, localDate.getYear());
                    cForCustom.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
                    cForCustom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());


                cForCustom.set(Calendar.HOUR_OF_DAY, 8);
                cForCustom.set(Calendar.MINUTE, 0);
                cForCustom.set(Calendar.SECOND, 0);
                Date dateFromCustomRepeat = cForCustom.getTime();
                repeats_listUPD.add(dateFromCustomRepeat);
                if (localDate.equals(date)) {
                    repeats_listUPD.remove(dateFromCustomRepeat);
                }
            }


        }

        if (repeats_listUPD.size() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(id_row)) {
                    for (int i = 0; i < repeats_listUPD.size(); i++) {

                        LocalDate dateDB = CalendarUtils.dateToLocalDate(repeats_listUPD.get(i));
                        LocalTime timeDB = CalendarUtils.dateToLocalTime(repeats_listUPD.get(i));

                        myDB.addEvent(cursor.getString(1), cursor.getString(2), dateDB, timeDB, "0",
                                "0", id_row,String.valueOf(color),location);

                        list_repeat_for_dbUPD.add(cursor.getString(0));


                    }
                }
            }
        }

        cursor.close();

        for (int j = 0; j < repeats_listUPD.size(); j++) {
            for (int i = 0; i < list_repeat_for_dbUPD.size(); i++) {
                startAlarm(Integer.parseInt(list_repeat_for_dbUPD.get(i)), CalendarUtils.dateToCalendar(repeats_listUPD.get(j)));
            }
        }
        myDB.close();
    }

    private void giveIdToStartAlarmsAllEvents(ArrayList<Date> allEventsAlarms)
    {

        Cursor secondRem = myDB.readAllReminder();

        secondRem.moveToPosition(-1);
        while (secondRem.moveToNext()) {

            for (int i = 0; i < allEventsAlarms.size(); i++) {

                long mytestLongUpd = Date.parse(secondRem.getString(2));
                Date lastDateUpd = new Date(mytestLongUpd);
                for (int j = 0; j < editAllArray.size(); j++) {
                    if (secondRem.getString(1).equals(editAllArray.get(j))) {
                        if (lastDateUpd.equals(allEventsAlarms.get(i))) {
                            idsOfNewReminders.add(secondRem.getString(0));
                        }
                    }
                }

            }

        }
        secondRem.close();
        myDB.close();

        for (int j = 0; j < allEventsAlarms.size(); j++) {
            for (int i = 0; i < idsOfNewReminders.size(); i++) {
                startAlarm(Integer.parseInt(idsOfNewReminders.get(i)), CalendarUtils.dateToCalendar(allEventsAlarms.get(j)));
            }
        }
            }
    private void giveIdToStartAlarmsRepeatingEvent() {

        Cursor secondRem = myDB.readAllReminder();

        secondRem.moveToPosition(-1);

        if (!(editAllArray.isEmpty())) {
            while (secondRem.moveToNext()) {

                for (int i = 0; i < reminders_upd_list.size(); i++) {

                    long mytestLongUpd = Date.parse(secondRem.getString(2));
                    Date lastDateUpd = new Date(mytestLongUpd);
                    for (int j = 0; j < editAllArray.size(); j++) {
                        if (secondRem.getString(1).equals(editAllArray.get(j))) {
                            if (lastDateUpd.equals(reminders_upd_list.get(i))) {
                                idsOfNewReminders.add(secondRem.getString(0));
                            }
                        }
                    }

                }

            }

        } else if (!(editFutureArray.isEmpty())) {
            while (secondRem.moveToNext()) {

                for (int i = 0; i < reminders_upd_list.size(); i++) {

                    long mytestLongUpd = Date.parse(secondRem.getString(2));
                    Date lastDateUpd = new Date(mytestLongUpd);
                    for (int j = 0; j < editFutureArray.size(); j++) {
                        if (secondRem.getString(1).equals(editFutureArray.get(j))) {
                            if (lastDateUpd.equals(reminders_upd_list.get(i))) {
                                idsOfNewReminders.add(secondRem.getString(0));
                            }
                        }
                    }

                }

            }

        } else {
            Toast.makeText(mCurrentActivity, "ERRORGIVEIDTOSTARTALARMS", Toast.LENGTH_SHORT).show();
        }


        secondRem.close();
        myDB.close();

        for (int j = 0; j < reminders_upd_list.size(); j++) {
            for (int i = 0; i < idsOfNewReminders.size(); i++) {
                startAlarm(Integer.parseInt(idsOfNewReminders.get(i)), CalendarUtils.dateToCalendar(reminders_upd_list.get(j)));
            }
        }
    }


    //-----------------------------------------------------------------------
    //IF EDIT FUTURE_EVENTS
    private void updateAndSaveTittleCommentsFutureEvents() {
        Cursor cursorEvent = myDB.readAllEvents();
        String eventTitle = eventNameETUPD.getText().toString();
        String eventComment = eventCommentETUPD.getText().toString();
        location = locationETUPD.getText().toString().trim();

        if (eventTitle.equals(""))
        {
            eventTitle="(Χώρις τίτλο)";
        }
        if (!editFutureArray.isEmpty()) {
            for (int i = 0; i < editFutureArray.size(); i++) {
                cursorEvent.moveToPosition(-1);
                while (cursorEvent.moveToNext()) {

                    if (cursorEvent.getString(0).equals(editFutureArray.get(i))) {
                        myDB.updateEventTitleComment(editFutureArray.get(i), eventTitle, eventComment);

                    }

                }
            }
        } else {
            Toast.makeText(mCurrentActivity, "ERROR updateAndSaveTittleCommentsFutureEvents", Toast.LENGTH_SHORT).show();
        }

        cursorEvent.close();
        myDB.close();

    }

    private void updateAndSaveDateFutureEvents() {
        Cursor cursorEvent = myDB.readAllEvents();

        if (!editFutureArray.isEmpty()) {
            for (int i = 0; i < editFutureArray.size(); i++) {
                cursorEvent.moveToPosition(-1);
                while (cursorEvent.moveToNext()) {
                    if (id_row.equals(cursorEvent.getString(0)) && !date.equals(stringToLocalDate(cursorEvent.getString(3)))) {
                        LocalDate oldDate = stringToLocalDate(cursorEvent.getString(3));
                            daysBetween = ChronoUnit.DAYS.between(oldDate, date);

                        myDB.updateEventDate(id_row, date);
                        myDB.updateEventTime(id_row, time);
                    }
                    if (editFutureArray.get(i).equals(cursorEvent.getString(0)) && !editFutureArray.get(i).equals(id_row)) {

                        LocalDate newDate = stringToLocalDate(cursorEvent.getString(3));
                        LocalDate newDateDB = null;
                            newDateDB = newDate.plusDays(daysBetween);

                        myDB.updateEventDate(editFutureArray.get(i), newDateDB);
                        myDB.updateEventTime(editFutureArray.get(i), time);

                    }

                }
            }

        } else {
            Toast.makeText(mCurrentActivity, "ERROR ifdatechange", Toast.LENGTH_SHORT).show();
        }

        cursorEvent.close();
        myDB.close();
    }

    private void updateAndSaveReminderFutureEvents() {
        Cursor cursorEvent = myDB.readAllEvents();
        Cursor cursorRem = myDB.readAllReminder();
//            reminders_upd_list.sort(Date::compareTo);
        Collections.sort(reminders_upd_list, new Comparator<Date>() {
            @Override
            public int compare(Date item1, Date item2) {
                return item1.compareTo(item2);
            }
        });


        if (!(editFutureArray.isEmpty())) {
            if (reminders_upd_list.size() > 0) {

                cursorEvent.moveToPosition(-1);
                while (cursorEvent.moveToNext()) {
                    if (cursorEvent.getString(0).equals(id_row)) {
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
                                myDB.updateEventAlarm(id_row, "1");
                            }
                        }
                    }
                    for (int k = 0; k < editFutureArray.size(); k++)
                    {

                        if (!(cursorEvent.getString(0).equals(id_row)) &&
                                !(editFutureArray.get(k)).equals(id_row) &&
                                cursorEvent.getString(0).equals(editFutureArray.get(k))) {
                            reminders_upd_list_all_events.clear();
                            LocalDate cursorLocalDate = stringToLocalDate(cursorEvent.getString(3));
                            Date cursorDate = null;
                                cursorDate = Date.from(cursorLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

                            cAllEventsReminder.setTime(cursorDate);
                            for (int i = 0; i < reminders_upd_list.size(); i++) {
                                Date getRemindersValues = reminders_upd_list.get(i);
                                LocalDateTime localDateTime = getRemindersValues.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                LocalTime localTime = localDateTime.toLocalTime();
                                int hour = localTime.getHour();
                                int minute = localTime.getMinute();
                                int second = localTime.getSecond();


                                cAllEventsReminder.set(Calendar.HOUR_OF_DAY, hour);
                                cAllEventsReminder.set(Calendar.MINUTE, minute);
                                cAllEventsReminder.set(Calendar.SECOND, second);
                                cAllEventsReminder.set(Calendar.MILLISECOND, 0);

                                reminders_upd_list_all_events.add(cAllEventsReminder.getTime());
                            }


                                for (int j = 0; j < reminders_upd_list_all_events.size(); j++) {


                                    while (cursorRem.moveToNext()) {
                                        long mytestLong = Date.parse(cursorRem.getString(2));
                                        Date lastDate = new Date(mytestLong);
                                        for (int i = 0; i < reminders_upd_list_all_events.size(); i++) {
                                            if (reminders_upd_list_all_events.get(i).equals(lastDate)) {
                                                reminders_upd_list_all_events.remove(i);
                                            }


                                        }

                                    }
                                    if (reminders_upd_list_all_events.size() > 0) {
                                        myDB.addReminder(cursorEvent.getString(0), reminders_upd_list_all_events.get(j));
                                        myDB.updateEventAlarm(cursorEvent.getString(0), "1");
                                        giveIdToStartAlarmsAllEvents(reminders_upd_list_all_events);
                                        reminders_upd_list_all_events.size();
                                    }
                                }

                        }


                }


                    reminders_upd_list_all_events.size();
                }
            }
        }



        cursorRem.close();
        cursorEvent.close();
        myDB.close();
    }

    private void updateAndSaveRepeatFutureEvents() {
        Cursor cursorEvent = myDB.readAllEvents();
        Cursor lastCursorEvent = myDB.readAllEvents();
        String eventTitle = eventNameETUPD.getText().toString();
        String eventComment = eventCommentETUPD.getText().toString();

        cRepeat.clear();
            cRepeat.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

        cRepeat.set(Calendar.HOUR_OF_DAY, 8);
        cRepeat.set(Calendar.MINUTE, 0);
        cRepeat.set(Calendar.SECOND, 0);
        cRepeat.set(Calendar.MILLISECOND, 0);
        repeats_listUPD.clear();
        if (repeatState == 1) {

            for (int i = 0; i < repeatCounterInt; i++) {
                cRepeat.add(Calendar.DAY_OF_YEAR, 1);
                repeats_listUPD.add(cRepeat.getTime());

            }
        } else if (repeatState == 2) {

            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.DAY_OF_YEAR, 7);
                repeats_listUPD.add(cRepeat.getTime());
            }


        } else if (repeatState == 3) {
            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.MONTH, 1);
                repeats_listUPD.add(cRepeat.getTime());
            }

        } else if (repeatState == 4) {

            for (int i = 0; i < repeatCounterInt; i++) {

                cRepeat.add(Calendar.YEAR, 1);
                repeats_listUPD.add(cRepeat.getTime());
            }
        } else if (repeatState == 5) {
            for (LocalDate localDate : CustomRepeatActivity.customDatesToSaveLocalDate) {
                Calendar cForCustom = Calendar.getInstance();

                    cForCustom.set(Calendar.YEAR, localDate.getYear());
                    cForCustom.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
                    cForCustom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());


                cForCustom.set(Calendar.HOUR_OF_DAY, 8);
                cForCustom.set(Calendar.MINUTE, 0);
                cForCustom.set(Calendar.SECOND, 0);
                Date dateFromCustomRepeat = cForCustom.getTime();
                repeats_listUPD.add(dateFromCustomRepeat);
                if (localDate.equals(date)) {
                    repeats_listUPD.remove(dateFromCustomRepeat);
                }
            }


        }

        while (cursorEvent.moveToNext()) {
            if (!editFutureArray.isEmpty()) {
                for (int i = 0; i < editFutureArray.size(); i++) {
                    if (id_row.equals(editFutureArray.get(i))) {
                        myDB.addEvent(eventTitle, eventComment, date, time, String.valueOf(alarmState),
                                String.valueOf(repeatState), null,String.valueOf(color),location);
                        while (lastCursorEvent.moveToNext()) {
                            if (lastCursorEvent.moveToLast()) {
                                head_id = lastCursorEvent.getString(0);
                            }
                        }
                    } else if (!editFutureArray.get(i).equals(id_row)) {
                        for (int j = 0; j < repeats_listUPD.size(); j++) {
                            myDB.addEvent(eventTitle, eventComment, CalendarUtils.dateToLocalDate(repeats_listUPD.get(j)), time, String.valueOf(alarmState),
                                    String.valueOf(repeatState), head_id,String.valueOf(color),location);
                            if (j == repeats_listUPD.size() - 1) {
                                break;
                            }
                        }
                        break;
                    }

                }
                break;
            }
        }


        if (repeats_listUPD.size() > 0) {
            while (cursorEvent.moveToNext()) {
                if (cursorEvent.getString(0).equals(id_row)) {
                    list_repeat_for_dbUPD.add(cursorEvent.getString(0));
                }
                if (!(cursorEvent.getString(7) == null) && cursorEvent.getString(7).equals(id_row)) {
                    list_repeat_for_dbUPD.add(cursorEvent.getString(0));
                }
            }
        }


        startAlarmForRepeats();
        lastCursorEvent.close();
        cursorEvent.close();
        myDB.close();


    }

    private void startAlarmForRepeats() {
        Cursor cursorEvent = myDB.readAllEvents();

        while (cursorEvent.moveToNext()) {
            if (!(cursorEvent.getString(7) == null) && cursorEvent.getString(7).equals(id_row)) {
                repeats_listUPD.add(CalendarUtils.stringToDateFormat(cursorEvent.getString(3)));
            }
        }
        for (int j = 0; j < repeats_listUPD.size(); j++) {
            for (int i = 0; i < list_repeat_for_dbUPD.size(); i++) {
                startAlarm(Integer.parseInt(list_repeat_for_dbUPD.get(i)), CalendarUtils.dateToCalendar(repeats_listUPD.get(j)));
            }
        }
    }
//----------------------------------------------------------------------------



    private void updEventAction() {


        Cursor eventCursor = myDB.readAllEvents();

        eventCursor.moveToPosition(-1);
        while (eventCursor.moveToNext()) {
            if (eventCursor.getString(0).equals(id_row) &&
                    events_date.equals(CalendarUtils.stringToLocalDate(eventCursor.getString(3))) &&
                    (editAllArray.isEmpty() && editFutureArray.isEmpty())) {
                updateAndSaveEvent();
                giveIdToStartAlarmsEvent();
                if (date.equals(CalendarUtils.selectedDate)) {
                    updateAndSaveRepeatAllEvents();
                } else {
                    alertDialogCountRepeatIfDateChange(date);
                    updateAndSaveRepeatAllEvents();
                }


                break;
            } else if (eventCursor.getString(0).equals(id_row) &&
                    events_date.equals(CalendarUtils.stringToLocalDate(eventCursor.getString(3))) &&
                    !(editAllArray.isEmpty())) {
                updateAndSaveTittleCommentsAllEvents();
                updateAndSaveReminderAllEvents();
                giveIdToStartAlarmsRepeatingEvent();
                if (date.equals(CalendarUtils.selectedDate)) {
                    makeAndSaveRepeatUPD();
                } else {
                    alertDialogCountRepeatIfDateChange(date);
                    makeAndSaveRepeatUPD();
                }
            } else if (eventCursor.getString(0).equals(id_row) &&
                    events_date.equals(CalendarUtils.stringToLocalDate(eventCursor.getString(3))) &&
                    !(editFutureArray.isEmpty())) {

                if (!(repeatState == 0))
                {
                    updateAndSaveTittleCommentsFutureEvents();
                    updateAndSaveReminderFutureEvents();
                    updateAndSaveDateFutureEvents();
                    updateAndSaveRepeatFutureEvents();
                }

                if (repeatState == 0) {
                    updateAndSaveTittleCommentsFutureEvents();
                    updateAndSaveReminderFutureEvents();
                    updateAndSaveDateFutureEvents();
                }

            }
        }


        eventCursor.close();
        myDB.close();

        Intent i1 = new Intent(Edit_Update_Activity.this, MainActivity.class);
            i1.putExtra("date", date);


        String myTemp = CalendarUtils.selectedDate.toString();
        i1.putExtra("tempDate",myTemp);
        i1.putExtra("stack",stackNow);

        myDB.removeDuplicateReminders();
        overridePendingTransition(0, 0);
        startActivity(i1);
        overridePendingTransition(0, 0);


    }
}
