package com.example.android.popularmovies;

import android.os.AsyncTask;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ReviewsJsonUtils;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by Lawrey on 27/9/17.
 */

public class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {

    WeakReference<MovieDetails> mWeakActivity;

    public FetchReviewsTask(MovieDetails movieDetails) {
        this.mWeakActivity = new WeakReference<>(movieDetails);
    }

    @Override
    protected Review[] doInBackground(String... params) {

            /* Must provide a sort type */
        if (params.length == 0) {
            return null;
        }

        String movieId = params[0];
        URL reviewsRequestUrl = NetworkUtils.buildReviewUrl(movieId);

        try {
            String reviewsResponseJSON = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
            return ReviewsJsonUtils.getReviewsFromJSON(reviewsResponseJSON);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Review[] reviewsData) {
        MovieDetails activity = mWeakActivity.get();

        if (reviewsData != null) {
            activity.mReviewAdapter.setReviewsData(reviewsData);
        }

        activity.isFetchReviewsCallFinished = true;
        activity.updateView();
    }
}
