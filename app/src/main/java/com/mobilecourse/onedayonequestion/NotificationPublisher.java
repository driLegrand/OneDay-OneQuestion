package com.mobilecourse.onedayonequestion;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationPublisher extends BroadcastReceiver {

    //when we receive an intent to make a notification
    @Override
    public void onReceive(Context context, Intent intent) {

        //creation of the notification
        Intent notfToActivity = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notfToActivity, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notif")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("One day, one question")
                .setContentText("Your daily question is ready !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(contentIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(2, builder.build());

    }
}