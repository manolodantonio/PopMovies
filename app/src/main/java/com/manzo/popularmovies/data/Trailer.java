package com.manzo.popularmovies.data;

/**
 * Created by Manolo on 08/02/2017.
 */

public class Trailer {

    private String key;
    private String title;
    private String type;
    private String site;


    private String thumbnail;

    public Trailer(String key, String title, String type, String site, String thumbnail) {
        this.key = key;
        this.title = title;
        this.type = type;
        this.site = site;
        this.thumbnail = thumbnail;
    }



    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
