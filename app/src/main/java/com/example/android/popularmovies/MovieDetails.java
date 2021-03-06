package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.data.MoviesContract;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = MovieDetails.class.getSimpleName();

    Movie mSelectedMovie;

    private boolean mIsFavourited = false;
    private int mFavouriteIcon;

    @BindView(R.id.sv_movie_details)
    ScrollView mScrollView;
    @BindView(R.id.rv_trailers) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.rv_reviews) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.tv_details_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_details_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.iv_details_poster) ImageView mPosterImageView;
    @BindView(R.id.tv_details_synopsis) TextView mSynopsisTextView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;

    boolean isFetchReviewsCallFinished = false;
    boolean isFetchTrailersCallFinished = false;

    public static final String LIFECYCLE_CALLBACKS_TRAILERS_TEXT_KEY = "callbacksForTrailers";
    public static final String LIFECYCLE_CALLBACKS_REVIEWS_TEXT_KEY = "callbacksForReviews";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

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

        mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra("Movie"));
        setTitle(mSelectedMovie.title);

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

        String voteAverage = String.valueOf(mSelectedMovie.vote_average);
        String voteAverageText = this.getString(R.string.movie_average_vote, voteAverage);
        mVoteAverageTextView.setText(voteAverageText);

        Glide.with(this).load(mSelectedMovie.image_url).into(mPosterImageView);

        mSynopsisTextView.setText(mSelectedMovie.plot_synopsis);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkIfMovieIsFavourited();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_TRAILERS_TEXT_KEY)) {
                Parcelable listParcelable = savedInstanceState.getParcelable(LIFECYCLE_CALLBACKS_TRAILERS_TEXT_KEY);
                ArrayList<Trailer> previousData = Parcels.unwrap(listParcelable);
                mTrailerAdapter.setTrailersData(previousData.toArray(new Trailer[previousData.size()]));
                isFetchTrailersCallFinished = true;
            }else{
                loadMovieTrailers();
            }

            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_REVIEWS_TEXT_KEY)) {
                Parcelable listParcelable = savedInstanceState.getParcelable(LIFECYCLE_CALLBACKS_REVIEWS_TEXT_KEY);
                ArrayList<Review> previousData = Parcels.unwrap(listParcelable);
                mReviewAdapter.setReviewsData(previousData.toArray(new Review[previousData.size()]));
                isFetchReviewsCallFinished = true;
            }else{
                loadMovieReviews();
            }

            updateView();
        }else{
            loadMovieTrailers();
            loadMovieReviews();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Trailer[] lifecycleDisplayTrailersContents = mTrailerAdapter.getTrailersData();
        Parcelable trailersListParcelable = Parcels.wrap(new ArrayList<>(Arrays.asList(lifecycleDisplayTrailersContents)));

        outState.putParcelable(LIFECYCLE_CALLBACKS_TRAILERS_TEXT_KEY, trailersListParcelable);

        Review[] lifecycleDisplayReviewsContents = mReviewAdapter.getReviewsData();
        Parcelable reviewsListParcelable = Parcels.wrap(new ArrayList<>(Arrays.asList(lifecycleDisplayReviewsContents)));

        outState.putParcelable(LIFECYCLE_CALLBACKS_REVIEWS_TEXT_KEY, reviewsListParcelable);

        Log.v("STATE SAVED!!", "STATE SAVED!!");
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
        isFetchTrailersCallFinished = false;
        new FetchTrailersTask(this).execute(mSelectedMovie.movie_id);
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
        isFetchReviewsCallFinished = false;
        new FetchReviewsTask(this).execute(mSelectedMovie.movie_id);
    }

    @Override
    public void onClick(Review review) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.url));
        startActivity(webIntent);
    }

    public void updateView() {
        if (isFetchReviewsCallFinished && isFetchTrailersCallFinished) {
            mScrollView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }else{
            mScrollView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }
}
