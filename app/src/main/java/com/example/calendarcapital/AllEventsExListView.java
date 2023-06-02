package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.stringToLocalDate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AllEventsExListView extends AppCompatActivity  {
    private MyDatabaseHelper dbHelper;
    private FloatingActionButton floatAddBtnMonthViewExList;
    private SQLiteDatabase db ;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;
    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    HourAdapter hourAdapter;
    private List<Event> listDataHeader;
    private HashMap<Event, List<Event>> listDataChild;
    ImageView allEventsBackButton,allEventsRefreshButton;
    private static int groupPos,childPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events_ex_list_view);
        initWidgets();
        dbHelper = new MyDatabaseHelper(this);

        db = dbHelper.getWritableDatabase();

        expandableListView = findViewById(R.id.AllEventsExListView);
        prepareListData();
        hourAdapter = new HourAdapter(getApplicationContext(), AllEventsList.hourEventListFromDatabase(myDB));
        setExListAdapter();







        expandableListView.setGroupIndicator(null);
        allEventsBackButton.setOnClickListener(v -> {

            Intent i = new Intent(AllEventsExListView.this, MainActivity.class);
            Boolean myBool = true;
            i.putExtra("bool", myBool);

            startActivity(i);
        });
        allEventsRefreshButton.setOnClickListener(v -> AllEventsList.reloadActivity(AllEventsExListView.this));
        floatAddBtnMonthViewExList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent saveIntent = new Intent(AllEventsExListView.this, EventEdit.class);

                    startActivity(saveIntent);

            }
        });

    }

    private void initWidgets()
    {
        allEventsBackButton = findViewById(R.id.allEventsBackButton);
        allEventsRefreshButton = findViewById(R.id.allEventsRefreshButton);
        floatAddBtnMonthViewExList = findViewById(R.id.floatAddBtnMonthViewExList);
    }
    public static void reloadActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);

    }

    private void setExListAdapter()
    {
        listAdapter = new EventExpandableListAdapter(this,  listDataHeaderEvent(), listDataChildEvent(),
                AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(),dbHelper),
                AllEventsList.hourEventListONLYRepeating(getApplicationContext(),dbHelper));
        expandableListView.setAdapter(listAdapter);
    }

    private void setExListAdapterAfterDelete()
    {
        listAdapter = new EventExpandableListAdapter(this,  listDataHeaderEvent(), listDataChildEvent(),
                AllEventsList.hourEventListFromDatabaseToShowAllEvents(getApplicationContext(),dbHelper),
                AllEventsList.hourEventListONLYRepeating(getApplicationContext(),dbHelper));
        expandableListView.setAdapter(listAdapter);
if (listAdapter.isEmpty())
{
    reloadActivity(AllEventsExListView.this);
}else
{
    expandableListView.expandGroup(groupPos);
    if (childPos>1)
    {
        expandableListView.setSelectedChild(groupPos,childPos-1,true);
    }else
    {
        expandableListView.setSelectedGroup(groupPos);
    }
}



    }

    private void ClickListenerExListViewGroup()
    {
        expandableListView.setOnGroupClickListener((parent, v, groupPosition,  id) -> {
            groupPos=groupPosition;


            ExpandableListAdapter adapter = parent.getExpandableListAdapter();
            int childCount = adapter.getChildrenCount(groupPosition);

            if (childCount == 0) {
                hourAdapterClickActionGroup(groupPosition,parent);
            } else {
                if (parent.collapseGroup(groupPosition))
                {
                    ClickListenerExListViewChild();
                }else
                {
                    hourAdapterClickActionGroup(groupPosition,parent);
                }

            }







            return false;

        });

    }

    private void ClickListenerExListViewChild()
    {


            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    groupPos=groupPosition;
                    childPos=childPosition;
                    hourAdapterClickActionChild( groupPosition, childPosition, parent);

                    return false;
                }
            });






    }



