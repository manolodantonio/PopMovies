package com.manzo.popularmovies;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;



public class MovieDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Data binding sets content view and binds data to views in just few steps!
        ActivityMovieDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        Movie movieData = getIntent().getParcelableExtra(getString(R.string.intent_key_moviedata));
        binding.setMovie(movieData);

        Picasso.with(this)
                .load(movieData.getImageLink())
                .into(binding.ivPoster);

    }


}
