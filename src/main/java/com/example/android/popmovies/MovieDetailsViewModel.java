package com.example.android.popmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

public class MovieDetailsViewModel extends ViewModel {

    private LiveData<Movies> movie;

    public MovieDetailsViewModel(AppDatabase database, int movieId) {
        movie = database.moviesDao().loadMovieById(movieId);
    }

    public LiveData<Movies> getMovie() {
        return movie;
    }
}
