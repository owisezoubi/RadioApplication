package com.hackathon.radioetzionapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hackathon.radioetzionapp.Adapters.FavoritesListAdapter;
import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.Data.FavoritesSharedPref;
import com.hackathon.radioetzionapp.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    Context context;
    View rootView;
    List<BroadcastDataClass> lstFavData; // Model
    ListView lstFavView; // view
    FavoritesListAdapter adapter; // controller

    FavoritesSharedPref favoritesSharedPref;

    // TODO fix errors !
    /*

        DONE 1.  on item click >> open track in HOME (same code as in SEARCH)
        DONE 2.  on item LONG click >> show track info (same same )
        DONE 3.  INSIDE ADAPTER >> on HEART click >> show also track info
        DONE 4.  on REMOVE click >> remove from shared pref and refresh adapter for this list

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
        }
    }


    private void setPointers() {
        this.context = getActivity();
        lstFavView = rootView.findViewById(R.id.lstFavorites);
        favoritesSharedPref = new FavoritesSharedPref(context);
        lstFavData = new ArrayList<>();
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, null);
        return rootView;
    }
}
