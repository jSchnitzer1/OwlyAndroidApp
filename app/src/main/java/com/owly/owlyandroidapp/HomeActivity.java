package com.owly.owlyandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.owly.owlyandroidapp.bean.Item;
import com.owly.owlyandroidapp.fresco.FrescoAdapter;
import com.owly.owlyandroidapp.view.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    AccessToken accessToken;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.rvList) RecyclerView rvList;
    TextView tvLastItem;
    private FrescoAdapter itemAdapter;
    private List<Item> listItem = new ArrayList<>();

    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

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
        Fresco.initialize(this);

        ButterKnife.bind(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        final GridLayoutManager manager = new GridLayoutManager(this, 6);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                int index = position % 5;
                switch (index) {
                    case 0:
                        return 2;
                    case 1:
                        return 2;
                    case 2:
                        return 2;
                    case 3:
                        return 3;
                    case 4:
                        return 3;
                }
                return -1;
            }
        });

        rvList.setLayoutManager(staggeredGridLayoutManager);
        itemAdapter = new FrescoAdapter(this);
        for (int i = 0; i < 10; i++) {
            listItem.add(ItemsSingleton.INSTANCE.getITEMS().get(i));
        }
        itemAdapter.setListItem(listItem);
        rvList.setAdapter(itemAdapter);
        rvList.setItemAnimator(null);

        endlessRecyclerViewScrollListener =
                new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {

                    @Override public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
           /* Log.e("", "onLoadMore: ---------------------------------------->" + page);*/
                        List<Item> localList = new ArrayList<>();
                        if (page == 1) {
                            for (int i = 10; i < 20; i++) {
                                localList.add(ItemsSingleton.INSTANCE.getITEMS().get(i));
                            }
                        } else if (page == 2) {
                            for (int i = 21; i < 30; i++) {
                                localList.add(ItemsSingleton.INSTANCE.getITEMS().get(i));
                            }
                        }
                        listItem.addAll(localList);
                        itemAdapter.setListItem(listItem);
                    }

                    @Override public void lastVisibleItem(int lastVisibleItem) {
//            Log.e("", "lastVisibleItem: -------------------------------------->" + lastVisibleItem);
//            tvLastItem.setText("With in " + lastVisibleItem);
                    }
                };
        rvList.addOnScrollListener(endlessRecyclerViewScrollListener);
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
               // if (firebaseAuth.getCurrentUser() == null && !isLoggedInFacebook()) {
                   // goToLoginActivity("You are logged out Google successfully!");
              //  }
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
