package com.pfoss.countdownlivewallpaper.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.pfoss.countdownlivewallpaper.R;
import com.pfoss.countdownlivewallpaper.activities.MainActivity;

public class TimerDueBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MainActivity.TIMER_DUE)
                .setSmallIcon(R.drawable.ic_baseline_access_time_24)
                .setContentTitle(context.getResources().getString(R.string.timer_is_due))
                .setContentText(String.format(context.getResources().getString(R.string.timer_is_due_decription), intent.getStringExtra("timer_name")))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, notificationBuilder.build());


    }
}
