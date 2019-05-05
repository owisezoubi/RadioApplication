package com.hackathon.radioetzionapp.Utils;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.CommentDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilsMapData {


    // a class to Map local data from Defaults.datalist & other data items
    // to a Map<String,Object>
    // in order to update document store and sync it with the remote one //

    public static Map<String, Object> getMappedData() {
        Map<String, Object> map = new HashMap<>();
        map.put(Defaults.BroadcastDoc_Key_docname, Defaults.docname);
        map.put(Defaults.BroadcastDoc_Key_note01, Defaults.notesList.get(0));
        map.put(Defaults.BroadcastDoc_Key_note02, Defaults.notesList.get(1));
        map.put(Defaults.BroadcastDoc_Key_note03, Defaults.notesList.get(2));
        map.put(Defaults.BroadcastDoc_Key_serverURL, Defaults.serverURL);
        map.put(Defaults.BroadcastDoc_Key_dataList, getMappedDataList());

        return map;
    }

    private static List<Map<String, Object>> getMappedDataList() {
        List<Map<String, Object>> mappedDataList = new ArrayList<>();
        for (BroadcastDataClass item : Defaults.dataList) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_index, item.getIndex());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_title, item.getTitle());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_description, item.getDescription());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_filename, item.getFilename());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_broadcastersList, item.getBroadcastersList());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_guestsList, item.getGuestsList());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_commentsList,
                    getMappedComments(item.getCommentsList()));

            mappedDataList.add(tmp);
        }

        return mappedDataList;
    }

    private static List<Map<String, Object>> getMappedComments(List<CommentDataClass> commentsList) {
        List<Map<String, Object>> mappedCommentsList = new ArrayList<>();
        for (CommentDataClass item : commentsList) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_comments_time, Long.toString(item.getTimestamp()));
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_comments_username, item.getUsername());
            tmp.put(Defaults.BroadcastDoc_Key_dataListItem_comments_content, item.getContent());

            mappedCommentsList.add(tmp);
        }

        return mappedCommentsList;
    }
}