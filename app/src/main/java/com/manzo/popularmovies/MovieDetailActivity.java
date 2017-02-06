package com.manzo.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MovieDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupInterface();
    }

    private void setupInterface() {
        Movie movieData = getIntent().getParcelableExtra(getString(R.string.intent_key_moviedata));

        //Poster
        final ImageView iv_poster = (ImageView) findViewById(R.id.iv_poster);
        Picasso.with(this)
                .load(movieData.getImageLink())
                .into(iv_poster);

        //Title
        final TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(movieData.getTitle());

        //Release date
        final TextView tv_release = (TextView) findViewById(R.id.tv_release);
        tv_release.setText(movieData.getReleaseDate());

        //Rating
        final TextView tv_rating = (TextView) findViewById(R.id.tv_rating);
        tv_rating.setText(movieData.getRating() + getString(R.string.slashten));

        //Original Title
        final TextView tv_original_title = (TextView) findViewById(R.id.tv_original_title);
        tv_original_title.setText(
                getString(R.string.original_title_split) + getString(R.string.newline) + movieData.getOriginalTitle());


        //Synopsis
        final TextView tv_synopsis = (TextView) findViewById(R.id.tv_synopsis);
        tv_synopsis.setText(movieData.getSynopsis());
    }



}
