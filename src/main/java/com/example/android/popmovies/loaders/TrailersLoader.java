package com.example.android.popmovies.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popmovies.models.Trailers;

import java.net.URL;
import java.util.List;

public class TrailersLoader extends AsyncTaskLoader<List<Trailers>> {

    private static final String LOG_TAG = TrailersLoader.class.getName();

    // Query URL
    private URL mUrl;
    private List<Trailers> trailerKeys;

    public TrailersLoader(Context context, URL url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Trailers> loadInBackground() {

        List<Trailers> trailerKeys = MovieUtils.fetchTrailerKeys(mUrl);
        return trailerKeys;
    }
}
