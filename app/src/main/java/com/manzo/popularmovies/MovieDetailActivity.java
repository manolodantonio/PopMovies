package com.manzo.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.manzo.popularmovies.data.Trailer;
import com.manzo.popularmovies.databinding.ActivityMovieDetailBinding;
import com.manzo.popularmovies.listComponents.MovieAdapter;
import com.manzo.popularmovies.listComponents.TrailerAdapter;
import com.manzo.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;


public class MovieDetailActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerClickListener {

    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    final TrailerAdapter trailerAdapter = new TrailerAdapter(this);
    private ActivityMovieDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Data binding sets content view and binds data to views in just few steps!
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        Movie movieData = getIntent().getParcelableExtra(getString(R.string.intent_key_moviedata));
        binding.setMovie(movieData);

        // Poster
        Picasso.with(this)
                .load(movieData.getImageLink())
                .into(binding.ivPoster);

        // Start Volley
        final RequestQueue queue = Volley.newRequestQueue(this);
        // Trailers
        binding.rvTrailersList.setAdapter(trailerAdapter);
        binding.rvTrailersList.setLayoutManager(linearLayoutManager);
        queue.add(fetchVideos(movieData));

    }

    private StringRequest fetchVideos(Movie movieData) {
        switchTrailerLoading();
        final String videosAddress = getString(R.string.builder_baseurl) +
                movieData.getId() + getString(R.string.builder_videos) +
                getString(R.string.builder_apikey) + getString(R.string.movieDB_API_v3) +
                getString(R.string.builder_language) + getString(R.string.builder_lang_enus);
        return new StringRequest(
                Request.Method.GET,
                videosAddress,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Trailer> videoList = MovieDbUtilities
                                    .jsonStringToTrailersList(MovieDetailActivity.this, response);
                            trailerAdapter.swapList(videoList);
                            switchTrailerLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            switchTrailerLoading();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switchTrailerLoading();
                        error.printStackTrace();
                    }
                }
        );
    }

    private void switchTrailerLoading() {
        if (binding.clpbTrailers.getVisibility() == View.GONE) {
            // RecyclerView is visible
            binding.clpbTrailers.setVisibility(View.VISIBLE);
            binding.rvTrailersList.setVisibility(View.GONE);
            binding.tvTrailersError.setVisibility(View.GONE);
        } else {
            // RecyclerView is not visible
            if (trailerAdapter.getItemCount() != 0) {
                binding.clpbTrailers.setVisibility(View.GONE);
                binding.rvTrailersList.setVisibility(View.VISIBLE);
                binding.tvTrailersError.setVisibility(View.GONE);
            } else {
                binding.clpbTrailers.setVisibility(View.GONE);
                binding.rvTrailersList.setVisibility(View.GONE);
                binding.tvTrailersError.setVisibility(View.VISIBLE);
                if (NetworkUtils.isOnline(this)) {
                    binding.tvTrailersError.setText(R.string.error_trailers_novideos);
                } else binding.tvTrailersError.setText(R.string.error_no_internet);
            }
        }
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        Uri videoUri = Uri.parse(getString(R.string.builder_youtube_base) + trailer.getKey());
        Intent playVideo = new Intent(Intent.ACTION_VIEW, videoUri);
        startActivity(playVideo);
    }
}
