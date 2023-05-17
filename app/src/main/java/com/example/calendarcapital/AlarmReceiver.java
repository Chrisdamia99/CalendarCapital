package com.example.calendarcapital;


import android.app.AlarmManager;
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
    private static final int SNOOZE_MINUTES = 5;

    private Vibrator vibrator;
    public static final String ACTION_STOP_RINGTONE = "com.example.app.ACTION_STOP_RINGTONE";
    private boolean isNotificationDeleted;

    public static final int NOTIFICATION_ID = 123;
    private AlarmManager alarmManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        event = "";
        comment = "";
isNotificationDeleted = false;

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
            text = "Υπενθύμιση για το συμβάν: " + "\n" + event + "\n" + "Σχόλια: " + "\n" + comment;
        }

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Intent stopIntent = new Intent(context, StopReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, 0);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"myandroid")
                .setSmallIcon(R.drawable.alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(event)
                .setContentText(text)
                .setContentIntent(stopPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(NotificationCompat.DEFAULT_ALL);



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(context, notification);
        r.play();




    }

    public void stopRingtone() {

        if (r != null && r.isPlaying()) {
            r.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }


    }

    public void getInApplication(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(),MainActivity.class);
        context.startActivity(i);
    }

    public static class StopReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Stop the ringtone
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.stopRingtone();
            alarmReceiver.getInApplication(context);



            // Dismiss the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.cancel(NOTIFICATION_ID);

        }
    }



}


