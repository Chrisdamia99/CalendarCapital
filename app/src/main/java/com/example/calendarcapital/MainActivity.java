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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;


//Implements calendaradapter onitemlistener
public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, NavigationView.OnNavigationItemSelectedListener {
    //Declare 3 variables textview(monthyeartext), the RecyclerView(calendarrecycleview)
    //and a LocalDate which is selectedDate
    FloatingActionButton floatAddBtnMonthAdd;
    private TextView monthYearText, daysOfWeekDaily;
    LinearLayout daysOfWeek;
    private RecyclerView calendarRecyclerView;
    private ListView monthListView;
    HourAdapter hourAdapter;
    ImageButton prevMonth, nextMonth;
    ImageView imageMenu, backMenuBtn, refreshMenuBtn,searchMenuBtn;
    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    DrawerLayout drawerLayout;
    public static ArrayDeque<String> stack = new ArrayDeque<>();
    public static String getSaveStack;
    public static LocalDate tempSelectedDate;
    public static String viewNow;
    private boolean isSearchViewExpanded = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);


        setContentView(R.layout.activity_main);
//        setContentView(R.layout.test_lay_main);

        initWidgets();

            selectedDate = LocalDate.now();

        if (getIntent().hasExtra("tempDate")) {
            String intentTemp = getIntent().getStringExtra("tempDate");
            tempSelectedDate = stringToLocalDate(intentTemp);

        } else {
            tempSelectedDate = selectedDate;
        }

        stack.addFirst("month");
        monthListView.setVisibility(View.GONE);
        setNavigationViewListener();
        getIntentFromEventEdit();
        dublicatesInStack();
        DialogClickedItemAndDelete();
        myDB.removeDuplicateReminders();
        myDB.updateAlarmValueIfIdNotExists();


        drawerLayout = findViewById(R.id.drawerLayout);

        refreshMenuBtn.setOnClickListener(v -> {

                selectedDate = LocalDate.now();
                if (Objects.equals(viewNow, "month"))
                {
                    setMonthView();
                }else if (Objects.equals(viewNow, "week"))
                {
                    setWeek();
                } else if (Objects.equals(viewNow, "daily")) {
                    setDaily();
                }else
                {
                    setMonthView();
                }




        });


        backMenuBtn.setOnClickListener(v -> {
            tempSelectedDate = selectedDate;
            dublicatesInStack();
            onMyBackPressed();

        });


        floatAddBtnMonthAdd.setOnClickListener(v -> newEventAction());


        imageMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        searchMenuBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,SearchActivity.class);
            String stackNow = stack.peekFirst();
            i.putExtra("stack",stackNow);
            startActivity(i);
        });
prevMonth.setOnClickListener(v -> previousMonthAction());

