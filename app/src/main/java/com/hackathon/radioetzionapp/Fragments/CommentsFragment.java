package com.hackathon.radioetzionapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.radioetzionapp.Adapters.CommentsListAdapter;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;


public class CommentsFragment extends Fragment {

    Context context;
    View rootView;
    TextView txtCommentTitle;
    ImageView btnAddComment, btnNextTitle, btnPrevTitle;
    ListView lstComments;
    CommentsListAdapter adapter;
    int trackIndex;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            // Toast.makeText(getActivity(), "hiding me", Toast.LENGTH_SHORT).show();
        } else {

            // updated every time this fragment is shown !

            setPointers();
            setListeners();
            showCommentData(); // for current track playing OR the first track (if non is playing)
        }
    }


    private void setPointers() {
        this.context = getActivity();
        txtCommentTitle = rootView.findViewById(R.id.txtTitle_Comments);
        btnAddComment = rootView.findViewById(R.id.btnAddComment);
        btnNextTitle = rootView.findViewById(R.id.btnNextTitle_Comments);
        btnPrevTitle = rootView.findViewById(R.id.btnPrevTitle_Comments);
        lstComments = rootView.findViewById(R.id.lstComments);
        trackIndex = setTrackIndex();
    }

    private int setTrackIndex() {
        if (Defaults.dataList.isEmpty()) {
            Toast.makeText(context, getString(R.string.toast_comments_datalistempty), Toast.LENGTH_LONG).show();
            return -1;
        }
        return HomeFragment.currentTrackIndex == -1 ? 0 : HomeFragment.currentTrackIndex;
    }

    private void setListeners() {
    }


    private void showCommentData() {

        if (trackIndex == -1) return;


        // refresh adapter
        adapter = new CommentsListAdapter(context, trackIndex);
        lstComments.setAdapter(adapter);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comments, null);
        return rootView;
    }
}
