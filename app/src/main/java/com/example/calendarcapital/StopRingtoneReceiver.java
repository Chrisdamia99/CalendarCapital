package com.example.calendarcapital;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class StopRingtoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("StopRingtoneReceiver", "received stop ringtone intent");

        // Stop the ringtone
        if (AlarmReceiver.r != null) {
            AlarmReceiver.r.stop();
        }

        // Dismiss the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID);
    }


}
