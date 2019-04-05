package com.hackathon.radioetzionapp.Utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.radioetzionapp.Data.BroadcastDataClass;
import com.hackathon.radioetzionapp.Data.Defaults;
import com.hackathon.radioetzionapp.R;

public class Utils {


    public static boolean hasInternet(Context context){
        //if we have internet >> true, otherwise>> false
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void displayMsg(String msg, View parentView){
        // display snackbar msg to user
        final Snackbar myMsg = Snackbar.make(parentView,msg,Snackbar.LENGTH_LONG);
        myMsg.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMsg.dismiss();
            }
        });
        myMsg.show();
    }

    public static void showTrackInfoDialog(Context context, int pos) {

        // inflate and set up views ...
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_track_details, null);

        TextView title = v.findViewById(R.id.details_view_track_title);
        TextView desc = v.findViewById(R.id.details_view_description);
        desc.setMovementMethod(new ScrollingMovementMethod());
        TextView broadcasters = v.findViewById(R.id.details_view_broadcasters);
        broadcasters.setMovementMethod(new ScrollingMovementMethod());
        ImageView cancel = v.findViewById(R.id.btnDialogDetailsCancel);

        // assign data to views ...
        assignDataToViews(context, pos, title, desc, broadcasters);

        // dialog
        final Dialog infoDialog = new Dialog(context, R.style.Theme_MaterialComponents_Dialog);
        infoDialog.setContentView(v);
        infoDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

        infoDialog.create();
        infoDialog.show();
    }

    // for alert dialog (Accessory method)
    private static void assignDataToViews(Context context, int pos, TextView title, TextView desc, TextView broadcasters) {
        BroadcastDataClass selectedItem = Defaults.dataList.get(pos);

        // title
        title.setText(selectedItem.getTitle());

        desc.setText("");
        broadcasters.setText("");

        // desc
        StringBuilder descStr = new StringBuilder();
        if (!selectedItem.getDescription().isEmpty()) {
            descStr.append(context.getString(R.string.dialog_subtitle_broadcast_details))
                    .append("\n\n").append(selectedItem.getDescription()).append("\n\n");
        }
        if (!selectedItem.getGuestsList().isEmpty()) {
            if (!selectedItem.getGuestsList().get(0).isEmpty()) { // to make sure because list !
                descStr.append(context.getString(R.string.dialog_subtitle_guests))
                        .append("\n\n").append(selectedItem.getGuestsListFormatted());
            }
        }
        if (descStr.toString().isEmpty())
            desc.setVisibility(View.GONE); // hide view cause empty content
        else desc.setText(descStr);

        // broadcasters
        StringBuilder broadcastersStr = new StringBuilder();
        if (!selectedItem.getBroadcastersList().isEmpty()) {
            if (!selectedItem.getBroadcastersList().get(0).isEmpty()) {  // to make sure because list !
                broadcastersStr.append(context.getString(R.string.dialog_subtitle_broadcasters_participants))
                        .append("\n\n").append(selectedItem.getBroadcastersListFormatted());
                broadcasters.setText(broadcastersStr);
            }
        }
        if (broadcastersStr.toString().isEmpty()) {
            broadcasters.setVisibility(View.GONE); // hide view cause empty content
            // hide the line above also // better looking
            broadcasters.getRootView().findViewById(R.id.lineAboveBroadcasters).setVisibility(View.GONE);
        } else {
            broadcasters.setText(broadcastersStr);
        }
    }
}
