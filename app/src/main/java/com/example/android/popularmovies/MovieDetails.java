package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ReviewsJsonUtils;
import com.example.android.popularmovies.utilities.TrailersJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.util.Date;

public class MovieDetails extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = MovieDetails.class.getSimpleName();

    private Movie mSelectedMovie;

    private boolean mIsFavourited = false;
    private int mFavouriteIcon;

    private RecyclerView mTrailersRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    private RecyclerView mReviewsRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mScrollView = (ScrollView) findViewById(R.id.sv_movie_details);

        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);

        LinearLayoutManager trailersLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mTrailersRecyclerView.setLayoutManager(trailersLayoutManager);

        LinearLayoutManager reviewsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        mTrailerAdapter = new TrailerAdapter(this);
        mReviewAdapter = new ReviewAdapter(this);

        mTrailersRecyclerView.setAdapter(mTrailerAdapter);
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        mSelectedMovie = (Movie) getIntent().getSerializableExtra("Movie");
        setTitle(mSelectedMovie.title);

        TextView mReleaseDateTextView = (TextView) findViewById(R.id.tv_details_release_date);
        String releaseDateText = mSelectedMovie.release_date;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = null;

        try {
            releaseDate = format.parse(releaseDateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("dd MMM yyyy");

        if (releaseDate == null)
            releaseDateText = "Not available yet";
        else
            releaseDateText = format.format(releaseDate).toString();

        releaseDateText = this.getString(R.string.movie_release_date, releaseDateText);
        mReleaseDateTextView.setText(releaseDateText);

        TextView mVoteAverageTextView = (TextView) findViewById(R.id.tv_details_vote_average);
        String voteAverage = String.valueOf(mSelectedMovie.vote_average);
        String voteAverageText = this.getString(R.string.movie_average_vote, voteAverage);
        mVoteAverageTextView.setText(voteAverageText);

        ImageView mPosterImageView = (ImageView) findViewById(R.id.iv_details_poster);
        Picasso.with(this).load(mSelectedMovie.image_url).into(mPosterImageView);

        TextView mSynopsisTextView = (TextView) findViewById(R.id.tv_details_synopsis);
        mSynopsisTextView.setText(mSelectedMovie.plot_synopsis);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkIfMovieIsFavourited();

        loadMovieTrailers();
        loadMovieReviews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_details_menu, menu);
        MenuItem favouriteMenuItem = menu.findItem(R.id.action_favourite_movie);
        setMenuIcon(favouriteMenuItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favourite_movie) {
            onFavourite(item);
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onFavourite(MenuItem item) {

        if (!mIsFavourited) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mSelectedMovie.movie_id);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER, mSelectedMovie.image_url);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATINGS, mSelectedMovie.vote_average);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE, mSelectedMovie.release_date);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, mSelectedMovie.plot_synopsis);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mSelectedMovie.title);

            Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

            if (uri != null) {
                mIsFavourited = true;
                setMenuIcon(item);
            }

        }else{

            Cursor cursor = getMovieCursor();

            String stringId = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID));
            Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            getContentResolver().delete(uri, null, null);

            mIsFavourited = false;
            setMenuIcon(item);
        }
    }

    private void checkIfMovieIsFavourited() {
        try {

            if (getMovieCursor() != null)
                mIsFavourited = true;

        } catch (Exception e) {

            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();

        }
    }

    private void setMenuIcon(MenuItem item) {
        mFavouriteIcon = mIsFavourited ? R.drawable.ic_favourite_on : R.drawable.ic_favourite_off;
        item.setIcon(ContextCompat.getDrawable(this, mFavouriteIcon));
    }

    private Cursor getMovieCursor() {
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesContract.MoviesEntry.COLUMN_TITLE);

        while (cursor.moveToNext() && cursor != null) {
            String name = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
            if (name.equals(mSelectedMovie.title))
                return cursor;
        }

        return null;
    }

    private void loadMovieTrailers() {
        new FetchTrailersTask().execute(mSelectedMovie.movie_id);
    }

    private class FetchTrailersTask extends AsyncTask<String, Void, Trailer[]> {

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
            if (trailersData != null) {
                mTrailerAdapter.setmTrailersData(trailersData);
            }
        }
    }

    @Override
    public void onClick(Trailer trailer) {
        watchYoutubeVideo(trailer.key);
    }

    private void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void loadMovieReviews() {
        new FetchReviewsTask().execute(mSelectedMovie.movie_id);
    }

    private class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {

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
            if (reviewsData != null) {
                mReviewAdapter.setReviewsData(reviewsData);
            }
        }
    }

    @Override
    public void onClick(Review review) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.url));
        startActivity(webIntent);
    }
}
