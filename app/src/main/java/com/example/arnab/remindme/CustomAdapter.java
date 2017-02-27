package com.example.arnab.remindme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Arnab on 15-Oct-16.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> dateTime = new ArrayList<>();
    ArrayList<String> massage = new ArrayList<>();

    public CustomAdapter(Context context, ArrayList<String> title, ArrayList<String> dateTime, ArrayList<String> massage) {
        super(context, R.layout.custom_rowlayout_for_listview);
        this.title = title;
        this.dateTime = dateTime;
        this.massage = massage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.custom_rowlayout_for_listview, parent, false);
        TextView titleView = (TextView) view.findViewById(R.id.rowTitle);
        TextView dateTimeView = (TextView) view.findViewById(R.id.rowDate);
        TextView massageView = (TextView) view.findViewById(R.id.rowMassage);
        titleView.setText(title.get(position));
        dateTimeView.setText(dateTime.get(position));
        massageView.setText(massage.get(position));
        massageView.setSelected(true);
        return view;
    }

    @Override
    public int getCount() {
        return title.size();
    }

}
