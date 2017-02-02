package com.manzo.popularmovies;

import android.net.ParseException;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.manzo.popularmovies.data.MovieDbUtilities;
import com.manzo.popularmovies.listComponents.MovieAdapter;
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
        String[] movieData = getIntent().getStringArrayExtra(getString(R.string.intent_key_moviedata));

        //Poster
        final ImageView iv_poster = (ImageView) findViewById(R.id.iv_poster);
        String imageUrl = MovieAdapter.IMAGE_BASE_URL +
                MovieAdapter.IMAGE_MEDIUM_QUALITY +
                movieData[MovieDbUtilities.LIST_IMAGE_INDEX];
        Picasso.with(this)
                .load(imageUrl)
                .into(iv_poster);

        //Title
        final TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(movieData[MovieDbUtilities.LIST_TITLE_INDEX]);

        //Release date
        final TextView tv_release = (TextView) findViewById(R.id.tv_release);
        tv_release.setText(
                formatStringDate(movieData[MovieDbUtilities.LIST_RELEASE_INDEX])
        );

        //Rating
        final TextView tv_rating = (TextView) findViewById(R.id.tv_rating);
        tv_rating.setText(movieData[MovieDbUtilities.LIST_RATING_INDEX] + getString(R.string.slashten));

        //Original Title
        final TextView tv_original_title = (TextView) findViewById(R.id.tv_original_title);
        tv_original_title.setText(
                getString(R.string.original_title_split) + getString(R.string.newline) +
                movieData[MovieDbUtilities.LIST_ORIGINAL_TITLE_INDEX]);


        //Synopsis
        final TextView tv_synopsis = (TextView) findViewById(R.id.tv_synopsis);
        tv_synopsis.setText(movieData[MovieDbUtilities.LIST_SYNOPSIS_INDEX]);
    }

    private String formatStringDate(String stringDate) {
        String[] split = stringDate.split("-");
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateformat_numeric_month));
        try {
            Date convertedDate = dateFormat.parse(split[1]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(convertedDate);
            String monthName = new SimpleDateFormat(getString(R.string.dateformat_month_name), Locale.US).format(cal.getTime());
            return monthName + " " + split[0];
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }

    }


}
