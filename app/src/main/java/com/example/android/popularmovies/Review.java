package com.example.android.popularmovies;

/**
 * Created by Lawrey on 26/9/17.
 */

public class Review {

    final String author;
    final String content;
    final String url;

    public Review(String _id, String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }
}
