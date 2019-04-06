package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Activities.MainActivity;
import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.Data.FavoritesSharedPref;
import com.hackathon.radioetzionapp.Fragments.HomeFragment;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.util.List;

public class FavoritesListAdapter extends BaseAdapter implements Animation.AnimationListener {

    Context context;
    List<BroadcastDataClass> lstData;
    private FavoritesSharedPref favoritesSharedPref;
    private Fragment homeFragment, favoritesFragment;
    private Animation entryRight, entryLeft;

    public FavoritesListAdapter(Context context, List<BroadcastDataClass> lstFavData) {
        this.context = context;
        this.lstData = lstFavData;

        favoritesSharedPref = new FavoritesSharedPref(context);

        // animation initialize
        entryRight = AnimationUtils.loadAnimation(context, R.anim.entry_from_right);
        entryRight.setAnimationListener(this);
        entryLeft = AnimationUtils.loadAnimation(context, R.anim.entry_from_left);
        entryLeft.setAnimationListener(this);

        // fragments
        homeFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_HOME);
        favoritesFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_FAVORITES);
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        view = getFavItemView(position);

        return view;
    }

    private View getFavItemView(final int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_favorites_layout, null);

        // link ids to pointers
        ImageView imgHeartAlbum = v.findViewById(R.id.img_ItemFavorite);
        ImageView btnRemove = v.findViewById(R.id.btnRemove_ItemFavorite);
        TextView txtTitle = v.findViewById(R.id.txtBroadcastTitle_ItemFavorite);
        RelativeLayout layItemFav = v.findViewById(R.id.layItemFavorites);

        // connect data & adjust views
        txtTitle.setText(lstData.get(position).getTitle());
        // set views' action listeners (onClick & onLongClick) for everything
        imgHeartAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showTrackInfoDialog(context, getSelectedItemIndex(position));
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove from shared pref
                favoritesSharedPref.removeFromFav(lstData.get(position).getTitle());
                // remove from this DATA LIST also
                lstData.remove(lstData.get(position));
                // refresh adapter
                notifyDataSetChanged();
            }
        });
        layItemFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // same steps as in SEARCH FRAGMENT's onItemClick //
                // when clicked, loads selected track in HOME & moves to HOME fragment
                HomeFragment.currentTrackIndex = getSelectedItemIndex(position);
                HomeFragment.wasCalledFromOtherFragment = true;
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                        .hide(favoritesFragment)
                        .show(homeFragment).commit();
                BottomNavigationView navBar = ((AppCompatActivity) context).findViewById(R.id.navigation);
                navBar.setSelectedItemId(R.id.navigation_home);
            }
        });
        layItemFav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showTrackInfoDialog(context, getSelectedItemIndex(position));
                return true;
            }
        });

        // set entry animations
        txtTitle.setAnimation(entryRight);
        imgHeartAlbum.setAnimation(entryRight);
        btnRemove.setAnimation(entryLeft);

        return v;
    }


    private int getSelectedItemIndex(int pos) {
        // a bit modified version of the method found in SEARCH FRAGMENT
        // because in this case, we are INSIDE the adapter

        // pos: is the order in the current FAVORITES list
        // method returns the ORIGINAL index in the Defaults.dataList [randomized or not .. ]
        // which is used to play tracks in home fragment
        // by COMPARING the track titles ... (basic searching)

        String itemTitle = lstData.get(pos).getTitle(); // title of current item
        for (int index = 0; index < Defaults.dataList.size(); index += 1) {
            if (Defaults.dataList.get(index).getTitle().equals(itemTitle)) {
                return index;
            }
        }
        return 0; // default return value to play first item in track list
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
