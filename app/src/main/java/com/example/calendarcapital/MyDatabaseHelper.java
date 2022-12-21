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

public  class MyDatabaseHelper  extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "CalendarCapital.db";
    private static final  int DATABASE_VERSION =1;

    private static final String TABLE_NAME ="my_events_db";
    public static final String COLUMN_ID ="_id";
    private static final String COLUMN_TITLE = "event_title";
    private static final String COLUMN_COMMENT = "event_comment";
    private static final String COLUMN_DATE = "event_date";
    private static final String COLUMN_TIME = "event_time";


MyDatabaseHelper(@Nullable Context context)
{
    super(context,DATABASE_NAME,null,DATABASE_VERSION);
    this.context = context;
}

@Override
    public void onCreate(SQLiteDatabase db)
{
    String query = "CREATE TABLE " + TABLE_NAME +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_COMMENT + " TEXT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_TIME + " TEXT);";
    
    db.execSQL(query);
}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    void addEvent(String title, String comment, LocalDate date, LocalTime time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_COMMENT, comment);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_TIME,  String.valueOf(time));
        
        long result = db.insert(TABLE_NAME, null, cv);
        if (result== -1)
        {
            Toast.makeText(context, "Data Failed", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }



    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String title, String comments, String date, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_COMMENT,comments);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_TIME, String.valueOf(time));

        long result = db.update(TABLE_NAME,cv,"_id=?",new String[]{row_id});

        if (result == -1)
        {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        long result =  db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1)
        {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }


}
