package com.example.android.popularmovies;

import android.os.AsyncTask;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TrailersJsonUtils;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by Lawrey on 27/9/17.
 */

public class FetchTrailersTask extends AsyncTask<String, Void, Trailer[]> {

    WeakReference<MovieDetails> mWeakActivity;

    public FetchTrailersTask(MovieDetails movieDetails) {
        this.mWeakActivity = new WeakReference<>(movieDetails);
    }

    @Override
    protected Trailer[] doInBackground(String... params) {

            /* Must provide a sort type */
        if (params.length == 0) {
            return null;
        }

        String movieId = params[0];
        URL trailersRequestUrl = NetworkUtils.buildVideoUrl(movieId);

        try {
            String trailersResponseJSON = NetworkUtils.getResponseFromHttpUrl(trailersRequestUrl);

            return TrailersJsonUtils.getTrailersFromJSON(trailersResponseJSON);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Trailer[] trailersData) {
        MovieDetails activity = mWeakActivity.get();

        if (trailersData != null) {
            activity.mTrailerAdapter.setTrailersData(trailersData);
        }

        activity.isFetchTrailersCallFinished = true;
        activity.updateView();
    }
}
