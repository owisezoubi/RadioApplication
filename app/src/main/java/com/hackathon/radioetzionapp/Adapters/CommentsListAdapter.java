package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.CommentDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;

import java.util.List;

public class CommentsListAdapter extends BaseAdapter {


    Context context;
    int trackIndex;
    List<CommentDataClass> commentsList;


    public CommentsListAdapter(Context context, int trackIndex) {
        this.context = context;
        this.trackIndex = trackIndex;
        this.commentsList = Defaults.dataList.get(trackIndex).getCommentsList();
    }

    @Override
    public int getCount() {
        // if list is empty returns 1, to show message to user
        // otherwise returns list's size...
        return commentsList.isEmpty() ? 1 : commentsList.size();
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

        View v;

        if (commentsList.isEmpty()) {
            v = getNoCommentsMessageView();
        } else {
            v = getCommentsView(position);
        }

        return v;
    }

    private View getCommentsView(int pos) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, null);

        TextView txtContent = view.findViewById(R.id.txtCommentContent);
        TextView txtUsername = view.findViewById(R.id.txtCommentatorName);
        TextView txtTime = view.findViewById(R.id.txtTimeStamp_Comment);
        RelativeLayout layout = view.findViewById(R.id.layItemCommentParent);
        layout.setLayoutDirection(pos % 2 == 0 ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        CommentDataClass item = commentsList.get(pos);

        txtContent.setText(item.getContent());
        txtTime.setText(item.getTimestampFormatted());
        txtUsername.setText(item.getUsername());

        return view;
    }

    private View getNoCommentsMessageView() {

        TextView txt = new TextView(context);
        txt.setTextSize(16);
        txt.setText(context.getString(R.string.no_comments_found));

        return txt;
    }
}
