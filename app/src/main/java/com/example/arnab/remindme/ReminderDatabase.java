package com.example.arnab.remindme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Arnab on 03-Jul-16.
 */
public class ReminderDatabase extends SQLiteOpenHelper {

    public static final String DBNAME = "reminderDatabase";
    public static final int VERSION = 1;
    public static final String _ID = "_id";
    public static final String TABLENAME = "remind";
    public static final String TITLE = "title";
    public static final String MASSAGE = "massage";
    public static final String DATE_TIME = "datetime";

    public static final String HISTORY_TABLE = "history";

    Context ctx;

    public ReminderDatabase(Context context) {
        super(context, DBNAME, null, VERSION);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String query = "create table " + TABLENAME + "(" + _ID + " integer PRIMARY KEY autoincrement," + TITLE + " TEXT," + MASSAGE + " TEXT," + DATE_TIME + " TEXT)";
            String h_query = "create table " + HISTORY_TABLE + "(" + _ID + " integer PRIMARY KEY autoincrement," + TITLE + " TEXT," + MASSAGE + " TEXT," + DATE_TIME + " TEXT)";
            db.execSQL(query);
            db.execSQL(h_query);
        } catch (Exception e) {
            Log.e("msg", "" + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
