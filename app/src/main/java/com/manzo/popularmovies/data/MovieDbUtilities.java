package com.manzo.popularmovies.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;

import com.manzo.popularmovies.R;
import com.manzo.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public static void sortArrayDbByColumn(List<String[]> arrayListDB, final int columnIndex) {
        Collections.sort(arrayListDB,new Comparator<String[]>() {
            public int compare(String[] strings, String[] otherStrings) {
                return strings[columnIndex].compareTo(otherStrings[columnIndex]);
            }
        });
        Collections.reverse(arrayListDB);
    }

    public static int getSortingPreferenceColumnIndex(Context context) {
        String orderPreference = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_sorting_key), context.getString(R.string.pref_key_popularity));
        switch (orderPreference) {
            case "most_popular": return LIST_POPULARITY_INDEX;
            case "top_rated": return LIST_RATING_INDEX;
            default: return 0;
        }
    }

}


