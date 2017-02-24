package com.manzo.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Manolo on 23/02/2017.
 */

public class MovieContainer {
    @SerializedName(value="results")
    public List<Movie> movies;


    public List<Movie> getMovies() {
        return movies;
    }
}
