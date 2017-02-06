package com.manzo.popularmovies.listComponents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manzo.popularmovies.R;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import java.util.List;

/**
 * Created by Manolo on 01/02/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    public List<String[]> moviesList;

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
        if (moviesList == null) {return;}
        try {
            populateItem(holder, moviesList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        if (moviesList != null) {
            return moviesList.size();
        } else return 0;
    }


    public List<String[]> swapList(List<String[]> newList) {

        // check if this JSONArray is the same as the previous JSONArray (jsonDB)
        if (moviesList == newList) {
            return moviesList; // bc nothing has changed
        }
        List<String[]> temp = moviesList;
        this.moviesList = newList; // new value assigned

        //check if this is a valid JSON, then update the JSON
        if (newList != null) {
            // refresh adapter shown data
            this.notifyDataSetChanged();
        }
        return temp; //previous db
    }

    private void populateItem(MovieViewHolder holder, String[] movieData) throws JSONException {

        //Poster
        String imageUrl = MovieDbUtilities.BUILDER_IMAGE_BASEURL +
                MovieDbUtilities.BUILDER_IMAGE_QUALITY_MEDIUM +
                movieData[MovieDbUtilities.LIST_IMAGE_INDEX];
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.iv_poster);
    }


}

