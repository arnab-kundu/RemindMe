package com.example.arnab.remindme;

import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Singleton technology
 */
public class RemindMeApplication extends Application {
    private static RemindMeApplication remindMeApplication;

    private RemindMeApplication() {
    }


    public static RemindMeApplication getInstance() {
        if (remindMeApplication == null) {
            remindMeApplication = new RemindMeApplication();
        }
        return remindMeApplication;
    }


    public String checkDeviceDateFormat(String dnt) {
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            return dnt;
        } else {
            return hrs24ToHrs12Format(dnt);
        }
    }

    public String hrs24ToHrs12Format(String dnt) {
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

}
