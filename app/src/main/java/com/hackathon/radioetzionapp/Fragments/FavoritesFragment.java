package com.hackathon.radioetzionapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackathon.radioetzionapp.R;

public class FavoritesFragment extends Fragment {

    Context context;
    View rootView;

    // TODO
    /*
        1.  on item click >> open track in HOME (same code as in SEARCH)
        2.  on item LONG click >> show track info (same same )
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
        }
    }

    private void setPointers() {
        this.context = getActivity();
    }

    private void loadFavoritesList() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, null);
        return rootView;
    }
}
