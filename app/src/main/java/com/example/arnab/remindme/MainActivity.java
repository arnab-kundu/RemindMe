package com.example.arnab.remindme;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.arnab.remindme.R.id.main_ll;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    //region GlobalVariables
    ListView listView;
    //SimpleCursorAdapter dataAdapter;
    ReminderDatabase rdb;
    SQLiteDatabase db;
    String thisReminder;
    Intent start_service_intent;
    FloatingActionButton fab;
    Animation blink, clockwise, slide, fade, bounce;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    int backgroundColor = Color.RED;
    GradientDrawable gradientDrawable;
    public static SharedPreferences sharedPreferences;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.overridePendingTransition(R.anim.fade_in, R.anim.zoom_out);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);


        blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        clockwise = AnimationUtils.loadAnimation(this, R.anim.clockwise);
        slide = AnimationUtils.loadAnimation(this, R.anim.slide);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        coordinatorLayout = findViewById(R.id.coo);
        appBarLayout = findViewById(R.id.appbar);
        linearLayout = findViewById(main_ll);

        swipeRefreshLayout = findViewById(R.id.srl);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setOnRefreshListener(this);
        sharedPreferences = getSharedPreferences("RemindMe", MODE_PRIVATE);
        if (sharedPreferences != null)
            backgroundColor = sharedPreferences.getInt("backgroundColor", Color.BLACK);

        LinearLayout linearLayout = findViewById(R.id.main_ll);
        gradientDrawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.layout_background);
        if (gradientDrawable != null)
            gradientDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.ADD);
        linearLayout.setBackground(gradientDrawable);

        rdb = new ReminderDatabase(this);
        db = rdb.getWritableDatabase();

        // RemindMeApplication remindMeApplication = RemindMeApplication.getInstance();

        if (!isMyServiceRunning(ReminderService.class)) {
            Log.i("msg", "service starting");
            start_service_intent = new Intent(this, ReminderService.class);
            startService(start_service_intent);
        }

        fab = findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddReminderActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.shrink_and_rotate__entrance, R.anim.shrink_and_rotate__exit);
        newdisplayCustomListView();
        fab.startAnimation(blink);
        toolbar.startAnimation(blink);
        //linearLayout.startAnimation(slide);
        //appBarLayout.startAnimation(clockwise);
        listView.startAnimation(bounce);
        //YoYo.with(Techniques.ZoomInDown).duration(2000).playOn(appBarLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        newdisplayCustomListView();
        swipeRefreshLayout.setRefreshing(false);
        listView.startAnimation(bounce);
    }

    public void openHistory(MenuItem menu) {
        startActivity(new Intent(this, HistoryActivity.class));
    }

    //for getting all data form database
    /*public Cursor fetchAllDataFromDatabase() {
        Cursor cursor = null;
        try {
            rdb = new ReminderDatabase(this);
            SQLiteDatabase db = rdb.getWritableDatabase();
            String columns[] = {ReminderDatabase._ID, ReminderDatabase.TITLE, ReminderDatabase.MASSAGE, ReminderDatabase.DATE_TIME};
            cursor = db.query(ReminderDatabase.TABLENAME, columns, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                Toast.makeText(MainActivity.this, "Reminder is empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("msg", "" + e);
        }
        return cursor;
    }*/

    //region for adding all data to a custom listView using SimpleCursorAdapter Old method not using now....
   /* protected void displayCustomListView() {
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
                            db.delete(ReminderDatabase.TABLENAME, ReminderDatabase.TITLE + "=?", new String[]{thisReminder});
                            displayCustomListView();
                        }
                    }).show();
                }
            });
        } catch (Exception e) {
            Log.e("msg", "" + e);
        }
    }*/
    //endregion

    //for checking whether service is running or not
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        Log.i("msg", "checking");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int count = 1;
        if (manager != null)
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                Log.i("msg", "checked number of services " + count++);
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("msg", "true");
                    return true;
                }
            }
        return false;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dnt);
        } catch (ParseException e) {
            Log.e("msg", "+e");
            Log.e("msg", "dateParsingException");
        }
        assert date != null;
        long timeInMilliSeconds = date.getTime();
        SimpleDateFormat sdf12 = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());
        Date date12 = new Date(timeInMilliSeconds);
        return sdf12.format(date12);
    }


    public void newdisplayCustomListView() {

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> dateTime = new ArrayList<>();
        ArrayList<String> massage = new ArrayList<>();

        ReminderDatabase reminderDatabase = new ReminderDatabase(this);
        SQLiteDatabase sqLiteDatabase = reminderDatabase.getReadableDatabase();
        String query = "select * from " + ReminderDatabase.TABLENAME;
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                title.add(c.getString(1));
                massage.add(c.getString(2));
                dateTime.add(checkDeviceDateFormat(c.getString(3)));
            } while (c.moveToNext());
        }
        c.close();

        CustomAdapter customAdapter = new CustomAdapter(this, title, dateTime, massage);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                thisReminder = ((TextView) view.findViewById(R.id.rowTitle)).getText().toString();
                //thisReminder = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabase.TITLE));
                Snackbar.make(view, "Do you want to delete this Reminder", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db = rdb.getWritableDatabase();
                        db.delete(ReminderDatabase.TABLENAME, ReminderDatabase.TITLE + "=?", new String[]{thisReminder});
                        newdisplayCustomListView();
                    }
                }).show();
            }
        });
    }

    public void openColorPicker(MenuItem item) {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, Color.RED, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                try {
                    Log.d("msg", "" + String.format("0x%08x", color));
                    Log.e("msg", "working");
                    String color1 = String.format("0x%08x", color);
                    Log.d("msg", "" + color1);
                    color1 = color1.substring(4);
                    color1 = "#" + color1;
                    Log.d("msg", "" + color1);
                    int BackgroundColor = Color.parseColor(color1);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("backgroundColor", BackgroundColor);
                    editor.apply();
                    gradientDrawable.setColorFilter(BackgroundColor, PorterDuff.Mode.ADD);
                    linearLayout.setBackground(gradientDrawable);
                } catch (Exception e) {
                    Log.e("msg", "" + e);
                }
            }
        });
        ambilWarnaDialog.show();
    }
}
