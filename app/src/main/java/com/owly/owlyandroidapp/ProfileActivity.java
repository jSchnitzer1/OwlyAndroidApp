package com.owly.owlyandroidapp;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends Activity implements OnMapReadyCallback {

    AccessToken accessToken;
    FirebaseAuth mAuth;

    TextView username;
    CircleImageView imgUser;

    GoogleMap map;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeGUI();
        initializeFacebook();
        initializeGoogle();
        showLoginInfo();
        initializeGoogleMaps();
    }

    private void initializeGoogleMaps() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeGUI() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //goToLocation(59.329073, 18.068043, 13);
        geoLocate();
    }

    private void goToLocation(double lat, double lng, int zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        map.moveCamera(update);
    }

    private void geoLocate() {
        String location = "HM Stockholm";
        Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = gc.getFromLocationName(location, 1);
            if(list.size() > 0) {
                Address address = list.get(0);
                String locality = address.getLocality();
                double lat = address.getLatitude();
                double lng = address.getLongitude();

                setMarker(locality, lat, lng, location);
            }

        } catch (IOException e) {
        }
    }

    private void setMarker(String locality, double lat, double lng, String location) {
        if(marker != null)
            marker.remove();

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hm_48))
                .position(new LatLng(lat, lng))
                .snippet(location);

        marker = map.addMarker(options);
        goToLocation(lat, lng, 17);
    }
}
