package com.example.arnab.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RemindMeReceiver extends BroadcastReceiver {

    public RemindMeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, ReminderService.class);
        context.startService(startServiceIntent);
    }
}
