package com.example.arnab.remindme;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @Created by Arnab on 15-Oct-16.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> dateTime = new ArrayList<>();
    private ArrayList<String> massage = new ArrayList<>();

    CustomAdapter(Context context, ArrayList<String> title, ArrayList<String> dateTime, ArrayList<String> massage) {
        super(context, R.layout.custom_rowlayout_for_listview);
        this.title = title;
        this.dateTime = dateTime;
        this.massage = massage;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.custom_rowlayout_for_listview, parent, false);
        TextView titleView = view.findViewById(R.id.rowTitle);
        TextView dateTimeView = view.findViewById(R.id.rowDate);
        TextView massageView = view.findViewById(R.id.rowMassage);
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
