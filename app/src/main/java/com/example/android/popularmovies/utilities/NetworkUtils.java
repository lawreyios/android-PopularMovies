package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_MOVIESDB_URL = "http://api.themoviedb.org/3/movie";

    private final static String POPULAR_PATH = "popular";
    private final static String TOP_RATED_PATH = "top_rated";
    private final static String API_KEY_PARAM = "api_key";

    //TODO: Add your own API_KEY from MoviesDB
    private final static String API_KEY = "ad4ffb49ab5d292de1cd561c5ff9fb71";

    public static URL buildUrl(SortType sortType) {
        String path = sortType == SortType.MOSTPOPULAR ? POPULAR_PATH : TOP_RATED_PATH;
        Uri builtUri = Uri.parse(BASE_MOVIESDB_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
