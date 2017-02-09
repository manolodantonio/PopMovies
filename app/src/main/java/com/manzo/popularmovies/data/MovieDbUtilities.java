package com.manzo.popularmovies.data;

import android.content.Context;
import android.os.AsyncTask;

import com.manzo.popularmovies.MovieDetailActivity;
import com.manzo.popularmovies.R;
import com.manzo.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Manolo on 01/02/2017.
 */

public class MovieDbUtilities {

    private static final String DATEFORMAT_NUMERIC_MONTH = "MM";
    private static final String DATEFORMAT_MONTH_NAME = "MMMM";


    private static final int LIST_POPULARITY_INDEX = 0;
    private static final int LIST_RATING_INDEX = 1;
    public static final int LIST_IMAGE_INDEX = 2;
    private static final int LIST_SYNOPSIS_INDEX = 3;
    private static final int LIST_TITLE_INDEX = 4;
    private static final int LIST_ORIGINAL_TITLE_INDEX = 5;
    private static final int LIST_RELEASE_INDEX = 6;
    private static final int LIST_ID_INDEX = 7;
    private static final int LIST_LENGTH = 8;


    private static final int LIST_VIDEO_KEY = 0;
    private static final int LIST_VIDEO_NAME = 1;
    private static final int LIST_VIDEO_TYPE = 2;
    private static final int LIST_VIDEO_SITE = 3;
    private static final int LIST_VIDEO_LENGTH = 4;



    public static class RequestToMovieDB extends AsyncTask<URL, Void, String> {

        NetworkUtils.AsyncTaskCompletedListener asyncTaskCompletedListener;

        public RequestToMovieDB(NetworkUtils.AsyncTaskCompletedListener listener) {
            this.asyncTaskCompletedListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(URL... urls) {
            try {
                return NetworkUtils.getResponseFromHttpUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            asyncTaskCompletedListener.onAsyncTaskCompleted(result);
        }

    }


    public static List<String[]> jsonStringToMovieList(Context context, String jsonString) throws JSONException {
        JSONObject resultObject = new JSONObject(jsonString);
        JSONArray jsonArray = resultObject.getJSONArray(context.getString(R.string.jskey_array_results));
        List<String[]> resultArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject movie = jsonArray.getJSONObject(i);
                String[] strings = new String[LIST_LENGTH];
                strings[LIST_POPULARITY_INDEX] =
                        movie.getString(context.getString(R.string.jskey_popularity));
                strings[LIST_RATING_INDEX] =
                        movie.getString(context.getString(R.string.jskey_vote_average));
                strings[LIST_IMAGE_INDEX] =
                        movie.getString(context.getString(R.string.jskey_image));
                strings[LIST_SYNOPSIS_INDEX] =
                        movie.getString(context.getString(R.string.jskey_synopsis));
                strings[LIST_TITLE_INDEX] =
                        movie.getString(context.getString(R.string.jskey_title));
                strings[LIST_ORIGINAL_TITLE_INDEX] =
                        movie.getString(context.getString(R.string.jskey_original_title));
                strings[LIST_RELEASE_INDEX] =
                        movie.getString(context.getString(R.string.jskey_release));
                strings[LIST_ID_INDEX] =
                        movie.getString(context.getString(R.string.jskey_id));
                resultArrayList.add(strings);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultArrayList;
    }

//    public static List<String[]> jsonStringToVideoList(Context context, String jsonString) throws JSONException {
//        JSONObject resultObject = new JSONObject(jsonString);
//        JSONArray jsonArray = resultObject.getJSONArray(context.getString(R.string.jskey_array_results));
//        List<String[]> resultArrayList = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//                JSONObject movie = jsonArray.getJSONObject(i);
//                String[] strings = new String[LIST_VIDEO_LENGTH];
//                strings[LIST_VIDEO_KEY] =
//                        movie.getString(context.getString(R.string.jskey_videokey));
//                strings[LIST_VIDEO_NAME] =
//                        movie.getString(context.getString(R.string.jskey_videoname));
//                strings[LIST_VIDEO_TYPE] =
//                        movie.getString(context.getString(R.string.jskey_videotype));
//                strings[LIST_VIDEO_SITE] =
//                        movie.getString(context.getString(R.string.jskey_videotype));
//                resultArrayList.add(strings);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultArrayList;
//    }

    public static List<Trailer> jsonStringToTrailersList(Context context, String jsonString) throws JSONException {
        JSONObject resultObject = new JSONObject(jsonString);
        JSONArray jsonArray = resultObject.getJSONArray(context.getString(R.string.jskey_array_results));
        List<Trailer> resultArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject movie = jsonArray.getJSONObject(i);

                String key =
                        movie.getString(context.getString(R.string.jskey_videokey));
                String title =
                        movie.getString(context.getString(R.string.jskey_videoname));
                String type =
                        movie.getString(context.getString(R.string.jskey_videotype));
                String site =
                        movie.getString(context.getString(R.string.jskey_videosite));
                Trailer trailer = new Trailer(key, title, type, site);
                resultArrayList.add(trailer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultArrayList;
    }

    public static List<Review> jsonStringToReviewsList(Context context, String jsonString) throws JSONException {
        JSONObject resultObject = new JSONObject(jsonString);
        JSONArray jsonArray = resultObject.getJSONArray(context.getString(R.string.jskey_array_results));
        List<Review> resultArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject movie = jsonArray.getJSONObject(i);

                String author =
                        movie.getString(context.getString(R.string.jskey_author));
                String content =
                        movie.getString(context.getString(R.string.jskey_content));
                String url =
                        movie.getString(context.getString(R.string.jskey_url));
                Review review = new Review(author, content, url);
                resultArrayList.add(review);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultArrayList;
    }

    public static Movie newMovieFromArrayString(Context context, String[] movieData) {
        return new Movie(
                // ID
                movieData[MovieDbUtilities.LIST_ID_INDEX],
                // ImageLink
                context.getString(R.string.builder_image_baseurl) +
                        context.getString(R.string.builder_image_quality_medium) +
                        movieData[MovieDbUtilities.LIST_IMAGE_INDEX],
                // Title
                movieData[MovieDbUtilities.LIST_TITLE_INDEX],
                // ReleaseDate
                formatStringDate(movieData[MovieDbUtilities.LIST_RELEASE_INDEX]),
                // Rating
                movieData[MovieDbUtilities.LIST_RATING_INDEX] +
                        context.getString(R.string.slashten),
                // OriginalTitle
                context.getString(R.string.original_title_split) +
                        context.getString(R.string.newline) +
                        movieData[MovieDbUtilities.LIST_ORIGINAL_TITLE_INDEX],
                // Synopsis
                movieData[MovieDbUtilities.LIST_SYNOPSIS_INDEX]
        );
    }

    private static String formatStringDate(String stringDate) {
        String[] split = stringDate.split("-");
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_NUMERIC_MONTH);
        try {
            Date convertedDate = dateFormat.parse(split[1]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(convertedDate);
            String monthName = new SimpleDateFormat(DATEFORMAT_MONTH_NAME, Locale.US).format(cal.getTime());
            return monthName + " " + split[0];
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

}


