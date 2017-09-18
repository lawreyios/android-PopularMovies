package com.example.android.popularmovies;

/**
 * Created by Lawrey on 18/9/17.
 */

public class Movie {
    String title;
    double vote_average;
    String image_url;
    String release_date;
    String plot_synopsis;

    public Movie(String title, double vote_average, String image_url, String release_date, String plot_synopsis) {
        this.title = title;
        this.vote_average = vote_average;
        this.image_url = image_url;
        this.release_date = release_date;
        this.plot_synopsis = plot_synopsis;
    }
}
