package com.example.calendarcapital;

import static com.example.calendarcapital.CalendarUtils.selectedDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EventExpandableListAdapter extends BaseExpandableListAdapter {
    private List<HourEvent> mEvents;
    private List<HourEvent> mRepeatingEvents;
    private Context mContext;
    private List<Event> listDataHeader;
    private HashMap<Event, List<Event>> listDataChild;


    public EventExpandableListAdapter(Context context, List<HourEvent> events) {
        mContext = context;
        mEvents = filterEvents(events);
    }

    public EventExpandableListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public EventExpandableListAdapter(AllEventsExListView context, List<Event> listDataHeader, HashMap<Event, List<Event>> listDataChild, List<HourEvent> events,List<HourEvent> RepeatingEvents) {
        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        mEvents = filterEvents(events);
        mRepeatingEvents = filterRepeatingEvents(RepeatingEvents);
    }


    private List<HourEvent> filterRepeatingEvents(List<HourEvent> events) {
        List<HourEvent> parentEvents = new ArrayList<>();
        for (HourEvent event : events) {
            if (!(event.getEvents().get(0).getParent_id() == null)) {
                parentEvents.add(event);
            }
        }
        return parentEvents;
    }

    private List<HourEvent> filterEvents(List<HourEvent> events) {
        List<HourEvent> parentEvents = new ArrayList<>();
        for (HourEvent event : events) {
            if (event.getEvents().get(0).getParent_id() == null) {
                parentEvents.add(event);
            }
        }
        return parentEvents;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        HourEvent parentEvent = (HourEvent) getGroup(groupPosition);
        long parentId = Long.parseLong(parentEvent.getId());
        List<HourEvent> childEvents = new ArrayList<>();
        for (int i=0; i<mRepeatingEvents.size(); i++)
        {
            if (Objects.equals(mRepeatingEvents.get(i).getEvents().get(0).getParent_id(), String.valueOf(parentId)))
            {
                childEvents.add(mRepeatingEvents.get(i));
            }

        }


            return childEvents.get(childPosition);


    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        HourEvent childEvent = (HourEvent) getChild(groupPosition, childPosition);


            return Long.parseLong(childEvent.getId());


    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hour_cell_expand_list, null);
        }
        HourEvent childEvent = (HourEvent) getChild(groupPosition, childPosition);

        ImageView indicator_down = convertView.findViewById(R.id.expandable_toggle_down);
        ImageView indicator_up = convertView.findViewById(R.id.expandable_toggle_up);

        indicator_down.setVisibility(View.GONE);
        indicator_up.setVisibility(View.GONE);


        setHour(convertView,childEvent.getEvents().get(0).getTime(),childEvent.getEvents().get(0).getDate());
        setEvents(convertView, childEvent.getEvents());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        HourEvent parentEvent = (HourEvent) getGroup(groupPosition);
        long parentId = Long.parseLong(parentEvent.getId());
        List<HourEvent> childEvents = new ArrayList<>();
        for (int i=0; i<mRepeatingEvents.size(); i++)
        {
            if (Objects.equals(mRepeatingEvents.get(i).getEvents().get(0).getParent_id(), String.valueOf(parentId)))
            {
                childEvents.add(mRepeatingEvents.get(i));
            }

        }
        return childEvents.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mEvents.get(groupPosition);
    }

    @Override
    public int getGroupCount() {

        return mEvents.size();

    }

    @Override
    public long getGroupId(int groupPosition) {
        HourEvent parentEvent = (HourEvent) getGroup(groupPosition);
        return Long.parseLong(parentEvent.getId());
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HourEvent parentEvent = (HourEvent) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hour_cell_expand_list, null);
        }
        ImageView indicator_down = convertView.findViewById(R.id.expandable_toggle_down);
        ImageView indicator_up = convertView.findViewById(R.id.expandable_toggle_up);

        if (isExpanded) {
            // Hide the indicator (arrow icon)

            indicator_up.setVisibility(View.VISIBLE);
            indicator_down.setVisibility(View.GONE);
        }else
        {

            indicator_up.setVisibility(View.GONE);
            indicator_down.setVisibility(View.VISIBLE);
        }
        if (getChildrenCount(groupPosition) == 0) {
            // Hide the indicator (arrow icon)
            indicator_up.setVisibility(View.GONE);
            indicator_down.setVisibility(View.GONE);
        }


        setHour(convertView,parentEvent.getEvents().get(0).getTime(),parentEvent.getEvents().get(0).getDate());
        setEvents(convertView, parentEvent.getEvents());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }




    private  void setHour(View convertView, LocalTime time, LocalDate date) {


        TextView timeTv = convertView.findViewById(R.id.timeTVEx);
        TextView dayofmonthTV = convertView.findViewById(R.id.dateTVEx);


        timeTv.setText(CalendarUtils.formattedShortTime(time));
        dayofmonthTV.setText(CalendarUtils.formattedDate(date));

    }


    private void setEvents(View convertView, ArrayList<Event> events) {
        TextView event1 = convertView.findViewById(R.id.event1Ex);
        TextView event2 = convertView.findViewById(R.id.event2Ex);
        TextView event3 = convertView.findViewById(R.id.event3Ex);
        TextView comment1 = convertView.findViewById(R.id.comment1Ex);
        TextView comment2 = convertView.findViewById(R.id.comment2Ex);
        TextView comment3 = convertView.findViewById(R.id.comment3Ex);

        if (events.size() == 0) {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
            hideEvent(comment1);
            hideEvent(comment2);
            hideEvent(comment3);
        } else if (events.size() == 1) {
            setEvent(event1, events.get(0));
            setComment(comment1, events.get(0));
            hideEvent(comment2);
            hideEvent(comment3);

            hideEvent(event2);
            hideEvent(event3);

        } else if (events.size() == 2) {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            hideEvent(comment3);
            hideEvent(event3);


        } else if (events.size() == 3) {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            setComment(comment3, events.get(2));
        } else {
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(events.size() - 2);
            eventsNotShown += " More Events";
            event3.setText(eventsNotShown);

            setComment(comment1, events.get(0));
            setComment(comment2, events.get(1));
            comment3.setVisibility(View.VISIBLE);
            String commentsNotShown = String.valueOf(events.size() - 2);
            commentsNotShown += " More Comments";
            comment3.setText(commentsNotShown);
        }

        if (comment1.getText().toString().isEmpty()) {
            comment1.setVisibility(View.GONE);
        }


    }

    private void setEvent(TextView textView, Event event) {

        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);

    }

    private void setComment(TextView textView, Event event) {

        textView.setText(event.getComment());
        textView.setVisibility(View.VISIBLE);

    }


    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }
}
