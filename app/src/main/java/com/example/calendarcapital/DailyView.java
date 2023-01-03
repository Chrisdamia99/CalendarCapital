package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.format.TextStyle;

import java.util.Locale;

public class DailyView extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener {

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    HourAdapter hourAdapter;
    private FloatingActionButton floatAddBtnDailyView;
    private MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    static String dayOfWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_view);
        initWidgets();
        setDayView();
        setNavigationViewListener();
       final DrawerLayout drawerLayout= findViewById(R.id.drawerLayout);


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
                newEventAction();
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
                                AllEventsList.reloadActivity(DailyView.this);
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
        drawerLayout.closeDrawer(GravityCompat.START);

    }

    private void initWidgets() {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTv);
        hourListView = findViewById(R.id.hourListView);
        floatAddBtnDailyView = findViewById(R.id.floatAddBtnDailyView);
    }

    private void setDayView() {

        Locale locale = new Locale("el", "GR");
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }


    public void setHourAdapter() {
         hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(getApplicationContext(), myDB));


        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));
        hourListView.setAdapter(hourAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setDayView();

        hourAdapter.notifyDataSetChanged();
        DialogClickedItemAndDelete();



    }

    private void DialogClickedItemAndDelete()
    {
        EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(),myDB.readAllData());

        hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
//                private ArrayList<MlaData> MlaDats = new ArrayList<MlaData>();
                HourEvent myEvent = (HourEvent) hourListView.getAdapter().getItem(position);

                String myEventId= myEvent.getEvents().get(0).getId();
                String myTitle = myEvent.getEvents().get(0).getName();
                String myComment = myEvent.getEvents().get(0).getComment();
                String myDate = String.valueOf(myEvent.getEvents().get(0).getDate());
                String myTime = String.valueOf(myEvent.getEvents().get(0).getTime());


                View viewFinal;
                String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
                String title_upd = hourAdapter.getItem(position).getEvents().get(0).getName();
                String comment_upd = hourAdapter.getItem(position).getEvents().get(0).getComment();
                String date_upd = String.valueOf(hourAdapter.getItem(position).getEvents().get(0).getDate());
                String time_upd = String.valueOf(hourAdapter.getItem(position).getEvents().get(0).getTime());



                AlertDialog.Builder builder = new AlertDialog.Builder(DailyView.this);

                viewFinal = CA.setAllFields(view1,myEventId,myTitle,myComment,myDate,myTime);
//                    viewFinal = SD.getView(position, view1, parent);


                builder.setView(viewFinal).

                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
                                myDB.deleteOneRow(id_row);


                                AllEventsList.reloadActivity(DailyView.this);

                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AllEventsList.reloadActivity(DailyView.this);


                            }
                        }).setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(DailyView.this,Edit_Update_Activity.class);
                                i.putExtra("id",id_row);
                                i.putExtra("title",title_upd);
                                i.putExtra("comment",comment_upd);
                                i.putExtra("date",date_upd);
                                i.putExtra("time",time_upd);
                                startActivity(i);
                            }
                        });


                builder.show();
            }

        });
    }
//    public void onBackPressed() {
//
//        DrawerLayout layoutMain = MainActivity.drawerLayout;
//        DrawerLayout layoutWeek = WeekViewActivity.drawerLayout;
//        if (layoutMain.isDrawerOpen(GravityCompat.END))
//        {
//            layoutMain.closeDrawer(GravityCompat.END);
//        }else if(layoutWeek.isDrawerOpen(GravityCompat.END))
//        {
//            layoutWeek.closeDrawer(GravityCompat.END);
//        }else {
//            DailyView.super.onBackPressed();
//        }
//
//
//    }

    public void previousDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();

    }

    public void nextDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();

    }



    public void newEventAction() {
        startActivity(new Intent(this, EventEdit.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSchedule:
                Intent i4 = new Intent(DailyView.this, MenuScheduleAllEvents.class);
                startActivity(i4);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_up_inlayout, menu);
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


