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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
    HourAdapter hourAdapter;
    private MyDatabaseHelper myDB = new MyDatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        findViewById(R.id.menu_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.refreshItemOnLay:
                                AllEventsList.reloadActivity(WeekViewActivity.this);
                                return true;
                            case R.id.previousAct:
                                onBackPressed();
                        }

                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_up_inlayout);
                popupMenu.show();
            }
        });


        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);


    }

    private void initWidgets() {
        calendarRecyclerView2 = findViewById(R.id.calendarRecyclerView2);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
        floatBtnEvent = findViewById(R.id.floatAddBtn);
    }

    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView2.setLayoutManager(layoutManager);
        calendarRecyclerView2.setAdapter(calendarAdapter);
        setEventListView();
    }

    private void setEventListView() {
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(getApplicationContext(), myDB));


        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));
        eventListView.setAdapter(hourAdapter);
    }



    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

//    @Override
//    public void onItemClick(int position, LocalDate date) {
//        CalendarUtils.selectedDate = date;
//        setWeekView();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        setWeekView();
        hourAdapter.notifyDataSetChanged();
        DialogClickedItemAndDelete();
    }


    private void DialogClickedItemAndDelete()
    {
        EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(),myDB.readAllData());

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
//                private ArrayList<MlaData> MlaDats = new ArrayList<MlaData>();
                HourEvent myEvent = (HourEvent) eventListView.getAdapter().getItem(position);

                String myEventId= myEvent.getEvents().get(0).getId();
                String myTitle = myEvent.getEvents().get(0).getName();
                String myComment = myEvent.getEvents().get(0).getComment();
                String myDate = String.valueOf(myEvent.getEvents().get(0).getDate());
                String myTime = String.valueOf(myEvent.getEvents().get(0).getTime());


                View viewFinal;



                AlertDialog.Builder builder = new AlertDialog.Builder(WeekViewActivity.this);

                viewFinal = CA.setAllFields(view1,myEventId,myTitle,myComment,myDate,myTime);
//                    viewFinal = SD.getView(position, view1, parent);


                builder.setView(viewFinal).

                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
                                myDB.deleteOneRow(id_row);


                                AllEventsList.reloadActivity(WeekViewActivity.this);

                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AllEventsList.reloadActivity(WeekViewActivity.this);


                            }
                        });


                builder.show();
            }

        });
    }



    public void EventAlertDialog() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog, null);

        final AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("Προσθήκη/Επεξεργασία/Διαγραφή συμβάντων");
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
        ad.getWindow().setLayout(1000, 800);

    }

    public void newEventAction() {
        startActivity(new Intent(this, EventEdit.class));
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
            Intent i5 = new Intent(WeekViewActivity.this, DailyView.class);
            startActivity(i5);
            isDoubleClicked = false;
            handler.removeCallbacks(r);
        } else {
            isDoubleClicked = true;
            handler.postDelayed(r, 500);
        }

        if (date != null) {
            CalendarUtils.selectedDate = date;
            setWeekView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_up_inlayout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refreshItemOnLay:
                AllEventsList.reloadActivity(WeekViewActivity.this);
                return true;
            case R.id.previousAct:
                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSchedule:
                Intent i4 = new Intent(WeekViewActivity.this, MenuScheduleAllEvents.class);
                startActivity(i4);
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