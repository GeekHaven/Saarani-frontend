package com.example.calendarapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class ButtonReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {

        int id= Objects.requireNonNull(intent.getExtras()).getInt("not",0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,  0, new Intent(), 0);
        NotificationCompat.Builder mb = new NotificationCompat.Builder(context);
        mb.setContentIntent(resultPendingIntent);
    }
}
