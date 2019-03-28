package com.hackathon.radioetzionapp.Data;

public class CommentDataClass {

    private long timestamp; // UNIX time
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

    public String getTimestampFormatted(String format)
    {
        // TODO
        // YYYYMMDD-hhmmss  // or else //
        return "";
    }

    public String getTimestampFormatted()
    {
        // TODO
        // YYYYMMDD-hhmmss  // default format to pass to above method //
        return "";
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
