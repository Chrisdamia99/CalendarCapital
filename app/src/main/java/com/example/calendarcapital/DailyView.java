package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyView extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener{

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    private FloatingActionButton floatAddBtnDailyView;
    MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    ArrayList<String> event_id, event_title, event_comment, event_date, event_time;
    static String dayOfWeek;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_view);
        initWidgets();
        setDayView();
        setNavigationViewListener();
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);


        setNavigationViewListener();





        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        floatAddBtnDailyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventAlertDialog();
            }
        });




        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
    }

    private void initWidgets() {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTv);
        hourListView = findViewById(R.id.hourListView);
        floatAddBtnDailyView = findViewById(R.id.floatAddBtnDailyView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDayView();

        String messageForAD = CalendarUtils.monthDayFromDate(selectedDate) + " " + dayOfWeek + "\n";

        //onItemClick για να δειχνει την ωρα την ημερομηνια και τα comment!
        hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DailyView.this);
                builder.setMessage(messageForAD).setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());

                    }
                });
                builder.show();
            }
        });


    }

    private void setDayView() {

        Locale locale = new Locale("el", "GR");
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
          dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
//        compareArrayDatabaseAndLocal();
    }

    public void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventListFromDatabase());
        hourListView.setAdapter(hourAdapter);
    }

    private void compareArrayDatabaseAndLocal()
    {
        if(!hourEventListFromDatabase().equals(hourEventList()))
        {
            HourAdapter hourAdapterDB = new HourAdapter(getApplicationContext(), hourEventList());
            hourListView.setAdapter(hourAdapterDB);
        }else
        {

        }
    }

    @NonNull
    private ArrayList<HourEvent> hourEventListFromDatabase()
    {
        ArrayList<HourEvent> eventsDB = new ArrayList<>();
        Cursor cursor = myDB.readAllData();
       if (cursor.getCount() == 0)
       {
           Toast.makeText(this, "Error read data failed", Toast.LENGTH_SHORT).show();
           hourEventList();
       }else
       {
           while (cursor.moveToNext())
           {
               LocalDate dateDB = CalendarUtils.stringToLocalDate(cursor.getString(3));
               LocalTime timeDB = LocalTime.parse(cursor.getString(4));
               String titleDB = cursor.getString(1);
               String commentDB = cursor.getString(2);
               Event eventDB = new Event(titleDB,commentDB,dateDB,timeDB);
               ArrayList<Event> eventarrayDB = new ArrayList<>();
                eventarrayDB.add(eventDB);
               HourEvent hourEventDB = new HourEvent(timeDB,eventarrayDB);


               eventsDB.add(hourEventDB);

           }
       }
       return  eventsDB;
    }



    private ArrayList<HourEvent> hourEventList() {

        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++)
        {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }
        return list;
    }

    public void previousDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();

    }

    public void nextDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();

    }



    public void EventAlertDialog()
    {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog, null);

        final AlertDialog ad = new AlertDialog.Builder(this).create();
        RadioButton rb1 = customView.findViewById(R.id.rbAdd);
        RadioButton rb2 = customView.findViewById(R.id.rbDelete);
        RadioButton rb3 = customView.findViewById(R.id.rbEdit);

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventAction();
                ad.hide();
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ad.setView(customView);
        ad.show();

    }



    public void newEventAction()
    {
        startActivity(new Intent(this, EventEdit.class));
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuSchedule:
                break;
            case R.id.daysView:
                Intent i = new Intent(DailyView.this, DailyView.class);
                startActivity(i);
                break;
            case R.id.monthView:
                Intent i2 = new Intent(DailyView.this, MainActivity.class);
                startActivity(i2);
                break;

            case R.id.weekView:
                Intent i3 = new Intent(DailyView.this, WeekViewActivity.class);
                startActivity(i3);
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

        @Override
        public void onItemClick(int position, LocalDate date) {

        }

}


