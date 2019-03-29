package com.hackathon.radioetzionapp.Data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BroadcastDataList {   // SINGLETON

// TODO

    View rootView;
    private List<BroadcastDataClass> broadcastsList;
    private String serverURL;
    private DocumentStore ds;  // ds object to store cloudAnt DB data from remote to local
    private static BroadcastDataList broadcasts;

    public static BroadcastDataList getInstance(Context context) {
        if (broadcasts == null) {
            broadcasts = new BroadcastDataList(context);
        }
        return broadcasts;
    }

    private BroadcastDataList(Context context) {

        rootView = ((AppCompatActivity)context).getWindow().
                getDecorView().findViewById(android.R.id.content);

        if(!Utils.hasInternet(context)) // if no internet connection, no need to continue
        {
            Utils.displayMsg(context.getString(R.string.no_internet),rootView);
            Log.e("errdata",context.getString(R.string.no_internet));
            return;
        }

        setDocStore(context);
    }


    public List<BroadcastDataClass> getDataList()
    {
        return broadcastsList;
    }

    public String getServerURL() {
        return serverURL;
    }

    @SuppressLint("StaticFieldLeak")
    private void setDocStore(Context context) {

        // part 1 // URI & DS instance creation
        URI uri = null;
        try {
            uri = new URI(Defaults.CloudantURL + "/" + Defaults.RadioDBName);
            ds = DocumentStore.getInstance(new File(
                    context.getDir(Defaults.LOCAL_DS_PATH,Context.MODE_PRIVATE),
                    Defaults.RadioDBName));
        }
        catch (URISyntaxException use){
            use.printStackTrace();
        }
        catch (DocumentStoreNotOpenedException dsnoe){
            dsnoe.printStackTrace();
        }

        if(uri == null || ds == null)
        {
            Utils.displayMsg(context.getString(R.string.error_getting_docstore_1),rootView);

            //Log.e("errdata",context.getString(R.string.error_getting_docstore_1)+"\n"+
            //"uri:"+uri+"\n"+"ds:"+ds);

            return;
        }

        // part 2
        // locally: uri & ds are OK .. now we can replicate from remote
        // inside asyncTask

        // declared final to pass into inner class
        final URI uriTmp = uri;
        final Context contextTmp = context;

        new AsyncTask<Void,Void,Replicator.State>(){

            // return true if successful, false otherwise
            @Override
            protected Replicator.State doInBackground(Void... voids) {

                Replicator pullReplicator = ReplicatorBuilder.pull().from(uriTmp).to(ds).build();
                pullReplicator.start();


                    return pullReplicator.getState();
            }

            @Override
            protected void onPostExecute(Replicator.State result) {
                super.onPostExecute(result);
                if(result != Replicator.State.COMPLETE)
                {
                    Utils.displayMsg(contextTmp.getString(R.string.error_getting_docstore_2),rootView);
                    Log.e("errdata",contextTmp.getString(R.string.error_getting_docstore_2));
                }
                else
                {
                    // all is ok, we have local DocStore
                    // time to set serverURL && fill up data list

                    DocumentRevision retrieved = null;
                    try {
                        retrieved = ds.database().read(Defaults.BroadcastsDocID);
                    } catch (DocumentNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentStoreException e) {
                        e.printStackTrace();
                    }

                    if(retrieved == null)
                    {
                        Utils.displayMsg(contextTmp.getString(R.string.error_getting_docstore_3),rootView);
                        Log.e("errdata",contextTmp.getString(R.string.error_getting_docstore_3));
                        return;
                    }
                    setServerURL(retrieved);
                    setDataList(retrieved);
                }
            }
        }.execute();
    }

    private void setServerURL(DocumentRevision rev) {
        this.serverURL = (String)rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_serverURL);
    }

    private void setDataList(DocumentRevision rev) {

        // temporary list of Maps of <String,Object> // to initially order dataList from docStore
        List<Map<String,Object>> tmpDataList = (List<Map<String,Object>>)
                rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_dataList);

        // our data list to fill up
        this.broadcastsList = new ArrayList<>();

        for (Map<String,Object> item: tmpDataList)
        {
            this.broadcastsList.add(new BroadcastDataClass(
                    (int)item.get(Defaults.BroadcastDoc_Key_dataListItem_index),
                    (String)item.get(Defaults.BroadcastDoc_Key_dataListItem_title),
                    (String)item.get(Defaults.BroadcastDoc_Key_dataListItem_description),
                    (String)item.get(Defaults.BroadcastDoc_Key_dataListItem_filename),
                    (List<String>)item.get(Defaults.BroadcastDoc_Key_dataListItem_broadcastersList),
                    (List<String>)item.get(Defaults.BroadcastDoc_Key_dataListItem_guestsList),
                    (List<CommentDataClass>)item.get(Defaults.BroadcastDoc_Key_dataListItem_commentsList)
            ));

            // TODO: initialize CommentDataClass inside the map (current item)
            /*
            int itemIndex = broadcastsList.size()-1;
            for(CommentDataClass commentItem: broadcastsList.get(itemIndex).getCommentsList())
            {
                commentItem = new CommentDataClass(
                  broadcastsList.get(itemIndex)
                );
            }
            */
        }
    }
}