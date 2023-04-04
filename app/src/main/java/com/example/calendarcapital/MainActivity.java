package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.daysInMonthArray;
import static com.example.calendarcapital.CalendarUtils.daysInWeekArray;
import static com.example.calendarcapital.CalendarUtils.monthYearFromDate;
import static com.example.calendarcapital.CalendarUtils.selectedDate;
import static com.example.calendarcapital.CalendarUtils.stringToLocalDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Locale;


//Implements calendaradapter onitemlistener
public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener {
    //Declare 3 variables textview(monthyeartext), the RecyclerView(calendarrecycleview)
    //and a LocalDate which is selectedDate
    FloatingActionButton floatAddBtnMonthAdd;
    private TextView monthYearText, daysOfWeekDaily;
    LinearLayout daysOfWeek;
    private RecyclerView calendarRecyclerView;
    private ListView monthListView;
    NestedScrollView nestedScrollView;
    HourAdapter hourAdapter;
    ImageButton prevMonth, nextMonth;
    ImageView imageMenu, backMenuBtn, refreshMenuBtn;
    private MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    DrawerLayout drawerLayout;
    public static ArrayDeque<String> stack = new ArrayDeque<String>();
    public static String getSaveStack;
    boolean changeEventEdit = true;
    Bundle b;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        monthListView.setVisibility(View.GONE);
        setNavigationViewListener();
        getIntentFromEventEdit();
        dublicatesInStack();



        drawerLayout = findViewById(R.id.drawerLayout);

        refreshMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stack.size() > 1) {
                    getSaveStack = stack.removeFirst();
                }
                finish();
                changeEventEdit = true;
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                getIntent().removeExtra("bool");

                startActivity(getIntent());


            }
        });


        backMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dublicatesInStack();
                onMyBackPressed();

            }
        });


        floatAddBtnMonthAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newEventAction();
                getSaveStack = stack.getFirst();
            }
        });


        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });


        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        drawerLayout.closeDrawer(GravityCompat.START);


    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        floatAddBtnMonthAdd = findViewById(R.id.floatAddBtnMonthView);
        monthListView = findViewById(R.id.monthListView);
        nestedScrollView = findViewById(R.id.scrollView1);
        daysOfWeekDaily = findViewById(R.id.daysOfWeekMain);
        daysOfWeek = findViewById(R.id.daysOfWeek);
        prevMonth = findViewById(R.id.prevMonthButton);
        nextMonth = findViewById(R.id.nextMonthButton);
        imageMenu = findViewById(R.id.imageMenu);
        backMenuBtn = findViewById(R.id.BackMenuBtn);
        refreshMenuBtn = findViewById(R.id.refreshMenuBtn);

    }
    public void getIntentFromEventEdit() {

        b = getIntent().getExtras();
        if (stack.size() > 1) {
            stack.add("month");
        }

        if (getIntent().hasExtra("bool") && changeEventEdit == false) {


            if (b.getBoolean("bool")) {
                stack.addFirst(getSaveStack);
                getStackFromSave();

            } else {
                setMonthView();
                if (stack.size() > 1)
                    stack.removeFirst();
            }


        } else {
            setMonthView();
            if (stack.size() > 1)
                stack.removeFirst();
        }

    }

    public void getStackFromSave() {

        if (getSaveStack == null) {
            setMonthView();
            stack.addFirst("month");
        } else if (getSaveStack.equals("daily")) {
            setDaily();
        } else if (getSaveStack.equals("week")) {
            setWeek();
        } else if (getSaveStack.equals("all")) {
            setAllEvents();
        } else if (getSaveStack.equals("month")) {
            setMonthView();
        } else if (getSaveStack.equals("double-click-month")) {
            setDaily();
        } else if (getSaveStack.equals("double-click-week")) {
            setDaily();
        }

    }

    public void dublicatesInStack() {
        if (stack.size() > 1) {
            stack.removeFirst();
        }
        // Check the previous view type
        Object[] arr = stack.toArray();
        for (int i = 0; i < stack.size() - 1; i++) {
            if (arr[i] == arr[i + 1]) {
                stack.remove(arr[i]);
            }
            if (arr[i] == "week" && arr[i + 1] == "double-click-week") {
                stack.remove(arr[i + 1]);
            }
        }
    }


    public void onMyBackPressed() {

        String previousViewType = stack.peekFirst();


        if (previousViewType == null) {
            // Nothing to go back to, so finish this Activity
            super.onBackPressed();
            return;
        }
        if (previousViewType.equals("daily")) {
            setDaily();
        } else if (previousViewType.equals("week")) {
            setWeek();
        } else if (previousViewType.equals("all")) {
            setAllEvents();
        } else if (previousViewType.equals("month")) {
            setMonthView();
        } else if (previousViewType.equals("double-click-month")) {
            setMonthView();
        } else if (previousViewType.equals("double-click-week")) {
            setWeek();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        changeEventEdit = true;
        getIntentFromEventEdit();
        setWeek();
        setMonthView();
        hourAdapter.notifyDataSetChanged();
        DialogClickedItemAndDelete();
        getStackFromSave();


    }

    private void setEventListView() {
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(myDB));


        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));
        monthListView.setAdapter(hourAdapter);

        myDB.close();
    }

    private void DialogClickedItemAndDelete() {
        EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(), myDB.readAllEvents());

        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
                HourEvent myEvent = (HourEvent) monthListView.getAdapter().getItem(position);
                LayoutInflater lf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = lf.inflate(R.layout.repeating_events_alert_dialog, null);
                TextView deleteAll = rowView.findViewById(R.id.deleteAll);
                TextView deleteOne = rowView.findViewById(R.id.deleteOne);
                String myEventId = myEvent.getEvents().get(0).getId();
                String myTitle = myEvent.getEvents().get(0).getName();
                String myComment = myEvent.getEvents().get(0).getComment();
                String myDate = String.valueOf(myEvent.getEvents().get(0).getDate());
                String myTime = String.valueOf(myEvent.getEvents().get(0).getTime());
                String alarm = myEvent.getEvents().get(0).getAlarm();


                View viewFinal;
                String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
                String title_upd = hourAdapter.getItem(position).getEvents().get(0).getName();
                String comment_upd = hourAdapter.getItem(position).getEvents().get(0).getComment();
                String date_upd = String.valueOf(hourAdapter.getItem(position).getEvents().get(0).getDate());
                String time_upd = String.valueOf(hourAdapter.getItem(position).getEvents().get(0).getTime());
                String alarmState = hourAdapter.getItem(position).getEvents().get(0).getAlarm();
                String repeatState = hourAdapter.getItem(position).getEvents().get(0).getRepeat();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                AlertDialog builderRepeating = new AlertDialog.Builder(MainActivity.this).setView(rowView).setTitle("Διαγραφή συμβάντος").create();





                viewFinal = CA.setAllFields(view1, myEventId, myTitle, myComment, myDate, myTime);


                builder.setView(viewFinal).

                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String parent_id_value = hourAdapter.getItem(position).getEvents().get(0).getParent_id();
                                        if (parent_id_value==null)
                                        {
                                            deleteEventNotRepeating(position,CA);
                                        }else
                                        {
                                            deleteEventIfRepeating(builderRepeating,position,CA,deleteAll,deleteOne);
                                            builderRepeating.show();
                                        }




                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String previousViewType = stack.peekFirst();
                                        if (previousViewType.equals("all")) {
                                            setAllEvents();
                                        } else if (previousViewType.equals("double-click-week")) {
                                            setDaily();
                                        } else if (previousViewType.equals("month")) {
                                            setDaily();
                                        } else if (previousViewType.equals("double-click-month")) {
                                            setDaily();
                                        } else if (previousViewType.equals("week")) {
                                            setWeek();
                                        } else if (previousViewType.equals("daily")) {
                                            setDaily();
                                        } else {
                                            onMyBackPressed();
                                        }

                                    }
                                }).setTitle("Are you sure you want to delete event " + title_upd + " ?");
                                builderDel.show();


                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.cancel();
                            }
                        }).setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(MainActivity.this, Edit_Update_Activity.class);
                                i.putExtra("id", id_row);
                                i.putExtra("title", title_upd);
                                i.putExtra("comment", comment_upd);
                                i.putExtra("date", date_upd);
                                i.putExtra("time", time_upd);
                                i.putExtra("alarm", alarmState);
                                i.putExtra("repeat",repeatState);
                                startActivity(i);
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                hourAdapter.notifyDataSetChanged();



                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                hourAdapter.notifyDataSetChanged();

                            }
                        });


                builder.show();


            }


        });


    }

    private void deleteEventIfRepeating(AlertDialog builderRepeating,int position,EventCursorAdapter CA,TextView deleteAll,TextView deleteOne)
    {

        builderRepeating.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                deleteAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String parent_id = hourAdapter.getItem(position).getEvents().get(0).getParent_id();
                        ArrayList<String> remIds = new ArrayList<>();

                        Cursor cursorEvent = myDB.readAllEvents();
                        Cursor remCursor = myDB.readAllReminder();

                        while(cursorEvent.moveToNext())
                        {
                            if (cursorEvent.getString(7)==parent_id)
                            {
                                remIds.add(cursorEvent.getString(7));
                            }
                        }

                        myDB.deleteOneRowReminder(parent_id);
                        cancelAlarm(Integer.parseInt(parent_id));
                        for (int i=0; i<remIds.size(); i++)
                        {
                            myDB.deleteOneRowReminder(remIds.get(i));
                            cancelAlarm(Integer.parseInt(remIds.get(i)));
                        }


                        CA.notifyDataSetChanged();
                        hourAdapter.notifyDataSetChanged();

                        myDB.deleteAllEventsParentId(parent_id);
                        myDB.deleteOneRow(parent_id);

                        remCursor.close();
                        myDB.close();




                        String previousViewType = stack.peekFirst();
                        if (previousViewType.equals("all")) {
                            setAllEvents();
                        } else if (previousViewType.equals("double-click-week")) {
                            setDaily();
                        } else if (previousViewType.equals("month")) {
                            setDaily();
                        } else if (previousViewType.equals("double-click-month")) {
                            setDaily();
                        } else if (previousViewType.equals("week")) {
                            setWeek();
                        } else if (previousViewType.equals("daily")) {
                            setDaily();
                        } else {
                            onMyBackPressed();
                        }
                        dialog.dismiss();
                    }
                });

                deleteOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String event_repeating_id = hourAdapter.getItem(position).getEvents().get(0).getId();


                        Cursor eventCursor = myDB.readAllEvents();
                        Cursor remCursor = myDB.readAllReminder();

                        myDB.deleteOneRow(event_repeating_id);

                        remCursor.moveToPosition(-1);
                        while (remCursor.moveToNext())
                        {
                            if (remCursor.getString(1).equals(event_repeating_id))
                            {
                                myDB.deleteOneRowReminder(remCursor.getString(0));
                                cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                            }
                        }



                        CA.notifyDataSetChanged();
                        hourAdapter.notifyDataSetChanged();

                        eventCursor.close();
                        remCursor.close();
                        myDB.close();

                        String previousViewType = stack.peekFirst();
                        if (previousViewType.equals("all")) {
                            setAllEvents();
                        } else if (previousViewType.equals("double-click-week")) {
                            setDaily();
                        } else if (previousViewType.equals("month")) {
                            setDaily();
                        } else if (previousViewType.equals("double-click-month")) {
                            setDaily();
                        } else if (previousViewType.equals("week")) {
                            setWeek();
                        } else if (previousViewType.equals("daily")) {
                            setDaily();
                        } else {
                            onMyBackPressed();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void deleteEventNotRepeating(int position, EventCursorAdapter CA)
    {
        String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
        String title_row=hourAdapter.getItem(position).getEvents().get(0).getName();
        String comment_row=hourAdapter.getItem(position).getEvents().get(0).getComment();
        LocalDate date_row = hourAdapter.getItem(position).getEvents().get(0).getDate();

        Cursor remCursor = myDB.readAllReminder();

        Cursor eventsCursor = myDB.readAllEvents();

        eventsCursor.moveToPosition(-1);
        while(eventsCursor.moveToNext())
        {
            String id_event = eventsCursor.getString(0);
            String event_title = eventsCursor.getString(1);
            String event_comment = eventsCursor.getString(2);
            String event_date = eventsCursor.getString(3);

            if (id_event.equals(id_row) && event_title.equals(title_row) && event_comment.equals(comment_row) &&
                    date_row.equals(stringToLocalDate(event_date)))
            {
                myDB.deleteOneRow(id_row);
                break;
            }

        }


        remCursor.moveToPosition(-1);
        while (remCursor.moveToNext()) {
            if (remCursor.getString(1).equals(id_row)){
                myDB.deleteOneRowReminder(remCursor.getString(0));

                String alarmId = remCursor.getString(0);
                cancelAlarm(Integer.parseInt(alarmId));}
            CA.notifyDataSetChanged();
            hourAdapter.notifyDataSetChanged();
        }


        eventsCursor.close();
        remCursor.close();
        myDB.close();

        String previousViewType = stack.peekFirst();
        if (previousViewType.equals("all")) {
            setAllEvents();
        } else if (previousViewType.equals("double-click-week")) {
            setDaily();
        } else if (previousViewType.equals("month")) {
            setDaily();
        } else if (previousViewType.equals("double-click-month")) {
            setDaily();
        } else if (previousViewType.equals("week")) {
            setWeek();
        } else if (previousViewType.equals("daily")) {
            setDaily();
        } else {
            onMyBackPressed();
        }
    }



    public void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


    }



    public void previousMonthAction(View view) {
        if (calendarRecyclerView.getVisibility() == View.GONE) {

            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
            setDaily();
        } else if (calendarRecyclerView.getMeasuredHeight() < 301) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);

            setWeek();
        } else {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
            setMonthView();
        }


    }

    public void nextMonthAction(View view) {

        if (calendarRecyclerView.getVisibility() == View.GONE) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
            setDaily();
        } else if (calendarRecyclerView.getMeasuredHeight() < 301) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);

            setWeek();
        } else {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
            setMonthView();
        }


    }


    boolean isDoubleClicked = false;

    Handler handler = new Handler();

    @Override
    public void onItemClick(int position, LocalDate date) {


        if (date != null) {
            selectedDate = date;

            if (calendarRecyclerView.getMeasuredHeight() < 401) {
                setWeek();

            } else if (calendarRecyclerView.getVisibility() == View.GONE) {
                setDaily();

            } else {
                setMonthView();

//                    calendarRecyclerView.getLayoutManager().scrollToPosition(position);
                calendarRecyclerView.getLayoutManager().smoothScrollToPosition(calendarRecyclerView, new RecyclerView.State(), position);


                if (position > 12) {
                    calendarRecyclerView.getLayoutManager().scrollToPosition(position);
//                    calendarRecyclerView.getLayoutManager().smoothScrollToPosition(calendarRecyclerView,new RecyclerView.State(),position);

                }
            }
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                isDoubleClicked = false;
            }
        };

        if (isDoubleClicked) {

            Object[] arr = stack.toArray();
            for (int i = 0; i < stack.size() - 1; i++) {
                if (arr[i] == arr[i + 1]) {
                    stack.remove(arr[i]);
                }
            }
            String previousViewType = stack.peekFirst();
            if (previousViewType.equals("month")) {
                stack.addFirst("double-click-month");
            } else if (previousViewType.equals("week")) {
                stack.addFirst("double-click-week");
            } else if (previousViewType.equals("double-click-month")) {
                stack.addFirst("double-click-month");
            } else if (previousViewType.equals("double-click-week")) {
                stack.addFirst("double-click-week");
            } else {
                stack.addFirst("month");
            }
            setDaily();
            drawerLayout.closeDrawer(GravityCompat.START);
            daysOfWeekDaily.setVisibility(View.VISIBLE);
            handler.removeCallbacks(r);
        } else {
            isDoubleClicked = true;

            Object[] arr = stack.toArray();
            for (int i = 0; i < stack.size() - 1; i++) {
                if (arr[i] == arr[i + 1]) {
                    stack.remove(arr[i]);
                }
            }
            String previousViewType = stack.peekFirst();
            if (previousViewType.equals("month")) {
                stack.addFirst("month");

            } else if (previousViewType.equals("week")) {
                stack.addFirst("week");
            } else if (previousViewType.equals("double-click-month")) {
                stack.addFirst("double-click-month");
            } else if (previousViewType.equals("double-click-week")) {
                stack.addFirst("double-click-week");
            } else {
                stack.addFirst("month");
            }
            handler.postDelayed(r, 500);
        }


    }


    private void setMonthView() {

        if (getIntent().hasExtra("date")) {
            Bundle b = getIntent().getExtras();

            selectedDate = (LocalDate) b.get("date");
            getIntent().removeExtra("date");

        }
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();


        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);


        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarRecyclerView.setAdapter(calendarAdapter);
        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        params.height = 1500;
        calendarRecyclerView.setLayoutParams(params);
        imageMenu.setVisibility(View.VISIBLE);
        monthListView.setVisibility(View.GONE);
        monthYearText.setVisibility(View.VISIBLE);
        daysOfWeekDaily.setVisibility(View.GONE);
        daysOfWeek.setVisibility(View.VISIBLE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.VISIBLE);
        backMenuBtn.setVisibility(View.GONE);


    }


    private void setWeek() {


        if (getIntent().hasExtra("date")) {
            Bundle b = getIntent().getExtras();

            selectedDate = (LocalDate) b.get("date");
            getIntent().removeExtra("date");

        }
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        params.height = 300;
        calendarRecyclerView.setLayoutParams(params);
        backMenuBtn.setVisibility(View.VISIBLE);
        imageMenu.setVisibility(View.GONE);


        ViewGroup.LayoutParams paramsListView = monthListView.getLayoutParams();
        paramsListView.height = 1200;

        monthListView.setLayoutParams(paramsListView);

        daysOfWeekDaily.setVisibility(View.GONE);
        monthListView.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.VISIBLE);
        daysOfWeek.setVisibility(View.VISIBLE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.VISIBLE);

        setEventListView();

    }

    private void setDaily() {

        if (getIntent().hasExtra("date")) {
            Bundle b = getIntent().getExtras();

            selectedDate = (LocalDate) b.get("date");
            getIntent().removeExtra("date");

        }

        Locale locale = new Locale("el", "GR");
        monthYearText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeekmain = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        setEventListView();
        daysOfWeekDaily.setText(dayOfWeekmain);

        ViewGroup.LayoutParams paramsListView = monthListView.getLayoutParams();
        paramsListView.height = 1500;


        monthListView.setLayoutParams(paramsListView);


        backMenuBtn.setVisibility(View.VISIBLE);
        imageMenu.setVisibility(View.GONE);
        backMenuBtn.setForegroundGravity(Gravity.LEFT);
        monthListView.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.VISIBLE);
        daysOfWeekDaily.setVisibility(View.VISIBLE);
        daysOfWeek.setVisibility(View.GONE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.GONE);
        hourAdapter.notifyDataSetChanged();


    }

    private void setAllEvents() {


        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(), myDB));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));

        hourAdapter.notifyDataSetChanged();

        ViewGroup.LayoutParams params = monthListView.getLayoutParams();
        params.height = 1800;

        monthListView.setLayoutParams(params);

        monthListView.setAdapter(hourAdapter);


        backMenuBtn.setVisibility(View.VISIBLE);
        imageMenu.setVisibility(View.GONE);
        backMenuBtn.setForegroundGravity(Gravity.LEFT);
        monthListView.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.GONE);
        daysOfWeekDaily.setVisibility(View.GONE);
        daysOfWeek.setVisibility(View.GONE);
        prevMonth.setVisibility(View.GONE);
        nextMonth.setVisibility(View.GONE);
        calendarRecyclerView.setVisibility(View.GONE);
        hourAdapter.notifyDataSetChanged();
        myDB.close();


    }


    public void newEventAction() {
        Intent saveIntent = new Intent(this, EventEdit.class);
        String stackNow = stack.peekFirst();
        changeEventEdit = false;
        saveIntent.putExtra("stack", stackNow);
        startActivity(saveIntent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSchedule:
                setAllEvents();
                stack.addFirst("all");
                drawerLayout.closeDrawer(GravityCompat.START);

                break;
            case R.id.daysView:
                setDaily();
                stack.addFirst("daily");
                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.weekView:

                setWeek();
                stack.addFirst("week");
                drawerLayout.closeDrawer(GravityCompat.START);


                break;

            case R.id.monthView:
                setMonthView();
                stack.addFirst("month");
                drawerLayout.closeDrawer(GravityCompat.START);


                break;
            case R.id.refreshItem:
                finish();
                startActivity(getIntent());
                break;
            case R.id.syncItem:
                break;

            default:
                onNavigationItemSelected(item);
        }


        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }


}

