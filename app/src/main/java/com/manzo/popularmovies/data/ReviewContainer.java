package com.manzo.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Manolo on 08/02/2017.
 */

public class ReviewContainer {

    @SerializedName(value="results")
    public List<Review> reviews;


    public List<Review> getReviews() {
        return reviews;
    }

}


