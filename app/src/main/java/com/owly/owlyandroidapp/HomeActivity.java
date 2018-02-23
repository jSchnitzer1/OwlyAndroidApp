package com.owly.owlyandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class HomeActivity extends AppCompatActivity {

    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    private boolean isLoggedInFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_item:
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);

                if(isLoggedInFacebook()) {
                    LoginManager.getInstance().logOut();
                    i.putExtra("txtStatusMsg", "You are logged out Facebook successfully!");
                }
                startActivity(i);
                break;
            case R.id.profile_item:
                break;
            case R.id.about_us_item:
                Toast.makeText(HomeActivity.this, "About us...", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
