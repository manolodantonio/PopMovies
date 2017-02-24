package com.manzo.popularmovies;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.manzo.popularmovies.data.ApiServiceGenerator;
import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.MovieContainer;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.manzo.popularmovies.listComponents.MovieAdapter;
import com.manzo.popularmovies.utilities.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieItemClickListener,
        Utils.AsyncTaskCompletedListener,
        Utils.SubMenuItemClickedListener {


    private static final int MOVIE_DETAIL_CODE = 657;
    private static final String FRAGMENT_DETAIL = "fragment detail";

    public static final ApiServiceGenerator.TMDBClient ApiClient =
            ApiServiceGenerator.createService(ApiServiceGenerator.TMDBClient.class);


    public GridLayoutManager gridLayoutManager;
    final MovieAdapter movieAdapter = new MovieAdapter(this);
    public RecyclerView rv_mainList;
    public ProgressBar clpb_empty;
    private TextView tv_error;


    private String currentSortOrder;
    private int page = 1;
    private boolean isLoadingMoreMovies = false;
    public static boolean isMasterDetail = false;
    private Movie currentMovie;
    private List<Movie> currentMovieList;
    private int currentPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            currentMovieList = savedInstanceState.getParcelableArrayList(getString(R.string.intent_key_movieslist));
            currentPosition = savedInstanceState.getInt(getString(R.string.intent_scroll_position));
            page = savedInstanceState.getInt(getString(R.string.intent_scroll_page));
        }

        /////////// LAYOUT SETUP
        isMasterDetail = (findViewById(R.id.fl_movie_detail_container) != null);


        int glSpanCount = Integer.parseInt(getString(R.string.grid_element_vertical));
        int glOrientation = GridLayoutManager.VERTICAL;
        if (!isMasterDetail && !Utils.isTablet(this) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // is a small screen in landscape orientation
            glSpanCount =  Integer.parseInt(getString(R.string.grid_element_horizontal));
            glOrientation = GridLayoutManager.HORIZONTAL;
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
    protected void onDestroy() {
        super.onDestroy();
        Fragment fragmentToDestroy = getSupportFragmentManager().findFragmentByTag(FRAGMENT_DETAIL);
        if(fragmentToDestroy != null) {
            getSupportFragmentManager().beginTransaction().remove(fragmentToDestroy).commitNow();}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.intent_key_moviedata), currentMovie);
        outState.putParcelableArrayList(getString(R.string.intent_key_movieslist), new ArrayList<>(movieAdapter.getMoviesList()));
        outState.putInt(getString(R.string.intent_scroll_position), gridLayoutManager.findFirstVisibleItemPosition());
        outState.putInt(getString(R.string.intent_scroll_page), page);
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
        currentMovie = movieAdapter.moviesList.get(clickedItemIndex);
        if (isMasterDetail) {
            switchDetailFragment(currentMovie);
        } else {
//            Fragment fragmentToDestroy = getSupportFragmentManager().findFragmentByTag(FRAGMENT_DETAIL);
//            if(fragmentToDestroy != null) {
//                getSupportFragmentManager().beginTransaction().remove(fragmentToDestroy).commitNow();}

            Intent detailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
            detailActivity.putExtra(getString(R.string.intent_key_moviedata), currentMovie);
            startActivityForResult(detailActivity, MOVIE_DETAIL_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isMasterDetail && Utils.isTablet(this) && requestCode == MOVIE_DETAIL_CODE) {
            try { // block to trace a non-reproducible null pointer exception
                currentMovie = data.getParcelableExtra(getString(R.string.intent_key_moviedata));
                switchDetailFragment(currentMovie);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchDetailFragment(Movie movie) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(getString(R.string.intent_key_moviedata), movie);
        MovieDetailFragment fragmentActivity = new MovieDetailFragment();
        fragmentActivity.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_movie_detail_container, fragmentActivity, FRAGMENT_DETAIL)
                .commitNow();
    }

    @Override
    public void onAsyncTaskCompleted(List<Movie> result) {
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

        if (orderIsChanged || currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
            // if sortorder is changed, or if is favourites list, load fetch new data
            currentSortOrder = newSortOrder;

            if (orderIsChanged) {
                gridLayoutManager.scrollToPosition(1);
                page = 1;
                currentSortOrder = newSortOrder;
            }

            getMovies();

        } else if (movieAdapter.getItemCount() == 0){
            // if adapter is empy..
            if (currentMovieList != null) {
                // .. and we have a save state, restore it
                movieAdapter.swapList(currentMovieList);
                gridLayoutManager.scrollToPosition(currentPosition);
            } else getMovies(); // .. and no save state, fetch movies data
        }

    }

//    public void getMovies() {
//        switchLoadingStatus();
//
//        if (currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
//            new MovieDbUtilities.LoadFavourites(this, this).execute();
//        } else if (Utils.isOnline(this)) {
//            URL fetchURL = null;
//            try {
//                fetchURL = new URL(
//                        getString(R.string.builder_baseurl) +
//                                currentSortOrder +
//                                getString(R.string.builder_apikey) + getString(R.string.movieDB_API_v3) +
//                                getString(R.string.builder_language) + getString(R.string.builder_lang_enus) +
//                                getString(R.string.builder_page) + page);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            new MovieDbUtilities.RequestToMovieDB(this, this).execute(fetchURL);
//        }
//    }

    public void getMovies() {
        switchLoadingStatus();

        if (currentSortOrder.equals(getString(R.string.pref_key_favourites))) {
            new MovieDbUtilities.LoadFavourites(this, this).execute();
        } else if (Utils.isOnline(this)) {
            final Call<MovieContainer> movieRequest = ApiClient.getMovies(
                    currentSortOrder, getString(R.string.movieDB_API_v3), String.valueOf(page));
            movieRequest.enqueue(new Callback<MovieContainer>() {
                @Override
                public void onResponse(Call<MovieContainer> call, Response<MovieContainer> response) {
                    List<Movie> movies = formatMovies(response.body().getMovies());
                    if (isLoadingMoreMovies) {
                        isLoadingMoreMovies = false;
                        movieAdapter.moviesList.addAll(movies);
                        movieAdapter.notifyItemRangeInserted(
                                movieAdapter.getItemCount() - 1, movies.size());
                    } else movieAdapter.swapList(movies);

                    switchLoadingStatus();
                }

                private List<Movie> formatMovies(List<Movie> movies) {
                    List<Movie> formattedList = new ArrayList<Movie>(0);
                    for (int i = 0; i < movies.size(); i++) {
                        Movie movie = movies.get(i);
                        movie.setImageLink(
                                getString(R.string.builder_image_baseurl) +
                                getString(R.string.builder_image_quality_medium) +
                                movie.getImageLink());
                        movie.setOriginalTitle(
                                getString(R.string.original_title_split) +
                                getString(R.string.newline) +
                                movie.getOriginalTitle());
                        movie.setReleaseDate(
                                getString(R.string.release_split) +
                                getString(R.string.newline) +
                                MovieDbUtilities.formatStringDate(movie.getReleaseDate()));
                        formattedList.add(movie);
                    }
                    return formattedList;
                }

                @Override
                public void onFailure(Call<MovieContainer> call, Throwable t) {
                    t.printStackTrace();
                    switchLoadingStatus();
                }
            });
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
                } else if (Utils.isOnline(this)) {  // is fetching from internet
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
//                        Log.d("Scrolled to" , String.valueOf(lastVisible));
                        isLoadingMoreMovies = true; //flag needed to prevent multiple parallel executions
                        page++;
                        getMovies();
                    }
                }
            }
        };
    }

}
