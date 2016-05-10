package com.ummo.barnes.qman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ummo.barnes.qman.fragment.BaseActivity;
import com.ummo.barnes.qman.fragment.ScrollViewFragment;
import com.ummo.barnes.qman.fragment.SelectableTreeFragment;
import com.ummo.barnes.qman.ummoAPI.JoinedQ;
import com.ummo.barnes.qman.ummoAPI.QUser;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.github.florent37.hollyviewpager.HollyViewPagerConfigurator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

/**
 * Created by barnes on 12/30/15.
 */
public class Main_Activity extends BaseActivity
{
    int pageCount = 2;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.hollyViewPager)
    HollyViewPager hollyViewPager;
    String qsJSON;
    List<JoinedQ> qnames = new ArrayList<JoinedQ>();
    List<ScrollViewFragment> frags = new ArrayList<ScrollViewFragment>();
    UmmoBrouadcatReciever broadcastReceiver;
    String qname;
    String qposition;
    String qalphaNum;

    @Override
    protected void exitToBottomAnimation() {
        NavUtils.navigateUpFromSameTask(this);
        super.exitToBottomAnimation();
    }

    @Override
    protected void onPause()
    {
        exitToBottomAnimation();
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.ummo.barnes.qman.CATEGORIES"));
        //enterFromBottomAnimation();
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        String cellnumb =  PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.PREF_USER_CELLNUMBER),"NULL");
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setBackgroundColor(getResources().getColor(R.color.ummo));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hollyViewPager.getViewPager().setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
        hollyViewPager.setConfigurator(new HollyViewPagerConfigurator() {
            @Override
            public float getHeightPercentForPage(int page) {
                return ((page + 4) % 10) / 10f;
            }
        });

        Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();

       /* if(extras == null)
        {
            hollyViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
            {
                @Override
                public Fragment getItem(int position)
                {
                    //if(position%2==0)
                    //    return new RecyclerViewFragment();
                    //else
                    qnames = new QUser(Main_Activity.this).getLocalJoinedQList();
                    enterFromBottomAnimation();
                    return ScrollViewFragment2.newInstance((String) getPageTitle(position));
                }

                @Override
                public int getCount() {
                    return pageCount;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return "TITLE" + position;
                }
            });
        }*/
        //else
        //{

       // }

        setView();
    }


    public void setView(){
        qnames = new QUser(this).getLocalJoinedQList();

        Log.d("MAINACT","Setting View");

        if (qnames.isEmpty())
        {
            TextView emptyQList = (TextView) findViewById(R.id.empty_qlist);
            emptyQList.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("QNAME)",qnames.get(0).getqName());
        }

        //Log.e("QNAME)",qnames.get(0).getqName());
            /*qsJSON = getIntent().getStringExtra("joinedQs");
            Log.d("QSTRING", qsJSON);
            try
            {
                String qAlphaNum ="";
                String pos = "";
                JSONArray qArrays = new JSONArray(qsJSON);
                for(int i=0;i<qArrays.length();i++)
                {
                    String q_name = qArrays.getJSONObject(i).getJSONObject("managedQ").getString("qName");
                    qname = q_name;
                    if(cellnumb!="NULL")
                    {
                        JoinedQ joinedQ = new JoinedQ(qArrays.getJSONObject(i),cellnumb);
                        qnames.add(joinedQ);
                        pos = qArrays.getJSONObject(i).getJSONObject("managedQ").getJSONObject("qErs").getJSONObject(cellnumb).getString("position");
                        qposition = getIntent().getStringExtra("qpos");
                        qAlphaNum = qArrays.getJSONObject(i).getJSONObject("managedQ").getJSONObject("qErs").getJSONObject(cellnumb).getString("numCode");
                        qalphaNum = qAlphaNum;
                    }
                    //TextView tv = (TextView)findViewById(R.id.man_one_text);
                    //tv.setText(qAlphaNum);
                }
            }
            catch (JSONException jse)
            {
                Log.e("JoinedQS",jse.toString());
            }*/

        hollyViewPager.setAdapter(adapter);
    }
    // handler for received Intents for the "my-event" event
    FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager())
    {

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position)
        {
            //if(position%2==0)
            //    return new RecyclerViewFragment();
            //else
            enterFromBottomAnimation();
            ScrollViewFragment sv = ScrollViewFragment.newInstance(qnames.get(position),qname, "4", "qalphaNum");
            frags.add(position,sv);

            return sv;
        }



        @Override
        public void notifyDataSetChanged() {

            super.notifyDataSetChanged();

        }



        @Override
        public int getCount() {
            return qnames.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return qnames.get(position).getqName();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            try{
                JSONObject obj = new JSONObject(message);
                QUser user = new QUser(Main_Activity.this);
                JoinedQ joinedQ = new JoinedQ(obj,user.getCellNumb());
                joinedQ.save(Main_Activity.this,user);
                qnames = new QUser(Main_Activity.this).getLocalJoinedQList();
                //adapter.notifyDataSetChanged();

                for(int i=0;i<qnames.size();i++){
                    frags.get(i).setInfo(qnames.get(i));
                }

                //hollyViewPager.removeAllViews();

                //setView();
            }
            catch (JSONException jse){
                Log.e("Q_ACTIVITY",jse.toString());
            }
            Log.d("receiver", "Got message: " + message);
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("com.ummo.barnes.qman.CATEGORIES"));
    }

}

class UmmoBrouadcatReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String mess =intent.getStringExtra("message");
        Log.d("MESSAGE",mess);
    }
}