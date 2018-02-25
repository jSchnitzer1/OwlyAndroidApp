package com.owly.owlyandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    AccessToken accessToken;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeFacebook();
        initializeGoogle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showLoginStatus();

    }

    private void showLoginStatus() {
        if (isLoggedInFacebook()) {
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                String firstName = profile.getFirstName();
                Snackbar.make(findViewById(R.id.search), "Hi " + firstName + "\nYou are logged in using Facebook account", Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(findViewById(R.id.search), "Hi\nYou are logged in using Facebook account", Snackbar.LENGTH_SHORT).show();
            }
        } else if (isLoggedInGoogle()) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personGivenName = acct.getGivenName();
                Snackbar.make(findViewById(R.id.search), "Hi " + personGivenName + "\nYou are logged in using Google account", Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    private void initializeFacebook() {
        accessToken = AccessToken.getCurrentAccessToken();
    }

    private void initializeGoogle() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null && !isLoggedInFacebook()) {
                    goToLoginActivity("You are logged out Google successfully!");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    private boolean isLoggedInFacebook() {
        return accessToken != null;
    }

    private boolean isLoggedInGoogle() {
        return mAuth != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_item:
                if (isLoggedInFacebook()) {
                    LoginManager.getInstance().logOut();
                    goToLoginActivity("You are logged out Facebook successfully!");
                } else if (isLoggedInGoogle()) {
                    mAuth.signOut();
                }
                break;
            case R.id.profile_item:
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.about_us_item:
                Toast.makeText(HomeActivity.this, "About us...", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity(String msg) {
        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
        i.putExtra("txtStatusMsg", msg);
        startActivity(i);
    }
}
