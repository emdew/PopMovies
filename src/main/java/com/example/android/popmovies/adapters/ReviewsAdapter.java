package com.example.android.popmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.models.Reviews;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private Context mContext;
    private List<Reviews> mReviewsList;

    public ReviewsAdapter(Context context, List<Reviews> reviewsList) {
        mContext = context;
        mReviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_rv_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.mAuthor.setText(mReviewsList.get(position).getAuthor());
        holder.mContent.setText(mReviewsList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView mAuthor;
        TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.review_author);
            mContent = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}