nextMonth.setOnClickListener(v -> nextMonthAction());



        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        drawerLayout.closeDrawer(GravityCompat.START);


    }


    private void initWidgets() {

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        floatAddBtnMonthAdd = findViewById(R.id.floatAddBtnMonthView);
        monthListView = findViewById(R.id.monthListView);
        daysOfWeekDaily = findViewById(R.id.daysOfWeekMain);
        daysOfWeek = findViewById(R.id.daysOfWeek);
        prevMonth = findViewById(R.id.prevMonthButton);
        nextMonth = findViewById(R.id.nextMonthButton);
        imageMenu = findViewById(R.id.imageMenu);
        backMenuBtn = findViewById(R.id.BackMenuBtn);
        refreshMenuBtn = findViewById(R.id.refreshMenuBtn);
        searchMenuBtn = findViewById(R.id.searchMenuBtn);



    }



    //----------Stacks--------------------


    private void getIntentFromEventEdit() {

        if (getIntent().hasExtra("stack")) {

            getStackFromSave();
            selectedDate = tempSelectedDate;

        } else {
            selectedDate = tempSelectedDate;
            setMonthView();

        }

    }

    private void getStackFromSave() {


        if (getIntent().hasExtra("stack")) {
            getSaveStack = getIntent().getStringExtra("stack");
            selectedDate = tempSelectedDate;

            if (getSaveStack == null) {
                getSaveStack = "month";
            }
            switch (getSaveStack) {
                case "daily":
                case "double-click-week":
                case "double-click-month":
                    setDaily();
                    break;
                case "week":
                    setWeek();
                    break;
                case "all":
                    setAllEventsExListView();
                    break;
                case "month":
                    setMonthView();
                stack.clear();
                    break;

            }
        } else {
            selectedDate = tempSelectedDate;

            setMonthView();

            stack.addFirst("month");

        }


    }

    private void dublicatesInStack() {

        HashSet<String> set = new HashSet<>();

        // Step 2: Iterate over the original ArrayDeque and remove duplicates
        Iterator<String> iterator = stack.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (set.contains(word)) {
                iterator.remove();
            } else {
                set.add(word);
            }
        }
        int initialSize = stack.size();
        for (int i = 0; i < initialSize; i++) {
            String word = stack.removeFirst();
            if (set.contains(word)) {
                set.remove(word);
                stack.addLast(word);
            }
        }


    }

    private boolean backButtonPressedOnce = false;


    private void onMyBackPressed() {

        if (!stack.isEmpty())
        {
            stack.removeFirst();
        }

        String previousViewType = stack.peekFirst();

        if (calendarRecyclerView.getMeasuredHeight() < 401 && previousViewType == null) {

            stack.addLast("month");
            setMonthView();
        }
        if (previousViewType == null) {
            setMonthView();
        } else
        {
            switch (previousViewType) {
                case "daily":
                case "double-click-month":
                case "month":
                    selectedDate = tempSelectedDate;
                    setMonthView();
                    stack.clear();

                    break;
                case "week":
                case "double-click-week":

                    selectedDate = tempSelectedDate;
                    setWeek();


                    break;
                case "all":
                    setAllEventsExListView();
                    stack.removeFirst();

                    break;

            }
    }
    }


    private int clickCounter = 1;
    private LocalDate mySelectedDateClickCounter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(int position, LocalDate date) {

        LocalDate tempStartSeletedDate = selectedDate;
        if (date != null) {
            selectedDate = date;

            if (calendarRecyclerView.getMeasuredHeight() < 401) {

                clickCounter++;
                if (clickCounter == 1) {
                    mySelectedDateClickCounter = daysInWeekArray(selectedDate).get(position);
                }
                setWeek();


            } else if (calendarRecyclerView.getVisibility() == View.GONE) {
                setDaily();

            } else {

                Objects.requireNonNull(calendarRecyclerView.getAdapter()).notifyDataSetChanged();

                    if (!selectedDate.getMonth().equals(tempStartSeletedDate.getMonth()))
                    {
                        setMonthView();
                    }



                clickCounter++;

                if (clickCounter == 1) {
                    mySelectedDateClickCounter = daysInMonthArray().get(position);
                }

            }
        }


        if (clickCounter == 2 && selectedDate.equals(mySelectedDateClickCounter)) {

            clickCounter = 0;

            String previousViewType = stack.peekFirst();

            if (Objects.equals(previousViewType, "week")) {
                stack.addFirst("double-click-week");


            } else if (Objects.equals(previousViewType, "month")) {
                stack.addFirst("double-click-month");


            }

            setDaily();
            drawerLayout.closeDrawer(GravityCompat.START);
            daysOfWeekDaily.setVisibility(View.VISIBLE);
        } else {


            if (clickCounter >= 2) {
                clickCounter = 0;

            } else {
                clickCounter = 1;
            }

            dublicatesInStack();

        }


    }

    //-------------Exit from the app----------------
    @Override
    public void onBackPressed() {


        if (calendarRecyclerView.getMeasuredHeight() > 401 && calendarRecyclerView.getVisibility() == View.VISIBLE) {

            if (backButtonPressedOnce) {
                super.onBackPressed();
            } else {
                backButtonPressedOnce = true;
                Toast.makeText(this, "Πιέστε ξανά για έξοδο.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> backButtonPressedOnce = false, 2000); // 2 seconds delay to reset the boolean flag
            }
        } else {
            onMyBackPressed();
        }

    }

    //------------Resume-ConfigChanged------------------
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {



        } else {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!(hourAdapter == null)) {
            hourAdapter.notifyDataSetChanged();
        }




    }

    //---------Set HourAdapter Events--------------
    private void setEventListView() {
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(myDB));


            hourAdapter.sort(Comparator.comparing(o -> o.events.get(0).getDate()));

            hourAdapter.sort(Comparator.comparing(o -> o.events.get(0).getTime()));

        monthListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        monthListView.setAdapter(hourAdapter);

        myDB.close();
    }

    //----------DialogsFor delete Event--------------------
    private void DialogClickedItemAndDelete() {
        EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(), myDB.readAllEvents());

        monthListView.setOnItemClickListener((parent, view, position, id) -> {
            @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
            HourEvent myEvent = (HourEvent) monthListView.getAdapter().getItem(position);
            LayoutInflater lf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = lf.inflate(R.layout.repeating_events_alert_dialog, null);

            TextView deleteAll = rowView.findViewById(R.id.deleteAll);
            TextView deleteOne = rowView.findViewById(R.id.deleteOne);
            TextView deleteFuture = rowView.findViewById(R.id.deleteFuture);


            String myEventId = myEvent.getEvents().get(0).getId();
            String myTitle = myEvent.getEvents().get(0).getName();
            String myComment = myEvent.getEvents().get(0).getComment();
            String myDate = String.valueOf(myEvent.getEvents().get(0).getDate());
            String myTime = String.valueOf(myEvent.getEvents().get(0).getTime());
            String location = myEvent.getEvents().get(0).getLocation();
            LocalDate EventDate = myEvent.getEvents().get(0).getDate();


            View viewFinal;
            String title_upd = hourAdapter.getItem(position).getEvents().get(0).getName();


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            AlertDialog builderRepeatingDelete = new AlertDialog.Builder(MainActivity.this).setView(rowView).setTitle("Διαγραφή συμβάντος").create();


            viewFinal = CA.setAllFields(view1, myEventId, myTitle, myComment, myDate, myTime,location);


            builder.setView(viewFinal).

                    setPositiveButton("Delete", (dialog, which) -> {

                        AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                        builderDel.setPositiveButton("Yes", (dialog1, which1) -> {
                            String parent_id_value = hourAdapter.getItem(position).getEvents().get(0).getParent_id();
                            String row_id = hourAdapter.getItem(position).getEvents().get(0).getId();


                            if (parent_id_value == null && !myDB.checkNextRowHasParentId(Integer.parseInt(row_id))) {
                                deleteEventNotRepeating(position, CA);
                            } else {
                                deleteEventIfRepeating(builderRepeatingDelete, position, CA, deleteAll, deleteOne, deleteFuture);
                                builderRepeatingDelete.show();
                            }


                        }).setNegativeButton("No", (dialog12, which12) -> {
                            String previousViewType = stack.peekFirst();
                            switch (Objects.requireNonNull(previousViewType)) {
                                case "all":
                                    setAllEventsExListView();
                                    break;
                                case "double-click-week":
                                case "month":

                                case "double-click-month":
                                case "daily":
                                    selectedDate = EventDate;

                                    setDaily();
                                    break;

                                case "week":
                                    selectedDate = EventDate;

                                    setWeek();
                                    break;

                                default:
                                    selectedDate = EventDate;

                                    onMyBackPressed();
                                    break;
                            }

                        }).setTitle("Are you sure you want to delete event " + title_upd + " ?");
                        builderDel.show();


                    }).setNegativeButton("Exit", (dialog, which) -> dialog.cancel()).setNeutralButton("Edit", (dialog, which) -> {
                        Intent i = new Intent(MainActivity.this, Edit_Update_Activity.class);

                        String row_id = hourAdapter.getItem(position).getEvents().get(0).getId();


                        i.putExtra("id", row_id);
                        String stackNow = stack.peekFirst();
                        i.putExtra("stack", stackNow);
                        startActivity(i);
                    }).setOnCancelListener(dialog -> hourAdapter.notifyDataSetChanged()).setOnDismissListener(dialog -> hourAdapter.notifyDataSetChanged());


            builder.show();


        });


    }

    private void deleteEventIfRepeating(AlertDialog builderRepeating, int position, EventCursorAdapter CA, TextView deleteAll, TextView deleteOne, TextView deleteFuture) {

        builderRepeating.setOnShowListener(dialog -> {
            deleteAll.setOnClickListener(v -> {
                String parent_id = hourAdapter.getItem(position).getEvents().get(0).getParent_id();
                String row_id = hourAdapter.getItem(position).getEvents().get(0).getId();
                LocalDate EventDate = hourAdapter.getItem(position).getEvents().get(0).getDate();

                Cursor cursorEvent = myDB.readAllEvents();
                Cursor remCursor = myDB.readAllReminder();


                if (parent_id == null) {
                    cursorEvent.moveToPosition(-1);
                    while (cursorEvent.moveToNext()) {
                        remCursor.moveToPosition(-1);
                        while (remCursor.moveToNext()) {

                            if (cursorEvent.getString(0).equals(row_id) &&
                                    cursorEvent.getString(5).equals("1")) {

                                if (remCursor.getString(1).equals(row_id)) {

                                    myDB.deleteOneRowReminder(remCursor.getString(0));
                                    cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                                }

                            }


                            if (!(cursorEvent.getString(7) == null) &&
                                    cursorEvent.getString(7).equals(row_id) &&
                                    cursorEvent.getString(5).equals("1")) {

                                if (remCursor.getString(1).equals(cursorEvent.getString(0))) {

                                    myDB.deleteOneRowReminder(remCursor.getString(0));
                                    cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                                }
                            }


                        }

                    }


                } else {

                    cursorEvent.moveToPosition(-1);
                    while (cursorEvent.moveToNext()) {
                        remCursor.moveToPosition(-1);
                        while (remCursor.moveToNext()) {
                            if (!(cursorEvent.getString(7) == null) && cursorEvent.getString(7).equals(parent_id) &&
                                    cursorEvent.getString(5).equals("1")) {
                                if (remCursor.getString(1).equals(cursorEvent.getString(0))) {
                                    myDB.deleteOneRowReminder(remCursor.getString(0));
                                    cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                                }
                            } else if (cursorEvent.getString(7) == null &&
                                    cursorEvent.getString(0).equals(parent_id) && cursorEvent.getString(5).equals("1")) {
                                if (remCursor.getString(1).equals(cursorEvent.getString(0))) {
                                    myDB.deleteOneRowReminder(remCursor.getString(0));
                                    cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                                }
                            }

                        }

                    }


                }


                CA.notifyDataSetChanged();
                hourAdapter.notifyDataSetChanged();

                if (parent_id == null) {
                    myDB.deleteAllEventsParentId(row_id);
                    myDB.deleteOneRow(row_id);
                } else {
                    myDB.deleteAllEventsParentId(parent_id);
                    myDB.deleteOneRow(parent_id);
                }

                cursorEvent.close();
                remCursor.close();
                myDB.close();


                String previousViewType = stack.peekFirst();
                if (previousViewType==null)
                {
                    previousViewType="daily";
                }
                switch (Objects.requireNonNull(previousViewType)) {
                    case "all":
                        setAllEventsExListView();
                        break;
                    case "double-click-week":
                    case "month":

                    case "double-click-month":
                    case "daily":
                        selectedDate = EventDate;

                        setDaily();
                        break;

                    case "week":
                        selectedDate = EventDate;

                        setWeek();
                        break;

                    default:
                        selectedDate = EventDate;

                        onMyBackPressed();
                        break;
                }
                dialog.dismiss();
            });

            deleteOne.setOnClickListener(v -> {

                String event_repeating_id = hourAdapter.getItem(position).getEvents().get(0).getId();
                LocalDate EventDate = hourAdapter.getItem(position).getEvents().get(0).getDate();
                String event_rep_parent_id = hourAdapter.getItem(position).getEvents().get(0).getParent_id();


                Cursor eventCursor = myDB.readAllEvents();
                Cursor newEventCursor = myDB.readAllEvents();

                Cursor remCursor = myDB.readAllReminder();
                int tempIdForNewParent = 0;
                int repeat = 0;
                if (event_rep_parent_id==null)
                {
                    eventCursor.moveToPosition(-1);
                    while(eventCursor.moveToNext())
                    {
                        if (eventCursor.getString(0).equals(event_repeating_id))
                        {
                            repeat= Integer.parseInt(eventCursor.getString(6));
                        }
                        if (!(eventCursor.getString(7)==null) && eventCursor.getString(7).equals(event_repeating_id))
                        {
                            tempIdForNewParent = Integer.parseInt(eventCursor.getString(0));
                            myDB.updateEventParentId(String.valueOf(tempIdForNewParent),null);
                            myDB.updateEventRepeat(String.valueOf(tempIdForNewParent), String.valueOf(repeat));
                            myDB.deleteOneRow(event_repeating_id);
                            break;
                        }
                    }

                    newEventCursor.moveToPosition(-1);
                    while(newEventCursor.moveToNext())
                    {
                        if (!(newEventCursor.getString(7)==null) && newEventCursor.getString(7).equals(event_repeating_id))
                        {
                            myDB.updateEventParentId(newEventCursor.getString(0), String.valueOf(tempIdForNewParent));
                            myDB.updateEventRepeat(newEventCursor.getString(0), String.valueOf(0));
                        }
                    }
                }else
                {
                    myDB.deleteOneRow(event_repeating_id);
                }

                remCursor.moveToPosition(-1);
                while (remCursor.moveToNext()) {
                    if (remCursor.getString(1).equals(event_repeating_id)) {
                        myDB.deleteOneRowReminder(remCursor.getString(0));
                        cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                    }
                }


                CA.notifyDataSetChanged();
                hourAdapter.notifyDataSetChanged();
                newEventCursor.close();
                eventCursor.close();
                remCursor.close();
                myDB.close();

                String previousViewType = stack.peekFirst();
                if (previousViewType==null)
                {
                    previousViewType="daily";
                }
                switch (Objects.requireNonNull(previousViewType)) {
                    case "all":
                        setAllEventsExListView();
                        break;
                    case "double-click-week":
                    case "month":

                    case "double-click-month":
                    case "daily":
                        selectedDate = EventDate;

                        setDaily();
                        break;

                    case "week":
                        selectedDate = EventDate;

                        setWeek();
                        break;

                    default:
                        selectedDate = EventDate;

                        onMyBackPressed();
                        break;
                }
                dialog.dismiss();
            });

            deleteFuture.setOnClickListener(v -> {
                String parent_id = hourAdapter.getItem(position).getEvents().get(0).getParent_id();
                String row_id = hourAdapter.getItem(position).getEvents().get(0).getId();
                LocalDate event_date = hourAdapter.getItem(position).getEvents().get(0).getDate();


                Cursor cursorEvent = myDB.readAllEvents();
                Cursor remCursor = myDB.readAllReminder();

                if (parent_id == null) {
                    myDB.deleteAllEventsParentId(row_id);
                    myDB.deleteOneRow(row_id);

                } else {

                    cursorEvent.moveToPosition(-1);
                    while (cursorEvent.moveToNext()) {
                        LocalDate cursorLocalDate = stringToLocalDate(cursorEvent.getString(3));
                        String cursorParentID = cursorEvent.getString(7);
                        int comparisonLocalDates = 0;
                            comparisonLocalDates = event_date.compareTo(cursorLocalDate);

                        if (!(cursorParentID == null) && cursorParentID.equals(parent_id)) {

                            if (comparisonLocalDates < 0 || event_date.equals(cursorLocalDate)) {
                                if (cursorEvent.getString(5).equals("1")) {
                                    remCursor.moveToPosition(-1);
                                    while (remCursor.moveToNext()) {
                                        if (remCursor.getString(1).equals(cursorEvent.getString(0))) {
                                            myDB.deleteOneRowReminder(remCursor.getString(0));
                                        }

                                    }
                                }

                                myDB.deleteOneRow(cursorEvent.getString(0));
                            }
                        }
                    }

                }


                cursorEvent.close();
                remCursor.close();
                myDB.close();

                String previousViewType = stack.peekFirst();
                if (previousViewType==null)
                {
                    previousViewType="daily";
                }
                switch (Objects.requireNonNull(previousViewType)) {
                    case "all":
                        setAllEventsExListView();
                        break;
                    case "double-click-week":
                    case "month":
                    case "daily":

                    case "double-click-month":
                        selectedDate = event_date;

                        setDaily();
                        break;

                    case "week":
                        selectedDate = event_date;

                        setWeek();
                        break;

                    default:
                        selectedDate = event_date;

                        onMyBackPressed();
                        break;
                }
                dialog.dismiss();
            });
        });
    }

    private void deleteEventNotRepeating(int position, EventCursorAdapter CA) {
        String id_row = hourAdapter.getItem(position).getEvents().get(0).getId();
        String title_row = hourAdapter.getItem(position).getEvents().get(0).getName();
        String comment_row = hourAdapter.getItem(position).getEvents().get(0).getComment();
        LocalDate date_row = hourAdapter.getItem(position).getEvents().get(0).getDate();

        Cursor remCursor = myDB.readAllReminder();

        Cursor eventsCursor = myDB.readAllEvents();

        eventsCursor.moveToPosition(-1);
        while (eventsCursor.moveToNext()) {
            String id_event = eventsCursor.getString(0);
            String event_title = eventsCursor.getString(1);
            String event_comment = eventsCursor.getString(2);
            String event_date = eventsCursor.getString(3);

            if (id_event.equals(id_row) && event_title.equals(title_row) && event_comment.equals(comment_row) &&
                    date_row.equals(stringToLocalDate(event_date))) {
                myDB.deleteOneRow(id_row);
                break;
            }

        }


        remCursor.moveToPosition(-1);
        while (remCursor.moveToNext()) {
            if (remCursor.getString(1).equals(id_row)) {
                myDB.deleteOneRowReminder(remCursor.getString(0));

                String alarmId = remCursor.getString(0);
                cancelAlarm(Integer.parseInt(alarmId));
            }
            CA.notifyDataSetChanged();
            hourAdapter.notifyDataSetChanged();
        }


        eventsCursor.close();
        remCursor.close();
        myDB.close();

        String previousViewType = stack.peekFirst();
        if (previousViewType==null)
        {
            previousViewType="daily";
        }
        switch (Objects.requireNonNull(previousViewType)) {
            case "all":
                setAllEventsExListView();
                break;
            case "double-click-week":
            case "month":

            case "double-click-month":
            case "daily":
                selectedDate = date_row;

                setDaily();
                break;

            case "week":
                selectedDate = date_row;

                setWeek();
                break;

            default:
                selectedDate = date_row;

                onMyBackPressed();
                break;
        }
    }


    //-------------Cancel Alarm---------------------------
    private void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


    }


    //--------------Previous and Next Month Actions---------
    public void previousMonthAction() {
        if (calendarRecyclerView.getVisibility() == View.GONE) {

                selectedDate = selectedDate.minusDays(1);

            setDaily();
        } else if (calendarRecyclerView.getMeasuredHeight() < 301) {
                selectedDate = selectedDate.minusWeeks(1);


            setWeek();
        } else {
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();



        }


    }

    public void nextMonthAction() {

        if (calendarRecyclerView.getVisibility() == View.GONE) {
                selectedDate = selectedDate.plusDays(1);

            setDaily();
        } else if (calendarRecyclerView.getMeasuredHeight() < 301) {
                selectedDate = selectedDate.plusWeeks(1);


            setWeek();
        } else {
                selectedDate = selectedDate.plusMonths(1);


            setMonthView();
            }

        }





    //--------------Set Views--------------------------------
    public void removeExtraDateIntent()
    {
        if (getIntent().hasExtra("date")) {
            Bundle b = getIntent().getExtras();

                selectedDate = (LocalDate) b.get("date");

            getIntent().removeExtra("date");

        }
    }
    public void setCalendarRecyclerViewMonth()
    {
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);


        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarRecyclerView.setAdapter(calendarAdapter);
        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) calendarRecyclerView.getLayoutParams();

        params.height = 1700;
        marginParams.bottomMargin = 100;

        calendarRecyclerView.setLayoutParams(params);
        calendarRecyclerView.setLayoutParams(marginParams);
    }
    private void setCalendarRecyclerViewWeek()
    {
        ArrayList<LocalDate> days = daysInWeekArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        ViewGroup.LayoutParams params = calendarRecyclerView.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) calendarRecyclerView.getLayoutParams();

        params.height = 300;
        marginParams.bottomMargin = 0;

        calendarRecyclerView.setLayoutParams(params);
        calendarRecyclerView.setLayoutParams(marginParams);
    }

    //MonthView
    private void setMonthView() {
        viewNow ="month";
        RecyclerViewActions.removeExtraDateIntent(this);
        monthYearText.setText(monthYearFromDate(selectedDate));
        RecyclerViewActions.setCalendarRecyclerViewMonth(this,getApplicationContext(),calendarRecyclerView);
        setVisibilityMonth();

    }

    public void setVisibilityMonth()
    {
    imageMenu.setVisibility(View.VISIBLE);
    monthListView.setVisibility(View.GONE);
    monthYearText.setVisibility(View.VISIBLE);
    daysOfWeekDaily.setVisibility(View.GONE);
    daysOfWeek.setVisibility(View.VISIBLE);
    prevMonth.setVisibility(View.VISIBLE);
    nextMonth.setVisibility(View.VISIBLE);
    calendarRecyclerView.setVisibility(View.VISIBLE);
    backMenuBtn.setVisibility(View.GONE);
}


    //WeeekView

    private void setWeek() {

        viewNow="week";
        RecyclerViewActions.removeExtraDateIntent(this);
        monthYearText.setText(monthYearFromDate(selectedDate));
        RecyclerViewActions.setCalendarRecyclerViewWeek(this,getApplicationContext(),calendarRecyclerView);
        setParamsMonthListViewWeek();


        setVisibilityWeek();
        setEventListView();

    }

    private void setParamsMonthListViewWeek()
    {
        ViewGroup.LayoutParams paramsListView = monthListView.getLayoutParams();
        paramsListView.height = 1200;
        monthListView.setLayoutParams(paramsListView);
    }

    public void setVisibilityWeek()
    {
        backMenuBtn.setVisibility(View.VISIBLE);
        imageMenu.setVisibility(View.GONE);
        daysOfWeekDaily.setVisibility(View.GONE);
        monthListView.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.VISIBLE);
        daysOfWeek.setVisibility(View.VISIBLE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.VISIBLE);
    }


    //DailyView

    @SuppressLint("RtlHardcoded")
    private void setDaily() {
        viewNow="daily";

        RecyclerViewActions.removeExtraDateIntent(this);

        Locale locale = new Locale("el", "GR");
        monthYearText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeekmain = null;
            dayOfWeekmain = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);

        daysOfWeekDaily.setText(dayOfWeekmain);
    setParamsMonthListViewDaily();

        setVisibilityDaily();
        setEventListView();
    }
    private void setParamsMonthListViewDaily()
    {
        ViewGroup.LayoutParams paramsListView = monthListView.getLayoutParams();
        paramsListView.height = 1500;

        monthListView.setLayoutParams(paramsListView);
    }

    public void setVisibilityDaily()
    {
        backMenuBtn.setVisibility(View.VISIBLE);
        imageMenu.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            backMenuBtn.setForegroundGravity(Gravity.START);
        }

        monthListView.setVisibility(View.VISIBLE);
        monthYearText.setVisibility(View.VISIBLE);
        daysOfWeekDaily.setVisibility(View.VISIBLE);
        daysOfWeek.setVisibility(View.GONE);
        prevMonth.setVisibility(View.VISIBLE);
        nextMonth.setVisibility(View.VISIBLE);
        calendarRecyclerView.setVisibility(View.GONE);
