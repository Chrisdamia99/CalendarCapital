package com.example.calendarcapital;



import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private String text;
    public static Ringtone r;

    private Vibrator vibrator;

    private static String alarm_id;
    private static String alarm_time;
    private static String alarm_tittle;
    private static String alarm_comment;
    private static int snoozeFlag;
    public static final int NOTIFICATION_ID = 123;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        String event = "";
        String comment = "";
        String id = "";
        String timeAlarm = "";

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            }

        }

        if (b != null) {
            event = b.getString("title");
            comment = b.getString("comment");
            timeAlarm = String.valueOf(b.getLong("alarmtime"));
            id = String.valueOf(b.getInt("alarmid"));

            text = "Υπενθύμιση για το συμβάν: " + "\n" + event + "\n" + "Σχόλια: " + "\n" + comment;
        }


            if (snoozeFlag==0)
            {
                alarm_id=id;
                alarm_time=timeAlarm;
                alarm_tittle=event;
                alarm_comment=comment;
            }





        Intent stopIntent = new Intent(context, StopReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        snoozeFlag=0;
        Intent snoozeIntent = new Intent(context, snooze.class);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myandroid")
                .setSmallIcon(R.drawable.alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(event)
                .setContentText(text)
                .setContentIntent(stopPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(new NotificationCompat.Action(R.drawable.clock,"Αναβολη",snoozePendingIntent))
                .setAutoCancel(true);



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
    public void snooze(String alarmId, String currentTimeMillis,Context context,String tittle,String comment)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        snoozeFlag=1;
        // Set the snooze time (e.g., 10 minutes)
        int snoozeMinutes = 5;
        long snoozeTime = snoozeMinutes * 60 * 1000;
        long snoozeTimeMillis = Long.parseLong(currentTimeMillis) +snoozeTime;

        // Create an Intent for the alarm receiver class
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmid", alarmId);
        intent.putExtra("alarmtime", snoozeTimeMillis);
        intent.putExtra("title", tittle);
        intent.putExtra("comment", comment);
        if (snoozeFlag==1)
        {
            alarm_time= String.valueOf(snoozeTimeMillis);
            alarm_id=alarmId;
            alarm_tittle=tittle;
            alarm_comment=comment;

        }

        // Create a PendingIntent for the alarm receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(alarmId), intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the snooze alarm using the AlarmManager
        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent);
            }else
            {
                alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent);

            }

        }
    }

    public void getInApplication(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(),MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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


    public static class snooze extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.snooze(alarm_id,alarm_time,context,alarm_tittle,alarm_comment);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.cancel(NOTIFICATION_ID);

        }
    }


}


