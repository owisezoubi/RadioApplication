package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;

import java.util.List;

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
        TextView txt = new TextView(context);
        txt.setText(lst.get(position).getTitle());

        return txt;
    }
}
