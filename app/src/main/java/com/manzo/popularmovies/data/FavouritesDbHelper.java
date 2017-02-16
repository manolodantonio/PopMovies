package com.manzo.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavouritesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_FILENAME = "fav.db";
    public static final int DATABASE_VERSION = 9;

    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " +
                DbContract.UserFavourites.TABLE_USER_FAVOURITES + " (" +
                DbContract.UserFavourites._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.UserFavourites.COLUMN_TMDB_ID + " TEXT," +
                DbContract.UserFavourites.COLUMN_IMAGELINK + " TEXT," +
                DbContract.UserFavourites.COLUMN_TITLE + " TEXT," +
                DbContract.UserFavourites.COLUMN_RELEASE_DATE + " TEXT," +
                DbContract.UserFavourites.COLUMN_RATING + " TEXT," +
                DbContract.UserFavourites.COLUMN_ORIGINAL_TITLE + " TEXT," +
                DbContract.UserFavourites.COLUMN_SYNOPSIS + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.UserFavourites.TABLE_USER_FAVOURITES);
        onCreate(sqLiteDatabase);
    }
}
