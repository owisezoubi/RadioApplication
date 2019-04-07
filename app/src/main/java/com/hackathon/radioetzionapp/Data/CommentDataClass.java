package com.hackathon.radioetzionapp.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentDataClass {

    private long timestamp; // UNIX time // in milliseconds
    private String username; // commentator
    private String content; // text content of comment

    public CommentDataClass(long timestamp, String username, String content) {
        this.timestamp = timestamp;
        this.username = username;
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimestampFormatted(String formatPattern)
    {
        Date tm = new Date(this.timestamp);
        SimpleDateFormat timeFormat = new SimpleDateFormat(formatPattern);
        return timeFormat.format(tm);
    }


    public String getTimestampFormatted()
    {
        return getTimestampFormatted("dd-MM-yyyy\nHH:mm");
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
