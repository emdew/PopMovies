package com.example.android.popmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMovieId;

    public MovieDetailsViewModelFactory(AppDatabase database, int movieId) {
        mDb = database;
        mMovieId = movieId;
    }

    // Note: This can be reused with minor modifications
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailsViewModel(mDb, mMovieId);
    }
}
