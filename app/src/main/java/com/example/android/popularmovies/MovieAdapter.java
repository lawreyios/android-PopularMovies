package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Movie[] mMoviesData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleView;
        private final TextView releaseDateView;
        private final TextView voteAverageView;
        private final ImageView posterImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);

            titleView = view.findViewById(R.id.grid_item_title);
            releaseDateView = view.findViewById(R.id.grid_item_release_date);
            voteAverageView = view.findViewById(R.id.grid_item_vote_average);
            posterImageView = view.findViewById(R.id.grid_item_image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesData[adapterPosition];
            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.grid_item_movie;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder viewHolder, int i) {
        Movie movie = mMoviesData[i];

        Picasso.with(viewHolder.itemView.getContext()).load(movie.image_url).into(viewHolder.posterImageView);

        viewHolder.titleView.setText(movie.title);
        String releaseDateText = viewHolder.itemView.getContext().getString(R.string.movie_release_date, movie.release_date);
        viewHolder.releaseDateView.setText(releaseDateText);

        String voteAverage = String.valueOf(movie.vote_average);
        String voteAverageText = viewHolder.itemView.getContext().getString(R.string.movie_average_vote, voteAverage);
        viewHolder.voteAverageView.setText(voteAverageText);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.length;
    }

    public void setMoviesData(Movie[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }

}
