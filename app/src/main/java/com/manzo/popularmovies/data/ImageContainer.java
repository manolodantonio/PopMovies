package com.manzo.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Manolo on 23/02/2017.
 */

public class ImageContainer {
    @SerializedName(value="backdrops")
    public List<BackdropImage> backdropImages;

    public List<BackdropImage> getBackdropImages() {
        return backdropImages;
    }



    public class BackdropImage {
        String file_path;


        public BackdropImage(String file_path) {
            this.file_path = file_path;
        }

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

    }
}
