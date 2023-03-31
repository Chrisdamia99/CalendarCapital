package com.example.calendarcapital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
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

    private static final String TABLE_NAME_REPEATING_EVENTS ="my_repeating_events_db";
    public static final String COLUMN_REPEATING_ID = "_id";
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    private static final String COLUMN_REPEATING_TITLE = "event_title";
    private static final String COLUMN_REPEATING_COMMENT = "event_comment";
    private static final String COLUM_REPEATING_DATE = "event_date";
    private static final String COLUMN_REPEATING_TIME = "event_time";
    private static final String COLUMN_REPEATING_ALARM = "event_alarm";
    private static final String COLUMN_REPEATING_REPEAT_ALARM = "event_repeat";

    private static final String TABLE_NAME_REMINDER = "my_reminders_db";
    public static final String COLUMN_ID_REMINDER = "_id";
    private static final String COLUMN_EVENT_ID = "event_id";
    private static final String COLUMN_REMINDER = "reminder_date";

    private static final String TABLE_NAME_REPEAT ="my_repeat_db";
    public static final String COLUMN_ID_REPEAT ="_id";
    private static final String COLUMN_EVENT_REPEAT_ID ="event_repeat_id";
    private static final String COLUMN_REPEAT ="repeat_date";




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
                COLUMN_REPEAT_ALARM + " TEXT);";

        String query_repeating_events ="CREATE TABLE " + TABLE_NAME_REPEATING_EVENTS +
                " (" + COLUMN_REPEATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UNIQUE_ID + " TEXT, " +
                COLUMN_REPEATING_TITLE + " TEXT, " +
                COLUMN_REPEATING_COMMENT + " TEXT, " +
                COLUM_REPEATING_DATE + " TEXT, " +
                COLUMN_REPEATING_TIME + " TEXT, " +
                COLUMN_REPEATING_ALARM + " TEXT, " +
                COLUMN_REPEATING_REPEAT_ALARM + " TEXT);";

        String query_reminder = "CREATE TABLE " + TABLE_NAME_REMINDER +
                " (" + COLUMN_ID_REMINDER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENT_ID + " TEXT, " +
                COLUMN_REMINDER + " TEXT);";

        String query_repeat ="CREATE TABLE " + TABLE_NAME_REPEAT +
                " (" + COLUMN_ID_REPEAT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENT_REPEAT_ID + " TEXT, " +
                COLUMN_REPEAT  + " TEXT);";

        db.execSQL(query);
        db.execSQL(query_repeating_events);
        db.execSQL(query_reminder);
        db.execSQL(query_repeat);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REMINDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REPEAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REPEATING_EVENTS);
        onCreate(db);

    }
//------------------------------------------ADD------------------------------------------------------------

    void addEvent(String title, String comment, LocalDate date, LocalTime time, String alarm,String repeat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_COMMENT, comment);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_TIME, String.valueOf(time));
        cv.put(COLUMN_ALARM, alarm);
        cv.put(COLUMN_REPEAT_ALARM,repeat);


        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Data Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }

void addRepeatingEvent(String event_id,String title, String comment, LocalDate date, LocalTime time, String alarm,String repeat)
{
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();

    cv.put(COLUMN_UNIQUE_ID, event_id);
    cv.put(COLUMN_REPEATING_TITLE, title);
    cv.put(COLUMN_REPEATING_COMMENT, comment);
    cv.put(COLUM_REPEATING_DATE, String.valueOf(date));
    cv.put(COLUMN_REPEATING_TIME, String.valueOf(time));
    cv.put(COLUMN_REPEATING_ALARM, alarm);
    cv.put(COLUMN_REPEATING_REPEAT_ALARM,repeat);


    long result = db.insert(TABLE_NAME_REPEATING_EVENTS, null, cv);
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

    void addRepeat(String event_repeat_id, LocalDate repeat)
    {  SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EVENT_REPEAT_ID, event_repeat_id);
        cv.put(COLUMN_REPEAT, String.valueOf(repeat));

        long result = db.insert(TABLE_NAME_REPEAT, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Data Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }
//------------------------------------------CURSORS------------------------------------------------------------
    Cursor readAllRepeat()
    {
        String query_repeat = "SELECT * FROM " + TABLE_NAME_REPEAT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query_repeat, null);
        }
        return cursor;
    }

    Cursor readAllRepeatingEvents()
    {
        String query_repeat = "SELECT * FROM " + TABLE_NAME_REPEATING_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query_repeat, null);
        }
        return cursor;
    }
    Cursor readAllReminder() {
        String query_reminder = "SELECT * FROM " + TABLE_NAME_REMINDER;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query_reminder, null);
        }
        return cursor;
    }


    Cursor readAllData() {
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
    void updateRepeatNum(String row_id, String repeat)
    {   SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_REPEAT, repeat);


        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }

    }
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

    void updateData(String row_id, String title, String comments, LocalDate date, LocalTime time, String alarm,String repeat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_COMMENT, comments);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_TIME, String.valueOf(time));
        cv.put(COLUMN_ALARM, alarm);
        cv.put(COLUMN_REPEAT_ALARM, repeat);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }
    }

    void updateRepeatingEvent(String row_id, String unique_id,String title, String comments, LocalDate date, LocalTime time, String alarm,String repeat)
    { SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_UNIQUE_ID, unique_id);
        cv.put(COLUMN_REPEATING_TITLE, title);
        cv.put(COLUMN_REPEATING_COMMENT, comments);
        cv.put(COLUM_REPEATING_DATE, String.valueOf(date));
        cv.put(COLUMN_REPEATING_TIME, String.valueOf(time));
        cv.put(COLUMN_REPEATING_ALARM, alarm);
        cv.put(COLUMN_REPEATING_REPEAT_ALARM,repeat);

        long result = db.update(TABLE_NAME_REPEATING_EVENTS, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }
    }

    void updateReminder(String row_id, String event_id, Date reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EVENT_ID, event_id);
        cv.put(COLUMN_REMINDER, String.valueOf(reminder));


        long result = db.update(TABLE_NAME_REMINDER, cv, "_id=?", new String[]{row_id});

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


    void deleteOneRowReminder(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME_REMINDER, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRowRepeat(String row_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME_REPEAT, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOnewRowRepeatingEvent(String row_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME_REPEATING_EVENTS, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneSpecificRepeatingEvent(String dateValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "COLUMN_EVENT_ID=(SELECT _id FROM table1 WHERE COLUMN_DATE=?)";
        String[] whereArgs = new String[] { dateValue };
        db.delete("table2", whereClause, whereArgs);


        db.delete(TABLE_NAME, "event_date=?", new String[] { dateValue });
        db.delete(TABLE_NAME_REPEATING_EVENTS, "event_date=?", new String[] { dateValue });
        db.delete(TABLE_NAME_REPEAT, "repeat_date=?", new String[] { dateValue });



    }

    void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }



    void deleteAllDataRepeat()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_REPEAT);
    }

    void deleteAllRepeatingEventSpecificID(String id_row)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_REPEATING_EVENTS + " WHERE unique_id = " + id_row);

    }

    void deleteAllRepeatsSpecificID(String id_row)
    {SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_REPEAT + " WHERE event_repeat_id = " + id_row);

    }








    void deleteAllDataReminder() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_REMINDER);
    }

    //------------------------------------------------------------------------------------------------------

}
