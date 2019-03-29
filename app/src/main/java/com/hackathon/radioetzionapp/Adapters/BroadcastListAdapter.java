package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.R;

import java.util.List;
import java.util.zip.Inflater;

public class BroadcastListAdapter extends BaseAdapter {



    private List<BroadcastDataClass> lst;
    private Context context;

    public BroadcastListAdapter(Context context,List<BroadcastDataClass> lst) {
        this.context=context;
        this.lst = lst;
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

        View checkView = getCheckView(position);

        return checkView;
    }

    private View getCheckView(int pos) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_check_broadcast,null);

        TextView txtIndex = v.findViewById(R.id.txtBIndex_Check);
        TextView txtTitle = v.findViewById(R.id.txtBTitle_Check);
        TextView txtDesc = v.findViewById(R.id.txtBDesc_Check);
        TextView txtBroadcasters = v.findViewById(R.id.txtBBroasdcasters_Check);
        TextView txtGuests = v.findViewById(R.id.txtBGuests_Check);
        TextView txtFilename = v.findViewById(R.id.txtBFileName_Check);

        v.setBackgroundColor(pos%2==0? Color.WHITE:Color.GRAY);

        BroadcastDataClass current = lst.get(pos);

        txtIndex.setText(new StringBuilder(current.getIndex()));
        txtTitle.setText(current.getTitle());
        txtDesc.setText(current.getDescription());
        txtFilename.setText(current.getFilename());

        txtBroadcasters.setText(current.getBroadcastersListFormatted());
        txtGuests.setText(current.getGuestsListFormatted());

        return v;
    }
}
