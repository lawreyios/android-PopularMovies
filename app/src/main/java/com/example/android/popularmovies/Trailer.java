package com.example.android.popularmovies;

/**
 * Created by Lawrey on 26/9/17.
 */

public class Trailer {

    final String _id;
    final String key;
    final String name;
    final String site;
    final String type;

    public Trailer(String _id, String key, String name, String site, String type) {
        this._id = _id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }
}
