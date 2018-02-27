package com.owly.owlyandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class AboutusActivity extends AppCompatActivity {

    private TextView credits;
    private Animation animation;
    private Typeface century_gothic;
    private int counter = 0;
    String creditsText = "";

    private AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    private AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.credits);
        century_gothic = Typeface.createFromAsset(getAssets(), "fonts/century_gothic_bold.ttf");
        credits = (TextView) findViewById(R.id.credits);

        creditsText = "Owly is implemented by:\n\n\n" + //25
                "Khaled Jendi\n[khaledj@kth.se]\n\n" +
                "Conrado Gisbert\n[comg@kth.se]\n\n" +
                "Zahra Soleymanzadeh\n[zahraso@kth.se]\n\n\n" +
                "Github:\n" +
                "https://github.com/jSchnitzer1/OwlyAndroidApp";

        credits.setTypeface(century_gothic);
        credits.setText(creditsText);

        credits.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                credits.startAnimation(fadeOut);
                fadeOut.setDuration(2500);
                fadeOut.setFillAfter(true);
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                credits.startAnimation(fadeIn);
                fadeIn.setDuration(2500);
                fadeIn.setFillAfter(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (++counter > 2) {
                    //credits.setTextColor(Color.parseColor("#283886"));
                    SpannableString sString = new SpannableString(creditsText);
                    sString.setSpan(new ForegroundColorSpan(Color.parseColor("#FCB37D")), 0, 25, 0);
                    sString.setSpan(new ForegroundColorSpan(Color.parseColor("#88B1FF")), 40, 54, 0);
                    sString.setSpan(new ForegroundColorSpan(Color.parseColor("#88B1FF")), 74, 85, 0);
                    sString.setSpan(new ForegroundColorSpan(Color.parseColor("#88B1FF")), 109, 123, 0);
                    sString.setSpan(new ForegroundColorSpan(Color.parseColor("#FCB37D")), 127, 134, 0);
                    sString.setSpan(new ForegroundColorSpan(Color.parseColor("#88B1FF")), 134, creditsText.length(), 0);
                    sString.setSpan(new RelativeSizeSpan(0.9f), 134, creditsText.length(), 0);
                    credits.setText(sString);
                    return;
                }
                credits.startAnimation(fadeOut);
                fadeOut.setDuration(2500);
                fadeOut.setFillAfter(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
