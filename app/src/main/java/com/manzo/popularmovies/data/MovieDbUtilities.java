package com.manzo.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

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

    public static class LoadFavourites extends AsyncTask<Void, Void, List<Movie>> {

        NetworkUtils.AsyncTaskCompletedListener asyncTaskCompletedListener;
        Context context;

        public LoadFavourites(NetworkUtils.AsyncTaskCompletedListener listener, Context context) {
            this.asyncTaskCompletedListener = listener;
            this.context = context;
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            Cursor cursor = context.getContentResolver().query(DbContract.UserFavourites.URI_CONTENT,
                    null, null, null, null);
            return cursorToMovieList(cursor);
        }


        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            asyncTaskCompletedListener.onAsyncTaskCompleted(movies);
        }
    }



    public static class RequestToMovieDB extends AsyncTask<URL, Void, List<Movie>> {

        NetworkUtils.AsyncTaskCompletedListener asyncTaskCompletedListener;
        Context context;

        public RequestToMovieDB(NetworkUtils.AsyncTaskCompletedListener listener, Context context) {
            this.asyncTaskCompletedListener = listener;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected List<Movie> doInBackground(URL... urls) {
            try {
                String jsonString = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                return MovieDbUtilities.jsonStringToMovieList(context, jsonString);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            super.onPostExecute(result);
            asyncTaskCompletedListener.onAsyncTaskCompleted(result);
        }

    }

    public static List<Movie> jsonStringToMovieList(Context context, String jsonString) throws JSONException {
        JSONObject resultObject = new JSONObject(jsonString);
        JSONArray jsonArray = resultObject.getJSONArray(context.getString(R.string.jskey_array_results));
        List<Movie> resultArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject movie = jsonArray.getJSONObject(i);
                String rating =
                        movie.getString(context.getString(R.string.jskey_vote_average)) +
                        context.getString(R.string.slashten);
                String imageLink =
                        context.getString(R.string.builder_image_baseurl) +
                        context.getString(R.string.builder_image_quality_medium) +
                        movie.getString(context.getString(R.string.jskey_image));
                String synopsis =
                        movie.getString(context.getString(R.string.jskey_synopsis));
                String title =
                        movie.getString(context.getString(R.string.jskey_title));
                String originalTitle =
                        context.getString(R.string.original_title_split) +
                        context.getString(R.string.newline) +
                        movie.getString(context.getString(R.string.jskey_original_title));
                String releaseDate =
                        formatStringDate(movie.getString(context.getString(R.string.jskey_release)));
                String tmbdId =
                        movie.getString(context.getString(R.string.jskey_id));
                resultArrayList.add(
                        new Movie(tmbdId, imageLink, title, releaseDate, rating, originalTitle, synopsis));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultArrayList;
    }

    public static List<Movie> cursorToMovieList(Cursor cursor) {
        List<Movie> resultArrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            resultArrayList.add(new Movie(
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_TMDB_ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_IMAGELINK)),
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_RELEASE_DATE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_RATING)),
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_ORIGINAL_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.UserFavourites.COLUMN_SYNOPSIS))
            ));
        }

        return resultArrayList;
    }

//    public static List<String[]> jsonStringToMovieList(Context context, String jsonString) throws JSONException {
//        JSONObject resultObject = new JSONObject(jsonString);
//        JSONArray jsonArray = resultObject.getJSONArray(context.getString(R.string.jskey_array_results));
//        List<String[]> resultArrayList = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//                JSONObject movie = jsonArray.getJSONObject(i);
//                String[] strings = new String[LIST_LENGTH];
//                strings[LIST_POPULARITY_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_popularity));
//                strings[LIST_RATING_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_vote_average));
//                strings[LIST_IMAGE_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_image));
//                strings[LIST_SYNOPSIS_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_synopsis));
//                strings[LIST_TITLE_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_title));
//                strings[LIST_ORIGINAL_TITLE_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_original_title));
//                strings[LIST_RELEASE_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_release));
//                strings[LIST_ID_INDEX] =
//                        movie.getString(context.getString(R.string.jskey_id));
//                resultArrayList.add(strings);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultArrayList;
//    }

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

    public static ContentValues movieToContentValues(Movie movieData) {
        ContentValues values = new ContentValues();
        values.put(DbContract.UserFavourites.COLUMN_TMDB_ID, movieData.getId());
        values.put(DbContract.UserFavourites.COLUMN_IMAGELINK, movieData.getImageLink());
        values.put(DbContract.UserFavourites.COLUMN_TITLE, movieData.getTitle());
        values.put(DbContract.UserFavourites.COLUMN_RELEASE_DATE, movieData.getReleaseDate());
        values.put(DbContract.UserFavourites.COLUMN_RATING, movieData.getRating());
        values.put(DbContract.UserFavourites.COLUMN_ORIGINAL_TITLE, movieData.getOriginalTitle());
        values.put(DbContract.UserFavourites.COLUMN_SYNOPSIS, movieData.getSynopsis());
        return values;
    }

}


