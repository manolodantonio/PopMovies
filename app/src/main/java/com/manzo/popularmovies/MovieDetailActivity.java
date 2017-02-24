package com.manzo.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.manzo.popularmovies.utilities.Utils;


public class MovieDetailActivity extends AppCompatActivity{
    private static final String FRAGMENT_ACTIVITY = "activity fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for existing fragment or create new
        MovieDetailFragment fragment;
        if (savedInstanceState != null) {
            fragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY);
        } else {
            fragment = new MovieDetailFragment();
        }

        if (Utils.isTablet(this) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            // Is a tablet in landscape orientation: close detailActivity and go to master/detail
            getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
            setResult(RESULT_OK,
                    new Intent().putExtra(
                            getString(R.string.intent_key_moviedata),
                            getIntent().getParcelableExtra(getString(R.string.intent_key_moviedata))
                    )
            );
            finish();
            }
        else {
            // set view and fragment content
            setContentView(R.layout.activity_fragment_detail);
            if (savedInstanceState == null) {
                // if new fragment, set data
                Bundle arguments = new Bundle();
                arguments.putParcelable(
                        getString(R.string.intent_key_moviedata),
                        getIntent().getParcelableExtra(getString(R.string.intent_key_moviedata)));
                fragment.setArguments(arguments);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_activity_detail_container, fragment, FRAGMENT_ACTIVITY)
                    .commit();
        }


    }




}
