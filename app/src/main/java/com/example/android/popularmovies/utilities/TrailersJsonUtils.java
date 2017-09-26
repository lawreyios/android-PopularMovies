package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lawrey on 26/9/17.
 */

public class TrailersJsonUtils {

    public static Trailer[] getTrailersFromJSON(String trailersJSONStr)
            throws JSONException {

        final String TRAILER_RESULTS = "results";

        /* Movies Info. Parameters Needed */
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_TYPE = "type";

        Trailer[] parsedTrailerData;

        JSONObject trailersJSON = new JSONObject(trailersJSONStr);

        JSONArray trailersArray = trailersJSON.getJSONArray(TRAILER_RESULTS);

        parsedTrailerData = new Trailer[trailersArray.length()];

        for (int i = 0; i < trailersArray.length(); i++) {
            /* These are the values that will be collected */
            String _id;
            String key;
            String name;
            String site;
            String type;

            JSONObject trailer = trailersArray.getJSONObject(i);

            _id = trailer.getString(TRAILER_ID);
            key = trailer.getString(TRAILER_KEY);
            name = trailer.getString(TRAILER_NAME);
            site = trailer.getString(TRAILER_SITE);
            type = trailer.getString(TRAILER_TYPE);

            parsedTrailerData[i] = new Trailer(_id, key, name, site, type);
        }

        return parsedTrailerData;
    }

}
