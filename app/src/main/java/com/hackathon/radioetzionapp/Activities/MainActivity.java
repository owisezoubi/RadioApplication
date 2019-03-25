package com.hackathon.radioetzionapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hackathon.radioetzionapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    public void onBackPressed() {
        // override to do nothing
        // i.e. not to finish the activity
        // so that won't go back to splash screen
    }
}

