package com.example.android.popmovies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class MoviesLoader extends AsyncTaskLoader<List<Movies>> {

    private static final String LOG_TAG = MoviesLoader.class.getName();

    // Query URL
    private String mUrl;

    public MoviesLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Movies> loadInBackground() {
        List<Movies> movies = MovieUtils.fetchMovieData(mUrl);
        return movies;
    }
}
