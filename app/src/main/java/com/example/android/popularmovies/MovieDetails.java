package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Movie movie = (Movie) getIntent().getSerializableExtra("Movie");

        setTitle(movie.title);

        TextView mReleaseDateTextView = (TextView) findViewById(R.id.tv_details_release_date);
        String releaseDateText = this.getString(R.string.movie_release_date, movie.release_date);
        mReleaseDateTextView.setText(releaseDateText);

        TextView mVoteAverageTextView = (TextView) findViewById(R.id.tv_details_vote_average);
        String voteAverage = String.valueOf(movie.vote_average);
        String voteAverageText = this.getString(R.string.movie_average_vote, voteAverage);
        mVoteAverageTextView.setText(voteAverageText);

        ImageView mPosterImageView = (ImageView) findViewById(R.id.iv_details_poster);
        Picasso.with(this).load(movie.image_url).into(mPosterImageView);

        TextView mSynopsisTextView = (TextView) findViewById(R.id.tv_details_synopsis);
        mSynopsisTextView.setText(movie.plot_synopsis);
    }
}
