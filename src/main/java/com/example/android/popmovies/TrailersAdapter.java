package com.example.android.popmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> implements Serializable {

    private Context mContext;
    private List<Trailers> mTrailersList;
    private Trailers mTrailer;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public TrailersAdapter(Context context, List<Trailers> trailersList, ListItemClickListener listener){
        mContext = context;
        mTrailersList = trailersList;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mTrailersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        // the data item is just an image stored as a string in this case
        private ImageView mTrailerThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrailerThumbnail = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.ViewHolder holder, int position) {
        mTrailer = mTrailersList.get(position);
        Picasso.get().load("https://img.youtube.com/vi/" + mTrailer.getYoutubeKey() + "/0.jpg").into(holder.mTrailerThumbnail);
    }
}
