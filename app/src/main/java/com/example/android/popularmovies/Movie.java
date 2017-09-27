package com.example.android.popularmovies;

@org.parceler.Parcel
public class Movie {

    // fields must be package private
    String title;
    double vote_average;
    String image_url;
    String release_date;
    String plot_synopsis;
    String movie_id;

    // empty constructor needed by the Parceler library
    public Movie() {
    }

    public Movie(String title, double vote_average, String image_url, String release_date, String plot_synopsis, String movie_id) {
        this.title = title;
        this.vote_average = vote_average;
        this.image_url = image_url;
        this.release_date = release_date;
        this.plot_synopsis = plot_synopsis;
        this.movie_id = movie_id;
    }
}
