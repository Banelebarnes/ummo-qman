package com.ummo.barnes.qman.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ummo.barnes.qman.Main_Activity;
import com.ummo.barnes.qman.R;
import com.ummo.barnes.qman.ummoAPI.JoinedQ;
import com.ummo.barnes.qman.ummoAPI.QUser;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.uber.sdk.android.rides.RequestButton;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by barnes on 12/30/15.
 */
public class ScrollViewFragment extends Fragment {

    @Bind(R.id.scrollView)
    ObservableScrollView scrollView;

    @Bind(R.id.man_one_text)
    TextView alphanum;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.man_four)
    Button btn4;

    @Bind(R.id.man_one)
    Button btn1;

    @Bind(R.id.man_two)
            Button btn2;

    @Bind(R.id.man_three)
            Button btn3;
    @Bind(R.id.man_five)
            Button btn5;

    View v;
    private int i = -1;
    Context c;

    static String qName;
    static String qPosition;
    static String qAlphanum;



    public static ScrollViewFragment newInstance(JoinedQ joinedQ, String qname, String qposition, String qalphanum)
    {
        Bundle args = new Bundle();
        args.putString("title", joinedQ.getqName());
        args.putString("qname", qname);
        args.putString("qposition", joinedQ.getMyPos());
        //qPosition = joinedQ.getMyPos();
        Log.e("NEWINST", "Possition=" + joinedQ.getMyPos());
        args.putString("qalphanum", joinedQ.getMyAlphanumCode());
        ScrollViewFragment fragment = new ScrollViewFragment();
        fragment.setArguments(args);
        //Log.e("POSSITION",qPosition);
        return fragment;
    }

    public void setInfo(JoinedQ joinedQ){
        String pos = joinedQ.getMyPos();
        int ps = Integer.valueOf(pos)%5;

        RelativeLayout.LayoutParams btn1_params = (RelativeLayout.LayoutParams)btn1.getLayoutParams();
        Log.e("SETINFO","BTNPARAM="+btn1_params.height);
        btn1_params.width=75;
        btn1_params.height=75;
        btn1.setLayoutParams(btn1_params);
        btn1.setText("");
        RelativeLayout.LayoutParams btn2_params = (RelativeLayout.LayoutParams)btn2.getLayoutParams();
        btn2_params.width=75;
        btn2_params.height=75;
        btn2.setLayoutParams(btn2_params);
        btn2.setText("");
        RelativeLayout.LayoutParams btn3_params = (RelativeLayout.LayoutParams)btn3.getLayoutParams();
        btn3_params.width=75;
        btn3_params.height=75;
        btn3.setLayoutParams(btn3_params);
        btn3.setText("");
        RelativeLayout.LayoutParams btn4_params = (RelativeLayout.LayoutParams)btn4.getLayoutParams();
        btn4_params.width = 75;
        btn4_params.height= 75;
        btn4.setLayoutParams(btn4_params);
        btn4.setText("");
        RelativeLayout.LayoutParams btn5_params = (RelativeLayout.LayoutParams)btn5.getLayoutParams();
        btn5_params.width=75;
        btn5_params.height=75;
        btn5.setText("");
        btn5.setLayoutParams(btn5_params);

        Log.e("LOADINFO", "BTN" + String.valueOf(ps));
        switch (ps){
            case 1:
                loadQinfo(btn1,pos);
                break;
            case 2:
                loadQinfo(btn2,pos);
                break;
            case 3:
                loadQinfo(btn3,pos);
                break;
            case 4:
                loadQinfo(btn4,pos);
                break;
            case 0:
                loadQinfo(btn5,pos);
                break;
        }


    }

    public void loadQinfo(Button qPositionbtn, final String position)
    {
        Log.e("LOADINFO", "Possition=" + position);
        qPositionbtn.setText("#" + position);


        qPositionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(c, "", position);
            }
        });
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) qPositionbtn.getLayoutParams();
        params.width = 150;
        //ScaleAnimation sa = new ScaleAnimation(75,150,75,150);
        //sa.setDuration(1000);
        //qPositionbtn.setAnimation(sa);
        qPositionbtn.animate();
        params.height = 150;
        //params.rightMargin = 150;
        //params.rightMargin = 90;
        qPositionbtn.setLayoutParams(params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            qName = getArguments().getString("qname");
            qPosition = getArguments().getString("qposition");
            qAlphanum = getArguments().getString("qalphanum");
        }

      /*  LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("com.example.barnes.ummo.CATEGORIES"));*/
    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            try{
                JSONObject obj = new JSONObject(message);
                QUser user = new QUser(getActivity());
                JoinedQ joinedQ = new JoinedQ(obj,user.getCellNumb());
                joinedQ.save(getActivity(),user);
               // button.setText(joinedQ.getMyPos());
                Bundle bundle = ScrollViewFragment.this.getArguments();
                Log.e("SCROLLVIEW", "POS=" + ScrollViewFragment.this.getArguments().getString("qposition"));
               // loadQinfo(button, bundle.getString("qposition"));
                //((Main_Activity)(getActivity())).qnames = new QUser(getActivity()).getLocalJoinedQList();
                //adapter.notifyDataSetChanged();

                /*for(int i=0;i<qnames.size();i++){
                    ((ScrollViewFragment)adapter.getItem(i)).setInfo(qnames.get(i));
                }*/

                //hollyViewPager.removeAllViews();

                //setView();
            }
            catch (JSONException jse){
                Log.e("Q_ACTIVITY",jse.toString());
            }
            Log.d("receiver", "Got message: " + message);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_scroll, container, false);
        c = view.getContext();
        v=view;
        //int pos = Integer.parseInt(qPosition);
        String lastNumber = "4";
        Button man_Q_1 = (Button)view.findViewById(R.id.man_one);

        RequestButton requestButton = new RequestButton(c);
        requestButton.setClientId("uber_ummo");
        //requestButton.findViewById(R.id.man_one);
        qPosition = getArguments().getString("qposition");
        String pos = qPosition;
        int ps = Integer.valueOf(qPosition)%5;
        Button man_Q_2 = (Button)view.findViewById(R.id.man_two);
        Button man_Q_3 = (Button)view.findViewById(R.id.man_three);
        Button man_Q_4 = (Button)view.findViewById(R.id.man_four);
        Button man_Q_5 = (Button)view.findViewById(R.id.man_five);
        //man_Q_1.setText("5");
        //loadQinfo(man_Q_1, "5");

       // button = man_Q_1;
        /*

        if (lastNumber.equals("0"))
        {
         //   button=man_Q_5;
            loadQinfo(man_Q_5, qPosition);
        }
        else if (lastNumber.equals("1"))
        {
       //     button=man_Q_1;
            loadQinfo(man_Q_1, qPosition);
        }
        else if (lastNumber.equals("2"))
        {
         //   button = man_Q_2;
            loadQinfo(man_Q_2, qPosition);
        }
        else if (lastNumber.equals("3"))
        {
           // button = man_Q_3;
            loadQinfo(man_Q_3, qPosition);
        }
        else if (lastNumber.equals("4"))
        {
            loadQinfo(man_Q_4, qPosition);
        }
        else if (lastNumber.equals("5"))
        {
            loadQinfo(man_Q_5, qPosition);
        }
        else if (lastNumber.equals("6"))
        {
            loadQinfo(man_Q_1, qPosition);
        }
        else if (lastNumber.equals("7"))
        {
            loadQinfo(man_Q_2, qPosition);
        }
        else if (lastNumber.equals("8"))
        {
            loadQinfo(man_Q_3, qPosition);
        }
        else if (lastNumber.equals("9"))
        {
            loadQinfo(man_Q_4, qPosition);
        }*/
        //return inflater.inflate(R.layout.fragment_scroll, container, false);

        switch (ps){
            case 1:
                loadQinfo(man_Q_1,pos);
                break;
            case 2:
                loadQinfo(man_Q_2,pos);
                break;
            case 3:
                loadQinfo(man_Q_3,pos);
                break;
            case 4:
                loadQinfo(man_Q_4,pos);
                break;
            case 0:
                loadQinfo(man_Q_5,pos);
                break;
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
            Log.e("CREATEVIEW", "Position" + getArguments().getString("qposition"));
        title.setText(getArguments().getString("title"));
        alphanum.setText(getArguments().getString("qalphanum"));
        HollyViewPagerBus.registerScrollView(getActivity(), scrollView);
    }

    public void dialog(Context context, final String text, final String position)
    {
        final Context con = context;
        final String text_ = text;
        final SweetAlertDialog pDialog;
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(" "+text)
                .setContentText("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);
        new CountDownTimer(800 * 7, 800)
        {
            public void onTick(long millisUntilFinished)
            {
                i++;
                switch (i)
                {
                    case 0:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.blue_btn_bg_color));
                        break;
                    case 1:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.material_deep_teal_50));
                        break;
                    case 2:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.success_stroke_color));
                        break;
                    case 3:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.material_deep_teal_20));
                        break;
                    case 4:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.material_blue_grey_80));
                        break;
                    case 5:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.warning_stroke_color));
                        break;
                    case 6:
                        pDialog.getProgressHelper().setBarColor(con.getResources().getColor(R.color.success_stroke_color));
                        break;
                }
            }
            public void onFinish()
            {
                i = -1;
                pDialog.setTitleText(" " + text_ + " Queue")
                        .setContentText("Position in queue : "+ position +
                                "\n" + "Length of queue : 22" +
                                "\n" + "Service time : 04:17" +
                                "\n" + "Wait time :01:05")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //pDialog.dismiss();
                            }
                        });
            }
        }.start();
    }


}