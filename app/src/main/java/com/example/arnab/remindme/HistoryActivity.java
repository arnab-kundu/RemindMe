package com.example.arnab.remindme;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    SQLiteDatabase db;
    ReminderDatabase rdb;
    ListView listView;
    SimpleCursorAdapter dataAdapter;
    String thisReminder;
    LinearLayout linearLayout;
    GradientDrawable gradientDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.history_listView);
        linearLayout = (LinearLayout) findViewById(R.id.history_ll);

        int backgroundColor = MainActivity.sharedPreferences.getInt("backgroundColor", Color.RED);
        gradientDrawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.layout_background);
        gradientDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.ADD);
        linearLayout.setBackground(gradientDrawable);

        overridePendingTransition(R.anim.shrink_and_rotate__entrance, R.anim.shrink_and_rotate__exit);
        rdb = new ReminderDatabase(this);
        db = rdb.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newdisplayCustomListView();
        listView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
    }

    //for getting all data form database
    public Cursor fetchAllDataFromDatabase() {
        Cursor cursor = null;
        try {
            //rdb = new ReminderDatabase(this);
            //db = rdb.getWritableDatabase();
            String columns[] = {ReminderDatabase._ID, ReminderDatabase.TITLE, ReminderDatabase.MASSAGE, ReminderDatabase.DATE_TIME};
            cursor = db.query(ReminderDatabase.HISTORY_TABLE, columns, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                Toast.makeText(HistoryActivity.this, "Reminder is empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("msg", "" + e);
        }
        return cursor;
    }

    //for adding all data to a custom listView
    protected void displayCustomListView() {
        try {
            String[] fromColumns = new String[]{

                    ReminderDatabase.TITLE,
                    ReminderDatabase.MASSAGE,
                    ReminderDatabase.DATE_TIME
            };

            int[] toView = new int[]{
                    R.id.rowTitle,
                    R.id.rowMassage,
                    R.id.rowDate
            };
            dataAdapter = new SimpleCursorAdapter(this, R.layout.custom_rowlayout_for_listview, fetchAllDataFromDatabase(), fromColumns, toView, 0);
            listView.setAdapter(dataAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                    thisReminder = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabase.TITLE));
                    Snackbar.make(view, "Do you want to delete this Reminder", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db = rdb.getWritableDatabase();
                            db.delete(ReminderDatabase.HISTORY_TABLE, ReminderDatabase.TITLE + "=?", new String[]{thisReminder});
                            displayCustomListView();
                        }
                    }).show();
                }
            });
        } catch (Exception e) {
            Log.e("msg", "" + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteAll) {
            db = rdb.getWritableDatabase();
            int no_of_row_deleted = db.delete(ReminderDatabase.HISTORY_TABLE, null, null);
            newdisplayCustomListView();
            String rem = "Reminders";
            if (no_of_row_deleted == 1 || no_of_row_deleted == 0) {
                rem = "Reminder";
            }
            Toast.makeText(HistoryActivity.this, no_of_row_deleted + " " + rem + " deleted", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public String checkDeviceDateFormat(String dnt) {
        if (DateFormat.is24HourFormat(this)) {
            return dnt;
        } else {
            return hrs24ToHrs12Format(dnt);
        }
    }

    //24hrs to 12hrs time conversion
    public static String hrs24ToHrs12Format(String dnt) {
        Log.d("msg", "" + dnt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse(dnt);
        } catch (ParseException e) {
            Log.e("msg", "+e");
            Log.e("msg", "dateParsingException");
        }
        assert date != null;
        long timeInMilliSeconds = date.getTime();
        SimpleDateFormat sdf12 = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
        Date date12 = new Date(timeInMilliSeconds);
        return sdf12.format(date12);
    }


    public void newdisplayCustomListView() {

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> dateTime = new ArrayList<>();
        ArrayList<String> massage = new ArrayList<>();

        db = rdb.getReadableDatabase();
        String query = "select * from " + ReminderDatabase.HISTORY_TABLE;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                title.add(c.getString(1));
                massage.add(c.getString(2));
                dateTime.add(checkDeviceDateFormat(c.getString(3)));
            } while (c.moveToNext());
        }

        CustomAdapter customAdapter = new CustomAdapter(this, title, dateTime, massage);
        listView.setAdapter(customAdapter);
    }
}
