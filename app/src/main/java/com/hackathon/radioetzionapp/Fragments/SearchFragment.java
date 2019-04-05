package com.hackathon.radioetzionapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class SearchFragment extends Fragment {


    View rootView;
    Toolbar toolbar;
    Context context;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    MaterialSearchView mMaterialSearchView;

    Fragment HomeFrag, SearchFrag;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            // clear action bar // hide //
            ((AppCompatActivity) getActivity()).setSupportActionBar(null);
        } else {
            // show action bar //
            toolbar = rootView.findViewById(R.id.searchToolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            // refresh/reset adapter  // in case there was no internet at onStart //
            setSearchPointers();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        context = getActivity();
        HomeFrag = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("home");
        SearchFrag = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("search");
        setSearch();
        setListView();
    }

    private void setListView() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO
                // start playing item in HOME fragment
                // ONLY AFTER stopping everything else (mp.reset())
                // check example in 4 - screen fragments app (soccer, brazil photos)

                // step1
                // set vari


                // step 2


                // step 3
                // hide this fragment and show home fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .hide(SearchFrag)
                        .show(HomeFrag)
                        .commit();

                // step 4
                // adjust navigation bar below to change selection to home
                BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
                navBar.setSelectedItemId(R.id.navigation_home);


            }
        });
    }

    private void setSearch() {

        // TODO add voice search
        // source:  https://github.com/MiguelCatalan/MaterialSearchView


        setSearchPointers();
        setSearchListeners();
    }

    private void setSearchPointers() {
        mMaterialSearchView = rootView.findViewById(R.id.searchView);
        listView = rootView.findViewById(R.id.listSearchItems);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                Defaults.searchSuggestions);
        listView.setAdapter(arrayAdapter);
    }

    private void setSearchListeners() {
        mMaterialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, Defaults.searchSuggestions);
                listView.setAdapter(arrayAdapter);
            }
        });

        mMaterialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1);

                if (newText != null && !newText.isEmpty()) {
                    for (String s : Defaults.searchSuggestions) {
                        if (s.toLowerCase().contains(newText))
                            arrayAdapter.add(s);
                    }
                } else {
                    arrayAdapter.addAll(Defaults.searchSuggestions);
                }

                listView.setAdapter(arrayAdapter);

                return false;
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mMaterialSearchView.setMenuItem(item);

        // settings to make search menu open directly on entry
        // less taps the better
        mMaterialSearchView.showSearch();
        mMaterialSearchView.setFocusable(true);
        mMaterialSearchView.setFocusableInTouchMode(true);
        mMaterialSearchView.requestFocus();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
