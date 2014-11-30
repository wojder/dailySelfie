package com.example.DailySelfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by wojder on 30.11.14.
 */
public class AlarmReceiverNotification extends BroadcastReceiver {


    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "Selfie Alarm Notfication Receiver";
    private Intent selfieNotificationIntent;
    private PendingIntent selfieNotificationPendingIntent;
    private java.lang.CharSequence tickerText = "Take selfie again!";
    private CharSequence notificationTitle = "Selfie ALARM";
    private CharSequence notificationContent = "Cmon and get back take another selfie!";
    private long[] vibratePattern = {0, 200, 200, 300};

    @Override
    public void onReceive(Context context, Intent intent) {

        selfieNotificationIntent = new Intent(context, MainActivity.class);

        selfieNotificationPendingIntent = PendingIntent.getBroadcast(context, 0, selfieNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification.Builder notificationBuilder = NotificationBuilderHelper(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        Log.i(TAG, "Sending notification at: " + DateFormat.getDateTimeInstance().format(new Date()));
    }

    private Notification.Builder NotificationBuilderHelper(Context context) {
        Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new Notification.Builder(context)
                .setTicker(tickerText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(selfieNotificationPendingIntent)
                .setSound(uriSound)
                .setVibrate(vibratePattern);
    }
}
