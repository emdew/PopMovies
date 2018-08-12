package com.example.android.popmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popmovies.activities.MovieDetailsActivity;
import com.example.android.popmovies.models.Movies;
import com.example.android.popmovies.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> implements Serializable {

    private Context mContext;
    private List<Movies> mMoviesList;
    private Movies mMovie;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviesAdapter(Context context, List<Movies> moviesList, ListItemClickListener listener) {
        mContext = context;
        mMoviesList = moviesList;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        // the data item is just an image stored as a string in this case
        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

            Movies currentMovie = mMoviesList.get(clickedPosition);
            Gson gson = new Gson();

            Intent intent = new Intent(mContext, MovieDetailsActivity.class);
            intent.putExtra("key", gson.toJson(currentMovie));

            mContext.startActivity(intent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get item from your data
        // Replace the contents of the view with that item
        mMovie = mMoviesList.get(position);
        Picasso.get().load("http://image.tmdb.org/t/p/w342/" + mMovie.getPoster()).into(holder.mImageView);
    }

    public void setMovieData(List<Movies> movieData) {
        mMoviesList = movieData;
        notifyDataSetChanged();
    }

    public List<Movies> getFavorites() {
        return mMoviesList;
    }
}
