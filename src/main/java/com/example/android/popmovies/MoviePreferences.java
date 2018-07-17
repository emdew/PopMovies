package com.example.android.popmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

public class MoviePreferences {

    public static String getPreferredSortOrder(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // key
        String keyForSortOrder = context.getString(R.string.sort_order_key);

        // value
        String defaultSort = context.getString(R.string.pref_most_popular_key);

        return prefs.getString(keyForSortOrder, defaultSort);
    }
}
