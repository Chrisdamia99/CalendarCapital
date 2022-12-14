package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.daysInMonthArray;
import static com.example.calendarcapital.CalendarUtils.monthYearFromDate;
import static com.example.calendarcapital.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



//Implements calendaradapter onitemlistener
public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener
{
    //Declare 3 variables textview(monthyeartext), the RecyclerView(calendarrecycleview)
    //and a LocalDate which is selectedDate
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private FloatingActionButton floatAddBtnMonthAdd;
    private ListView monthListView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        setNavigationViewListener();
         final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        floatAddBtnMonthAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventAlertDialog();
            }
        });

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });



        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

    }


    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
       floatAddBtnMonthAdd = (FloatingActionButton) findViewById(R.id.floatAddBtnMonthView);
       monthListView = findViewById(R.id.monthListView);
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setMonthAdapter();
    }


    private void setMonthAdapter() {
        MonthAdapter monthAdapter = new MonthAdapter(getApplicationContext(), monthEventList());
        monthListView.setAdapter(monthAdapter);
    }

    private ArrayList<MonthEvent> monthEventList() {
        ArrayList<MonthEvent> list = new ArrayList<>();

        for (int month = 0; month < 42; month++)
        {
            LocalDate date = LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),selectedDate.getDayOfMonth());
            ArrayList<Event> events = Event.eventsForDate(date);
            MonthEvent monthEvent = new MonthEvent(date,events);
            list.add(monthEvent);
        }
        return list;

    }

    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }


    public void EventAlertDialog()
    {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog, null);

        final AlertDialog ad = new AlertDialog.Builder(this).create();
        String message = getString(R.string.add_edit_delete_event_popupmsg);
        ad.setTitle(message);
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




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuSchedule:
                break;
            case R.id.daysView:
                Intent i = new Intent(MainActivity.this, DailyView.class);
                startActivity(i);
                break;

            case R.id.weekView:
                Intent i2 = new Intent(MainActivity.this, WeekViewActivity.class);
                startActivity(i2);
                break;
            case R.id.monthView:
                finish();
                startActivity(getIntent());
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

