package com.manzo.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Manolo on 13/02/2017.
 */

public class DbContract {

    public static final String AUTHORITY = "com.manzo.popularmovies";
    public static final Uri URI_BASE = Uri.parse("content://" + AUTHORITY);


    public static final class UserFavourites implements BaseColumns {

        public static final String TABLE_USER_FAVOURITES = "favourites";
        public static final String COLUMN_TMDB_ID = "remote_id";
        public static final String COLUMN_IMAGELINK = "image_link";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_SYNOPSIS = "synopsis";


        public static final Uri URI_CONTENT =
                URI_BASE.buildUpon().appendPath(TABLE_USER_FAVOURITES).build();
        //todo add images uri path here
    }
}
