package com.hackathon.radioetzionapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnTouchListener {

    // public & static so that can be accessed and changed from other fragments ... //

    // current playing track index [in datalist: 0 - (last-1)]  // -1 is initial condition!
    public static int currentTrackIndex = -1;
    public static String currentTrackTitle = "";

    public static boolean wasCalledFromOtherFragment = false; // used to communicate between different fragments

    ////////////////////////////////////////////////////////////////////

    DocumentStore ds;  // ds object to store cloudAnt DB data from remote to local

    ListView listViewBroadcasts; // view
    BroadcastListAdapter adapter; // adapter - controller

    ProgressBar progressLoadingList; // progress for list view items while loading from remote
    AVLoadingIndicatorView indicatorLoadingBroadcast; // progress for track while loading/preparing
    TextView txtLoadingList, txtPlayingNow;
    ImageButton btnRefreshList; // refresh button , shown when no internet connection at startup ..
    RelativeLayout layoutSeekbars;
    ImageButton btnPlay, btnNext, btnPrev, btnShuffle, btnRepeatOne; // mini-player buttons
    ImageView imgLogo; // logo inside mini-player
    FloatingActionButton btnShare; // floating (& movable) share button
    SeekBar positionBar, volumeBar;
    ImageView btnVolume;
    TextView timeLabel;
    int totalTime;

    // media player (aka: mini-player) and related fields  ...
    MediaPlayer mp;
    boolean isPaused, isPrepared, isShuffled;
    int errorCounter;
    final int ERR_MAX_RELOADS = 2;

    Context context;
    View rootView;

    // variables used for "share" button to be movable/draggable
    float dX, dY;
    int lastAction;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //Toast.makeText(getActivity(), "hiding me", Toast.LENGTH_SHORT).show();
        } else {
            if (wasCalledFromOtherFragment) {
                loadTrack_at(currentTrackIndex);
            }
            wasCalledFromOtherFragment = false; // reset to false , for next time ...

            if (mp != null && mp.isPlaying()) {
                getActivity().findViewById(R.id.laySeekbars).setVisibility(View.VISIBLE);
            } else {
                // hidden until track is prepared to play
                getActivity().findViewById(R.id.laySeekbars).setVisibility(View.GONE);
            }
        }
    }

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


        // grouped into subgroups for ease of use ....
        mediaPlayerListeners();
        mediaButtonsListeners();
        listListeners();
        otherListeners();
    }



    private void mediaPlayerListeners() {

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                finishTrackLoadingEffects();
                seekbars_initialize();
                mpStart();
            }
        });

        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return handleError(mp, what, extra);
                // false: goto onCompletion listener (play next track)
                // true: error was handled here (mp resources released and re-loaded same track again)
            }

            private boolean handleError(MediaPlayer mp, int what, int extra) {
                genDevErrMsg(what, extra); // for developer use

                // handling strategy:
                // try (ERR_MAX_RELOADS) times to load same track (current)
                // if unsuccessful, load next ... and so on ...

                mp.release(); // release mp resources
                mp_Initialize(); // re-create mp instance
                if (errorCounter < ERR_MAX_RELOADS) {
                    Utils.displayMsg(getString(R.string.err_mp_reloading), rootView); // display msg to user
                    loadTrack_at(currentTrackIndex); // re-load current track
                    errorCounter = +1;
                    return true;
                } else {
                    Utils.displayMsg(getString(R.string.err_mp_loading_next), rootView); // display msg to user
                    errorCounter = 0;
                    return false; // goto onCompletion listener, to load next track in list
                }
            }

            private void genDevErrMsg(int what, int extra) {
                String errMsgDev = ""; // error msg for developer
                switch (what) { // main
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        errMsgDev += "Media Error Unknown.\t";
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        errMsgDev += "Media Error: Server Died.\t";
                        break;
                    default:
                        errMsgDev = "";
                }
                switch (extra) { // extra // more details //
                    case MediaPlayer.MEDIA_ERROR_IO:
                        errMsgDev += "(IO) File or network related operation error";
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        errMsgDev += "Bitstream is not conforming to the related coding standard or file spec";
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        errMsgDev += "The media framework does not support the the related coding standard or file spec";
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        errMsgDev += "operation timed out";
                        break;
                    default:
                        errMsgDev += "Error:(-2147483648), low-level system error";
                }

                Log.e("MPErr: ", errMsgDev); // for developer use
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnNext.performClick(); // play next track in line ...
            }
        });


    }

    private void mediaButtonsListeners() {


        // play - pause
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // in case track list is not loaded yet >> nothing to play //
                if (Defaults.dataList.isEmpty()) return; // do nothing & exit

                if (currentTrackIndex == -1) // at beginning, nothing loaded yet into media player !
                {
                    if (noInternet_mp()) return;
                    loadTrack_at(0); // load first track in list
                } else if (isPrepared) // a track is DONE loading/loaded into m.p.  but either paused or is playing
                {
                    finishTrackLoadingEffects();
                    changePlayPause();
                }
                // if track is NOT prepared [currently loading] >>> do nothing on click //
            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // in case track list is not loaded yet >> nothing to play //
                // in case no internet (after loading list) //
                if (Defaults.dataList.isEmpty() || noInternet_mp()) return;
                playNextTrack();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // in case track list is not loaded yet >> nothing to play //
                // in case no internet (after loading list) //
                if (Defaults.dataList.isEmpty() || noInternet_mp()) return;
                playPrevTrack();
            }
        });


        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleToggle();
            }
        });

        btnRepeatOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatOneToggle();
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoColorBlinkEffect();
            }
        });
    }

    private void listListeners() {

        listViewBroadcasts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (noInternet_mp()) return;

                itemBlinkEffect(view, Color.WHITE, 750);

                if (position == currentTrackIndex && isPrepared) // if current track is clicked again
                // added isPrepared check, in case tapped more than once before is loaded !
                {
                    changePlayPause();
                } else // different track is selected >> load & play //
                {
                    loadTrack_at(position);
                }
            }

            private void itemBlinkEffect(final View v, int color, final int delayMsec) {
                v.setBackgroundColor(color); // set blink color
                new Thread(new Runnable() { // generate delay in background thread
                    @Override
                    public void run() {
                        SystemClock.sleep(delayMsec);
                        getActivity().runOnUiThread(new Runnable() { // clear blink color after delay
                            @Override
                            public void run() {
                                v.setBackgroundColor(ContextCompat.getColor(context, R.color.clear));
                            }
                        });
                    }
                }).start();
            }
        });


        listViewBroadcasts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.showTrackInfoDialog(context, position);
                return true;  // to make short click work ok ...
            }
        });
    }

    private void otherListeners() {


        btnRefreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingEffects(); // reset effects of loading list
                btnRefreshList.setVisibility(View.INVISIBLE);  // hide when clicked
                // reload list again
                getDataFromRemote();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if list is empty, do nothing // nothing to share //
                if (Defaults.dataList.isEmpty()) return;
                // if no track has been selected yet ... request user to ...
                if (currentTrackIndex == -1) {
                    Utils.displayMsg(getString(R.string.select_track_to_share), rootView);
                    return;
                }

                dialog_ShareOrDownload();
            }

            private void dialog_ShareOrDownload() {

                View v = LayoutInflater.from(context).inflate(R.layout.dialog_share_download, null);
                Button share = v.findViewById(R.id.btnDialogShare);
                Button download = v.findViewById(R.id.btnDialogDownload);
                Button cancel = v.findViewById(R.id.btnDialogCancel);

                final Dialog dialog = new Dialog(context, R.style.Theme_MaterialComponents_Dialog);
                dialog.setContentView(v);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.create();

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        shareURL(Defaults.serverURL + Defaults.dataList.
                                get(currentTrackIndex).getFilename(), currentTrackTitle);
                    }
                });

                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        downloadURL(Defaults.serverURL + Defaults.dataList.
                                get(currentTrackIndex).getFilename(), currentTrackTitle);

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setCanceledOnTouchOutside(false);
                dialog.show();  // show the dialog
            }

            private void shareURL(String url, String name) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TITLE, name); // share title-name
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, name); // share subject-name // for emails
                    shareIntent.putExtra(Intent.EXTRA_TEXT, url); // share message
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.sharing_choice)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void downloadURL(String url, String name) {
                // TODO
            }
        });

        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (volumeBar.getVisibility() == View.VISIBLE) {
                    volumeBar.setVisibility(View.INVISIBLE);
                    positionBar.setVisibility(View.VISIBLE);
                } else {
                    volumeBar.setVisibility(View.VISIBLE);
                    positionBar.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private void logoColorBlinkEffect() {
        // TODO  color blink animation ?!?!
    }


    private void seekbars_initialize() { // for each track after loading ...

        layoutSeekbars.setVisibility(View.VISIBLE);
        mp.seekTo(0); // initial: at start
        mp.setVolume(0.5f, 0.5f);
        volumeBar.setProgress(volumeBar.getMax() / 2);  // initial: at middle position
        totalTime = mp.getDuration();
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum = progress / 100f;
                        btnVolume.setBackgroundResource(getVolumeIcon((int) (volumeNum * 100)));
                        mp.setVolume(volumeNum, volumeNum);
                    }

                    private int getVolumeIcon(int volumePercent) {
                        if (volumePercent < 5) return R.drawable.ic_volume_mute;
                        else if (volumePercent < 60) return R.drawable.ic_volume_middle;
                        else return R.drawable.ic_volume_up;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        updatePositionBar();
    }


    private void updatePositionBar() {
        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        if (mp.isPlaying()) { // this solved many crashes ...
                            Message msg = new Message();
                            msg.what = mp.getCurrentPosition();
                            handler.sendMessage(msg);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            String totalTimeStr = createTimeLabel(totalTime);
            timeLabel.setText(elapsedTime + "/" + totalTimeStr);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    private void shuffleToggle() {
        // TODO
    }

    private void repeatOneToggle() {

        // if not initialized or is loading , get out
        if (mp == null || !isPrepared) return;

        if (mp.isLooping()) // on
        {   // turn off
            mp.setLooping(false);
            btnRepeatOne.setColorFilter(ContextCompat.getColor(context, R.color.lightPrimaryColor));
        } else {   // turn on
            mp.setLooping(true);
            btnRepeatOne.setColorFilter(ContextCompat.getColor(context, R.color.navbar_item_text_selected));
        }
    }

    private void playPrevTrack() {
        int prevTrackIndex = currentTrackIndex < 1 ? // 0 or -1 //
                Defaults.dataList.size() - 1 : currentTrackIndex - 1;
        loadTrack_at(prevTrackIndex);
    }

    private void playNextTrack() {
        int nextTrackIndex = (currentTrackIndex + 1) % (Defaults.dataList.size());
        loadTrack_at(nextTrackIndex);
    }

    private void loadTrack_at(int trackIndex) {
        hideSeekbars();
        loadTrackEffects();
        loadTrack(trackIndex);
        updateCurrentTrackInfo(trackIndex);
    }

    private void hideSeekbars() {
        // better error handling , to hide them while loading a track ...
        // and show when prepared
        layoutSeekbars.setVisibility(View.GONE);
    }

    private boolean noInternet_mp() {
        // check internet connection / in case disconnected after loading items list
        if (!Utils.hasInternet(context)) // if NO internet connection, return true
        {
            Utils.displayMsg(getString(R.string.no_internet_mp), rootView);
            return true;
        }
        return false;
    }

    private void changePlayPause() {
        if (isPaused) {
            mpStart();
        } else {
            mpPause();
        }
    }

    private void mpPause() {
        mp.pause();
        btnPlay.setImageResource(R.drawable.ic_play_circle_outline);
        isPaused = true;
    }

    private void mpStart() {
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_pause_circle_outline);
        isPaused = false;
    }

    private void updateCurrentTrackInfo(int pos) {
        currentTrackIndex = pos;
        currentTrackTitle = Defaults.dataList.get(pos).getTitle();
    }

    private void finishTrackLoadingEffects() {
        // add track title to now playing top-bar
        txtPlayingNow.setText(makeMarqueeable(currentTrackTitle));
        txtPlayingNow.setSelected(true); // to start marquee effect
        // hide indicator animation
        indicatorLoadingBroadcast.smoothToHide();
        // change icon of play to pause
        btnPlay.setImageResource(R.drawable.ic_pause_circle_outline);
    }

    private StringBuilder makeMarqueeable(String txt) {
        // duplicates title and adds spaces so that long enough
        // to start marquee effect in the top text view (now playing)
        StringBuilder str = new StringBuilder(txt);
        StringBuilder space = new StringBuilder();
        StringBuilder result = new StringBuilder();
        // space string // tabX10 //
        for (int i = 0; i < 10; i += 1) {
            space.append("\t");
        }
        // result string // [str + space] X 3 //
        for (int i = 0; i < 3; i += 1) {
            result.append(str).append(space);
        }
        return result;
    }


    private void loadTrack(int pos) {
        mp.reset(); // in any case, reset first , then load
        isPrepared = false;
        try {
            String url = Defaults.serverURL +
                    URLEncoder.encode(Defaults.dataList.get(pos).getFilename(), "UTF-8");
            mp.setDataSource(url);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTrackEffects() {
        indicatorLoadingBroadcast.smoothToShow();
        txtPlayingNow.setText(getString(R.string.loading_selected_track));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPointers() {

        context = getActivity();
        // list layout & contents
        listViewBroadcasts = rootView.findViewById(R.id.lstBroadcasts);
        progressLoadingList = rootView.findViewById(R.id.progressLoadingList);
        txtLoadingList = rootView.findViewById(R.id.txtLoadingBroadcasts);
        btnRefreshList = rootView.findViewById(R.id.btnRefreshBList);
        indicatorLoadingBroadcast = rootView.findViewById(R.id.indicatorLoadingBroadcast);
        indicatorLoadingBroadcast.hide(); // hidden on list loading
        // mini player layout & contents
        layoutSeekbars = rootView.findViewById(R.id.laySeekbars);
        btnPlay = rootView.findViewById(R.id.btnPlayPause_MiniPlayer);
        btnNext = rootView.findViewById(R.id.btnNext_MiniPlayer);
        btnPrev = rootView.findViewById(R.id.btnPrev_MiniPlayer);
        btnShuffle = rootView.findViewById(R.id.btnShuffle_MiniPlayer);
        btnRepeatOne = rootView.findViewById(R.id.btnRepeatOne_MiniPlayer);
        imgLogo = rootView.findViewById(R.id.imgLogoStart_MiniPlayer);
        volumeBar = rootView.findViewById(R.id.volumeBar);
        positionBar = rootView.findViewById(R.id.positionBar);
        timeLabel = rootView.findViewById(R.id.txtTimeLabel);
        btnVolume = rootView.findViewById(R.id.btnVolume_MiniPlayer);
        // now playing
        txtPlayingNow = rootView.findViewById(R.id.txtPlayingNow);
        // share
        btnShare = rootView.findViewById(R.id.btnShare);
        btnShare.setOnTouchListener(this); // movable/draggable action

        /////////////////////////////////////////
        ////////// load track-list data /////////
        loadData();
        ////////////////////////////////////////

        mp_Initialize();
        errorCounter = 0;
    }

    private void mp_Initialize() {
        if (mp != null) return; // in case coming back after closing app
        //media player // instantiate & set audio attributes & initial boolean states
        mp = new MediaPlayer();
        AudioAttributes attributes = new AudioAttributes.Builder().
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        mp.setAudioAttributes(attributes);
        isPaused = true;
        isPrepared = false;
        isShuffled = false;
    }


    private void loadData() {
        // in case data is not loaded yet to list >> load it from remote
        if (Defaults.dataList.isEmpty()) {
            // AsyncTask to get data from database (remote) to DocStore (local)
            // & set adapter afterwards
            getDataFromRemote();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void getDataFromRemote() {

        // part 0 // check if has internet connection //
        if (!Utils.hasInternet(context)) // if no internet connection, no need to continue
        {
            Utils.displayMsg(context.getString(R.string.no_internet), rootView);
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
                if (result != Replicator.State.COMPLETE) {
                    Utils.displayMsg(contextTmp.getString(R.string.error_getting_docstore_2), rootView);
                } else {
                    // all is ok, we have local DocStore
                    // time to set serverURL && fill up data list && extras

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
                        return;
                    }

                    // storing db data into local objects & lists
                    setExtras(retrieved);
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
        // hide loading text & progress
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


    private void setExtras(DocumentRevision rev) {
        // internal use //
        Defaults.docname = (String) (rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_docname));
        Defaults.notesList.add((String) rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_note01));
        Defaults.notesList.add((String) rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_note02));
        Defaults.notesList.add((String) rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_note03));
    }

    private void setServerURL(DocumentRevision rev) {
        Defaults.serverURL = (String) (rev.getBody().asMap().get(Defaults.BroadcastDoc_Key_serverURL));

        Log.e("some info ... ", "\n" + Defaults.serverURL +
                "\n" + Defaults.docname +
                "\n" + Defaults.notesList.get(0) +
                "\n" + Defaults.notesList.get(1) +
                "\n" + Defaults.notesList.get(2));
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

        // after done loading data, add titles to searchSuggestions list // "sub-list" //
        setSearchSuggestions();
    }

    private void setSearchSuggestions() {

        for (BroadcastDataClass item : Defaults.dataList) {
            Defaults.searchSuggestions.add(item.getTitle());
        }
    }

    private void requestInternetConnection() {
        txtLoadingList.setText(getString(R.string.request_internet_connection)); // request msg
        progressLoadingList.setVisibility(View.INVISIBLE); // hide
        btnRefreshList.setVisibility(View.VISIBLE); // show refresh button_back
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }


    // for the SHARE button (floating) to be movable / draggable //
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                v.setY(event.getRawY() + dY);
                v.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    btnShare.performClick();
                break;

            default:
                return false;
        }
        return true;
    }
}
