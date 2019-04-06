package com.hackathon.radioetzionapp.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FavoritesSharedPref {

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public FavoritesSharedPref(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void addtoFav(String trackTitle) {
        editor.putBoolean(trackTitle, true);
        editor.commit();
    }

    public void removeFromFav(String trackTitle) {
        editor.putBoolean(trackTitle, false);
        editor.commit();
    }

    public boolean isInFav(String trackTitle) {
        return preferences.getBoolean(trackTitle, false);
    }
}
