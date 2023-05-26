package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;
import static com.example.calendarcapital.CalendarUtils.stringToLocalDate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    ListView searchListView;
    ImageView BackMenuBtnSearch,refreshMenuBtnSearch;

    HourAdapter hourAdapter;
    String stackNow;
    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);

    private static String newTextSearchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initWidgets();
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newTextSearchBar=newText;
                if (newText.isEmpty())
                {
                    if (!(hourAdapter==null))
                    {
                        hourAdapter.clear();
                    }

                }else
                {
                    filterList(newText);
                }


                return false;
            }
        });



        setIntentFromMainActivity();
        BackMenuBtnSearch.setOnClickListener(v -> {
            Intent i = new Intent(SearchActivity.this, MainActivity.class);

            i.putExtra("stack", stackNow);

            String myTemp = CalendarUtils.selectedDate.toString();
            i.putExtra("tempDate", myTemp);

            startActivity(i);

        });
        refreshMenuBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllEventsExListView.reloadActivity(SearchActivity.this);
            }
        });

    }

    private void initWidgets()
    {
        searchView = findViewById(R.id.searchView);
        searchListView = findViewById(R.id.searchRecylerView);
        BackMenuBtnSearch = findViewById(R.id.BackMenuBtnSearch);
        refreshMenuBtnSearch = findViewById(R.id.refreshMenuBtnSearch);

    }

    private void setIntentFromMainActivity()
    {
        if (getIntent().hasExtra("stack")) {
            stackNow = getIntent().getStringExtra("stack");
        }
    }

    private void filterList(String text)
    {

        ArrayList<HourEvent> filteredList = new ArrayList<>();

        ArrayList<HourEvent> test = AllEventsList.hourEventListAllEventsAndRepeatingEvents(getApplicationContext(),myDB);

        if(!test.isEmpty())
        {
            for (int i=0; i<test.size(); i++)
            {
                if (test.get(i).getEvents().get(0).getName().toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(test.get(i));
                }
            }
        }else
        {
            Toast.makeText(this, "Δεν υπάρχουν διαθέσιμα γεγονότα.", Toast.LENGTH_SHORT).show();
        }


        if (filteredList.isEmpty())
        {
            Toast.makeText(this, "Δεν υπάρχουν διαθέσιμα γεγονότα.", Toast.LENGTH_SHORT).show();
            AllEventsExListView.reloadActivity(SearchActivity.this);
        }else
        {
            hourAdapter = new HourAdapter(getApplicationContext(), filteredList);
            searchListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
            searchListView.setAdapter(hourAdapter);
        }


    }

    private void searchClickListener()
    {
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(), myDB.readAllEvents());

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
                HourEvent myEvent = (HourEvent) parent.getAdapter().getItem(position);
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
                LocalDate EventDate = myEvent.getEvents().get(0).getDate();


                View viewFinal;
                String title_upd = hourAdapter.getItem(position).getEvents().get(0).getName();


                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                AlertDialog builderRepeatingDelete = new AlertDialog.Builder(SearchActivity.this).setView(rowView).setTitle("Διαγραφή συμβάντος").create();


                viewFinal = CA.setAllFields(view1, myEventId, myTitle, myComment, myDate, myTime);


                builder.setView(viewFinal).

                        setPositiveButton("Delete", (dialog, which) -> {

                            AlertDialog.Builder builderDel = new AlertDialog.Builder(SearchActivity.this);
                            builderDel.setPositiveButton("Yes", (dialog1, which1) -> {
                                String parent_id_value = hourAdapter.getItem(position).getEvents().get(0).getParent_id();
                                String row_id = hourAdapter.getItem(position).getEvents().get(0).getId();


                                if (parent_id_value == null && !myDB.checkNextRowHasParentId(Long.parseLong(row_id))) {
                                    deleteEventNotRepeating(position, CA);
                                } else {
                                    deleteEventIfRepeating(builderRepeatingDelete, position, CA, deleteAll, deleteOne, deleteFuture);
                                    builderRepeatingDelete.show();
                                }


                            }).setNegativeButton("No", (dialog12, which12) -> {


                            }).setTitle("Are you sure you want to delete event " + title_upd + " ?");
                            builderDel.show();


                        }).setNegativeButton("Exit", (dialog, which) -> dialog.cancel()).setNeutralButton("Edit", (dialog, which) -> {
                            Intent i = new Intent(SearchActivity.this, Edit_Update_Activity.class);

                            String row_id = hourAdapter.getItem(position).getEvents().get(0).getId();


                            i.putExtra("id", row_id);

                            startActivity(i);
                        }).setOnCancelListener(dialog -> hourAdapter.notifyDataSetChanged()).setOnDismissListener(dialog -> hourAdapter.notifyDataSetChanged());


                builder.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(hourAdapter == null)) {
            hourAdapter.notifyDataSetChanged();
        }
        searchClickListener();
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


                filterList(newTextSearchBar);
                dialog.dismiss();
            });

            deleteOne.setOnClickListener(v -> {

                String event_repeating_id = hourAdapter.getItem(position).getEvents().get(0).getId();
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

                filterList(newTextSearchBar);
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            comparisonLocalDates = event_date.compareTo(cursorLocalDate);
                        }
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

                filterList(newTextSearchBar);
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
        filterList(newTextSearchBar);
    }
    private void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


    }

}