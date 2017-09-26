package com.example.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;

import static android.R.attr.format;

public class MovieDetails extends AppCompatActivity {

    private static final String TAG = MovieDetails.class.getSimpleName();

    private Movie mSelectedMovie;

    private boolean mIsFavourited = false;
    private int mFavouriteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

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
        mVoteAverageTextView.setText(voteAverageText + "/10");

        ImageView mPosterImageView = (ImageView) findViewById(R.id.iv_details_poster);
        Picasso.with(this).load(mSelectedMovie.image_url).into(mPosterImageView);

        TextView mSynopsisTextView = (TextView) findViewById(R.id.tv_details_synopsis);
        mSynopsisTextView.setText(mSelectedMovie.plot_synopsis);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkIfMovieIsFavourited();
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

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
            if (name.equals(mSelectedMovie.title))
                return cursor;
        }

        return null;
    }


}
