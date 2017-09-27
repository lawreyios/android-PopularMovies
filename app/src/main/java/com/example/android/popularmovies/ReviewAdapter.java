package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lawrey on 26/9/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private Review[] mReviewsData;

    private final ReviewAdapterOnClickHandler mClickHandler;

    public interface ReviewAdapterOnClickHandler {
        void onClick(Review review);
    }

    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_author) TextView mAuthorTextView;
        @BindView(R.id.tv_content) TextView mContentTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review review = mReviewsData[adapterPosition];
            mClickHandler.onClick(review);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder viewHolder, int i) {
        Review review = mReviewsData[i];

        viewHolder.mAuthorTextView.setText(review.author);
        viewHolder.mContentTextView.setText(review.content);
    }

    @Override
    public int getItemCount() {
        if (null == mReviewsData) return 0;
        return mReviewsData.length;
    }

    public void setReviewsData(Review[] reviewsData) {
        mReviewsData = reviewsData;
        notifyDataSetChanged();
    }
}