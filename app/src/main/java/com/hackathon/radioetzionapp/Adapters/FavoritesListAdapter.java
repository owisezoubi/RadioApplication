package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;

import java.util.List;

public class FavoritesListAdapter extends BaseAdapter implements Animation.AnimationListener {

    Context context;
    List<BroadcastDataClass> lstData;

    public FavoritesListAdapter(Context context, List<BroadcastDataClass> lstFavData) {
        this.context = context;
        this.lstData = lstFavData;
    }

    @Override
    public int getCount() {
        return 0;
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
        return null;
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
