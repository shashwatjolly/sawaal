package com.sgsj.sawaal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class ParsePushReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_DATA_KEY = "com.parse.Data";

//    @Override
//    protected android.support.v4.app.NotificationCompat.Builder getNotification(Context context, Intent intent) {
//        // deactivate standard notification
//        return null;
//    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        // Implement
        JSONObject data = getDataFromIntent(intent);
        Log.e("TAG", "onPushOpen: "+data);

    }

//    @Override
//    protected void onPushReceive(Context context, Intent intent) {
//        JSONObject data = getDataFromIntent(intent);
//        // Do something with the data. To create a notification do:
//
//        NotificationChannel channel = new NotificationChannel("channel01", "name",
//                NotificationManager.IMPORTANCE_HIGH);   // for heads-up notifications
//        channel.setDescription("description");
//
//// Register channel with system
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//
//    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}