package com.manzo.popularmovies;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.manzo.popularmovies.data.MovieDbUtilities;
import com.manzo.popularmovies.listComponents.MovieAdapter;
import com.manzo.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieItemClickListener,
        NetworkUtils.AsyncTaskCompletedListener {


    final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    final  MovieAdapter movieAdapter = new MovieAdapter(this);
    public RecyclerView rv_mainList;
    public ProgressBar clpb_empty;
    private TextView tv_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_mainList = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_mainList.setAdapter(movieAdapter);
        rv_mainList.setLayoutManager(gridLayoutManager);

        clpb_empty = (ProgressBar) findViewById(R.id.pb_empty);
        tv_error = (TextView) findViewById(R.id.tv_error);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getPopularMoviesIfNeeded();
        setActionBarTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent startOptions = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(startOptions);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemClick(int clickedItemIndex) {
        Intent detailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
        String[] movieData = movieAdapter.moviesList.get(clickedItemIndex);
        detailActivity.putExtra(getString(R.string.intent_key_moviedata), movieData);
        startActivity(detailActivity);
    }

    @Override
    public void onAsyncTaskCompleted(String result) {
        try {
            JSONObject resultObject = new JSONObject(result);
            JSONArray resultArray = resultObject.getJSONArray(getString(R.string.jskey_array_results));
            List<String[]> arrayListDB = MovieDbUtilities.jsonArrayToList(this, resultArray);
            movieAdapter.swapList(arrayListDB);
            switchLoadingStatus();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPopularMoviesIfNeeded() {
        if (movieAdapter.getItemCount() == 0) {

            switchLoadingStatus();

            if (NetworkUtils.isOnline(this)) {
                URL fetchURL = null;
                String sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.pref_sorting_key), getString(R.string.pref_key_popularity));
                try {
                    fetchURL = new URL(
                            getString(R.string.builder_baseurl) +
                            sortOrder +
                            getString(R.string.builder_apikey) + getString(R.string.movieDB_API_v3) +
                            getString(R.string.builder_language) + getString(R.string.builder_lang_enus));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new MovieDbUtilities.RequestToMovieDB(this).execute(fetchURL);
            }
        }
    }

    private void switchLoadingStatus() {
        if (movieAdapter != null && movieAdapter.getItemCount() != 0) {
            // List is populated
            rv_mainList.setVisibility(View.VISIBLE);

            clpb_empty.setVisibility(View.GONE);
            tv_error.setVisibility(View.GONE);
        } else {
            // List is empty
            if (NetworkUtils.isOnline(this)) {
                rv_mainList.setVisibility(View.GONE);

                clpb_empty.setVisibility(View.VISIBLE);
                tv_error.setVisibility(View.GONE);
            } else {
                rv_mainList.setVisibility(View.GONE);

                clpb_empty.setVisibility(View.GONE);
                tv_error.setVisibility(View.VISIBLE);
            }

        }
    }

    private void setActionBarTitle() {
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_sorting_key), getString(R.string.pref_key_popularity));
        if (sortOrder.equals(getString(R.string.pref_key_popularity))) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } else { getSupportActionBar().setTitle(
                getString(R.string.pref_label_toprated) + getString(R.string.singlewhitespace) + getString(R.string.movies)); }

    }

}
