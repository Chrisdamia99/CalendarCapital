package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.daysInMonthArray;
import static com.example.calendarcapital.CalendarUtils.daysInWeekArray;
import static com.example.calendarcapital.CalendarUtils.monthYearFromDate;
import static com.example.calendarcapital.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Locale;


//Implements calendaradapter onitemlistener
public class MainActivity extends AppCompatActivity  implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener  {
    //Declare 3 variables textview(monthyeartext), the RecyclerView(calendarrecycleview)
    //and a LocalDate which is selectedDate
    FloatingActionButton floatAddBtnMonthAdd;
    private TextView monthYearText,daysOfWeekDaily;
    LinearLayout daysOfWeek;
    private RecyclerView calendarRecyclerView;
    private ListView monthListView;
    NestedScrollView nestedScrollView;
    HourAdapter hourAdapter;
    Button prevMonth,nextMonth;
    ImageView refreshMenuBtn,BackMenuBtn;
    private MyDatabaseHelper myDB = new MyDatabaseHelper(this);
     DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        monthListView.setVisibility(View.GONE);
        setMonthView();
        setNavigationViewListener();

        drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.refreshMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }
        });

        findViewById(R.id.BackMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyBackPressed();

            }
        });


        floatAddBtnMonthAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventAction();
            }
        });



        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

//        findViewById(R.id.menu_new).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        switch (item.getItemId()) {
//                            case R.id.refreshItemOnLay:
//                                finish();
//                               overridePendingTransition(0, 0);
//                                startActivity(getIntent());
//                               overridePendingTransition(0, 0);
//                               return true;
//
//                            case R.id.addEventMenu:
//
//
//                                newEventAction();
//
//                            case R.id.previousAct:
//                                onMyBackPressed();
//                        }
//
//                        return false;
//                    }
//                });
//        popupMenu.inflate(R.menu.menu_up_inlayout);
//        popupMenu.show();
//            }
//        });




        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        drawerLayout.closeDrawer(GravityCompat.START);


    }

    ArrayDeque<String> stack = new ArrayDeque<String>();


    public void onMyBackPressed() {
        // Pop current view type off the stack
        stack.removeFirst();
        // Check the previous view type
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
        } else if (previousViewType.equals("all"))
        {
            setAllEvents();
        }else if (previousViewType.equals("month"))
        {
            setMonthView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setWeek();
        hourAdapter.notifyDataSetChanged();
        DialogClickedItemAndDelete();



    }

    private void DialogClickedItemAndDelete()
    {
        EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(),myDB.readAllData());

        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
//                private ArrayList<MlaData> MlaDats = new ArrayList<MlaData>();
                HourEvent myEvent = (HourEvent) monthListView.getAdapter().getItem(position);

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



                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                viewFinal = CA.setAllFields(view1,myEventId,myTitle,myComment,myDate,myTime);
//                    viewFinal = SD.getView(position, view1, parent);


                builder.setView(viewFinal).

                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
                                        myDB.deleteOneRow(id_row);


                                        AllEventsList.reloadActivity(MainActivity.this);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AllEventsList.reloadActivity(MainActivity.this);
                                    }
                                }).setTitle("Are you sure you want to delete event " + title_upd + " ?");
                                builderDel.show();


                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AllEventsList.reloadActivity(MainActivity.this);


                            }
                        }).setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(MainActivity.this,Edit_Update_Activity.class);
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




    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        floatAddBtnMonthAdd = findViewById(R.id.floatAddBtnMonthView);
        monthListView = findViewById(R.id.monthListView);
         nestedScrollView = findViewById(R.id.scrollView1);
        daysOfWeekDaily= findViewById(R.id.daysOfWeekMain);
        daysOfWeek = findViewById(R.id.daysOfWeek);
        prevMonth = findViewById(R.id.prevMonthButton);
        nextMonth = findViewById(R.id.nextMonthButton);

    }






    public void previousMonthAction(View view) {
        if (calendarRecyclerView.getVisibility() == View.GONE){

            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
            setDaily();
        }else if (calendarRecyclerView.getMeasuredHeight() < 301) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);

            setWeek();
        }
            else {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
                setMonthView();
            }



    }

    public void nextMonthAction(View view) {

        if (calendarRecyclerView.getVisibility() == View.GONE){
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
            setDaily();
        }else  if (calendarRecyclerView.getMeasuredHeight() < 301) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);

            setWeek();
        }else{
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
            setMonthView();
        }



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


            setDaily();
            drawerLayout.closeDrawer(GravityCompat.START);
            daysOfWeekDaily.setVisibility(View.VISIBLE);
            isDoubleClicked = false;
            handler.removeCallbacks(r);
        } else {
            isDoubleClicked = true;
            handler.postDelayed(r, 500);
        }

        if (date != null) {
            CalendarUtils.selectedDate = date;

            if (calendarRecyclerView.getMeasuredHeight() < 301) {
                setWeek();
            } else if (calendarRecyclerView.getVisibility() == View.GONE){
             setDaily();

                }else {
                setMonthView();

                if (position > 12) {
                    calendarRecyclerView.getLayoutManager().scrollToPosition(position);
                }
                }
            }
        }


    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();


        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);


        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarRecyclerView.setAdapter(calendarAdapter);
        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        params.height=1500;
        calendarRecyclerView.setLayoutParams(params);

        monthListView.setVisibility(View.GONE);
        monthYearText.setVisibility(View.VISIBLE);
        daysOfWeekDaily.setVisibility(View.GONE);
        daysOfWeek.setVisibility(View.VISIBLE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.VISIBLE);


    }


    private void setWeek()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        params.height=300;
        calendarRecyclerView.setLayoutParams(params);

        setEventListView();

        daysOfWeekDaily.setVisibility(View.GONE);
        monthListView.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.VISIBLE);
        daysOfWeek.setVisibility(View.VISIBLE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.VISIBLE);


    }

    private void setDaily()
    {
        Locale locale = new Locale("el", "GR");
        monthYearText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeekmain = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);

        setEventListView();
        daysOfWeekDaily.setText(dayOfWeekmain);

        monthListView.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.VISIBLE);
        daysOfWeekDaily.setVisibility(View.VISIBLE);
        daysOfWeek.setVisibility(View.GONE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.GONE);


    }

    private void setAllEvents()
    {
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(), myDB));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));

        hourAdapter.notifyDataSetChanged();
        ViewGroup.LayoutParams params = monthListView.getLayoutParams();
        params.height=1800;

        monthListView.setLayoutParams(params);

        monthListView.setAdapter(hourAdapter);

        monthListView.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.GONE);
        daysOfWeekDaily.setVisibility(View.GONE);
        daysOfWeek.setVisibility(View.GONE);
        prevMonth.setVisibility(View.GONE);
        nextMonth.setVisibility(View.GONE);
        calendarRecyclerView.setVisibility(View.GONE);
        hourAdapter.notifyDataSetChanged();
    }

    private void setEventListView() {
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(getApplicationContext(), myDB));


        hourAdapter.sort((o1, o2) -> o1.events.get(0).getDate().compareTo(o2.events.get(0).getDate()));
        hourAdapter.sort((o1, o2) -> o1.events.get(0).getTime().compareTo(o2.events.get(0).getTime()));
        monthListView.setAdapter(hourAdapter);


    }



    public void newEventAction() {
        startActivity(new Intent(this, EventEdit.class));
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

