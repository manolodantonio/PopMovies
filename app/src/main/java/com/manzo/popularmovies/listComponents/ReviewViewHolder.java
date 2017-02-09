package com.manzo.popularmovies.listComponents;

import android.support.v7.widget.RecyclerView;

import com.manzo.popularmovies.data.Review;
import com.manzo.popularmovies.data.Trailer;
import com.manzo.popularmovies.databinding.ItemReviewBinding;
import com.manzo.popularmovies.databinding.ItemTrailerBinding;

public class ReviewViewHolder extends RecyclerView.ViewHolder
{

    public final ItemReviewBinding binding;

    public ReviewViewHolder(ItemReviewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Review review) {
        binding.setReview(review);

//        executePendingBindings() runs the binding now instead of
//        waiting for next frame: avoids data/view sync errors
        binding.executePendingBindings();
    }

}
