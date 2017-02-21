package com.manzo.popularmovies;


import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.manzo.popularmovies.listComponents.MovieAdapter;
import com.manzo.popularmovies.utilities.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieItemClickListener,
        NetworkUtils.AsyncTaskCompletedListener,
        NetworkUtils.SubMenuItemClickedListener {


    public GridLayoutManager gridLayoutManager;
    final MovieAdapter movieAdapter = new MovieAdapter(this);
    public RecyclerView rv_mainList;
    public ProgressBar clpb_empty;
    private TextView tv_error;


    private String currentSortOrder;
    private int page = 1;
    private boolean isLoadingMoreMovies = false;
    public static boolean isMasterDetail = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////////// LAYOUT SETUP
        isMasterDetail = (findViewById(R.id.fl_movie_detail_container) != null);

        int glSpanCount = 2; //todo constants?
        int glOrientation = GridLayoutManager.VERTICAL;
        if (!isMasterDetail &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // is a small screen in landscape orientation
            glSpanCount = 1;
            glOrientation = GridLayoutManager.HORIZONTAL;
//            findViewById(R.id.iv_item_poster).setLayoutParams(
//                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            findViewById(R.id.ll_movie_container).setLayoutParams(
//                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        gridLayoutManager = new GridLayoutManager(this, glSpanCount, glOrientation, false);
        ////////////

        currentSortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_sorting_key),
                        getString(R.string.pref_key_popularity));


        rv_mainList = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_mainList.setAdapter(movieAdapter);
        rv_mainList.setLayoutManager(gridLayoutManager);

        clpb_empty = (ProgressBar) findViewById(R.id.pb_empty);
        tv_error = (TextView) findViewById(R.id.tv_error);


    }



    @Override
    protected void onResume() {
        super.onResume();
        getMoviesIfNeeded();
        setActionBarTitle();
        if (!currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
            rv_mainList.addOnScrollListener(newScrollMoviesListener());
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
//            case R.id.action_settings:
//                Intent startOptions = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(startOptions);
//                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSubMenuItemClicked(MenuItem item) {
        String selectedSortOrder = null;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_menu_most_popular:
                selectedSortOrder = getString(R.string.pref_key_popularity);
                break;
            case R.id.action_menu_top_rated:
                selectedSortOrder = getString(R.string.pref_key_toprated);
                break;
            case R.id.action_menu_favourites:
                selectedSortOrder = getString(R.string.pref_key_favourites);
                break;
            default:
                selectedSortOrder = getString(R.string.pref_key_popularity);
                break;
        }

        if (selectedSortOrder != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(
                    getString(R.string.pref_sorting_key), selectedSortOrder)
                    .commit();
            onResume();
        }

    }



    @Override
    public void onMovieItemClick(int clickedItemIndex) {
        Movie clickedMovie = movieAdapter.moviesList.get(clickedItemIndex);
        if (isMasterDetail) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(getString(R.string.intent_key_moviedata), clickedMovie);
            MovieDetailFragment fragmentActivity = new MovieDetailFragment();
            fragmentActivity.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_movie_detail_container, fragmentActivity)
                    .commit();
        } else {
            Intent detailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
            detailActivity.putExtra(getString(R.string.intent_key_moviedata), clickedMovie);
            startActivity(detailActivity);
        }
    }


    @Override
    public void onAsyncTaskCompleted(List<Movie> result) {
//            List<Movie> arrayListDB = MovieDbUtilities.jsonStringToMovieList(this, result);
        if (isLoadingMoreMovies) {
            isLoadingMoreMovies = false;
            movieAdapter.moviesList.addAll(result);
            movieAdapter.notifyItemRangeInserted(
                    movieAdapter.getItemCount() - 1, result.size());
        } else movieAdapter.swapList(result);

        switchLoadingStatus();
    }

    private void getMoviesIfNeeded() {
        String newSortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_sorting_key), getString(R.string.pref_key_popularity));
        boolean orderIsChanged = !currentSortOrder.equals(newSortOrder);

        if (movieAdapter.getItemCount() == 0 || orderIsChanged ||
                currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
            currentSortOrder = newSortOrder;

            if (orderIsChanged) {
                gridLayoutManager.scrollToPosition(1);
                page = 1;
                currentSortOrder = newSortOrder;
            }

            getMovies();

        }
    }

    public void getMovies() {
        switchLoadingStatus();

        if (currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
            new MovieDbUtilities.LoadFavourites(this, this).execute();
        } else if (NetworkUtils.isOnline(this)) {
            URL fetchURL = null;
            try {
                fetchURL = new URL(
                        getString(R.string.builder_baseurl) +
                                currentSortOrder +
                                getString(R.string.builder_apikey) + getString(R.string.movieDB_API_v3) +
                                getString(R.string.builder_language) + getString(R.string.builder_lang_enus) +
                                getString(R.string.builder_page) + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new MovieDbUtilities.RequestToMovieDB(this, this).execute(fetchURL);
        }
    }

    private void switchLoadingStatus() {
        FrameLayout masterDetail = (FrameLayout)findViewById(R.id.fl_masterdetail_container);
        if (clpb_empty.getVisibility() == View.GONE) {

            // Progressbar is gone, List is visible
                if (currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
                    // is Favourites
                    if (movieAdapter.getItemCount() == 0) {

                        // List is empty
                        masterDetail.setVisibility(View.GONE);

                        clpb_empty.setVisibility(View.GONE);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error_no_favs);
                    } else {
                        // List is populated
                        masterDetail.setVisibility(View.VISIBLE);

                        clpb_empty.setVisibility(View.GONE);
                        tv_error.setVisibility(View.GONE);
                    }
                } else if (NetworkUtils.isOnline(this)) {  // is fetching from internet
                    // no internet connection
                    if (isLoadingMoreMovies){
                        masterDetail.setVisibility(View.VISIBLE);

                        clpb_empty.setVisibility(View.VISIBLE);
                    } else {
                        masterDetail.setVisibility(View.GONE);

                        clpb_empty.setVisibility(View.VISIBLE);
                    }
                    tv_error.setVisibility(View.GONE);
                } else {
                    // internet connection ok
                    masterDetail.setVisibility(View.GONE);

                    clpb_empty.setVisibility(View.GONE);
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(getString(R.string.error_no_internet));
                }
        } else {
            // List is gone, Progressbar is visible
            masterDetail.setVisibility(View.VISIBLE);

            clpb_empty.setVisibility(View.GONE);
            tv_error.setVisibility(View.GONE);

        }

    }

    private void setActionBarTitle() {
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_sorting_key), getString(R.string.pref_key_popularity));
        if (sortOrder.equals(getString(R.string.pref_key_favourites))) {
            getSupportActionBar().setTitle(getString(R.string.favourites));
        } else if (sortOrder.equals(getString(R.string.pref_key_popularity))) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } else { getSupportActionBar().setTitle(
                getString(R.string.pref_label_toprated) + getString(R.string.singlewhitespace) + getString(R.string.movies)); }

    }

    private RecyclerView.OnScrollListener newScrollMoviesListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisible = gridLayoutManager.findLastVisibleItemPosition();
                int totalItems = gridLayoutManager.getItemCount();
                if ( !isLoadingMoreMovies && !currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
                    if ((lastVisible >= (totalItems - 5) || lastVisible == totalItems)) {
                        Log.d("Scrolled to" , String.valueOf(lastVisible));
                        isLoadingMoreMovies = true; //flag needed to prevent multiple parallel executions
                        page++;
                        getMovies();
                    }
                }
            }
        };
    }

}
