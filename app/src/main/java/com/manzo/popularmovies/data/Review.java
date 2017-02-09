package com.manzo.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Manolo on 08/02/2017.
 */

public class Review implements Parcelable {


    private String author;
    private String content;
    private String url;


    public Review(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }


    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
