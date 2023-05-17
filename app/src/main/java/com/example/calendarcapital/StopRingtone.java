package com.example.calendarcapital;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class StopRingtone extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the ringtone playback
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        Log.v("test2","stop2");
        r.stop();

        // Call onNotificationDeleted to remove the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
    }
}