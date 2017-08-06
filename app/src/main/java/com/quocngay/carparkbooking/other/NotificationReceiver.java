package com.quocngay.carparkbooking.other;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.quocngay.carparkbooking.services.NotificationIntentService;
import com.quocngay.carparkbooking.services.ReceiveNotifyMessage;

/**
 * Created by Quang Si on 8/3/2017.
 */

public class NotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent notificationIntent = new Intent(context, NotificationIntentService.class);
//        context.startService(notificationIntent);
//        ComponentName comp = new ComponentName(context.getPackageName(),
//                NotificationIntentService.class.getName());
//        // Start the service, keeping the device awake while it is launching.
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
    }
}
