package com.hackathon.radioetzionapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.radioetzionapp.Activities.MainActivity;
import com.hackathon.radioetzionapp.Adapters.CommentsListAdapter;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;


public class CommentsFragment extends Fragment {

    Context context;
    View rootView;
    TextView txtCommentTitle;
    ImageView btnAddComment, btnNextTitle, btnPrevTitle;
    RelativeLayout layTop, layBody;
    ListView lstComments;
    CommentsListAdapter adapter;
    int trackIndex;

    Fragment commentsFragment, homeFragment;
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
        layBody = rootView.findViewById(R.id.layCommentsBody);
        layTop = rootView.findViewById(R.id.layCommentsTop);

        // fragments
        homeFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_HOME);
        commentsFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_COMMENTS);

        trackIndex = setTrackIndex();
    }

    private int setTrackIndex() {
        if (Defaults.dataList.isEmpty()) {
            Toast.makeText(context, getString(R.string.toast_comments_datalistempty), Toast.LENGTH_LONG).show();

            layBody.setVisibility(View.INVISIBLE);
            layTop.setVisibility(View.INVISIBLE);

            return -1;
        }
        return HomeFragment.currentTrackIndex == -1 ? 0 : HomeFragment.currentTrackIndex;
    }

    private void setListeners() {

        btnNextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackIndex = (trackIndex + 1) % (Defaults.dataList.size());
                showCommentData();
            }
        });

        btnPrevTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackIndex = trackIndex < 1 ? // 0 or -1 //
                        Defaults.dataList.size() - 1 : trackIndex - 1;
                showCommentData();
            }
        });

        txtCommentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // same steps as in SEARCH FRAGMENT's onItemClick //
                // when clicked, loads selected track in HOME & moves to HOME fragment
                HomeFragment.currentTrackIndex = trackIndex; // updates track index in home, with this one ...
                HomeFragment.wasCalledFromOtherFragment = true;
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                        .hide(commentsFragment)
                        .show(homeFragment).commit();
                BottomNavigationView navBar = ((AppCompatActivity) context).findViewById(R.id.navigation);
                navBar.setSelectedItemId(R.id.navigation_home);
            }
        });

        txtCommentTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showTrackInfoDialog(context, trackIndex);
                return true;
            }
        });
    }


    private void showCommentData() {

        if (trackIndex == -1) return;

        txtCommentTitle.setText(Defaults.dataList.get(trackIndex).getTitle());

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
