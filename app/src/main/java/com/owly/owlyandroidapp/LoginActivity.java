package com.owly.owlyandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginActivity extends Activity {

    TextView txtStatus;
    LoginButton login_button;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeControls();
        loginFB();

        if(isLoggedIn())
            goToHomeActivity();

        ImageView owlyIcon = (ImageView) findViewById(R.id.owlyIcon);
        owlyIcon.setBackgroundResource(R.drawable.login_owly_icon);

        ImageView owlyTxtImg = (ImageView) findViewById(R.id.owlyTxtImg);
        owlyTxtImg.setBackgroundResource(R.drawable.owly_text_main);

        ImageView bankIdImg = (ImageView) findViewById(R.id.bankIdImg);
        bankIdImg.setBackgroundResource(R.drawable.bankid);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String txtStatusMsg = extras.getString("txtStatusMsg");
            if(txtStatusMsg != null) {
                txtStatus.setText(txtStatusMsg);
            }
        }

    }

    private void loginFB() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                txtStatus.setText("Successfully logged in Facebook");
                goToHomeActivity();
            }

            @Override
            public void onCancel() {
                txtStatus.setText("Facebook Login canceled!");
            }

            @Override
            public void onError(FacebookException exception) {
                txtStatus.setText("Login error: " + exception.getMessage());
            }
        });
    }

    private void goToHomeActivity() {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
    }

    private void initializeControls() {
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        login_button = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void login(View v) {
        if (v.getId() == R.id.login) {
            goToHomeActivity();
        }
    }
}