private void hourAdapterClickActionChild(int groupPosition,int childPosition,ExpandableListView parent)
{
    EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(), myDB.readAllEvents());

    @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
    HourEvent myEvent = (HourEvent) listAdapter.getChild(groupPosition, childPosition);
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
    String parent_id = myEvent.getEvents().get(0).getParent_id();
    String location = myEvent.getEvents().get(0).getLocation();

    View viewFinal;


    AlertDialog.Builder builder = new AlertDialog.Builder(AllEventsExListView.this);
    AlertDialog builderRepeatingDelete = new AlertDialog.Builder(AllEventsExListView.this).setView(rowView).setTitle("Διαγραφή συμβάντος").create();


    viewFinal = CA.setAllFields(view1, myEventId, myTitle, myComment, myDate, myTime,location);


    builder.setView(viewFinal).

            setPositiveButton("Delete", (dialog, which) -> {

                AlertDialog.Builder builderDel = new AlertDialog.Builder(AllEventsExListView.this);
                builderDel.setPositiveButton("Yes", (dialog1, which1) -> {
                    String parent_id_value = myEvent.getEvents().get(0).getParent_id();
                    String row_id = myEvent.getEvents().get(0).getId();
                    LocalDate event_date = myEvent.getEvents().get(0).getDate();

                    if (parent_id_value == null && !myDB.checkNextRowHasParentId(Integer.parseInt(row_id))) {
                        deleteEventNotRepeating(CA,row_id,myTitle,myComment,event_date);
                    } else {
                        deleteEventIfRepeating(builderRepeatingDelete,CA, deleteAll, deleteOne, deleteFuture,row_id,parent_id,event_date);
                        builderRepeatingDelete.show();
                    }


                }).setNegativeButton("No", (dialog12, which12) -> dialog12.cancel()).setTitle("Are you sure you want to delete event " + myTitle + " ?");
                builderDel.show();


            }).setNegativeButton("Exit", (dialog, which) -> dialog.dismiss()).setNeutralButton("Edit", (dialog, which) -> {
                Intent i = new Intent(AllEventsExListView.this, Edit_Update_Activity.class);

                String row_id = ((HourEvent) parent.getAdapter().getItem(childPosition)).getEvents().get(0).getId();


                i.putExtra("id", row_id);
                String stackNow = "all";
                i.putExtra("stack", stackNow);
                startActivity(i);
            }).setOnCancelListener(dialog -> hourAdapter.notifyDataSetChanged()).setOnDismissListener(dialog -> hourAdapter.notifyDataSetChanged() );




    builder.show();
}
private void hourAdapterClickActionGroup(int groupPosition,ExpandableListView parent)
{
    EventCursorAdapter CA = new EventCursorAdapter(getApplicationContext(), myDB.readAllEvents());

    groupPos=groupPosition;
    @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.show_event_from_listview, null);
    HourEvent myEvent = (HourEvent) listAdapter.getGroup(groupPosition);
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
    String parent_id = myEvent.getEvents().get(0).getParent_id();
    String location = myEvent.getEvents().get(0).getLocation();

    View viewFinal;


    AlertDialog.Builder builder = new AlertDialog.Builder(AllEventsExListView.this);
    AlertDialog builderRepeatingDelete = new AlertDialog.Builder(AllEventsExListView.this).setView(rowView).setTitle("Διαγραφή συμβάντος").create();


    viewFinal = CA.setAllFields(view1, myEventId, myTitle, myComment, myDate, myTime,location);


    builder.setView(viewFinal).

            setPositiveButton("Delete", (dialog, which) -> {

                AlertDialog.Builder builderDel = new AlertDialog.Builder(AllEventsExListView.this);
                builderDel.setPositiveButton("Yes", (dialog1, which1) -> {
                    String parent_id_value = myEvent.getEvents().get(0).getParent_id();
                    String row_id = myEvent.getEvents().get(0).getId();
                    LocalDate event_date = myEvent.getEvents().get(0).getDate();

                    if (parent_id_value == null && !myDB.checkNextRowHasParentId(Integer.parseInt(row_id))) {
                        deleteEventNotRepeating(CA,row_id,myTitle,myComment,event_date);
                    } else {
                        deleteEventIfRepeating(builderRepeatingDelete,CA, deleteAll, deleteOne, deleteFuture,row_id,parent_id,event_date);
                        builderRepeatingDelete.show();
                    }


                }).setNegativeButton("No", (dialog12, which12) -> dialog12.cancel()).setTitle("Are you sure you want to delete event " + myTitle + " ?");
                builderDel.show();


            }).setNegativeButton("Exit", (dialog, which) -> dialog.dismiss()).setNeutralButton("Edit", (dialog, which) -> {
                Intent i = new Intent(AllEventsExListView.this, Edit_Update_Activity.class);


                String row_id = ((HourEvent) parent.getAdapter().getItem(groupPosition)).getEvents().get(0).getId();


                i.putExtra("id", row_id);
                String stackNow = "all";
                i.putExtra("stack", stackNow);
                startActivity(i);
            }).setOnCancelListener(dialog -> hourAdapter.notifyDataSetChanged()).setOnDismissListener(dialog -> hourAdapter.notifyDataSetChanged() );




    builder.show();




}
    private void deleteEventIfRepeating(AlertDialog builderRepeating,EventCursorAdapter CA, TextView deleteAll, TextView deleteOne, TextView deleteFuture,
                                        String row_id,String parent_id,LocalDate event_date) {

        builderRepeating.setOnShowListener(dialog -> {
            deleteAll.setOnClickListener(v -> {



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


                setExListAdapterAfterDelete();

                dialog.dismiss();
            });

            deleteOne.setOnClickListener(v -> {


                Cursor eventCursor = myDB.readAllEvents();
                Cursor newEventCursor = myDB.readAllEvents();

                Cursor remCursor = myDB.readAllReminder();
                int tempIdForNewParent = 0;
                int repeat = 0;
                if (parent_id==null)
                {
                    eventCursor.moveToPosition(-1);
                    while(eventCursor.moveToNext())
                    {
                        if (eventCursor.getString(0).equals(row_id))
                        {
                            repeat= Integer.parseInt(eventCursor.getString(6));
                        }
                        if (!(eventCursor.getString(7)==null) && eventCursor.getString(7).equals(row_id))
                        {
                            tempIdForNewParent = Integer.parseInt(eventCursor.getString(0));
                            myDB.updateEventParentId(String.valueOf(tempIdForNewParent),null);
                            myDB.updateEventRepeat(String.valueOf(tempIdForNewParent), String.valueOf(repeat));
                            myDB.deleteOneRow(row_id);
                            break;
                        }
                    }

                    newEventCursor.moveToPosition(-1);
                    while(newEventCursor.moveToNext())
                    {
                        if (!(newEventCursor.getString(7)==null) && newEventCursor.getString(7).equals(row_id))
                        {
                            myDB.updateEventParentId(newEventCursor.getString(0), String.valueOf(tempIdForNewParent));
                            myDB.updateEventRepeat(newEventCursor.getString(0), String.valueOf(0));
                        }
                    }
                }else
                {
                    myDB.deleteOneRow(row_id);
                }


                remCursor.moveToPosition(-1);
                while (remCursor.moveToNext()) {
                    if (remCursor.getString(1).equals(row_id)) {
                        myDB.deleteOneRowReminder(remCursor.getString(0));
                        cancelAlarm(Integer.parseInt(remCursor.getString(0)));
                    }
                }


                prepareListData();
                CA.notifyDataSetChanged();

                hourAdapter.notifyDataSetChanged();
                expandableListView.invalidateViews();
                expandableListView.invalidate();

                hourAdapter.notifyDataSetInvalidated();

                newEventCursor.close();
                eventCursor.close();
                remCursor.close();
                myDB.close();


                setExListAdapterAfterDelete();

                dialog.dismiss();
            });

            deleteFuture.setOnClickListener(v -> {




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

                CA.notifyDataSetChanged();
                hourAdapter.notifyDataSetChanged();
                setExListAdapterAfterDelete();

                dialog.dismiss();
            });
        });
    }

    private void deleteEventNotRepeating(EventCursorAdapter CA,String id_row,String title_row,String comment_row,LocalDate date_row) {


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
        setExListAdapterAfterDelete();


    }

    private void cancelAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


        prepareListData();
        ClickListenerExListViewGroup();
        ClickListenerExListViewChild();

        if (!(hourAdapter == null)) {
            hourAdapter.notifyDataSetChanged();
        }
        expandableListView.invalidateViews();

    }


    @SuppressLint("Range")
    private List<Event> listDataHeaderEvent()
    {   SQLiteDatabase db = myDB.getWritableDatabase();
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
                @SuppressLint("Range") LocalTime time = null;

                    time = LocalTime.parse(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME)));

                @SuppressLint("Range") String alarm = String.valueOf(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ALARM)));
                @SuppressLint("Range") String parentId = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PARENT_ID)));
                @SuppressLint("Range")  String location = String.valueOf(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LOCATION));
                Event event = new Event(id, title, comment, date, time, alarm, parentId,location);

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

    @SuppressLint("Range")
    private HashMap<Event, List<Event>> listDataChildEvent()
    {   SQLiteDatabase db = myDB.getWritableDatabase();
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
                @SuppressLint("Range") LocalTime time = null;
                    time = LocalTime.parse(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME)));

                @SuppressLint("Range") String alarm = String.valueOf(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ALARM)));
                @SuppressLint("Range") String parentId = String.valueOf(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PARENT_ID)));
                @SuppressLint("Range")  String location = String.valueOf(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LOCATION));

                Event event = new Event(id, title, comment, date, time, alarm, parentId,location);

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
    @SuppressLint("Range")
    private void prepareListData() {
         SQLiteDatabase db = myDB.getWritableDatabase();

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
                @SuppressLint("Range")  String location = String.valueOf(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LOCATION));

                Event event = new Event(id, title, comment, date, time, alarm, parentId,location);

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