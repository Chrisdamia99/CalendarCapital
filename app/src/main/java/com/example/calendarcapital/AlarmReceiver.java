package com.example.calendarcapital;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private String event;
    private String comment;
    private String text;
    public static Ringtone r;
    private Vibrator vibrator;
    public static final String ACTION_STOP_RINGTONE = "com.example.app.ACTION_STOP_RINGTONE";

    public static final int NOTIFICATION_ID = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        event = "";
        comment = "";


        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
        }

        if (b != null) {
            event = b.getString("title");
            comment = b.getString("comment");
            text = "Reminder for the Event: " + "\n" + event + "\n" + "Comments: " + "\n" + comment;
        }

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopRingtoneIntent = new Intent(context, StopRingtoneReceiver.class);
        PendingIntent pendingStopRingtoneIntent = PendingIntent.getBroadcast(context, 0, stopRingtoneIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d("AlarmReceiver", "pendingStopRingtoneIntent created");
        Intent stopRingtoneButtonIntent = new Intent(ACTION_STOP_RINGTONE);
        PendingIntent pendingStopRingtoneButtonIntent = PendingIntent.getBroadcast(context, 0, stopRingtoneButtonIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create the notification action button to stop the ringtone
        NotificationCompat.Action stopRingtoneAction = new NotificationCompat.Action.Builder(
                R.drawable.cancel_reminder,
                "Stop Ringtone",
                pendingStopRingtoneButtonIntent)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myandroid")
                .setSmallIcon(R.drawable.alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(event)
                .setContentText(text)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingStopRingtoneIntent)
                .addAction(stopRingtoneAction)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(context, notification);
        r.play();


    }

}


