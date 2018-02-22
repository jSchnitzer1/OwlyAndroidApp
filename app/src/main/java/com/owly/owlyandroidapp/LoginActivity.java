package com.owly.owlyandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView owlyIcon = (ImageView)findViewById(R.id.owlyIcon);
        owlyIcon.setBackgroundResource(R.drawable.login_owly_icon);

        ImageView owlyTxtImg = (ImageView)findViewById(R.id.owlyTxtImg);
        owlyTxtImg.setBackgroundResource(R.drawable.owly_text_main);

        ImageView bankIdImg = (ImageView)findViewById(R.id.bankIdImg);
        bankIdImg.setBackgroundResource(R.drawable.bankid);
    }

    public void login(View v){
        if(v.getId() == R.id.login) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
        }
    }
}