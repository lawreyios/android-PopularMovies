package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.utilities.MoviesJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.SortType;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private SortType sortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        sortType = SortType.MOSTPOPULAR;

        setTitle(getText(R.string.most_popular));

        loadMoviesData();
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
        Intent i = new Intent(this, MovieDetails.class);
        i.putExtra("Movie", movie);

        startActivity(i);
    }

    public Movie[] getFavouritedMovies() {
        ArrayList<Movie> favouritedMovies;

        Cursor cursor = this.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        favouritedMovies = new ArrayList<Movie>();

        while (cursor.moveToNext()) {
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

        return movies;
    }
}
