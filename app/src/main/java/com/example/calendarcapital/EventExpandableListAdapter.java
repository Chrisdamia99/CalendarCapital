package com.example.calendarcapital;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
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

    @SuppressLint("InflateParams")
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
        setEvents(convertView, childEvent.getEvents(),childEvent.getEvents().get(0).getColor());


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
        if (groupPosition >= 0 && groupPosition < mEvents.size()) {
            return mEvents.get(groupPosition);
        } else {
            return null;
        }
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

    @SuppressLint("InflateParams")
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
        setEvents(convertView, parentEvent.getEvents(),parentEvent.getEvents().get(0).getColor());
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
        notifyDataSetChanged();
    }


    private void setEvents(View convertView, ArrayList<Event> events,String color) {
        TextView event1 = convertView.findViewById(R.id.event1Ex);


        TextView comment1 = convertView.findViewById(R.id.comment1Ex);


        if (events.size() == 0) {
            hideEvent(event1);
            hideEvent(comment1);
        } else if (events.size() == 1) {
            setEvent(event1, events.get(0),color);
            setComment(comment1, events.get(0),color);


        }

        if (comment1.getText().toString().isEmpty()) {
            comment1.setVisibility(View.GONE);
        }
        notifyDataSetChanged();

    }

    private void setEvent(TextView textView, Event event,String color) {

        if (event.getName().equals(""))
        {

            textView.setVisibility(View.GONE);
        }else
        {
            textView.setText(event.getName());
            textView.setVisibility(View.VISIBLE);
        }
        if (color.equals("0"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner);
        }else if (color.equals("1"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_red);

        }else if (color.equals("2"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_yellow);

        }else if (color.equals("3"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_green);

        }else if (color.equals("4"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_blue);

        }else if (color.equals("5"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_purple);
        }else
        {
            textView.setBackgroundResource(R.drawable.rounded_corner);

        }
        notifyDataSetChanged();

    }

    private void setComment(TextView textView, Event event,String color) {

        if (event.getComment().equals(""))
        {

            textView.setVisibility(View.GONE);
        }else {
            textView.setText(event.getComment());
            textView.setVisibility(View.VISIBLE);
        }

        if (color.equals("0"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner);
        }else if (color.equals("1"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_red);

        }else if (color.equals("2"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_yellow);

        }else if (color.equals("3"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_green);

        }else if (color.equals("4"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_blue);

        }else if (color.equals("5"))
        {
            textView.setBackgroundResource(R.drawable.rounded_corner_purple);
        }else
        {
            textView.setBackgroundResource(R.drawable.rounded_corner);

        }
        notifyDataSetChanged();
    }


    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }

    // Add this method to your EventExpandableListAdapter class



}
