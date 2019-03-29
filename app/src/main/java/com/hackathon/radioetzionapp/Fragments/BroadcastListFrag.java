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

import com.hackathon.radioetzionapp.Adapters.BroadcastListAdapter;
import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.BroadcastDataList;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.util.List;

public class BroadcastListFrag extends Fragment {


    ListView listViewBroadcasts; // view
    List<BroadcastDataClass> dataList; // data - model
    BroadcastListAdapter adapter; // adapter - controller
    Context context;
    View rootView;


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

        context = getActivity();
        listViewBroadcasts = rootView.findViewById(R.id.lstBroadcasts);


        setDataListAdapter();





    }

    private void setDataListAdapter() {
        // get data list and make sure it is not null first
        BroadcastDataList broadcastDataList = BroadcastDataList.getInstance(context);
        if(broadcastDataList == null)
        {
            Utils.displayMsg(getString(R.string.error_set_broadcastlist_1),rootView);
        }
        else
        {

            dataList = broadcastDataList.getDataList();
            if(dataList == null)
            {
                Utils.displayMsg(getString(R.string.error_set_broadcastlist_2),rootView);
            }
            else  // set adapter to connect data list to list view
            {
                adapter = new BroadcastListAdapter(context,dataList);
                listViewBroadcasts.setAdapter(adapter);
            }
        }
    }
}
