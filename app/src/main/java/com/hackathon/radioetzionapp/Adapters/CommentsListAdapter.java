package com.hackathon.radioetzionapp.Adapters;

import android.content.Context;
import android.graphics.Color;
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

    String SUFFIX_STRING = " ... ";
    final int MAX_CONTENT_LENGTH = 100;

    Context context;
    int trackIndex;
    List<CommentDataClass> commentsList;


    public CommentsListAdapter(Context context, int trackIndex) {
        this.context = context;
        this.trackIndex = trackIndex;
        this.commentsList = Defaults.dataList.get(trackIndex).getCommentsList();
        SUFFIX_STRING += context.getString(R.string.see_more);
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

        final TextView txtContent = view.findViewById(R.id.txtCommentContent);
        TextView txtUsername = view.findViewById(R.id.txtCommentatorName);
        TextView txtTime = view.findViewById(R.id.txtTimeStamp_Comment);
        RelativeLayout layout = view.findViewById(R.id.layItemCommentParent);
        layout.setLayoutDirection(pos % 2 == 0 ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        final CommentDataClass item = commentsList.get(pos);

        txtContent.setText(getShortContent(item.getContent()));
        txtTime.setText(item.getTimestampFormatted());
        txtUsername.setText(item.getUsername());

        // expand, collapse , on click ...
        txtContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded(txtContent.getText().toString())) {
                    // collapse
                    txtContent.setText(getShortContent(item.getContent()));
                } else {
                    // expand
                    txtContent.setText(item.getContent());
                }
            }

            private boolean isExpanded(String str) {
                return !(str.length() <= (MAX_CONTENT_LENGTH + SUFFIX_STRING.length()));
            }
        });

        return view;
    }

    private String getShortContent(String content) {
        return content.length() > MAX_CONTENT_LENGTH ?
                content.substring(0, MAX_CONTENT_LENGTH) + SUFFIX_STRING : content;
    }

    private View getNoCommentsMessageView() {

        TextView txt = new TextView(context);
        txt.setTextSize(16);
        txt.setTextColor(Color.BLACK);
        txt.setText(context.getString(R.string.no_comments_found));

        return txt;
    }
}
