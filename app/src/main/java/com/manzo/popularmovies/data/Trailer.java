package com.manzo.popularmovies.data;

/**
 * Created by Manolo on 08/02/2017.
 */

public class Trailer {

    private String key;
    private String name;
    private String type;
    private String site;


    public Trailer(String key, String name, String type, String site) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.site = site;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
