package com.hackathon.radioetzionapp.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.hackathon.radioetzionapp.Activities.MainActivity;
import com.hackathon.radioetzionapp.Adapters.CommentsListAdapter;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;
import com.hackathon.radioetzionapp.Utils.Utils;


public class CommentsFragment extends Fragment {

    Context context;
    View rootView;
    TextView txtCommentTitle;
    ImageView btnAddComment, btnNextTitle, btnPrevTitle;
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
        btnAddComment = rootView.findViewById(R.id.btnAddComment);
        btnNextTitle = rootView.findViewById(R.id.btnNextTitle_Comments);
        btnPrevTitle = rootView.findViewById(R.id.btnPrevTitle_Comments);
        lstComments = rootView.findViewById(R.id.lstComments);
        layBody = rootView.findViewById(R.id.layCommentsBody);
        layTop = rootView.findViewById(R.id.layCommentsTop);

        // fragments
        homeFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_HOME);
        commentsFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_COMMENTS);

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
        final Dialog addCommentDialog = new Dialog(context, R.style.Theme_MaterialComponents_Dialog);
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
            public void onClick(View v) {/*
                submitComment(txtInputContent.getText().toString(), txtInputUsername.getText().toString());
            */
            }
        });

        addCommentDialog.create();
        addCommentDialog.show();
    }
/*
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

        // returns true if all is OK & done
        // false otherwise


        // step 0:  check internet connection
        if (!Utils.hasInternet(context)) // if no internet connection, no need to continue
        {
            return false;
        }


        // step 1:  get doc from remote // PULL
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


        // 2 // change & update (local)
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

        // !!!!!!!!!!!!!!!!!!!!!!!!!!! stuck here !! ///////
        Map<String, Object> tmpMap = prevRevision.getBody().asMap();
        tmpMap.put(Defaults.BroadcastDoc_Key_dataList,
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

        if (newRevision == null) {
            msgFailed("ERROR !  # 3");
            return;
        }

        /*



        // 2 // change & update (local)
        DocumentRevision prevRevision=null;
        try {
            prevRevision = dsTmp.database().read(docID);
        } catch (DocumentNotFoundException e) { e.printStackTrace(); }
        catch (DocumentStoreException e) { e.printStackTrace(); }

        if(prevRevision==null) {
            msgFailed("ERROR !  # 2 ");
            return;
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

        // 3 // replicate [local-> remote] // PUSH
        Replicator replicator = ReplicatorBuilder.push().from(dsTmp).to(uri).build();
        replicator.start();

    }
*/

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
