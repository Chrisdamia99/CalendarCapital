package com.example.calendarcapital;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmReceiver extends BroadcastReceiver {
Vibrator v;
String event;
String comment;
String text;
LocalDateTime now ;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle b = intent.getExtras();

        event ="";
        comment="";

        now = (LocalDateTime) b.get("calendar");
        Instant instant2 = Instant.from(now.atZone(ZoneId.of("Europe/Athens")));
        Date date = Date.from(instant2);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (b != null) {

            event = (String) b.get("title");
            comment = (String) b.get("comment");

            text = "Reminder for the Event: " + "\n" + event + "\n" + "Comments: " + "\n" + comment;
        }

        Timer t = new Timer();
        TimerTask tm = new TimerTask() {
            @Override
            public void run() {


                Intent activityIntent = new Intent(context, MainActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myandroid")
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle(event)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(pendingIntent)

                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(123, builder.build());

                Notification notification1 = builder.build();
                notification1.flags |= Notification.FLAG_AUTO_CANCEL;



                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 300, 1000};


//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//
//                Ringtone r = RingtoneManager.getRingtone(context, notification);
//                r.play();
                v.vibrate(VibrationEffect.createWaveform(pattern, -1));




//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (r.isPlaying())
//                            r.stop();
//                    }
//                }, 500 * 10);


            }
        };
        t.schedule(tm, calendar.getTime());



    }

}
