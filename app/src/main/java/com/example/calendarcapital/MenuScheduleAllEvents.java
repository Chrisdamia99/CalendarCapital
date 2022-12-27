package com.example.calendarcapital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class MenuScheduleAllEvents extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    private FloatingActionButton floatAddBtnMenuView;
    private ListView MenuSceduleEventListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_schedule_all_events);

        initWidgets();
        setHourAdapter();
        setNavigationViewListener();
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMenu);
        setNavigationViewListener();

        floatAddBtnMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventAlertDialog();
            }
        });


        findViewById(R.id.imageMenuSchedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

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
                                AllEventsList.reloadActivity(MenuScheduleAllEvents.this);
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

        MenuSceduleEventListView = findViewById(R.id.MenuSceduleEventListView);
        floatAddBtnMenuView = findViewById(R.id.floatAddBtnMenuView);
    }



    @Override
    protected void onResume() {
        super.onResume();
        setHourAdapter();



        //onItemClick για να δειχνει την ωρα την ημερομηνια και τα comment!
        MenuSceduleEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = MenuSceduleEventListView.getItemAtPosition(position).toString();



                AlertDialog.Builder builder = new AlertDialog.Builder(MenuScheduleAllEvents.this);
                builder.setMessage(listItem.toString()).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
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


    public void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(), myDB));
        MenuSceduleEventListView.setAdapter(hourAdapter);
    }


    public void EventAlertDialog() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog, null);

        final AlertDialog ad = new AlertDialog.Builder(this).create();
        RadioButton rb1 = customView.findViewById(R.id.rbAdd);

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventAction();
                ad.hide();
                finish();
                startActivity(getIntent());
            }
        });


    }


    public void newEventAction() {
        startActivity(new Intent(this, EventEdit.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuSchedule:
                finish();
                startActivity(getIntent());
                AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(),myDB);
                break;
            case R.id.daysView:
                Intent i = new Intent(MenuScheduleAllEvents.this, DailyView.class);
                startActivity(i);
                break;

            case R.id.weekView:
                Intent i2 = new Intent(MenuScheduleAllEvents.this, WeekViewActivity.class);
                startActivity(i2);
                break;
            case R.id.monthView:
                Intent i3 = new Intent(MenuScheduleAllEvents.this, MainActivity.class);
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

}