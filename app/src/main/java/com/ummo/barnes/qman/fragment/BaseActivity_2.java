package com.ummo.barnes.qman.fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ummo.barnes.qman.R;


/**
 * Created by barnes on 12/17/15.
 */
public class BaseActivity_2 extends AppCompatActivity
{
    protected void setUpToolbarWithTitle(String title, boolean hasBackButton)
    {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_actionbar_);
        setSupportActionBar(toolBar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayShowHomeEnabled(hasBackButton);
            getSupportActionBar().setDisplayHomeAsUpEnabled(hasBackButton);
        }
    }

    protected void enterFromBottomAnimation()
    {
        overridePendingTransition(R.anim.activity_open_translate_from_bottom, R.anim.activity_no_animation);
    }

    protected void exitToBottomAnimation()
    {
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_close_translate_to_bottom);
    }
}
