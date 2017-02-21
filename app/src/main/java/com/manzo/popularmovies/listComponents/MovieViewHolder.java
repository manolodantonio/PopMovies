package com.manzo.popularmovies.listComponents;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.manzo.popularmovies.R;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView iv_poster;
    LinearLayout ll_item_container;

    public MovieViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);


        iv_poster = (ImageView) itemView.findViewById(R.id.iv_item_poster);
        ll_item_container = (LinearLayout) itemView.findViewById(R.id.ll_movie_container);

    }

    @Override
    public void onClick(View view) {
        int clickedPosition = getAdapterPosition();
        MovieAdapter.movieItemClickListener.onMovieItemClick(clickedPosition);
    }
}
