package com.manzo.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Manolo on 23/02/2017.
 */

public class VideoContainer {
    @SerializedName(value="results")
    public List<Trailer> videos;


    public List<Trailer> getVideos() {
        return videos;
    }
}
