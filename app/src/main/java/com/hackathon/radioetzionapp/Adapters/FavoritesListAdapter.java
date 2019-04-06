package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.FavoritesSharedPref;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.util.List;

public class FavoritesListAdapter extends BaseAdapter implements Animation.AnimationListener {

    Context context;
    List<BroadcastDataClass> lstData;
    private FavoritesSharedPref favoritesSharedPref;

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
        // connect data & adjust views
        txtTitle.setText(lstData.get(position).getTitle());
        // set views' action listeners (onClick)
        imgHeartAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showTrackInfoDialog(context, position);
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

        // set entry animations
        txtTitle.setAnimation(entryRight);
        imgHeartAlbum.setAnimation(entryRight);
        btnRemove.setAnimation(entryLeft);

        return v;
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
