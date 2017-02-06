package com.manzo.popularmovies.data;

import android.content.Context;
import android.os.AsyncTask;

import com.manzo.popularmovies.R;
import com.manzo.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
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



    public static final int LIST_POPULARITY_INDEX = 0;
    public static final int LIST_RATING_INDEX = 1;
    public static final int LIST_IMAGE_INDEX = 2;
    public static final int LIST_SYNOPSIS_INDEX = 3;
    public static final int LIST_TITLE_INDEX = 4;
    public static final int LIST_ORIGINAL_TITLE_INDEX = 5;
    public static final int LIST_RELEASE_INDEX = 6;
    private static final int LIST_LENGHT = 7;

    public static final String BUILDER_IMAGE_BASEURL = "http://image.tmdb.org/t/p/";
    public static final String BUILDER_IMAGE_QUALITY_MEDIUM = "w185";

    private static final String DATEFORMAT_NUMERIC_MONTH = "MM";
    private static final String DATEFORMAT_MONTH_NAME = "MMMM";
    public static final String  BUILDER_SLASHTEN = "/10" ;


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


    public static List<String[]> jsonArrayToList(Context context, JSONArray jsonArray) {
        List<String[]> resultArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject movie = jsonArray.getJSONObject(i);
                String[] strings = new String[LIST_LENGHT];
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
                resultArrayList.add(strings);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultArrayList;
    }


    public static Movie newMovieFromArrayString(Context context, String[] movieData) {
        return new Movie(
                BUILDER_IMAGE_BASEURL + BUILDER_IMAGE_QUALITY_MEDIUM +
                        movieData[MovieDbUtilities.LIST_IMAGE_INDEX],
                movieData[MovieDbUtilities.LIST_TITLE_INDEX],
                formatStringDate(movieData[MovieDbUtilities.LIST_RELEASE_INDEX]),
                movieData[MovieDbUtilities.LIST_RATING_INDEX] + context.getString(R.string.slashten),
                context.getString(R.string.original_title_split) + context.getString(R.string.newline) + movieData[MovieDbUtilities.LIST_ORIGINAL_TITLE_INDEX],
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


