package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.daysInMonthArray;
import static com.example.calendarcapital.CalendarUtils.monthYearFromDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;



//Implements calendaradapter onitemlistener
public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener {
    //Declare 3 variables textview(monthyeartext), the RecyclerView(calendarrecycleview)
    //and a LocalDate which is selectedDate
    private TextView monthYearText;

    String id_row;
    HourAdapter hourAdapter;
    private RecyclerView calendarRecyclerView;
    private FloatingActionButton floatAddBtnMonthAdd;
    private ListView monthListView;
    private MyDatabaseHelper myDB = new MyDatabaseHelper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        setNavigationViewListener();
        getAndSetIntent();


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



    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();


        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        setMonthAdapter();
    }

    private void setMonthAdapter() {
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(getApplicationContext(), myDB));

        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));


        monthListView.setAdapter(hourAdapter);



    }

    void getAndSetIntent()
    {
        if (getIntent().hasExtra("id"))
        {
            id_row = getIntent().getStringExtra("id");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DialogClickedItemAndDelete();


    }

    public  void DialogClickedItemAndDelete()
    {                EventCursorAdapter EC = new EventCursorAdapter(MainActivity.this, myDB.readAllData());
            Cursor cursor= myDB.readAllData();
                test2 test2 = new test2(getApplicationContext(),AllEventsList.hourEventListFromDatabase(getApplicationContext(), myDB));
        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                builder.setView(test2.getView(position,view1,parent)).

                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
                                myDB.deleteOneRow(id_row);


                                AllEventsList.reloadActivity(MainActivity.this);

                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AllEventsList.reloadActivity(MainActivity.this);


                            }
                        });

                builder.show();


                }

        });
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        floatAddBtnMonthAdd = (FloatingActionButton) findViewById(R.id.floatAddBtnMonthView);
        monthListView = findViewById(R.id.monthListView);
    }






//    private void setMonthAdapter() {
//        MonthAdapter monthAdapter = new MonthAdapter(getApplicationContext(), monthEventList());
//        monthListView.setAdapter(monthAdapter);
//    }


    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    boolean isDoubleClicked = false;

    Handler handler = new Handler();

    @Override
    public void onItemClick(int position, LocalDate date) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                isDoubleClicked = false;
            }
        };

        if (isDoubleClicked) {
            Intent i5 = new Intent(MainActivity.this, DailyView.class);
            startActivity(i5);
            isDoubleClicked = false;
            handler.removeCallbacks(r);
        } else {
            isDoubleClicked = true;
            handler.postDelayed(r, 500);
        }

        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }


    public void EventAlertDialog() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog, null);

        final AlertDialog ad = new AlertDialog.Builder(this).create();
        String message = getString(R.string.add_edit_delete_event_popupmsg);
        ad.setTitle(message);
        RadioButton rb1 = customView.findViewById(R.id.rbAdd);


        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventAction();
                ad.hide();
            }
        });


        ad.setView(customView);
        ad.show();
        ad.getWindow().setLayout(600, 600);

    }


    public void newEventAction() {
        startActivity(new Intent(this, EventEdit.class));
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSchedule:
                Intent i4 = new Intent(MainActivity.this, MenuScheduleAllEvents.class);
                startActivity(i4);
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

