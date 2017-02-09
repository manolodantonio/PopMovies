package com.manzo.popularmovies.listComponents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.manzo.popularmovies.R;
import com.manzo.popularmovies.data.Review;
import com.manzo.popularmovies.data.Trailer;
import com.manzo.popularmovies.databinding.ItemReviewBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Manolo on 01/02/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    public interface ReviewClickListener {
        void onReviewClick(Review review);
    }


    private Context context;
    public static ReviewClickListener reviewClickListener;
    private List<Review> reviewList;

    public ReviewAdapter(ReviewClickListener listener) {
        reviewClickListener = listener;
    }



    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemReviewBinding reviewBinding = ItemReviewBinding.inflate(
                layoutInflater, parent, false);
        reviewBinding.setClickListener(reviewClickListener);
        return new ReviewViewHolder(reviewBinding);

    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);

        String url = review.getUrl();

        if (url.contains(context.getString(R.string.key_moviedb))){
            Picasso.with(context).load(R.mipmap.ic_tmdb).into(holder.binding.ivReviewSiteIcon);
        }

    }

    @Override
    public int getItemCount() {
        if (reviewList != null) {
            return reviewList.size();
        } else return 0;
    }


    public List<Review> swapList(List<Review> newList) {

        // check if this List is the same as the previous List (trailersList)
        if (reviewList == newList) {
            return reviewList; // bc nothing has changed
        }
        List<Review> temp = reviewList;
        this.reviewList = newList; // new value assigned

        //check if this is a valid List
        if (newList != null) {
            // refresh adapter shown data
            this.notifyDataSetChanged();
        }
        return temp; //previous db
    }
}

