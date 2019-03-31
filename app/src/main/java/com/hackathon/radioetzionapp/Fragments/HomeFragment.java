package com.hackathon.radioetzionapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.hackathon.radioetzionapp.Adapters.BroadcastListAdapter;
import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.CommentDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    /////  data to pass to fragment: BroadcastPlayerFrag ////////////////

    public static int currentTrackIndex; // current playing track index (in datalist)
    public static String currentTrackTitle;
    public static int currentTrackTimePosition; // current track's position in milliseconds

    ////////////////////////////////////////////////////////////////////

    DocumentStore ds;  // ds object to store cloudAnt DB data from remote to local

    ListView listViewBroadcasts; // view
    BroadcastListAdapter adapter; // adapter - controller

    ProgressBar progressLoadingList;
    TextView txtLoadingList, txtCurrentTrack;
    ImageButton btnRefreshList;
    LinearLayout layMiniPlayer;
    ImageButton btnPlay, btnNext, btnPrev, btnShuffle, btnRepeatOne;
    ImageView imgLogo;

    MediaPlayer mp;
    View currentTrackView;

    Context context;
    View rootView;

    // TODO mini-player


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setPointers();
        setListeners();
    }

    private void setListeners() {
        // TODO

        btnRefreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingEffects(); // reset effects of loading list
                btnRefreshList.setVisibility(View.INVISIBLE);  // hide when clicked
                // reload list again
                getDataFromRemote();
            }
        });


        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                finishTrackLoadingEffects();
                mp.start();
            }
        });

        listViewBroadcasts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO check internet FIRST !!!

                currentTrackView = view; // to use in other methods
                loadTrackEffects(view);
                loadTrack(position);
                updateCurrentTrackInfo(position);
            }
        });
    }

    private void updateCurrentTrackInfo(int pos) {
        currentTrackIndex = Defaults.dataList.get(pos).getIndex();
        currentTrackTitle = Defaults.dataList.get(pos).getTitle();
    }

    private void finishTrackLoadingEffects() {

        imgLogo.setVisibility(View.GONE); // hide logo // gone ! // as if not there
        txtCurrentTrack.setVisibility(View.VISIBLE);
        txtCurrentTrack.setText(currentTrackTitle); // trackTitle instead of logo

        // hide track progress // show NOW PLAYING in status instead
        currentTrackView.findViewById(R.id.progress_ItemBRoadcast).setVisibility(View.INVISIBLE);
        ((TextView) currentTrackView.findViewById(R.id.status_ItemBroadcast)).
                setText(getString(R.string.track_nowplaying));
        currentTrackView.findViewById(R.id.status_ItemBroadcast).setVisibility(View.VISIBLE);
    }


    private void loadTrack(int pos) {
        try {
            String url = Defaults.serverURL +
                    URLEncoder.encode(Defaults.dataList.get(pos).getFilename(), "UTF-8");
            mp.setDataSource(url);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTrackEffects(View v) {
        v.findViewById(R.id.progress_ItemBRoadcast).setVisibility(View.VISIBLE);
    }

    private void setPointers() {

        context = getActivity();
        // list layout & contents
        listViewBroadcasts = rootView.findViewById(R.id.lstBroadcasts);
        progressLoadingList = rootView.findViewById(R.id.progressLoadingBroadcasts);
        txtLoadingList = rootView.findViewById(R.id.txtLoadingBroadcasts);
        btnRefreshList = rootView.findViewById(R.id.btnRefreshBList);
        // mini player layout & contents
        layMiniPlayer = rootView.findViewById(R.id.layoutMiniPlayer);
        btnPlay = rootView.findViewById(R.id.btnPlayPause_MiniPlayer);
        btnNext = rootView.findViewById(R.id.btnNext_MiniPlayer);
        btnPrev = rootView.findViewById(R.id.btnPrev_MiniPlayer);
        btnShuffle = rootView.findViewById(R.id.btnShuffle_MiniPlayer);
        btnRepeatOne = rootView.findViewById(R.id.btnRepeatOne_MiniPlayer);
        imgLogo = rootView.findViewById(R.id.imgLogoStart_MiniPlayer);
        txtCurrentTrack = rootView.findViewById(R.id.txtBroadcastTitle_MiniPlayer);

        // load track-list data not loaded yet //
        loadData();

        //media player // initialize
        mp = new MediaPlayer();
        AudioAttributes attributes = new AudioAttributes.Builder().
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        mp.setAudioAttributes(attributes);
    }


    private void loadData() {

        if (Defaults.dataList.isEmpty()) {
            // AsyncTask to get data from database (remote) to DocStore (local)
            // & set adapter afterwards
            getDataFromRemote();
        } else {
            //finishLoadingEffects();
        }
            /*
        } else {
            // set adapter & effects of finished loading
            setListAdapter();
            finishLoadingEffects();
        }
        */

    }


    @SuppressLint("StaticFieldLeak")
    private void getDataFromRemote() {

        // part 0 // check if has internet connection //
        if (!Utils.hasInternet(context)) // if no internet connection, no need to continue
        {
            Utils.displayMsg(context.getString(R.string.no_internet), rootView);
            //Log.e("errdata", context.getString(R.string.no_internet));

            requestInternetConnection();

            return;
        }


        // part 1 // URI & DS instance creation
        URI uri = null;
        try {
            uri = new URI(Defaults.CloudantURL + "/" + Defaults.RadioDBName);
            ds = DocumentStore.getInstance(new File(
                    context.getDir(Defaults.LOCAL_DS_PATH, Context.MODE_PRIVATE),
                    Defaults.RadioDBName));
        } catch (URISyntaxException use) {
            use.printStackTrace();
        } catch (DocumentStoreNotOpenedException dsnoe) {
            dsnoe.printStackTrace();
        }

        if (uri == null || ds == null) {
            Utils.displayMsg(context.getString(R.string.error_getting_docstore_1), rootView);

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

        new AsyncTask<Void, Void, Replicator.State>() {

            // return true if successful, false otherwise
            @Override
            protected Replicator.State doInBackground(Void... voids) {

                Replicator pullReplicator = ReplicatorBuilder.pull().from(uriTmp).to(ds).build();
                pullReplicator.start();

                // while NOT {COMPLETE or ERROR or else} // stay in background task //
                // i.e. while replicating , stay in background  task //
                while (pullReplicator.getState() == Replicator.State.STARTED) {
                    SystemClock.sleep(100);
                }

                return pullReplicator.getState();
            }

            @Override
            protected void onPostExecute(Replicator.State result) {
                super.onPostExecute(result);
                //Log.e("errdata",result.name());
                if (result != Replicator.State.COMPLETE) {
                    Utils.displayMsg(contextTmp.getString(R.string.error_getting_docstore_2), rootView);
                    //Log.e("errdata",contextTmp.getString(R.string.error_getting_docstore_2));
                } else {
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

                    if (retrieved == null) {
                        Utils.displayMsg(contextTmp.getString(R.string.error_getting_docstore_3), rootView);
                        //Log.e("errdata",contextTmp.getString(R.string.error_getting_docstore_3));
                        return;
                    }

                    setServerURL(retrieved);
                    setDataList(retrieved);


                    // data is ready, time to set ADAPTER
                    setListAdapter();
                    finishLoadingEffects();
                }
            }
        }.execute();
    }

    private void finishLoadingEffects() {
        txtLoadingList.setVisibility(View.INVISIBLE);
        progressLoadingList.setVisibility(View.INVISIBLE);
    }

    private void startLoadingEffects() {
        txtLoadingList.setText(R.string.loading_broadcasts);
        txtLoadingList.setVisibility(View.VISIBLE);
        progressLoadingList.setVisibility(View.VISIBLE);
    }

    private void setListAdapter() {
        adapter = new BroadcastListAdapter(context, Defaults.dataList);
        listViewBroadcasts.setAdapter(adapter);
    }


    private void setServerURL(DocumentRevision rev) {
        Defaults.serverURL = (String) (rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_serverURL));

        //Log.e("errdata","serverURL:"+this.serverURL);
    }

    private void setDataList(DocumentRevision rev) {

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
                    (List<CommentDataClass>) item.get(Defaults.BroadcastDoc_Key_dataListItem_commentsList)
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

    private void requestInternetConnection() {
        txtLoadingList.setText(getString(R.string.request_internet_connection)); // request msg
        progressLoadingList.setVisibility(View.INVISIBLE); // hide
        btnRefreshList.setVisibility(View.VISIBLE); // show refresh button
    }

}
