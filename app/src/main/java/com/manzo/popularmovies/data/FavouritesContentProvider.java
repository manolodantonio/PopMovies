package com.manzo.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.manzo.popularmovies.R;

/**
 * Created by Manolo on 13/02/2017.
 */

public class FavouritesContentProvider extends ContentProvider {

    // Constants as index for matching the directory or single line
    public static final int FAVOURITES = 100;
    public static final int FAV_ROW = 101;

    private FavouritesDbHelper favouritesDbHelper;

    public static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DbContract.AUTHORITY,
                DbContract.UserFavourites.TABLE_USER_FAVOURITES,
                FAVOURITES );
        uriMatcher.addURI(DbContract.AUTHORITY,
                DbContract.UserFavourites.TABLE_USER_FAVOURITES + "/#",
                FAV_ROW);


        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        favouritesDbHelper = new FavouritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        final SQLiteDatabase database = favouritesDbHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);

        switch (match) {
            case FAVOURITES:
                returnCursor = database.query(
                        DbContract.UserFavourites.TABLE_USER_FAVOURITES,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase database = favouritesDbHelper.getWritableDatabase();
        Uri returnUri;

        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVOURITES:
                long id = database.insert(
                        DbContract.UserFavourites.TABLE_USER_FAVOURITES,
                        null,
                        contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(DbContract.UserFavourites.URI_CONTENT, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                } else throw new UnsupportedOperationException(getContext().getString(R.string.error_unknown_uri) + uri);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.error_unknown_uri) + uri);
        }

        // Notify the resolver if the uri has been changed
        // Return inserted uri
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = favouritesDbHelper.getWritableDatabase();
        int deletedFavs;

        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVOURITES:
                int deletedRows = database.delete(
                        DbContract.UserFavourites.TABLE_USER_FAVOURITES,
                        selection, selectionArgs);
                if (deletedRows > 0) {
                    deletedFavs = deletedRows;
                    getContext().getContentResolver().notifyChange(uri, null);
                } else throw new Resources.NotFoundException("Object not in database");
                break;
            default: throw new UnsupportedOperationException(getContext().getString(R.string.error_unknown_uri) + uri);
        }

        return deletedFavs;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
