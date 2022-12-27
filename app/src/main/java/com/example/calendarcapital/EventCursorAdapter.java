package com.example.calendarcapital;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;


public class EventCursorAdapter extends CursorAdapter{

    private  Context mContext;
    Cursor mCursor;




    public EventCursorAdapter(Context context, Cursor c) {
        super(context, c,0);

        this.mCursor = c;
        this.mContext=context;


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        return LayoutInflater.from(context).inflate(R.layout.show_event_from_listview, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView id_lv_tv = (TextView) view.findViewById(R.id.id_lv_tv);
        TextView title_lv_tv = (TextView) view.findViewById(R.id.title_lv_tv);
        TextView comment_lv_tv = (TextView) view.findViewById(R.id.comment_lv_tv);
        TextView date_lv_tv = (TextView) view.findViewById(R.id.date_lv_tv);
        TextView time_lv_tv = (TextView) view.findViewById(R.id.time_lv_tv);


                String id_row = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("event_title"));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow("event_comment"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("event_date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("event_time"));

                id_lv_tv.setText(id_row);
                title_lv_tv.setText(title);
                comment_lv_tv.setText(comment);
                date_lv_tv.setText(date);
                time_lv_tv.setText(time);

    }


        public View setAllFields(View view,String id, String title, String comment, String date, String time)
    {

        TextView id_lv_tv = (TextView) view.findViewById(R.id.id_lv_tv);
        TextView title_lv_tv = (TextView) view.findViewById(R.id.title_lv_tv);
        TextView comment_lv_tv = (TextView) view.findViewById(R.id.comment_lv_tv);
        TextView date_lv_tv = (TextView) view.findViewById(R.id.date_lv_tv);
        TextView time_lv_tv = (TextView) view.findViewById(R.id.time_lv_tv);

        id_lv_tv.setText(id);
        title_lv_tv.setText(title);
        comment_lv_tv.setText(comment);
        date_lv_tv.setText(date);
        time_lv_tv.setText(time);

        return view;
    }



    public  int getCursorToPosition(String id_row, int position)
    {

        if (id_row.equals(mCursor.getString(0)))
        {
            return position;
        }else
        {
           return 0;
        }



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return super.getView(position, convertView, parent);
    }
}
