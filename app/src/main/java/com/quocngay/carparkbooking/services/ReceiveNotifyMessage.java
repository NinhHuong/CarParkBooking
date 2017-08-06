package com.quocngay.carparkbooking.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.activity.BookingActivity;
import com.quocngay.carparkbooking.activity.BookingDetailActivity;
import com.quocngay.carparkbooking.activity.LoginActivity;
import com.quocngay.carparkbooking.activity.MapActivity;
import com.quocngay.carparkbooking.other.Constant;

/**
 * Created by Quang Si on 8/4/2017.
 */

public class ReceiveNotifyMessage extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(getClass().getName(), "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(getClass().getName(), "Message data payload: " + remoteMessage.getData());
            if(remoteMessage.getData().get(Constant.MESSAGE).equals("booking_timeout")){
                String title = getResources().getString(R.string.notify_booking_timeout_title);
                String body = getResources().getString(R.string.notify_booking_timeout_body);
                sendNotification(title, body);
            }

            if(remoteMessage.getData().get(Constant.MESSAGE).equals("booking_canceled")){
                String title = getResources().getString(R.string.notify_booking_canceled_title);
                String body = getResources().getString(R.string.notify_booking_canceled_body);
                sendNotification(title, body);
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(getClass().getName(), "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getTitle(),
//                    remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String title, String body) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Constant.NOTIFICATION_TIME_OUT, mBuilder.build());
    }
}
