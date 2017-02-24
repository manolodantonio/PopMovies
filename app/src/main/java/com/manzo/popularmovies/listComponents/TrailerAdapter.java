package com.manzo.popularmovies.listComponents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.manzo.popularmovies.R;
import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.Trailer;
import com.manzo.popularmovies.databinding.ItemTrailerBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Manolo on 01/02/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerViewHolder> {

    private static final int THUMB_WIDTH = 480;
    private static final int THUMB_HEIGHT = 270;

    public interface TrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }


    private Context context;
    public static TrailerClickListener trailerClickListener;
    private List<Trailer> trailersList;

    public TrailerAdapter(TrailerClickListener listener) {
        trailerClickListener = listener;
    }



    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemTrailerBinding trailerBinding = ItemTrailerBinding.inflate(
                layoutInflater, parent, false);
        trailerBinding.setClickListener(trailerClickListener);
        return new TrailerViewHolder(trailerBinding);

    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer trailer = trailersList.get(position);
        holder.bind(trailer);

        String site = trailer.getSite();

        if (trailer.getSite().equals(context.getString(R.string.jsvalue_youtube))){
            String thumbnail = context.getString(R.string.builder_base_youtube_image) +
                            trailer.getKey() +
                            context.getString(R.string.builer_youimage_quality0);
            Picasso.with(context)
                    .load(thumbnail)
                    .resize(THUMB_WIDTH, THUMB_HEIGHT)
                    .centerCrop()
                    .into(holder.binding.ivTrailerSiteIcon);
        }

    }

    @Override
    public int getItemCount() {
        if (trailersList != null) {
            return trailersList.size();
        } else return 0;
    }


    public List<Trailer> swapList(List<Trailer> newList) {

        // check if this List is the same as the previous List (trailersList)
        if (trailersList == newList) {
            return trailersList; // bc nothing has changed
        }
        List<Trailer> temp = trailersList;
        this.trailersList = newList; // new value assigned

        //check if this is a valid List
        if (newList != null) {
            // refresh adapter shown data
            this.notifyDataSetChanged();
        }
        return temp; //previous db
    }
}

