package com.hackathon.radioetzionapp.Data;

import java.util.ArrayList;
import java.util.List;

public class Defaults {

    // local
    public static final String LOCAL_DS_PATH = "docstore";

    // cloudAnt section
    // credentials, dbName, docName
    public static final String CloudantURL = "https://da3e51e3-3ad3-4c14-84f2-41bfd2a1f982-bluemix"
            + ":"+ "496007d652f08ef7a2c3014d7f2930e02382c8035a0d7341676da93ac857544a"
            +"@"+ "da3e51e3-3ad3-4c14-84f2-41bfd2a1f982-bluemix.cloudantnosqldb.appdomain.cloud";

    public static final String RadioDBName = "radio";
    public static final String BroadcastsDocID = "2e4c74a3f56457cbd5fc49f9edbfcdf0";

    // BroadcastDocument MapKeys
    public static final String BroadcastDoc_Key_docname = "docname";
    public static final String BroadcastDoc_Key_note01 = "note_01";
    public static final String BroadcastDoc_Key_note02 = "note_02";
    public static final String BroadcastDoc_Key_note03 = "note_03";
    public static final String BroadcastDoc_Key_serverURL = "server-url";
    public static final String BroadcastDoc_Key_dataList = "data";

    // BroadcastDocument >> DataList >> Object/Item MapKeys
    public static final String BroadcastDoc_Key_dataListItem_index = "index";
    public static final String BroadcastDoc_Key_dataListItem_title = "title";
    public static final String BroadcastDoc_Key_dataListItem_description = "description";
    public static final String BroadcastDoc_Key_dataListItem_filename = "filename";
    public static final String BroadcastDoc_Key_dataListItem_broadcastersList = "broadcasters";
    public static final String BroadcastDoc_Key_dataListItem_guestsList = "guests";
    public static final String BroadcastDoc_Key_dataListItem_commentsList = "comments";

    // BroadcastDocument >> DataList >> commentsList >> Object/Item MapKeys
    public static final String BroadcastDoc_Key_dataListItem_comments_time = "time";
    public static final String BroadcastDoc_Key_dataListItem_comments_username = "username";
    public static final String BroadcastDoc_Key_dataListItem_comments_content = "content";

    ///////////////////////////////////////////////////////////////////////////////////////

    // GLOBAL Static VARIABLES // which store data cached from cloudAnt db //
    // (to be accessed & changed from every class / activity / fragment) as needed //
    public static List<BroadcastDataClass> dataList = new ArrayList<>();
    public static String serverURL = ""; // where media is stored
    public static List<String> searchSuggestions = new ArrayList<>(); // sub-list of dataList
    public static String docname = ""; // extra
    public static List<String> notesList = new ArrayList<>(); // extras

}