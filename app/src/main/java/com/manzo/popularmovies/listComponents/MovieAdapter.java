package com.manzo.popularmovies.listComponents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manzo.popularmovies.R;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;

/**
 * Created by Manolo on 01/02/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_MEDIUM_QUALITY = "w185";
    public List<String[]> listDB;

    public interface MovieItemClickListener {
        void onMovieItemClick (int clickedItemIndex);
    }


    private Context context;
    public static MovieItemClickListener movieItemClickListener;

    public MovieAdapter(MovieItemClickListener listener) {
        movieItemClickListener = listener;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        boolean attachToRoot = false;
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_movie, parent, attachToRoot);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (listDB == null) {return;}
        try {
            populateItem(holder, listDB.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        if (listDB != null) {
            return listDB.size();
        } else return 0;
    }


    public List<String[]> swapList(List<String[]> arrayListDB) {

        // check if this JSONArray is the same as the previous JSONArray (jsonDB)
        if (listDB == arrayListDB) {
            return listDB; // bc nothing has changed
        }
        List<String[]> temp = listDB;
        this.listDB = arrayListDB; // new value assigned

        //check if this is a valid JSON, then update the JSON
        if (arrayListDB != null) {
            // refresh adapter shown data
            this.notifyDataSetChanged();
        }
        return temp; //previous db
    }

    private void populateItem(MovieViewHolder holder, String[] movieData) throws JSONException {

        //Poster
        String imageUrl = IMAGE_BASE_URL +
                IMAGE_MEDIUM_QUALITY +
                movieData[MovieDbUtilities.LIST_IMAGE_INDEX];
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.iv_poster);
    }


}

