package com.example.arnab.remindme;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderService extends Service {

    String dbtitle, dbmassage, dbdateandtime;
    public static int notification_counter = 1200;
    Thread t;
    static NotificationManager nm;
    ContentValues cv;
    public static final String SECONDARY_CHANNEL = "second";

    public ReminderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long futureTime = System.currentTimeMillis() + 5000;
                    while (System.currentTimeMillis() < futureTime) {
                        synchronized (this) {
                            Log.i("msg", "Service is running");
                            try {
                                wait(futureTime - System.currentTimeMillis());
                                ReminderDatabase reminderDatabase = new ReminderDatabase(getApplicationContext());
                                SQLiteDatabase db = reminderDatabase.getWritableDatabase();
                                Date d = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                                String cd = simpleDateFormat.format(d);
                                try {
                                    //String query = "select * from " + ReminderDatabase.TABLENAME + " where " + ReminderDatabase.DATE_TIME + " = \"" + cd.trim()+"\"";
                                    //Cursor cursor = db.rawQuery(query, null);
                                    String column[] = {ReminderDatabase.TITLE, ReminderDatabase.MASSAGE, ReminderDatabase.DATE_TIME};
                                    String where = ReminderDatabase.DATE_TIME + "=?";
                                    String input[] = {cd};
                                    Cursor cursor = db.query(ReminderDatabase.TABLENAME, column, where, input, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        Log.i("msg", "match found in database");
                                        dbtitle = cursor.getString(0);
                                        dbmassage = cursor.getString(1);
                                        dbdateandtime = cursor.getString(2);
                                        cursor.close();
                                        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        long v[] = {0, 100, 200, 300};
                                        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        Uri customNotificationSound = Uri.parse("android.resource://" + getPackageName() + "/raw/carlock");
                                        Notification.Builder n = new Notification.Builder(getApplicationContext());
                                        n.setSmallIcon(R.mipmap.ic_launcher);
                                        n.setTicker(dbtitle);
                                        n.setContentTitle(dbtitle);
                                        n.setContentText(dbmassage);
                                        n.setContentIntent(pi);
                                        n.setVibrate(v);
                                        n.setSound(customNotificationSound);
                                        n.setAutoCancel(true);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                            n.setChannelId(SECONDARY_CHANNEL);
                                        //n.build();
                                        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            NotificationChannel notificationChannel = new NotificationChannel(SECONDARY_CHANNEL, getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
                                            notificationChannel.setLightColor(Color.BLUE);
                                            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                                            nm.createNotificationChannel(notificationChannel);
                                        }
                                        nm.notify(notification_counter++, n.build());
                                        cv = new ContentValues();
                                        cv.put(ReminderDatabase.TITLE, dbtitle);
                                        cv.put(ReminderDatabase.MASSAGE, dbmassage);
                                        cv.put(ReminderDatabase.DATE_TIME, dbdateandtime);
                                        db.insert(ReminderDatabase.HISTORY_TABLE, null, cv);
                                        db.delete(ReminderDatabase.TABLENAME, ReminderDatabase.TITLE + "=?", new String[]{dbtitle});
                                        //db.execSQL("delete from "+ReminderDatabase.TABLENAME+" where "+ReminderDatabase.TITLE+"='"+dbtitle+"'");
                                        db.close();
                                        Log.i("msg", "service deleted past reminder");

                                        if (isForeground("com.example.arnab.remindme")) {
                                            Log.d("msg", "ok");


                                            startActivity(new Intent(getApplicationContext(), AddReminderActivity.class));
                                        }

                                    }
                                } catch (Exception e) {
                                    Log.e("msg", "" + e);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        t = new

                Thread(r);
        t.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("123123","Reminder");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        Intent resultIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent) //intent
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());
        startForeground(1, notification);
    }


    private void stopForegroundService() {
        Log.d("msg", "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.d("msg", componentInfo.getPackageName());
        return componentInfo.getPackageName().equals(myPackage);
    }
}
