package com.example.mycalendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> items;
    private List<Integer> icons;
    private List<Drawable> iconsDraw;// Resource IDs of vector images




    public CustomSpinnerAdapter(@NonNull Context context,List<String> items, List<Drawable> iconsDraw) {
        super(context, R.layout.custom_spinner_item, items);
        this.context = context;
        this.items = items;
        this.iconsDraw = iconsDraw;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_spinner_item, parent, false);

        TextView textView = view.findViewById(R.id.spinnerText);
        ImageView imageView = view.findViewById(R.id.spinnerIcon);

        textView.setText(items.get(position));
        if (icons != null && position < icons.size()) {
            imageView.setImageResource(icons.get(position));
        } else if (iconsDraw != null && position < iconsDraw.size()) {
            imageView.setImageDrawable(iconsDraw.get(position));
        }
        return view;
    }
}

