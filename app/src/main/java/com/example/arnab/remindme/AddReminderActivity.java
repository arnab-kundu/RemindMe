package com.example.arnab.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    AutoCompleteTextView actv;
    EditText et;
    private TextView textView;
    String reminderDateAndTime;
    ReminderDatabase rdb;
    SQLiteDatabase db;
    ContentValues cv;
    String cd;
    String items[] = {"Weak up", "Interview", "Meeting", "Client meeting", "School", "Collage",
            "Exam", "Class", "Training", "Work Shop", "Birth Day", "Appointment", "Wedding Ceremony",
            "Work", "Movie", "Film", "Mail", "Profile update", "Assignment", "Medicine Time",
            "Marketing", "Shopping", "Health Checkup", "Boss", "Test", "Gym", "Jogging"};
    String title, massage;
    String dnt = "Date and Time";
    Animation shake, fade, blink;
    FloatingActionButton fab;
    Toolbar toolbar;
    GradientDrawable gradientDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        this.overridePendingTransition(R.anim.fade_in, R.anim.zoomout_and_drop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int backgroundColor = MainActivity.sharedPreferences.getInt("backgroundColor", Color.RED);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.add_ll);
        gradientDrawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.layout_background);
        gradientDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.ADD);
        linearLayout.setBackground(gradientDrawable);

        actv = (AutoCompleteTextView) findViewById(R.id.actv);
        et = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView2);
        //Toast.makeText(this, "Please avoid setting a past time.You wouldn\'t get any reminder on that case.", Toast.LENGTH_SHORT).show();

        shake = AnimationUtils.loadAnimation(this, R.anim.shake_horizontal);
        fade = AnimationUtils.loadAnimation(this, R.anim.zoom);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        actv.setAdapter(aa);
        actv.setThreshold(1);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            dnt = savedInstanceState.getString("dnt");
            textView.setText(dnt);
            actv.setText(savedInstanceState.getString("title"));
            et.setText(savedInstanceState.getString("msg"));
        }
        if (dnt != null)
            if (dnt.equals("Date and Time")) {
                setDateTime();
            }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = actv.getText().toString().trim();
                massage = et.getText().toString().trim();
                dnt = textView.getText().toString();
                if (title.equals("")) {
                    Snackbar.make(view, "Enter a reminder title", Snackbar.LENGTH_LONG).show();
                    actv.startAnimation(shake);
                    et.startAnimation(shake);
                    actv.requestFocus();
                }
                //checking user has giver a date and time input or not
                else if (textView.length() != 16) {
                    //Hide Keyboard from window
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isAcceptingText()) {
                        Log.d("msg", "have to hide");
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    Snackbar.make(view, "Set date and time", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setDateTime();

                        }
                    }).show();
                }
                //checking user input date and time is a future time or not
                else if (simpleDateFormatToMilliseconds(dnt)) {
                    //Hide Keyboard from window
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isAcceptingText()) {
                        Log.d("msg", "have to hide");
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    Snackbar.make(view, "Enter a future the for Reminder", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setDateTime();
                        }
                    }).show();
                } else {
                    try {
                        cv = new ContentValues();
                        cv.put(ReminderDatabase.TITLE, title);
                        cv.put(ReminderDatabase.MASSAGE, massage);
                        cv.put(ReminderDatabase.DATE_TIME, dnt);
                        rdb = new ReminderDatabase(getApplicationContext());
                        db = rdb.getWritableDatabase();
                        db.insert(ReminderDatabase.TABLENAME, null, cv);
                        db.close();
                        Toast.makeText(getApplicationContext(), "Reminder added successfully", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(AddReminderActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } catch (Exception e) {
                        Log.e("msg", "" + e);
                    }
                }

            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.startAnimation(blink);
        fab.startAnimation(blink);
        toolbar.startAnimation(blink);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("dnt", textView.getText().toString());
        outState.putString("title", title);
        outState.putString("msg", massage);
        super.onSaveInstanceState(outState);
    }

    //for date and time picker dialog
    public void setDateTime() {
        Date d = new Date();
        int presentYear = d.getYear() + 1900;
        int presentMonth = d.getMonth();
        int presentDate = d.getDate();

        final TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 || minute < 10) {
                    if (hourOfDay < 10 && minute < 10) {
                        reminderDateAndTime = reminderDateAndTime + " 0" + hourOfDay + ":0" + minute;
                        textView.setText(reminderDateAndTime);
                        textView.startAnimation(fade);
                    } else if (hourOfDay < 10) {
                        reminderDateAndTime = reminderDateAndTime + " 0" + hourOfDay + ":" + minute;
                        textView.setText(reminderDateAndTime);
                        textView.startAnimation(fade);
                    } else if (minute < 10) {
                        reminderDateAndTime = reminderDateAndTime + " " + hourOfDay + ":0" + minute;
                        textView.setText(reminderDateAndTime);
                        textView.startAnimation(fade);
                    }
                } else {
                    reminderDateAndTime = reminderDateAndTime + " " + hourOfDay + ":" + minute;
                    textView.setText(reminderDateAndTime);
                    textView.startAnimation(fade);
                }
            }
        }, d.getHours(), d.getMinutes(), false);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (monthOfYear < 9 || dayOfMonth < 10) {
                    if (monthOfYear < 9 && dayOfMonth < 10) {
                        reminderDateAndTime = year + "/0" + (monthOfYear + 1) + "/0" + dayOfMonth;//monthOfYear +1 because 0 to 11 count
                    } else if (monthOfYear < 9) {
                        reminderDateAndTime = year + "/0" + (monthOfYear + 1) + "/" + dayOfMonth;
                    } else if (dayOfMonth < 10) {
                        reminderDateAndTime = year + "/" + (monthOfYear + 1) + "/0" + dayOfMonth;
                    }
                } else {
                    reminderDateAndTime = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                }
                tpd.show();
            }
        }, presentYear, presentMonth, presentDate);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        cd = simpleDateFormat.format(d);
    }

    //for change date and time once selected by clicking date & time TextView
    public void setDateTimeAgain(View view) {
        setDateTime();
    }

    public static boolean simpleDateFormatToMilliseconds(String dnt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dnt);
        } catch (ParseException e) {
            Log.e("msg", "dateParsingException");
        }
        //Log.d("msg",""+date.getTime());
        //Log.d("msg",""+System.currentTimeMillis());
        //System.out.println("msg in milliseconds: " + date.getTime());
        assert date != null;
        return System.currentTimeMillis() > date.getTime();
    }
}
