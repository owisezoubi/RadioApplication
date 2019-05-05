package com.hackathon.radioetzionapp.Utils;

import android.util.Log;

import com.cloudant.sync.documentstore.DocumentRevision;
import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.CommentDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UtilsSetData {


    // a class to collect all setData methods after retrieving data from db
    // methods are mainly used for PARSING received data and storing it in objects


    public static void setAllData(DocumentRevision rev) {
        setExtras(rev);
        setServerURL(rev);
        setDataList(rev);
    }

    private static void setExtras(DocumentRevision rev) {
        // internal use //
        Defaults.docname = (String) (rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_docname));
        Defaults.notesList.add((String) rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_note01));
        Defaults.notesList.add((String) rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_note02));
        Defaults.notesList.add((String) rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_note03));
    }

    private static void setServerURL(DocumentRevision rev) {
        Defaults.serverURL = (String) (rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_serverURL));

        Log.e("some info ... ", "\n" + Defaults.serverURL +
                "\n" + Defaults.docname +
                "\n" + Defaults.notesList.get(0) +
                "\n" + Defaults.notesList.get(1) +
                "\n" + Defaults.notesList.get(2));
    }

    private static void setDataList(DocumentRevision rev) {

        // temporary list of Maps of <String,Object> // to initially order dataList from docStore
        List<Map<String, Object>> tmpDataList = (List<Map<String, Object>>)
                rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_dataList);

        // our data list to fill up
        Defaults.dataList = new ArrayList<>();

        for (Map<String, Object> item : tmpDataList) {
            Defaults.dataList.add(new BroadcastDataClass(
                    (int) item.get(Defaults.BroadcastDoc_Key_dataListItem_index),
                    (String) item.get(Defaults.BroadcastDoc_Key_dataListItem_title),
                    (String) item.get(Defaults.BroadcastDoc_Key_dataListItem_description),
                    (String) item.get(Defaults.BroadcastDoc_Key_dataListItem_filename),
                    (List<String>) item.get(Defaults.BroadcastDoc_Key_dataListItem_broadcastersList),
                    (List<String>) item.get(Defaults.BroadcastDoc_Key_dataListItem_guestsList),
                    parseCommentsList((List<Map<String, Object>>) item.get(Defaults.BroadcastDoc_Key_dataListItem_commentsList))
            ));
        }

        // after done loading data, add titles to searchSuggestions list // "sub-list" //
        setSearchSuggestions();
    }

    private static List<CommentDataClass> parseCommentsList(List<Map<String, Object>> commentsList) {

        // commentsList is a list of Map<String,Object> items
        // doing the same as done to data-list above

        List<CommentDataClass> result = new ArrayList<>(); // our comments list to fill up & RETURN //

        for (Map<String, Object> item : commentsList) {
            result.add(new CommentDataClass(
                    Long.parseLong((String) item.get(Defaults.BroadcastDoc_Key_dataListItem_comments_time)),
                    (String) item.get(Defaults.BroadcastDoc_Key_dataListItem_comments_username),
                    (String) item.get(Defaults.BroadcastDoc_Key_dataListItem_comments_content)
            ));
        }

        return result;
    }

    private static void setSearchSuggestions() {

        for (BroadcastDataClass item : Defaults.dataList) {
            Defaults.searchSuggestions.add(item.getTitle());
        }
    }
}
