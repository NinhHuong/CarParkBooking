package com.quocngay.carparkbooking.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.activity.LoginActivity;
import com.quocngay.carparkbooking.activity.MapActivity;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Quang Si on 7/11/2017.
 */

public class NotificationIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (SocketIOClient.client == null) {
            new SocketIOClient();
        }
        Emitter.Listener onResponseNotiTimeOut = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    if (jsonObject.getBoolean(Constant.RESULT)) {
                        createTimeOutNotification();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_NOTI_TIME_OUT, onResponseNotiTimeOut);

    }

    private void createTimeOutNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(NotificationIntentService.this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        Intent resultIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);
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

    private void deliverResultToReceiver(int resultCode, String message, String title) {

        Bundle bundle = new Bundle();
        bundle.putString(Constant.RESULT_DATA_KEY, message);
        bundle.putString(Constant.RESULT_TITLE, title);
        mReceiver.send(resultCode, bundle);
    }

}
