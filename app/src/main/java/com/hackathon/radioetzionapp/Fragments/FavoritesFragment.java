package com.hackathon.radioetzionapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hackathon.radioetzionapp.Activities.MainActivity;
import com.hackathon.radioetzionapp.Adapters.FavoritesListAdapter;
import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.Data.FavoritesSharedPref;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    Context context;
    View rootView;
    List<BroadcastDataClass> lstFavData; // Model
    ListView lstFavView; // view
    FavoritesListAdapter adapter; // controller

    FavoritesSharedPref favoritesSharedPref;
    Fragment homeFragment, favoritesFragment;

    // TODO
    /*

        DONE 1.  on item click >> open track in HOME (same code as in SEARCH)
        DONE 2.  on item LONG click >> show track info (same same )
        3.  INSIDE ADAPTER >> on HEART click >> show also track info
        4.  on REMOVE click >> remove from shared pref and refresh adapter for this list

     */

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //Toast.makeText(getActivity(), "hiding me", Toast.LENGTH_SHORT).show();
        } else {
            setPointers();
            loadFavoritesList(); // updated every time this fragment is shown !
            resetAdapter();
            setListListeners();
        }
    }


    private void setPointers() {
        this.context = getActivity();
        lstFavView = rootView.findViewById(R.id.lstFavorites);
        favoritesSharedPref = new FavoritesSharedPref(context);
        lstFavData = new ArrayList<>();
        homeFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_HOME);
        favoritesFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_FAVORITES);
    }

    private void loadFavoritesList() {
        for (BroadcastDataClass item : Defaults.dataList) {
            if (favoritesSharedPref.isInFav(item.getTitle())) {
                lstFavData.add(item);
            }
        }
    }


    private void resetAdapter() {
        adapter = new FavoritesListAdapter(context, lstFavData);
        lstFavView.setAdapter(adapter);
    }


    private void setListListeners() {

        lstFavView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // same steps as in SEARCH FRAGMENT's onItemClick //
                // when clicked, loads selected track in HOME & moves to HOME fragment
                HomeFragment.currentTrackIndex = getSelectedItemIndex(position);
                HomeFragment.wasCalledFromOtherFragment = true;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .hide(favoritesFragment)
                        .show(homeFragment).commit();
                BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
                navBar.setSelectedItemId(R.id.navigation_home);
            }
        });

        lstFavView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.showTrackInfoDialog(context, getSelectedItemIndex(position));
                return true;
            }
        });
    }

    private int getSelectedItemIndex(int pos) {
        // a bit modified version of the method found in SEARCH FRAGMENT

        // pos: is the order in the current FAVORITES list
        // method returns the ORIGINAL index in the Defaults.dataList [randomized or not .. ]
        // which is used to play tracks in home fragment
        // by COMPARING the track titles ... (basic searching)

        BroadcastDataClass item = (BroadcastDataClass) adapter.getItem(pos);
        String itemTitle = item.getTitle();
        for (int index = 0; index < Defaults.dataList.size(); index += 1) {
            if (Defaults.dataList.get(index).getTitle().equals(itemTitle)) {
                return index;
            }
        }
        return 0; // default return value to play first item in track list
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, null);
        return rootView;
    }
}
