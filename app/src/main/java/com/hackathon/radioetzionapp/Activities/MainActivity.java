package com.hackathon.radioetzionapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hackathon.radioetzionapp.Fragments.CommentsFragment;
import com.hackathon.radioetzionapp.Fragments.FavoritesFragment;
import com.hackathon.radioetzionapp.Fragments.HomeFragment;
import com.hackathon.radioetzionapp.Fragments.SearchFragment;
import com.hackathon.radioetzionapp.R;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    Context context;

    // fragment stuff
    FragmentManager fm;
    HomeFragment homeFrag;
    FavoritesFragment favFrag;
    CommentsFragment commFrag;
    SearchFragment searchFrag;

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

    private void setFragments() {
        // our basic 4 fragments
        homeFrag = new HomeFragment();
        favFrag = new FavoritesFragment();
        commFrag = new CommentsFragment();
        searchFrag = new SearchFragment();
        // Fragment Manager
        fm = getSupportFragmentManager();
    }

    private void loadAllFragments() {
        fm.beginTransaction()
                .add(R.id.fragment_container, homeFrag)
                .add(R.id.fragment_container, favFrag)
                .add(R.id.fragment_container, commFrag)
                .add(R.id.fragment_container, searchFrag)
                .commit();
    }

    private boolean showFragment(Fragment fragment) {

        // first , hide all 4
        fm.beginTransaction()
                .hide(favFrag)
                .hide(commFrag)
                .hide(homeFrag)
                .hide(searchFrag)
                .commit();

        // then, show selected
        fm.beginTransaction()
                .show(fragment)
                .commit();

        return true;
    }


    @Override
    public void onBackPressed() {
        // override to do nothing
        // i.e. not to finish the activity
        // so that won't go back to splash screen
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = homeFrag;
                break;
            case R.id.navigation_favorites:
                fragment = favFrag;
                break;
            case R.id.navigation_comments:
                fragment = commFrag;
                break;
            case R.id.navigation_search:
                fragment = searchFrag;
                break;
        }
        //return loadFragment(fragment);
        return showFragment(fragment);
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }
}

