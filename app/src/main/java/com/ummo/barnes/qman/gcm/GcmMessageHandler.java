package com.ummo.barnes.qman.gcm;

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

import com.ummo.barnes.qman.Main_Activity;
import com.ummo.barnes.qman.Qs;
import com.ummo.barnes.qman.R;
import com.ummo.barnes.qman.SingleFragmentActivity;
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
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        String cellNumber = sp.getString(getString(R.string.PREF_USER_CELLNUMBER),null);



        String message = data.getString("message");
        try{
            JSONObject object = new JSONObject(message);
            String title = object.getJSONObject("managedQ").getString("qName");
            String  myPos=String.valueOf(object.getJSONObject("managedQ").getJSONObject("qErs").getJSONObject(cellNumber).getInt("position") + 1);
            if(Integer.valueOf(myPos)<4){
                createNotification(title,"Position: "+myPos);
            }


        }





        catch (JSONException jse){
            Log.e("NOTIFICATION",jse.toString());
        }


        Intent intent = new Intent("com.ummo.barnes.qman.CATEGORIES");
        intent.putExtra("message",message);
        Log.e("GCM",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        //Context context = getBaseContext();
        Intent resultIntent = new Intent(this, Main_Activity.class);

        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Main_Activity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

}