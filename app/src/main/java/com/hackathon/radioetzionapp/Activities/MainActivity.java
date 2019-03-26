package com.hackathon.radioetzionapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hackathon.radioetzionapp.Fragments.FavoritesFragment;
import com.hackathon.radioetzionapp.Fragments.HomeFragment;
import com.hackathon.radioetzionapp.Fragments.CommentsFragment;
import com.hackathon.radioetzionapp.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar_layout);

        context = this;

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) context);

        loadFragment(new HomeFragment());

    }


    @Override
    public void onBackPressed() {
        // override to do nothing
        // i.e. not to finish the activity
        // so that won't go back to splash screen
    }

    private boolean loadFragment (Fragment fragment){
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;

        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_dashboard:
                fragment = new FavoritesFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new CommentsFragment();
                break;

        }

        return loadFragment(fragment);
    }
}

