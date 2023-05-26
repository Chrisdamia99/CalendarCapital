package com.example.calendarcapital;



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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private String text;
    public static Ringtone r;

    private Vibrator vibrator;


    public static final int NOTIFICATION_ID = 123;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        String event = "";
        String comment;


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




        Intent stopIntent = new Intent(context, StopReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myandroid")
                .setSmallIcon(R.drawable.alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(event)
                .setContentText(text)
                .setContentIntent(stopPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(NotificationCompat.DEFAULT_ALL);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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



}


