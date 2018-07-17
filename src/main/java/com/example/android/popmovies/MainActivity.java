package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popmovies.MoviesAdapter.ListItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Movies>>, ListItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView mEmptyView;
    RecyclerView mRecyclerView;
    MoviesAdapter mMoviesAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<Movies> mMoviesList;
    private static final int MOVIE_LOADER_ID = 0;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    // URL to query the movie database
    private static final String MOVIE_MOST_POPULAR_URL =
            "https://api.themoviedb.org/3/movie/popular?&api_key=36bfad9d0ba02dad9b3c2c167b27d286";

    private static final String MOVIE_HIGH_RATED_URL =
            "https://api.themoviedb.org/3/movie/top_rated?&api_key=36bfad9d0ba02dad9b3c2c167b27d286";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rv);

        // set up main activity
        setupMainUi();

        // connect to the internet and initiate Loader
        connectToInternet();

        // Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
        // SharedPreference has changed.
        Log.d(LOG_TAG, "onCreate: registering preference changed listener");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    private void setupMainUi() {
        // Find RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Then initialize the empty state
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mRecyclerView.setHasFixedSize(true);

        int numberOfColumns = 2;
        mLayoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMoviesList = new ArrayList<>();
        mMoviesAdapter = new MoviesAdapter(this, mMoviesList, this);

        mRecyclerView.setAdapter(mMoviesAdapter);
    }

    private void connectToInternet() {
        // Instead of just a loader manager we check for network connectivity
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
            mEmptyView.setVisibility(View.GONE);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        } else if (id == R.id.action_favorites) {
            Intent startFavoritesActivity = new Intent(this, FavoritesActivity.class);
            startActivity(startFavoritesActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.support.v4.content.Loader<List<Movies>> onCreateLoader(int id, Bundle bundle) {

        String preferredSortOrder = MoviePreferences.getPreferredSortOrder(this);

        if (preferredSortOrder.equals(getString(R.string.pref_most_popular_key))) {
            Log.d(LOG_TAG, "LoadInBackground: popular url should be used");
            MoviesLoader movies = new MoviesLoader(this, MOVIE_MOST_POPULAR_URL);
            return movies;
        } else {
            Log.d(LOG_TAG, "LoadInBackground: top rated url should be used");
            MoviesLoader movies = new MoviesLoader(this, MOVIE_HIGH_RATED_URL);
            return movies;
        }
        // Can be converted to inline variable!
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<Movies>> loader, List<Movies> movies) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        if (movies != null && !movies.isEmpty()) {
            mMoviesList.addAll(movies);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        // Set empty state text to display "No movies available!"
        mEmptyView.setText(R.string.no_movies);

        mMoviesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<List<Movies>> loader) {
        mMoviesList.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(LOG_TAG, "onSharedPrefs: preferences were updated");
            mMoviesList.clear();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // do nothing? Handled by Adapter.
    }
}
