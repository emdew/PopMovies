package com.example.android.popmovies.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popmovies.models.Reviews;

import java.net.URL;
import java.util.List;

public class ReviewsLoader extends AsyncTaskLoader<List<Reviews>> {

    private static final String LOG_TAG = ReviewsLoader.class.getName();

    // Query URL
    private URL mUrl;
    private List<Reviews> reviews;

    public ReviewsLoader(Context context, URL url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Reviews> loadInBackground() {

        List<Reviews> reviews = MovieUtils.fetchReviews(mUrl);
        return reviews;
    }
}
