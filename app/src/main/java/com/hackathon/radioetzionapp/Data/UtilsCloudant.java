package com.hackathon.radioetzionapp.Data;

import java.net.URI;

public class UtilsCloudant {

    /*
    private void showDataOnView() {

        DocumentRevision retrieved = null;
        try {
            retrieved = dsDownload.database().read("899eb45c581477cffc66cce801025439"); // document id
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentStoreException e) {
            e.printStackTrace();
        }

        if(retrieved!=null) {
            DocumentBody body = retrieved.getBody();
            HashMap<String, Object> map = (HashMap<String, Object>) body.asMap();

            List<HashMap<String,Object>> mapObjectsList =  (List<HashMap<String, Object>>) map.get("data");

            StringBuilder tmpStr=new StringBuilder(); // to collect output

            for(HashMap<String,Object> mapObject : mapObjectsList) {
                String tmpPath = getFilePath(mapObject);
                tmpStr.append(tmpPath
                        .substring(tmpPath.lastIndexOf("/")+1, tmpPath.lastIndexOf(".mp4"))
                        .replace("_"," "))
                        .append("\n\n");
            }

            txtIn.setText(tmpStr);
        }
        else{
            msgFailed("failed to retrieve data!");
        }

    }
    */

    /*
    private String getFilePath(HashMap<String, Object> mapObject) {
        return (String)mapObject.get("filePath");
    }

    private void msgFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

*/
    /*
    private boolean readDataFromRemote() {

        // returns false if FAILED to read data from remote database
        // true if success

        URI uri = null;
        try {
            uri = new URI(CloudantDefaults.URL + "/" + CloudantDefaults.DB_NAME);
            dsDownload = DocumentStore.getInstance(new File(DS_path,CloudantDefaults.DB_NAME));
        }
        catch (URISyntaxException use){
            use.printStackTrace();
        }
        catch (DocumentStoreNotOpenedException dsnoe){
            dsnoe.printStackTrace();
        }

        if(uri == null || dsDownload == null)
        {
            msgFailed("ERROR!");
            return;
        }

        Replicator pullReplicator = ReplicatorBuilder.pull().from(uri).to(dsDownload).build();
        pullReplicator.start();

    }
*/
}
