package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.BroadcastDataList;

import java.util.List;

public class BroadcastListAdapter extends BaseAdapter {


    List<BroadcastDataClass> lst;
    Context context;

    public BroadcastListAdapter(Context context, List<BroadcastDataClass> dataList) {
        this.context=context;
        this.lst = dataList;
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
