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
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class RepeatReceiver extends BroadcastReceiver {

    String event;
    String comment;
    String text;
    private Ringtone r;
    private Vibrator vibrator;
    private boolean isNotificationDeleted;



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

            event = (String) b.get("title");
            comment = (String) b.get("comment");
            text = "Reminder for the Event: " + "\n" + event + "\n" + "Comments: " + "\n" + comment;
        }


        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Intent stopRingtoneIntent = new Intent(context, StopRingtoneReceiver.class);
        PendingIntent pendingStopRingtoneIntent = PendingIntent.getBroadcast(context, 0, stopRingtoneIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myandroid")
                .setSmallIcon(R.drawable.alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(event)
                .setContentText(text)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingStopRingtoneIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(context, notification);
        r.play();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isNotificationDeleted) {
                    stopRingtoneRepeat();
                }
            }
        }, 1000);


    }

    public void stopRingtoneRepeat() {
        if (r != null && r.isPlaying()) {
            r.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }


    public void onNotificationDeleted(Context context, int notificationId, int reason) {
        isNotificationDeleted = true;
        stopRingtoneRepeat();
    }


}