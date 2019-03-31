package com.hackathon.radioetzionapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hackathon.radioetzionapp.Fragments.CommentsFragment;
import com.hackathon.radioetzionapp.Fragments.FavoritesFragment;
import com.hackathon.radioetzionapp.Fragments.HomeFragment;
import com.hackathon.radioetzionapp.R;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    Context context;

    // fragment stuff
    FragmentManager fm;
    FragmentTransaction ft;
    HomeFragment homeFrag;
    FavoritesFragment favFrag;
    CommentsFragment commFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(
                (BottomNavigationView.OnNavigationItemSelectedListener) context);


        // TODO fix fragments transition in NavBar >> check again when done ! //
        // TODO add search

        setFragments(); // initialize
        loadAllFragments(); // load all to view-group
        showFragment(homeFrag); // show only home frag at first
    }

    private void loadAllFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, homeFrag)
                .add(R.id.fragment_container, favFrag)
                .add(R.id.fragment_container, commFrag)
                .commit();
    }

    private void setFragments() {
        // our basic 3 fragments
        homeFrag = new HomeFragment();
        favFrag = new FavoritesFragment();
        commFrag = new CommentsFragment();
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
                fragment = homeFrag;
                break;

            case R.id.navigation_dashboard:
                fragment = favFrag;
                break;

            case R.id.navigation_notifications:
                fragment = commFrag;
                break;

        }

        //return loadFragment(fragment);

        return showFragment(fragment);
    }

    private boolean showFragment(Fragment fragment) {

        // hide all 3
        getSupportFragmentManager()
                .beginTransaction()
                .hide(favFrag)
                .hide(commFrag)
                .hide(homeFrag)
                .commit();

        // show selected
        getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commit();

        return true;
    }

}

