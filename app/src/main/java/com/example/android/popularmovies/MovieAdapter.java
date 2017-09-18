package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import static android.R.attr.resource;

/**
 * Created by Lawrey on 18/9/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Movie[] mMoviesData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mMovieTitleTextView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieTitleTextView = (TextView) view.findViewById(R.id.grid_item_title);
            // COMPLETED (7) Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // COMPLETED (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie= mMoviesData[adapterPosition];
            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, int i) {
        Movie movie = mMoviesData[i];

        Picasso.with(viewHolder.itemView.getContext()).load(movie.image_url).into(viewHolder.posterImageView);

        viewHolder.titleView.setText(movie.title);
        viewHolder.releaseDateView.setText("Release Date: " + movie.release_date);

        String voteAverage = String.valueOf(movie.vote_average);
        viewHolder.voteAverageView.setText("Average Vote: " + voteAverage);

        viewHolder.synopsisView.setText(movie.plot_synopsis);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleView;
        private TextView releaseDateView;
        private TextView voteAverageView;
        private ImageView posterImageView;
        private TextView synopsisView;

        public ViewHolder(View view) {
            super(view);

            titleView = (TextView)view.findViewById(R.id.grid_item_title);
            releaseDateView = (TextView)view.findViewById(R.id.grid_item_release_date);
            voteAverageView = (TextView)view.findViewById(R.id.grid_item_vote_average);
            posterImageView = (ImageView) view.findViewById(R.id.grid_item_image);
            synopsisView = (TextView)view.findViewById(R.id.grid_item_plot_synopsis);
        }
    }

    public void setMoviesData(Movie[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
