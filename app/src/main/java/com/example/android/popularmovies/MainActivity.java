package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.utilities.MoviesJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.SortType;

import org.parceler.Parcels;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private MovieAdapter mMovieAdapter;

    private SortType sortType;

    public static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        sortType = SortType.MOSTPOPULAR;

        setTitle(getText(R.string.most_popular));

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_TEXT_KEY)) {
                Parcelable listParcelable = savedInstanceState.getParcelable(LIFECYCLE_CALLBACKS_TEXT_KEY);
                ArrayList<Movie> allPreviousLifecycleCallbacks = Parcels.unwrap(listParcelable);
                mMovieAdapter.setMoviesData(allPreviousLifecycleCallbacks.toArray(new Movie[allPreviousLifecycleCallbacks.size()]));
            }
        }else{
            loadMoviesData();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sortType == SortType.FAVOURITES) {
            Movie[] moviesData = getFavouritedMovies();
            mMovieAdapter.setMoviesData(moviesData);
            showMoviesDataView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Movie[] lifecycleDisplayMoviesContents = mMovieAdapter.getMoviesData();
        Parcelable listParcelable = Parcels.wrap(new ArrayList<>(Arrays.asList(lifecycleDisplayMoviesContents)));

        outState.putParcelable(LIFECYCLE_CALLBACKS_TEXT_KEY, listParcelable);
    }

    private void loadMoviesData() {
        showMoviesDataView();
        new FetchMoviesTask().execute(sortType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            sortType = SortType.MOSTPOPULAR;
            setTitle(getText(R.string.most_popular));
            mMovieAdapter.setMoviesData(null);
            loadMoviesData();
            return true;
        }

        if (id == R.id.action_highest_rated) {
            sortType = SortType.TOPRATED;
            setTitle(getText(R.string.top_rated));
            mMovieAdapter.setMoviesData(null);
            loadMoviesData();
            return true;
        }

        if (id == R.id.action_favourites) {
            sortType = SortType.FAVOURITES;
            setTitle(getText(R.string.favourites));
            Movie[] moviesData = getFavouritedMovies();
            mMovieAdapter.setMoviesData(moviesData);
            showMoviesDataView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchMoviesTask extends AsyncTask<SortType, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showMoviesDataView();
                mMovieAdapter.setMoviesData(moviesData);
            } else {
                showErrorMessage();
            }
        }
    }

    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra("Movie", Parcels.wrap(movie));

        startActivity(intent);
    }

    private Movie[] getFavouritedMovies() {
        ArrayList<Movie> favouritedMovies;

        Cursor cursor = this.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        favouritedMovies = new ArrayList<>();

        while (cursor.moveToNext() && cursor != null) {
            String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
            String poster = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER));
            double ratings = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATINGS));
            String movie_id = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
            String release = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE));
            String synopsis = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS));

            Movie movie = new Movie(title, ratings, poster, release, synopsis, movie_id);

            favouritedMovies.add(movie);
        }

        Movie[] movies = favouritedMovies.toArray(new Movie[favouritedMovies.size()]);
        favouritedMovies.toArray(movies);

        cursor.close();

        return movies;
    }
}
