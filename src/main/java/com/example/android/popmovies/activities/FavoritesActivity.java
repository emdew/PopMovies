package com.example.android.popmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.popmovies.AppDatabase;
import com.example.android.popmovies.MainViewModel;
import com.example.android.popmovies.models.Movies;
import com.example.android.popmovies.R;
import com.example.android.popmovies.adapters.MoviesAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener  {

    private final String LOG_TAG = FavoritesActivity.class.getSimpleName();

    RecyclerView mRecyclerView;
    MoviesAdapter mMoviesAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<Movies> mFavoritesList;
    private AppDatabase mDb;
    MoviesAdapter.ViewHolder mHolder;

    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_MOVIE_ID = "instanceMovieId";

    public int mMovieId = DEFAULT_MOVIE_ID;

    // Constant for default movie id to be used when not in update mode
    public static final int DEFAULT_MOVIE_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_favorites);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.favorites_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        int numberOfColumns = 2;
        mLayoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFavoritesList = new ArrayList<>();
        mMoviesAdapter = new MoviesAdapter(this, mFavoritesList, this);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // handled by adapter
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movies>>() {
            @Override
            public void onChanged(@Nullable List<Movies> moviesList) {
                Log.d(LOG_TAG, "Updating list of movies from LiveData in ViewModel");
                mMoviesAdapter.setMovieData(moviesList);
            }
        });
    }
}
