package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.util.List;

public class BroadcastListAdapter extends BaseAdapter implements Animation.AnimationListener {



    private List<BroadcastDataClass> lst;
    private Context context;

    private Animation entryRight, entryLeft;

    public BroadcastListAdapter(Context context,List<BroadcastDataClass> lst) {
        this.context=context;
        this.lst = lst;

        // animation initialize
        entryRight = AnimationUtils.loadAnimation(context, R.anim.entry_from_right);
        entryRight.setAnimationListener(this);
        entryLeft = AnimationUtils.loadAnimation(context, R.anim.entry_from_left);
        entryLeft.setAnimationListener(this);
    }


    @Override
    public int getCount() {
        return lst.size();
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

        // TODO modify adapter to make better looking list

        View view;

        //view = getCheckView(position);

        view = getBroadcastItemView(position);

        return view;
    }

    private View getBroadcastItemView(final int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_broadcast, null);

        // link ids to pointers
        ImageView imgAlbum = v.findViewById(R.id.img_ItemBroadcast);
        ImageView imgFav = v.findViewById(R.id.imgFav_ItemBroadcast);
        TextView txtTitle = v.findViewById(R.id.txtBroadcastTitle_ItemBroadcast);

        // connect data & adjust views
        imgAlbum.setColorFilter(getAlternateColor(position));
        txtTitle.setTextColor(getAlternateColor(position));
        txtTitle.setText(lst.get(position).getTitle());
        setFavoritesStatus(imgFav, position);

        // set listener for imgAlbum onClick
        imgAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showTrackInfoDialog(context, position);
            }
        });

        // adjust main view (parent)
        v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        v.setTextDirection(View.LAYOUT_DIRECTION_RTL);
        v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

        // set entry animation for text & image
        txtTitle.setAnimation(entryRight);
        imgAlbum.setAnimation(entryRight);
        // set entry animation for fav icon
        imgFav.setAnimation(entryLeft);

        return v;

    }

    private void setFavoritesStatus(ImageView imgFav, int position) {

        // TODO based on saved shared preferences


        // default
        imgFav.setColorFilter(getAlternateColor(position));
    }

    private int getAlternateColor(int pos) {

        int returnValue = Color.WHITE;

        switch (pos % 2) {

            case 0:
                returnValue = ContextCompat.getColor(context, R.color.album_alternate_1);
                break;
            case 1:
                returnValue = ContextCompat.getColor(context, R.color.album_alternate_2);
                break;
        }

        return returnValue;
    }

    private View getCheckView(int pos) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_check_broadcast,null);

        TextView txtIndex = v.findViewById(R.id.txtBIndex_Check);
        TextView txtTitle = v.findViewById(R.id.txtBTitle_Check);
        TextView txtDesc = v.findViewById(R.id.txtBDesc_Check);
        TextView txtBroadcasters = v.findViewById(R.id.txtBBroasdcasters_Check);
        TextView txtGuests = v.findViewById(R.id.txtBGuests_Check);
        TextView txtFilename = v.findViewById(R.id.txtBFileName_Check);

        v.setBackgroundColor(pos%2==0? Color.WHITE:Color.LTGRAY);
        v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        v.setTextDirection(View.LAYOUT_DIRECTION_RTL);
        v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

        BroadcastDataClass current = lst.get(pos);

        //Log.e("index:",current.getIndex()+"");

        txtIndex.setText(new StringBuilder(current.getIndex()+""));
        txtTitle.setText(current.getTitle());
        txtDesc.setText(current.getDescription());
        txtFilename.setText(current.getFilename());

        txtBroadcasters.setText(current.getBroadcastersListFormatted());
        txtGuests.setText(current.getGuestsListFormatted());

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