//        hourAdapter.notifyDataSetChanged();
    }

    //AllEventsExpandable
    private void setAllEventsExListView() {
        Intent i = new Intent(this, AllEventsExListView.class);
        String stackNow = stack.peekFirst();
        i.putExtra("stack", stackNow);
        startActivity(i);

    }



    //---------Add Event-------------------------------------
    private void newEventAction() {
        Intent saveIntent = new Intent(this, EventEdit.class);
        String stackNow = stack.peekFirst();
//        changeEventEdit = false;
        saveIntent.putExtra("stack", stackNow);
        startActivity(saveIntent);
    }


    //--------OnNavigSelected Stack------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuSchedule) {
            // Handle menuSchedule
            setAllEventsExListView();
            stack.addFirst("all");
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.daysView) {
            // Handle daysView
            setDaily();
            stack.addFirst("daily");
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.weekView) {
            // Handle weekView
            setWeek();
            stack.addFirst("week");
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.monthView) {
            // Handle monthView
            selectedDate = tempSelectedDate;
            setMonthView();
            stack.addFirst("month");
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.refreshItem) {
            // Handle refreshItem
            finish();
            startActivity(getIntent());
        } else if (itemId == R.id.syncItem) {
            Toast.makeText(this, "SYNCING...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ERROR NAVIGATION", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }


}

