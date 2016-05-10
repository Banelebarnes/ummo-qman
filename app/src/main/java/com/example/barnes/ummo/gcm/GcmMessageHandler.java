package com.example.barnes.ummo.gcm;

/**
 * Created by sihle on 12/3/15.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.barnes.ummo.Main_Activity;
import com.example.barnes.ummo.Qs;
import com.example.barnes.ummo.R;
import com.example.barnes.ummo.SingleFragmentActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sihle on 11/28/15.
 */
public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        try{
            JSONObject object = new JSONObject(message);
            String title = object.getJSONObject("managedQ").getString("qName");
            createNotification(title,"Changed");

        }

        catch (JSONException jse){
            Log.e("NOTIFICATION",jse.toString());
        }


        Intent intent = new Intent("com.example.barnes.ummo.CATEGORIES");
        intent.putExtra("message",message);
        Log.e("GCM",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        Intent resultIntent = new Intent(this, Main_Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Main_Activity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

}