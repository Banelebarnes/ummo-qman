package com.ummo.barnes.qman.ummoAPI;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ummo.barnes.qman.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by barnes on 11/1/15.
 */
public class QUser
{
    private Activity callingActivity;
    private String uCellNumber;
    private boolean registered = false;
    private String uName;
    private boolean mBound=false;
    private UmmoDaemon daemon;
    //private UmmoDaemon ummoDaemon;
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.e("Function","onServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UmmoDaemon.LocalBinder binder = (UmmoDaemon.LocalBinder) service;
            daemon = binder.getService();
            mBound = true;
            daemon.setCalee(callingActivity);
            daemon.getUpadates(QUser.this);
            daemon.makeNotification();
            if(daemon==null)
            {
                Log.e("Error3","Demon is NULL");
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public boolean isRegistered() {
        return registered;
    }

    public void setCallingActivity(Activity activity) {
        callingActivity = activity;
    }

    public void register(final String name, final String cellNumber)
    {
        try
        {
            uName = name;
            uCellNumber = cellNumber;
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "user/register";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("cellnum", cellNumber);
            formPoster.add("alias", name);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final String uname = name;
                        final String cellphone = cellNumber;
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).userRegistered(objString);
                            }
                        });
                        //This would mean the registration was compleate
                        //
                        //JSONObject obj = new JSONObject(objString);
                        //Set the Shared Preferences for User Name and CellNumber
                        SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(callingActivity);
                        sp.edit().putString(callingActivity.getString(R.string.PREF_USER_NAME), uname).apply();
                        sp.edit().putString(callingActivity.getString(R.string.PREF_USER_CELLNUMBER), cellphone).apply();
                        sp.edit().putBoolean(callingActivity.getString(R.string.PREF_USER_REGISTERED), true).apply();
                        //Log.d("Response",id);
                        //Toast.makeText(calee,"Sent Information",Toast.LENGTH_LONG).show();
                    } catch (final IOException ioe)
                    {
                        Log.e("IO Exception", ioe.toString());
                        Log.e("IO Exception", ioe.toString());
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).userRegistrationError(ioe.toString());
                            }
                        });
                    }
                }
            });
            thread.start();
        }
        catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }
    public QUser(Activity activity)
    {
        setCallingActivity(activity);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(callingActivity);
        registered = sp.getBoolean(callingActivity.getString(R.string.PREF_USER_REGISTERED), false);
        if (registered)
        {
            uName = sp.getString(callingActivity.getString(R.string.PREF_USER_NAME), "NO NAME");
            uCellNumber = sp.getString(callingActivity.getString(R.string.PREF_USER_CELLNUMBER), "NO NUMBER");
            //   Intent intent = new Intent(Register.this, MainActivity.class);
            //  startActivity(intent);
            //  finish();
            Log.d("prefs", "registered");
        }
        //startUpdatesDaemon();
        if (daemon==null)
        {
            Log.e("Error","Daemon is still null");
        }
    }

    public void getAvailableQs()
    {
        try
        {
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "/user/availQs";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            //formPoster.add("uid",uCellNumber);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).allQsReady(objString);
                            }
                        });
                    }
                    catch (final IOException ioe)
                    {
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).allQError(ioe.toString());
                            }
                        });
                    }
                }
            });
            thread.start();
        } catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }

    public void setName()
    {
    }

    public UmmoDaemon getDaemon() {
        return daemon;
    }

    public void setCellNumber()
    {
    }

    public String getName() {
        return uName;
    }

    public String getCellNumb() {
        return uCellNumber;
    }

    public void getCategories()
    {
        try
        {
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "user/categories";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("user",uCellNumber);
            String token = PreferenceManager.getDefaultSharedPreferences(callingActivity).getString("gcmToken","token");
            formPoster.add("token",token);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).categoriesReady(objString);
                            }
                        });
                    }
                    catch (final IOException ioe)
                    {
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).categoriesError(ioe.toString());
                            }
                        });
                    }
                }
            });
            thread.start();
        }
        catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }

    public void getQ(String qcell)
    {
        try {

            String urlString = callingActivity.getString(R.string.SERVER_URL) + "user/getQ";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("qid",qcell);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream is = formPoster.post();

                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();

                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).qReady(objString);
                            }
                        });


                    } catch (final IOException ioe) {
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).qError(ioe.toString());
                            }
                        });
                    }

                }
            });

            thread.start();


        } catch (MalformedURLException me) {
            Log.e("NetWork Exception", me.toString());
        }

    }

    public void clearQList(){
        Log.e("QUSER","Clearing Qs");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(callingActivity);
        String arrString = sp.getString("JOINEDQS_LIST_PREF","PREF");
        if (arrString!="PREF"){
            try {
                JSONArray arr = new JSONArray(arrString);
                for (int i=0;i<arr.length();i++){
                    new JoinedQ().deleateQ(arr.getString(i),callingActivity);
                }

                sp.edit().remove("JOINEDQS_LIST_PREF").commit();
            }

            catch (JSONException jse){
                Log.e("CLEARQS",jse.toString());
            }
        }
    }

    public List<JoinedQ> getLocalJoinedQList(){
        List<JoinedQ> qlist = new ArrayList<JoinedQ>();
        String arrString = PreferenceManager.getDefaultSharedPreferences(callingActivity).getString("JOINEDQS_LIST_PREF","PREF");
        if (arrString!="PREF"){
            try {
                JSONArray arr = new JSONArray(arrString);
                for (int i=0;i<arr.length();i++){
                    JoinedQ joinedQ = new JoinedQ();
                    joinedQ.readByQId(arr.getString(i), callingActivity);

                   // Log.d("QNAME",joinedQ.getMyPos());
                    qlist.add(joinedQ);
                }
            }

            catch (JSONException jse){
                Log.e("LOCALQLIST",jse.toString());
            }
        }

        return qlist;
    }

    public void updateJoinedQList(JSONArray arr){
        clearQList();
        Log.e("USER","UpdateList");
        try {
            JSONArray jsonArray = new JSONArray();
            for(int i =0; i<arr.length();i++){
                Log.d("ARRAYLENGTH",String.valueOf(arr.length()));
                JoinedQ joinedQ= new JoinedQ(arr.getJSONObject(i),uCellNumber);
                jsonArray.put(joinedQ.getqId());
            }

            PreferenceManager.getDefaultSharedPreferences(callingActivity).edit().putString("JOINEDQS_LIST_PREF", jsonArray.toString()).commit();
        }

        catch (JSONException jse){
            Log.e("UPDTAEJQLIST",jse.toString());
        }


        try {
            JSONArray jsonArray = new JSONArray();
            for(int i =0; i<arr.length();i++){
                Log.d("ARRAYLENGTH",String.valueOf(arr.length()));
                JoinedQ joinedQ= new JoinedQ(arr.getJSONObject(i),uCellNumber);
                joinedQ.save(callingActivity,this);
            }


        }

        catch (JSONException jse){
            Log.e("UPDTAEJQLIST",jse.toString());
        }

    }

    public void getProvides()
    {
    }


    public boolean isQJoined(String q_id){
        boolean retval = false;
        String arrString = PreferenceManager.getDefaultSharedPreferences(callingActivity).getString("JOINEDQS_LIST_PREF","PREF");
        if (arrString!="PREF"){
            try {
                JSONArray arr = new JSONArray(arrString);
                for (int i=0;i<arr.length();i++){
                    Log.d("ISJOINED",arr.getString(i));
                    Log.d("QIDISJ",q_id);
                    if(q_id.equals(arr.getString(i))){
                        retval=true;
                        Log.d("JOINED",q_id);
                    }
                }
            }

            catch (JSONException jse){
                Log.e("ISQJOINED",jse.toString());
            }
        }

        else {
            Log.e("ISQJOINED",arrString);
        }

        return retval;
    }
    public void updateJoinedQs()
    {
        try
        {
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "user/joinedQs";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("uid", uCellNumber);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        Log.e("QUSER","JoinedQS"+objString);
                        try{
                            JSONArray arr = new JSONArray(objString);
                            updateJoinedQList(arr);

                        }

                        catch (JSONException jse){
                            Log.e("JSONERR",jse.toString());
                        }
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).updated(objString);
                            }
                        });
                        Log.d("QUSER_UPDATE",response.toString());
                    }
                    catch (final IOException ioe)
                    {

                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).updateError(ioe.toString());
                            }
                        });

                        Log.e("quser",ioe.toString());
                    }
                }
            });
            thread.start();
        }
        catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }

    public void joinQ(String qCellnumber)
    {
        try
        {
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "/user/joinQ";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("uid", uCellNumber);
            formPoster.add("qid", qCellnumber);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).qJoined(objString);
                            }
                        });
                    }
                    catch (final IOException ioe)
                    {
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).qJoinedError(ioe.toString());
                            }
                        });
                    }
                }
            });
            thread.start();
        }
        catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }

    public void leaveQ(String qCellnumber)
    {
        try
        {
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "/user/leaveQ";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("uid", uCellNumber);
            formPoster.add("qid", qCellnumber);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).qLeft(objString);
                            }
                        });
                    }
                    catch (final IOException ioe)
                    {
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).qLeftError(ioe.toString());
                            }
                        });
                    }
                }
            });
            thread.start();
        }
        catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }

    public void startUpdatesDaemon()
    {
        Log.e("daemon","about To satrt");
        Intent intent = new Intent(callingActivity, UmmoDaemon.class);
        if((callingActivity!=null)||(mConnection!=null))
        {
            callingActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.e("Activity","Calling activity is not null");
        }
        else
        {
            Log.e("Fuck","Calling activity is null");
        }
    }

    public void makeNotification()
    {
        if(daemon == null)
        {
            Log.e("DAEMON", "Still Null");
        }
        else
        {
            Log.e("DAEMON","YAY ITS NOT NULL");
        }
    }

    public ServiceConnection getServiceConnection()
    {
        return new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                UmmoDaemon.LocalBinder binder = (UmmoDaemon.LocalBinder) service;
                daemon = binder.getService();
                daemon.setCalee((QUser.this).callingActivity);
            }
            @Override
            public void onServiceDisconnected(ComponentName arg0)
            {
                //mBound = false;
            }
        };
    }

    public void getJoinedQs()
    {
        try
        {
            String urlString = callingActivity.getString(R.string.SERVER_URL) + "/user/getJoinedQs";
            final FormPoster formPoster = new FormPoster(new URL(urlString));
            formPoster.add("uid", uCellNumber);
            formPoster.add("data", "data");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        InputStream is = formPoster.post();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        final StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                        String line;
                        while ((line = rd.readLine()) != null)
                        {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        final String objString = response.toString();
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).gotJoinedQs(objString);
                            }
                        });
                    }
                    catch (final IOException ioe)
                    {
                        callingActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((QUserListner) callingActivity).joinedQsError(ioe.toString());
                            }
                        });
                    }
                }
            });
            thread.start();
        }
        catch (MalformedURLException me)
        {
            Log.e("NetWork Exception", me.toString());
        }
    }
}