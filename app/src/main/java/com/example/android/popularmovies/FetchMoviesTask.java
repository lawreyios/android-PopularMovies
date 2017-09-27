package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.view.View;

import com.example.android.popularmovies.utilities.MoviesJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.SortType;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by Lawrey on 27/9/17.
 */

class FetchMoviesTask extends AsyncTask<SortType, Void, Movie[]> {

    WeakReference<MainActivity> mWeakActivity;

    public FetchMoviesTask(MainActivity mainActivity) {
        this.mWeakActivity = new WeakReference<>(mainActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        MainActivity activity = mWeakActivity.get();
        activity.mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected Movie[] doInBackground(SortType... params) {

            /* Must provide a sort type */
        if (params.length == 0) {
            return null;
        }

        SortType sortType = params[0];
        URL moviesRequestUrl = NetworkUtils.buildUrl(sortType);

        try {
            String moviesResponseJSON = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

            return MoviesJsonUtils.getMoviesFromJSON(moviesResponseJSON);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Movie[] moviesData) {
        MainActivity activity = mWeakActivity.get();

        activity.mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (moviesData != null) {
            activity.showMoviesDataView();
            activity.mMovieAdapter.setMoviesData(moviesData);
        } else {
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        MainActivity activity = mWeakActivity.get();

        activity.mRecyclerView.setVisibility(View.INVISIBLE);
        activity.mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
