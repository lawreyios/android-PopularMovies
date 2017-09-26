package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lawrey on 26/9/17.
 */

public class ReviewsJsonUtils {

    public static Review[] getReviewsFromJSON(String reviewsJSONStr)
            throws JSONException {

        final String REVIEW_RESULTS = "results";

        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        Review[] parsedReviewData;

        JSONObject reviewsJSON = new JSONObject(reviewsJSONStr);

        JSONArray reviewsArray = reviewsJSON.getJSONArray(REVIEW_RESULTS);

        parsedReviewData = new Review[reviewsArray.length()];

        for (int i = 0; i < reviewsArray.length(); i++) {
            /* These are the values that will be collected */
            String _id;
            String author;
            String content;
            String url;

            JSONObject review = reviewsArray.getJSONObject(i);

            _id = review.getString(REVIEW_ID);
            author = review.getString(REVIEW_AUTHOR);
            content = review.getString(REVIEW_CONTENT);
            url = review.getString(REVIEW_URL);

            parsedReviewData[i] = new Review(_id, author, content, url);
        }

        return parsedReviewData;
    }

}
