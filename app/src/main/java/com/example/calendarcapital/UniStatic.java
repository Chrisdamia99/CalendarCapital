package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.stringToLocalDate;
import android.database.Cursor;
import java.time.LocalDate;
import java.util.ArrayList;

public class UniStatic {


    public static ArrayList<String> getAllEventsArray(MyDatabaseHelper myDB,String parent_id,String id_row)
    {
        Cursor cursorEvent = myDB.readAllEvents();
        Cursor remCursor = myDB.readAllReminder();
        ArrayList<String> editAllArray = new ArrayList<>();

        if (parent_id == null) {
            cursorEvent.moveToPosition(-1);
            while (cursorEvent.moveToNext()) {
                if (cursorEvent.getString(0).equals(id_row) || (!(cursorEvent.getString(7) == null) && cursorEvent.getString(7).equals(id_row))) {
                    editAllArray.add(cursorEvent.getString(0));
                }
            }



        } else {

            cursorEvent.moveToPosition(-1);
            while (cursorEvent.moveToNext()) {
                if (cursorEvent.getString(0).equals(parent_id) || (!(cursorEvent.getString(7) == null) && cursorEvent.getString(7).equals(parent_id))) {
                    editAllArray.add(cursorEvent.getString(0));
                }
            }


        }




        cursorEvent.close();
        remCursor.close();
        myDB.close();

        return editAllArray;
    }

    public static ArrayList<String> getFutureEventsArray(MyDatabaseHelper myDB,String parent_id,String id_row,LocalDate selectedDate)
    {

        Cursor cursorEvent = myDB.readAllEvents();
        Cursor remCursor = myDB.readAllReminder();
        ArrayList<String> editFutureArray = new ArrayList<>();
        if (parent_id == null) {
            cursorEvent.moveToPosition(-1);
            while (cursorEvent.moveToNext()) {
                if (cursorEvent.getString(0).equals(id_row) ||
                        (!(cursorEvent.getString(7) == null) && cursorEvent.getString(7).equals(id_row))) {
                    editFutureArray.add(cursorEvent.getString(0));
                }
            }
            editFutureArray.size();

        } else {

            cursorEvent.moveToPosition(-1);
            while (cursorEvent.moveToNext()) {
                LocalDate cursorLocalDate = stringToLocalDate(cursorEvent.getString(3));
                String cursorParentID = cursorEvent.getString(7);
                int comparisonLocalDates = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    comparisonLocalDates = selectedDate.compareTo(cursorLocalDate);
                }
                if (!(cursorParentID == null) && cursorParentID.equals(parent_id)) {

                    if (comparisonLocalDates < 0 || selectedDate.equals(cursorLocalDate)) {
                        editFutureArray.add(cursorEvent.getString(0));

                    }
                }
            }


        }




        cursorEvent.close();
        remCursor.close();
        myDB.close();
        return editFutureArray;

    }


}
