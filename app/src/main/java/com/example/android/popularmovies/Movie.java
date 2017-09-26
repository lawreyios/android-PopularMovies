package com.example.android.popularmovies;

import java.io.Serializable;

@SuppressWarnings("DefaultFileTemplate")

public class Movie implements Serializable {
    final String title;
    final double vote_average;
    final String image_url;
    final String release_date;
    final String plot_synopsis;
    final String movie_id;

    public Movie(String title, double vote_average, String image_url, String release_date, String plot_synopsis, String movie_id) {
        this.title = title;
        this.vote_average = vote_average;
        this.image_url = image_url;
        this.release_date = release_date;
        this.plot_synopsis = plot_synopsis;
        this.movie_id = movie_id;
    }
}
