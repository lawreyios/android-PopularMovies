package com.example.android.popularmovies.utilities;

/**
 * Created by Lawrey on 18/9/17.
 */

import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle MoviesDB JSON data.
 */

public class MoviesJsonUtils {

    public static Movie[] getMoviesFromJSON(Context context, String moviesJSONStr)
        throws JSONException {

        /* Movies List. Each movie's info is an element of the "results" array */
        final String MDB_RESULTS = "results";

        /* Movies Info. Parameters Needed */
        final String MDB_VOTE_AVERAGE = "vote_average";
        final String MDB_TITLE = "title";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";

        final String MDB_STATUS_CODE = "status_code";

        final String MDB_BASE_IMAGE_URL = "http://image.tmdb.org/t/p";
        final String MDB_IMAGE_WIDTH_PATH = "/w185";

        /* Movie array to hold each movie's object */
        Movie[] parsedMovieData = null;

        JSONObject moviesJSON = new JSONObject(moviesJSONStr);

                /* Is there an error? */
        if (moviesJSON.has(MDB_STATUS_CODE)) {
            int errorCode = moviesJSON.getInt(MDB_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = moviesJSON.getJSONArray(MDB_RESULTS);

        parsedMovieData = new Movie[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            /* These are the values that will be collected */
            double voteAverage;
            String title;
            String posterPath;
            String overview;
            String releaseDate;

            /* Get the JSON object representing the movie */
            JSONObject movie = moviesArray.getJSONObject(i);

            voteAverage = movie.getDouble(MDB_VOTE_AVERAGE);
            title = movie.getString(MDB_TITLE);
            posterPath = movie.getString(MDB_POSTER_PATH);
            overview = movie.getString(MDB_OVERVIEW);
            releaseDate = movie.getString(MDB_RELEASE_DATE);

            String imageUrl = MDB_BASE_IMAGE_URL + MDB_IMAGE_WIDTH_PATH + posterPath;

            parsedMovieData[i] = new Movie(title, voteAverage, imageUrl, releaseDate, overview);
        }

        return parsedMovieData;
    }

}
