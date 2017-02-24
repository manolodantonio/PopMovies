package com.manzo.popularmovies.listComponents;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manzo.popularmovies.MainActivity;
import com.manzo.popularmovies.R;
import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import java.util.List;

/**
 * Created by Manolo on 01/02/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {


    public List<Movie> moviesList;



    public interface MovieItemClickListener {
        void onMovieItemClick (int clickedItemIndex);
    }


    private Context context;
    public static MovieItemClickListener movieItemClickListener;
    private boolean isLandscape = false;

    public MovieAdapter(MovieItemClickListener listener) {movieItemClickListener = listener;}


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }

        boolean attachToRoot = false;
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_movie, parent, attachToRoot);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (moviesList == null) {return;}
        populateItem(holder, moviesList.get(position));
    }


    @Override
    public int getItemCount() {
        if (moviesList != null) {
            return moviesList.size();
        } else return 0;
    }


    public List<Movie> swapList(List<Movie> newList) {

        // check if this List is the same as the previous List (moviesList)
        if (moviesList == newList) {
            return moviesList; // bc nothing has changed
        }
        List<Movie> temp = moviesList;
        this.moviesList = newList; // new value assigned

        //check if this is a valid List
        if (newList != null) {
            // refresh adapter shown data
            this.notifyDataSetChanged();
        }
        return temp; //previous db
    }

    private void populateItem(MovieViewHolder holder, Movie movieData) {
        if (!MainActivity.isMasterDetail && isLandscape) {
            holder.iv_poster.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            holder.ll_item_container.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        Picasso.with(context)
                .load(movieData.getImageLink())
                .into(holder.iv_poster);
    }

    public List<Movie> getMoviesList() {
        return moviesList;
    }


}

