package com.example.android.popmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies.AppDatabase;
import com.example.android.popmovies.AppExecutors;
import com.example.android.popmovies.MovieDetailsViewModel;
import com.example.android.popmovies.MovieDetailsViewModelFactory;
import com.example.android.popmovies.loaders.MovieUtils;
import com.example.android.popmovies.models.Movies;
import com.example.android.popmovies.R;
import com.example.android.popmovies.models.Reviews;
import com.example.android.popmovies.models.Trailers;
import com.example.android.popmovies.adapters.TrailersAdapter;
import com.example.android.popmovies.adapters.TrailersAdapter.ListItemClickListener;
import com.example.android.popmovies.adapters.ReviewsAdapter;
import com.example.android.popmovies.loaders.ReviewsLoader;
import com.example.android.popmovies.loaders.TrailersLoader;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, ListItemClickListener {

    public final static String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w780/";

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    // Extra for the movie ID to be received in the intent
    public static final String ITEM_MOVIE_ID = "itemMovieId";

    public int mMovieId = DEFAULT_MOVIE_ID;

    // Constant for default movie id to be used when not in update mode
    public static final int DEFAULT_MOVIE_ID = -1;

    private static final int TRAILER_LOADER_ID = 1;

    private static final int REVIEW_LOADER_ID = 2;

    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_MOVIE_ID = "instanceMovieId";

    private static boolean FAVORITED = false;

    private AppDatabase mDb;
    TextView mTitleTV;
    TextView mRatingTV;
    TextView mReleaseDate;
    TextView mSynopsis;
    ImageButton mImageButton;
    Intent mIntent;
    RecyclerView mTrailerRv;
    RecyclerView mReviewsRv;
    URL mTrailerUrl;
    URL mReviewsUrl;
    private List<Trailers> mTrailersList;
    private List<Reviews> mReviewsList;
    TrailersAdapter mTrailersAdapter;
    ReviewsAdapter mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mDb = AppDatabase.getInstance(getApplicationContext());

        final Movies currentMovie = getCurrentMovie();
        populateUi(currentMovie);

        mIntent = getIntent();
        if (mIntent != null && mIntent.hasExtra(ITEM_MOVIE_ID)) {
            if (mMovieId == DEFAULT_MOVIE_ID) {
                mMovieId = mIntent.getIntExtra(ITEM_MOVIE_ID, DEFAULT_MOVIE_ID);
                MovieDetailsViewModelFactory factory = new MovieDetailsViewModelFactory(mDb, mMovieId);
                final MovieDetailsViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(MovieDetailsViewModel.class);
                viewModel.getMovie().observe(this, new Observer<Movies>() {
                    @Override
                    public void onChanged(@Nullable Movies movies) {
                        viewModel.getMovie().removeObserver(this);
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private Movies getCurrentMovie() {
        Gson gson = new Gson();
        mIntent = getIntent();
        String stringCurrentMovie = mIntent.getStringExtra("key");
        return gson.fromJson(stringCurrentMovie, Movies.class);
    }

    private void populateUi(Movies currentMovie) {

        // Find TextView with the ID "backdrop_iv"
        ImageView backdrop = (ImageView) findViewById(R.id.backdrop_iv);
        // Display text in TextView... samzies below
        Picasso.get().load(BASE_IMAGE_URL + currentMovie.getBackdrop()).into(backdrop);

        mTitleTV = (TextView) findViewById(R.id.title_tv);
        mTitleTV.setText(currentMovie.getTitle());

        mReleaseDate = (TextView) findViewById(R.id.release_date_tv);
        mReleaseDate.setText("Released: " + currentMovie.getReleaseDate());

        mRatingTV = (TextView) findViewById(R.id.rating_tv);
        mRatingTV.setText("Viewer rating: " + currentMovie.getRating() + "/10");

        mSynopsis = (TextView) findViewById(R.id.synopsis_tv);
        mSynopsis.setText(currentMovie.getSynopsis());

        mImageButton = (ImageButton) findViewById(R.id.favorite_button);
        mImageButton.setOnClickListener(this);

        // get the id set by the website and hold it in String var.
        String movieId = Integer.toString(currentMovie.getMovieId());

        // use it to set up the trailer RV
        setupTrailerRv(movieId);

        // also use it to get reviews
        setupReviewRv(movieId);
    }

    private void setupTrailerRv(String movieId) {

        // Movie ID used to create the URL for the trailer info on the Movie website -- not ready for youtube yet
        // This url needs to be passed into onCreateLoader which will call fetchTrailerData with this url
        mTrailerUrl = MovieUtils.buildTrailerURLFromMdb(movieId);

        // Trailer RV nested in activity_movie_details
        mTrailerRv = (RecyclerView) findViewById(R.id.trailer_rv);

        mTrailerRv.setHasFixedSize(false);
        mTrailerRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mTrailersList = new ArrayList<>();
        mTrailersAdapter = new TrailersAdapter(this, mTrailersList, this);

        mTrailerRv.setAdapter(mTrailersAdapter);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // init trailer loader
            LoaderManager ld = getSupportLoaderManager();
            ld.initLoader(TRAILER_LOADER_ID, null, trailerLoaderListener);
        } else {
            // do nothing for now
            // was trying to set an empty view but it kept throwing errors
        }
        Log.d(LOG_TAG, "TRAILERRVSETUP: " + mTrailerUrl);
    }

    private void setupReviewRv(String movieId) {

        mReviewsUrl = MovieUtils.buildReviewURLFromMdb(movieId);
        mReviewsRv = findViewById(R.id.review_rv);
        mReviewsRv.setHasFixedSize(true);
        mReviewsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mReviewsList = new ArrayList<>();
        mReviewsAdapter = new ReviewsAdapter(this, mReviewsList);
        mReviewsRv.setAdapter(mReviewsAdapter);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // init trailer loader
            LoaderManager lm = getSupportLoaderManager();
            lm.initLoader(REVIEW_LOADER_ID, null, reviewLoaderListener);
        } else {
            // do nothing for now
            // was trying to set an empty view but it kept throwing errors
        }
        Log.d(LOG_TAG, "REVIEWRVSETUP: " + mReviewsUrl);
    }

    @Override
    public void onClick(View v) {
        mIntent.getIntExtra(ITEM_MOVIE_ID, DEFAULT_MOVIE_ID);
        if (!FAVORITED) {
            favorited();
            FAVORITED = true;
        } else {
            unfavorited();
            FAVORITED = false;
        }
    }

    private void favorited() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movies movie = getCurrentMovie();
                mDb.moviesDao().insertMovie(movie);
                movie.setMovieId(mMovieId);
                Log.d(LOG_TAG, "SAVING TO DATABASE");
            }
        });
    }

    private void unfavorited () {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movies movie = getCurrentMovie();
                mDb.moviesDao().deleteMovie(movie);
                //movie.setDatabaseId(DEFAULT_MOVIE_ID);
                Log.d(LOG_TAG, "DELETING FROM DATABASE");
            }
        });
    }

    private LoaderCallbacks<List<Trailers>> trailerLoaderListener = new LoaderCallbacks<List<Trailers>>() {
        @NonNull
        @Override
        public Loader<List<Trailers>> onCreateLoader(int id, @Nullable Bundle args) {
            TrailersLoader trailers = new TrailersLoader(MovieDetailsActivity.this, mTrailerUrl);
            return trailers;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Trailers>> loader, List<Trailers> trailers) {

            if (trailers != null && !trailers.isEmpty()) {
                mTrailersList.addAll(trailers);
            } else {
                // do nothing for now
                // was trying to set an empty view but it kept throwing errors
            }

            mTrailersAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Trailers>> loader) {
            mTrailersList.clear();
        }
    };

    private LoaderCallbacks<List<Reviews>> reviewLoaderListener = new LoaderCallbacks<List<Reviews>>() {
        @NonNull
        @Override
        public Loader<List<Reviews>> onCreateLoader(int id, @Nullable Bundle args) {
            ReviewsLoader reviews = new ReviewsLoader(MovieDetailsActivity.this, mReviewsUrl);
            return reviews;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Reviews>> loader, List<Reviews> reviews) {

            if (reviews != null && !reviews.isEmpty()) {
                mReviewsList.addAll(reviews);
            } else {
                // do nothing for now
                // was trying to set an empty view but it kept throwing errors
            }

            mReviewsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Reviews>> loader) {
            mReviewsList.clear();
        }
    };

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Trailers currentTrailer = mTrailersList.get(clickedItemIndex);

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + currentTrailer.getYoutubeKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + currentTrailer.getYoutubeKey()));

        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }
}
