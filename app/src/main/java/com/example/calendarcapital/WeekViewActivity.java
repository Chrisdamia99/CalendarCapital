package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.daysInWeekArray;
import static com.example.calendarcapital.CalendarUtils.monthDayFromDate;
import static com.example.calendarcapital.CalendarUtils.monthYearFromDate;
import static com.example.calendarcapital.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton floatBtnEvent;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView2;
    private ListView eventListView;
    ArrayList<String> event_id, event_title, event_comments, event_date,event_time;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        initWidgets();
        setWeekView();
        setNavigationViewListener();


        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        floatBtnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventAlertDialog();
            }
        });

        event_id = new ArrayList<>();
       event_title = new ArrayList<>();
        event_comments = new ArrayList<>();
        event_date = new ArrayList<>();
        event_time= new ArrayList<>();

//        storeDataInArrays(event_id,event_time,event_date,monthDayFromDate(selectedDate),monthYearFromDate(selectedDate),myDB);
        //String event, String time,String date, String month, String year, SQLiteDatabase database

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);



    }

    private void initWidgets()
    {
        calendarRecyclerView2 = findViewById(R.id.calendarRecyclerView2);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
        floatBtnEvent = findViewById(R.id.floatAddBtn);
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView2.setLayoutManager(layoutManager);
        calendarRecyclerView2.setAdapter(calendarAdapter);
//        setEventAdpater(myDB);
    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        setEventAdpater(myDB);
    }

//    private void setEventAdpater(DBOpenHelper dbOpenHelper)
//    {
//        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
//        Cursor data = myDB.ReadEvents(selectedDate.toString(), dbOpenHelper.getReadableDatabase());
//        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
//        eventListView.setAdapter(eventAdapter);
//    }

    public void EventAlertDialog()
    {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog, null);

        final AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("Προσθήκη/Επεξεργασία/Διαγραφή συμβάντων");
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
        ad.getWindow().setLayout(1000,800);

    }

    public void newEventAction()
    {
        startActivity(new Intent(this, EventEdit.class));
    }

//    void storeDataInArrays(String event, String time,String date, String month, String year, SQLiteDatabase database){
//
//        myDB = new DBOpenHelper(context);
//         database = myDB.getWritableDatabase();
//        myDB.SaveEvent(event,time,date,month,year, database);
//        myDB.close();
//        Toast.makeText(context, "Event successfully saved.", Toast.LENGTH_SHORT).show();
//
//
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuSchedule:
                break;
            case R.id.daysView:
                Intent i = new Intent(WeekViewActivity.this, DailyView.class);
                startActivity(i);
                break;

            case R.id.weekView:
                finish();
                startActivity(getIntent());
                break;
            case R.id.monthView:
                Intent i2 = new Intent(WeekViewActivity.this, MainActivity.class);
                startActivity(i2);
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