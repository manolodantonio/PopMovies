package com.manzo.popularmovies.listComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manzo.popularmovies.R;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView iv_poster;

    public MovieViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);


        iv_poster = (ImageView) itemView.findViewById(R.id.iv_poster);
    }

    @Override
    public void onClick(View view) {
        int clickedPosition = getAdapterPosition();
        MovieAdapter.movieItemClickListener.onMovieItemClick(clickedPosition);
    }
}
