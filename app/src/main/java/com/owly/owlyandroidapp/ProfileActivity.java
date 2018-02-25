package com.owly.owlyandroidapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;


public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView imgUser = (ImageView)findViewById(R.id.imgUser);
        imgUser.setBackgroundResource(R.drawable.profile_user);

        ImageView str1 = (ImageView)findViewById(R.id.str1);
        str1.setBackgroundResource(R.drawable.star);
        ImageView str2 = (ImageView)findViewById(R.id.str2);
        str2.setBackgroundResource(R.drawable.star);
        ImageView str3 = (ImageView)findViewById(R.id.str3);
        str3.setBackgroundResource(R.drawable.star);
        ImageView str4 = (ImageView)findViewById(R.id.str4);
        str4.setBackgroundResource(R.drawable.star);
        ImageView str5 = (ImageView)findViewById(R.id.str5);
        str5.setBackgroundResource(R.drawable.star);
    }
}
