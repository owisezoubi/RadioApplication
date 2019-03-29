package com.hackathon.radioetzionapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.R;

import java.util.List;

public class BroadcastListFrag extends Fragment {


    ListView listViewBroadcasts; // view
    List<BroadcastDataClass> listDataBroadcasts; // data
    View rootView;

    BroadcastListFrag fragBroadcasts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_broadcasts,container,false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setPointers();
        setListeners();
    }

    private void setListeners() {
        // TODO
    }

    private void setPointers() {

        listViewBroadcasts = rootView.findViewById(R.id.lstBroadcasts);
        //listDataBroadcasts = BroadcastDataList.getBroadcastsDataList();
        // TODO  get dataList + set ADAPTER


    }
}
