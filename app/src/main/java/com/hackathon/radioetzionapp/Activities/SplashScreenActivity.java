package com.hackathon.radioetzionapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.hackathon.radioetzionapp.R;


public class SplashScreenActivity extends AppCompatActivity {

    Animation anim;
    LinearLayout laySplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        runSplash();
    }

    private void runSplash() {

        // TODO choose one or more ...
        // Add Animation  ?
        // Background Sound ?
        //  personal logo / random logo ?


        // animation to pass time only // does nothing
        // add animation maybe // TO BE DECIDED ?!?!? //
        laySplash = findViewById(R.id.laySplash);
        anim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.splash_anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent); // go to main activity
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        laySplash.startAnimation(anim);
    }

}
