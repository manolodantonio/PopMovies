package com.manzo.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Manolo on 06/02/2017.
 */

public class Movie implements Parcelable {


    private String id;

    @SerializedName(value="poster_path")
    private String imageLink;

    private String title;

    @SerializedName(value="release_date")
    private String releaseDate;

    @SerializedName(value="vote_average")
    private String rating;

    @SerializedName(value="original_title")
    private String originalTitle;

    @SerializedName(value="overview")
    private String synopsis;

    public Movie(
            String id,
            String imageLink,
            String title,
            String releaseDate,
            String rating,
            String originalTitle,
            String synopsis) {

        this.id = id;
        this.imageLink = imageLink;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.originalTitle = originalTitle;
        this.synopsis = synopsis;

    }


    public Movie(Parcel in) {
        this.id = in.readString();
        this.imageLink = in.readString();
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.rating = in.readString();
        this.originalTitle = in.readString();
        this.synopsis = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageLink);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(originalTitle);
        dest.writeString(synopsis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
