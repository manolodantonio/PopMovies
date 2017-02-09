package com.manzo.popularmovies.listComponents;

import android.support.v7.widget.RecyclerView;

import com.manzo.popularmovies.data.Trailer;
import com.manzo.popularmovies.databinding.ItemTrailerBinding;

public class TrailerViewHolder extends RecyclerView.ViewHolder
{

    public final ItemTrailerBinding binding;

    public TrailerViewHolder(ItemTrailerBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Trailer trailer) {
        binding.setTrailer(trailer);

//        executePendingBindings() runs the binding now instead of
//        waiting for next frame: avoids data/view sync errors
        binding.executePendingBindings();
    }

}
