package com.example.calendarcapital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    private static final String DATABASE_NAME = "CalendarCapital.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_events_db";
    public static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "event_title";
    private static final String COLUMN_COMMENT = "event_comment";
    private static final String COLUMN_DATE = "event_date";
    private static final String COLUMN_TIME = "event_time";
    private static final String COLUMN_ALARM = "event_alarm";
    private static final String COLUMN_REPEAT_ALARM = "event_repeat";
    private static final String COLUMN_PARENT_ID = "event_parent_id";


    private static final String TABLE_NAME_REMINDER = "my_reminders_db";
    public static final String COLUMN_ID_REMINDER = "_id";
    private static final String COLUMN_EVENT_ID = "event_id";
    private static final String COLUMN_REMINDER = "reminder_date";




    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_COMMENT + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_ALARM + " TEXT, " +
                COLUMN_REPEAT_ALARM + " TEXT, " +
                COLUMN_PARENT_ID + " TEXT);";


        String query_reminder = "CREATE TABLE " + TABLE_NAME_REMINDER +
                " (" + COLUMN_ID_REMINDER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENT_ID + " TEXT, " +
                COLUMN_REMINDER + " TEXT);";



        db.execSQL(query);
        db.execSQL(query_reminder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REMINDER);

        onCreate(db);

    }
//------------------------------------------ADD------------------------------------------------------------

    void addEvent(String title, String comment, LocalDate date, LocalTime time, String alarm,String repeat,String parent_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_COMMENT, comment);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_TIME, String.valueOf(time));
        cv.put(COLUMN_ALARM, alarm);
        cv.put(COLUMN_REPEAT_ALARM,repeat);
        cv.put(COLUMN_PARENT_ID,parent_id);


        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Data Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }



    void addReminder(String event_id, Date reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_EVENT_ID, event_id);
        cv.put(COLUMN_REMINDER, String.valueOf(reminder));

        long result = db.insert(TABLE_NAME_REMINDER, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Data Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }

//------------------------------------CURSORS----------------------------------------------------------------
    Cursor readAllReminder() {
        String query_reminder = "SELECT * FROM " + TABLE_NAME_REMINDER;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query_reminder, null);
        }
        return cursor;
    }


    Cursor readAllEvents() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }


//------------------------------------------------------------------------------------------------------------


    //------------------------------------------UPDATE------------------------------------------------------------

    void updateAlarmNum(String row_id, String alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ALARM, alarm);


        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }
    }

    void updateReminder(String row_id, Date reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_REMINDER, String.valueOf(reminder));


        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }
    }

    void updateData(String row_id, String title, String comments, LocalDate date, LocalTime time, String alarm,String repeat,String parent_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_COMMENT, comments);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_TIME, String.valueOf(time));
        cv.put(COLUMN_ALARM, alarm);
        cv.put(COLUMN_REPEAT_ALARM, repeat);
        cv.put(COLUMN_PARENT_ID, parent_id);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }
    }



//----------------------------------------------------------------------------------------------------------

//------------------------------------------DELETE------------------------------------------------------------

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllEventsParentId(String parent_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLUMN_PARENT_ID + "=?";
        String[] whereArgs = {parent_id};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }
    void deleteOneRowReminder(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME_REMINDER, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }


    void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }


    void deleteAllDataReminder() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_REMINDER);
    }

    //------------------------------------------------------------------------------------------------------

}
