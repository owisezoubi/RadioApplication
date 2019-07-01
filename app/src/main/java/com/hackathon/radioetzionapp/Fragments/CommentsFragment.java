package com.hackathon.radioetzionapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudant.sync.documentstore.AttachmentException;
import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.hackathon.radioetzionapp.Activities.MainActivity;
import com.hackathon.radioetzionapp.Adapters.CommentsListAdapter;
import com.hackathon.radioetzionapp.Data.CommentDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;
import com.hackathon.radioetzionapp.Utils.UtilsMapData;
import com.hackathon.radioetzionapp.Utils.UtilsSetData;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


public class CommentsFragment extends Fragment {

    Context context;
    View rootView;
    DocumentStore dst;
    TextView txtCommentTitle, txtLoadingComments;
    ImageView btnAddComment, btnNextTitle, btnPrevTitle, btnRefreshComments;
    RelativeLayout layTop, layBody;
    ListView lstComments;
    CommentsListAdapter adapter;
    int trackIndex;

    Fragment commentsFragment, homeFragment;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            // Toast.makeText(getActivity(), "hiding me", Toast.LENGTH_SHORT).show();
        } else {

            // updated every time this fragment is shown !

            setPointers();
            setListeners();
            showCommentData(); // for current track playing OR the first track (if non is playing)
        }
    }


    private void setPointers() {
        this.context = getActivity();
        txtCommentTitle = rootView.findViewById(R.id.txtTitle_Comments);
        txtLoadingComments = rootView.findViewById(R.id.txtLoadingComments);
        btnAddComment = rootView.findViewById(R.id.btnAddComment);
        btnRefreshComments = rootView.findViewById(R.id.btnRefreshComments);
        btnNextTitle = rootView.findViewById(R.id.btnNextTitle_Comments);
        btnPrevTitle = rootView.findViewById(R.id.btnPrevTitle_Comments);
        lstComments = rootView.findViewById(R.id.lstComments);
        layBody = rootView.findViewById(R.id.layCommentsBody);
        layTop = rootView.findViewById(R.id.layCommentsTop);

        // fragments
        homeFragment = getFragmentManager().findFragmentByTag(MainActivity.TAG_HOME);
        commentsFragment = getFragmentManager().findFragmentByTag(MainActivity.TAG_COMMENTS);


        trackIndex = setTrackIndex();
    }

    private int setTrackIndex() {
        if (Defaults.dataList.isEmpty()) {
            Toast.makeText(context, getString(R.string.toast_comments_datalistempty), Toast.LENGTH_LONG).show();

            layBody.setVisibility(View.INVISIBLE);
            layTop.setVisibility(View.INVISIBLE);

            return -1;
        }
        layBody.setVisibility(View.VISIBLE);
        layTop.setVisibility(View.VISIBLE);
        return HomeFragment.currentTrackIndex == -1 ? 0 : HomeFragment.currentTrackIndex;
    }

    private void setListeners() {

        btnNextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackIndex = (trackIndex + 1) % (Defaults.dataList.size());
                showCommentData();
            }
        });

        btnPrevTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackIndex = trackIndex < 1 ? // 0 or -1 //
                        Defaults.dataList.size() - 1 : trackIndex - 1;
                showCommentData();
            }
        });

        txtCommentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // same steps as in SEARCH FRAGMENT's onItemClick //
                // when clicked, loads selected track in HOME & moves to HOME fragment
                HomeFragment.currentTrackIndex = trackIndex; // updates track index in home, with this one ...
                HomeFragment.wasCalledFromOtherFragment = true;
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                        .hide(commentsFragment)
                        .show(homeFragment).commit();
                BottomNavigationView navBar = ((AppCompatActivity) context).findViewById(R.id.navigation);
                navBar.setSelectedItemId(R.id.navigation_home);
            }
        });

        txtCommentTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showTrackInfoDialog(context, trackIndex);
                return true;
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCommentDialog();
            }
        });

        btnRefreshComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingEffects();
                refreshData();
            }


        });
    }

    private void startLoadingEffects() {
        txtLoadingComments.setVisibility(View.VISIBLE);
        txtLoadingComments.setText(getString(R.string.loading_comments_list));
    }

    private void requestInternetConnection() {
        txtLoadingComments.setText(getString(R.string.request_internet_connection)); // request msg
    }

    private void finishLoadingEffects() {
        txtLoadingComments.setVisibility(View.INVISIBLE);
    }

    private void refreshData() {
        // pre-check // check if has internet connection //
        if (!Utils.hasInternet(context)) // if no internet connection, no need to continue
        {
            Utils.displayMsg(context.getString(R.string.no_internet_comments), rootView);
            requestInternetConnection();
            return;
        }

        // AsyncTask to get data from database (remote) to DocStore (local)
        // & set adapter afterwards
        getDataFromRemote();
    }

    @SuppressLint("StaticFieldLeak")
    private void getDataFromRemote() {

        // part 1 // URI & DS instance creation
        URI uri = null;
        dst = null;
        try {
            uri = new URI(Defaults.CloudantURL + "/" + Defaults.RadioDBName);
            dst = DocumentStore.getInstance(new File(
                    context.getDir(Defaults.LOCAL_DS_PATH, Context.MODE_PRIVATE),
                    Defaults.RadioDBName));
        } catch (URISyntaxException use) {
            use.printStackTrace();
        } catch (DocumentStoreNotOpenedException dsnoe) {
            dsnoe.printStackTrace();
        }

        if (uri == null || dst == null) {
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

                Replicator pullReplicator = ReplicatorBuilder.pull().from(uriTmp).to(dst).build();
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
                        retrieved = dst.database().read(Defaults.BroadcastsDocID);
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
                    UtilsSetData.setAllData(retrieved);

                    // data is ready, time to set current comments' list adapter!
                    showCommentData(); // to refresh adapter !
                    finishLoadingEffects(); // effects !
                }
            }
        }.execute();
    }



    private void showAddCommentDialog() {

        // inflate and set up views ...
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_add_comment, null);

        ImageView cancel = v.findViewById(R.id.btnCancelDialogAddComment);
        TextView title = v.findViewById(R.id.txtTitle_DialogAddComment);

        final EditText txtInputContent = v.findViewById(R.id.txtInputCommentContent);
        final EditText txtInputUsername = v.findViewById(R.id.txtInputCommentUsername);
        Button btnSubmit = v.findViewById(R.id.btnCommentSubmit);


        // dialog
        final Dialog addCommentDialog = new Dialog(context);
        addCommentDialog.setContentView(v);
        addCommentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog.dismiss();
            }
        });

        title.setText(Defaults.dataList.get(trackIndex).getTitle());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment(txtInputContent.getText().toString(), txtInputUsername.getText().toString());
                addCommentDialog.dismiss();
            }
        });

        addCommentDialog.create();
        addCommentDialog.show();
    }

    private void submitComment(String content, String username) {

        // step 1: check content & username validity // for now if not empty fields is enough
        if (content.isEmpty() || username.isEmpty()) {
            Toast.makeText(context, getString(R.string.dialog_add_comment_empty_fields), Toast.LENGTH_LONG).show();
            return;
        }

        // step 2: create tmp Comment object & add timestamp
        CommentDataClass tmp = new CommentDataClass(System.currentTimeMillis(), username, content);

        // step 3: UPDATE local (Defaults.datalist) & remote db (cloudant) (pull & push to sync)
        if (!updateCommentsList(tmp)) {
            Toast.makeText(context, getString(R.string.dialog_add_comment_error_while_updating), Toast.LENGTH_LONG).show();
            return;
        }

        // step 4:  refresh adapter
        showCommentData();
    }


    private boolean updateCommentsList(CommentDataClass newCommentObj) {

        // returns true if all is OK & done <=> data updated locally and remotely
        // false otherwise

        // step 0:  check internet connection
        if (!Utils.hasInternet(context)) // if no internet connection, no need to continue
        {
            return false;
        }

        // step 1.1:  PULL previous revision from remote (START of sync)
        URI uri = null;
        DocumentStore dsTmp = null;
        try {
            uri = new URI(Defaults.CloudantURL + "/" + Defaults.RadioDBName);
            dsTmp = DocumentStore.getInstance(new File(
                    context.getDir(Defaults.LOCAL_DS_PATH, Context.MODE_PRIVATE),
                    Defaults.RadioDBName));
        } catch (URISyntaxException use) {
            use.printStackTrace();
        } catch (DocumentStoreNotOpenedException dsnoe) {
            dsnoe.printStackTrace();
        }

        if (uri == null || dsTmp == null) {
            return false;
        }

        Replicator pullReplicator = ReplicatorBuilder.pull().from(uri).to(dsTmp).build();
        pullReplicator.start();


        DocumentRevision prevRevision = null;
        try {
            prevRevision = dsTmp.database().read(Defaults.BroadcastsDocID);
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentStoreException e) {
            e.printStackTrace();
        }

        if (prevRevision == null) {
            return false;
        }

        // step 1.2: set/update LOCAL data objects and lists from the NEWLY-pulled revision!
        UtilsSetData.setAllData(prevRevision);

        // step 2: add a NEW comment obj to LOCAL data list
        Defaults.dataList.get(trackIndex).getCommentsList().add(newCommentObj);

        // step 3: reverse map building from local objects & lists to a MAP<String,Object>
        Map<String, Object> mappedData = UtilsMapData.getMappedData();

        // step 4:  update prev. document revision with newly mapped data
        prevRevision.setBody(DocumentBodyFactory.create(mappedData));
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
        if (newRevision == null) {
            return false;
        }

        // step 5: PUSH the new revision to remote (end of sync)
        Replicator replicator = ReplicatorBuilder.push().from(dsTmp).to(uri).build();
        replicator.start();


        // reached end and all is ok , return true
        return true;
    }


    private void showCommentData() {

        if (trackIndex == -1) return;

        txtCommentTitle.setText(Defaults.dataList.get(trackIndex).getTitle());

        // refresh adapter
        adapter = new CommentsListAdapter(context, trackIndex);
        lstComments.setAdapter(adapter);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comments, null);
        return rootView;
    }
}
