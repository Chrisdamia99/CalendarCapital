package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AllEventsExListView extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db ;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;
    private List<Event> listDataHeader;
    private HashMap<Event, List<Event>> listDataChild;
    ImageView allEventsBackButton,allEventsRefreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events_ex_list_view);
        initWidgets();
        dbHelper = new MyDatabaseHelper(this);

        db = dbHelper.getWritableDatabase();

        expandableListView = findViewById(R.id.AllEventsExListView);
        prepareListData();


    listAdapter = new EventExpandableListAdapter(this,  listDataHeaderEvent(), listDataChildEvent(),
                AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(),dbHelper),
                AllEventsList.hourEventListONLYRepeating(getApplicationContext(),dbHelper));
        expandableListView.setAdapter(listAdapter);


        expandableListView.setGroupIndicator(null);
        allEventsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(AllEventsExListView.this, MainActivity.class);
                Boolean myBool = true;
                i.putExtra("bool", myBool);

                startActivity(i);
            }
        });

        allEventsRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllEventsList.reloadActivity(AllEventsExListView.this);

            }
        });

    }

    private void initWidgets()
    {
        allEventsBackButton = findViewById(R.id.allEventsBackButton);
        allEventsRefreshButton = findViewById(R.id.allEventsRefreshButton);
    }
    public static void reloadActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);

    }


    private List<Event> listDataHeaderEvent()
    {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        String selectQuery = "SELECT * FROM " + MyDatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID)));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
                @SuppressLint("Range") String comment = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_COMMENT));
                @SuppressLint("Range") LocalDate date = CalendarUtils.stringToLocalDate(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DATE)));
                @SuppressLint("Range") LocalTime time = LocalTime.parse(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME)));
                @SuppressLint("Range") String alarm = String.valueOf(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ALARM)));
                @SuppressLint("Range") String parentId = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PARENT_ID)));

                Event event = new Event(id, title, comment, date, time, alarm, parentId);

                if (parentId.equals("0")) {
                    listDataHeader.add(event);
                } else {
                    for (Event parent : listDataHeader) {
                        if (Objects.equals(parent.getId(), parentId)) {
                            List<Event> childList = listDataChild.get(parent);
                            if (childList == null) {
                                childList = new ArrayList<>();
                                listDataChild.put(parent, childList);
                            }
                            childList.add(event);
                        }
                    }
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listDataHeader;
    }

    private HashMap<Event, List<Event>> listDataChildEvent()
    {    listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        String selectQuery = "SELECT * FROM " + MyDatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID)));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
                @SuppressLint("Range") String comment = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_COMMENT));
                @SuppressLint("Range") LocalDate date = CalendarUtils.stringToLocalDate(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DATE)));
                @SuppressLint("Range") LocalTime time = LocalTime.parse(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME)));
                @SuppressLint("Range") String alarm = String.valueOf(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ALARM)));
                @SuppressLint("Range") String parentId = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PARENT_ID)));

                Event event = new Event(id, title, comment, date, time, alarm, parentId);

                if (parentId.equals("0")) {
                    listDataHeader.add(event);
                } else {
                    for (Event parent : listDataHeader) {
                        if (Objects.equals(parent.getId(), parentId)) {
                            List<Event> childList = listDataChild.get(parent);
                            if (childList == null) {
                                childList = new ArrayList<>();
                                listDataChild.put(parent, childList);
                            }
                            childList.add(event);
                        }
                    }
                }
            } while (cursor.moveToNext());
        }

        cursor.close();


        return listDataChild;
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        String selectQuery = "SELECT * FROM " + MyDatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID)));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
                @SuppressLint("Range") String comment = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_COMMENT));
                @SuppressLint("Range") LocalDate date = CalendarUtils.stringToLocalDate(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DATE)));
                @SuppressLint("Range") LocalTime time = LocalTime.parse(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME)));
                @SuppressLint("Range") String alarm = String.valueOf(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ALARM)));
                @SuppressLint("Range") String parentId = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PARENT_ID)));

                Event event = new Event(id, title, comment, date, time, alarm, parentId);

                if (parentId.equals("0")) {
                    listDataHeader.add(event);
                } else {
                    for (Event parent : listDataHeader) {
                        if (Objects.equals(parent.getId(), parentId)) {
                            List<Event> childList = listDataChild.get(parent);
                            if (childList == null) {
                                childList = new ArrayList<>();
                                listDataChild.put(parent, childList);
                            }
                            childList.add(event);
                        }
                    }
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
    }
}