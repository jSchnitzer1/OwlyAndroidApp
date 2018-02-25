package com.owly.owlyandroidapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends Activity {

    AccessToken accessToken;
    FirebaseAuth mAuth;

    TextView username;
    CircleImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        imgUser = (CircleImageView) findViewById(R.id.imgUser);
        username = (TextView) findViewById(R.id.username);

        initializeFacebook();
        initializeGoogle();
        showLoginInfo();
    }

    private void showLoginInfo() {
        if (isLoggedInFacebook()) {
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                String name = profile.getName();
                String imgUrl = profile.getProfilePictureUri(70, 70).toString();

                username.setText(name);
                Picasso.with(getApplicationContext()).load(imgUrl)
                        .placeholder(R.drawable.default_user).error(R.drawable.ic_launcher_background)
                        .into(imgUser);

                return;
            }
        } else if (isLoggedInGoogle()) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String name = acct.getDisplayName();
                String imgUrl = acct.getPhotoUrl().toString();

                username.setText(name);
                Picasso.with(getApplicationContext()).load(imgUrl)
                        .placeholder(R.drawable.default_user).error(R.drawable.ic_launcher_background)
                        .into(imgUser);
                return;
            }
        }

        username.setText("Unknown");
        imgUser.setImageResource(R.drawable.default_user);

    }

    private void initializeFacebook() {
        accessToken = AccessToken.getCurrentAccessToken();
    }

    private void initializeGoogle() {
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean isLoggedInFacebook() {
        return accessToken != null;
    }

    private boolean isLoggedInGoogle() {
        return mAuth != null;
    }

}
