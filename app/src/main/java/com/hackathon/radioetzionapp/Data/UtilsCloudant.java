package com.hackathon.radioetzionapp.Data;

import android.provider.DocumentsContract;

import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class UtilsCloudant {

    public static boolean getDataFromRemote(DocumentStore ds) {

        // returns false if FAILED to get data from remote database
        // true if success

        URI uri = null;
        try {
            uri = new URI(Defaults.CloudantURL + "/" + Defaults.RadioDBName);
            ds = DocumentStore.getInstance(new File(Defaults.LOCAL_DS_PATH,Defaults.RadioDBName));
        }
        catch (URISyntaxException urise){
            urise.printStackTrace();
        }
        catch (DocumentStoreNotOpenedException dsnoe){
            dsnoe.printStackTrace();
        }


        if(uri == null || ds == null)
        {
            return false;
        }

        // TODO inside asyncTask
        Replicator pullReplicator = ReplicatorBuilder.pull().from(uri).to(ds).build();
        pullReplicator.start();

        return true;

    }


    public static boolean uploadToRemote(DocumentStore ds)
    {
        // returns false if FAILED to upload local data to remote
        // true if success

        URI uri = null;
        try {
            uri = new URI(Defaults.CloudantURL + "/" + Defaults.RadioDBName);
        }
        catch (URISyntaxException urise){
            urise.printStackTrace();
        }

        if(uri == null || ds == null)
        {
            return false;
        }

        // TODO inside asyncTask
        Replicator pushReplicator = ReplicatorBuilder.push().from(ds).to(uri).build();
        pushReplicator.start();

        return true;
    }

    // TODO
    /*
    public static boolean updateLocal(DocumentStore ds, DocumentRevision newRevision){

        /// returns false if FAILED to update LOCAL ds
        /// true if success

        /*
        if(newRevision ==null || ds==null) { return false; }

        // 2 // change & update (local)
        DocumentRevision prevRevision=null;
        try {
            prevRevision = ds.database().read(Defaults.BroadcastsDocID);
        } catch (DocumentNotFoundException e) { e.printStackTrace(); }
        catch (DocumentStoreException e) { e.printStackTrace(); }

        if(prevRevision==null) {
            return false;
        }

        Map<String,Object> tmpMap = prevRevision.getBody().asMap();
        tmpMap.put("new DATA",txtUpdate.getText().toString().isEmpty()?
                "N/A":txtUpdate.getText().toString());
        prevRevision.setBody(DocumentBodyFactory.create(tmpMap));


        // updating prevRevision with new one
        DocumentRevision newRevision = null;
        try {
            newRevision = dsTmp.database().update(prevRevision);
        } catch (ConflictException e) {
            e.printStackTrace();
        } catch (AttachmentException e) {
            e.printStackTrace();
        } catch (DocumentStoreException e) {
            e.printStackTrace();
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
        }

        if(newRevision == null){
            msgFailed("ERROR !  # 3");
            return;
        }

    }
    */

}
