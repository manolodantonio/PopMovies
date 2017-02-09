package com.manzo.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manzo.popularmovies.data.Movie;
import com.manzo.popularmovies.data.MovieDbUtilities;
import com.manzo.popularmovies.data.Review;
import com.manzo.popularmovies.data.Trailer;
import com.manzo.popularmovies.databinding.ActivityMovieDetailBinding;
import com.manzo.popularmovies.listComponents.ReviewAdapter;
import com.manzo.popularmovies.listComponents.TrailerAdapter;
import com.manzo.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;


public class MovieDetailActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerClickListener, ReviewAdapter.ReviewClickListener {

    final LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);
    final TrailerAdapter trailerAdapter = new TrailerAdapter(this);
    final LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
    final ReviewAdapter reviewAdapter = new ReviewAdapter(this);
    private ActivityMovieDetailBinding binding;

    private Review currentReviewReading;

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
        binding.rvTrailersList.setLayoutManager(trailersLayoutManager);
        queue.add(fetchVideos(movieData));

//        Reviews
        binding.rvReviewsList.setAdapter(reviewAdapter);
        binding.rvReviewsList.setLayoutManager(reviewLayoutManager);
        queue.add(fetchReviews(movieData));

        // Reopen review detail if reading while saveinstancestate
        if (savedInstanceState != null) {
            currentReviewReading = savedInstanceState.getParcelable(getString(R.string.outstate_review));
            if (currentReviewReading != null) {openReviewDialog(currentReviewReading);}
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.outstate_review), currentReviewReading);
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

    private StringRequest fetchReviews(Movie movieData) {
        switchReviewLoading();
        final String reviewAddress = getString(R.string.builder_baseurl) +
                movieData.getId() + getString(R.string.builder_reviews) +
                getString(R.string.builder_apikey) + getString(R.string.movieDB_API_v3) +
                getString(R.string.builder_language) + getString(R.string.builder_lang_enus);
        return new StringRequest(
                Request.Method.GET,
                reviewAddress,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Review> reviewList = MovieDbUtilities
                                    .jsonStringToReviewsList(MovieDetailActivity.this, response);
                            reviewAdapter.swapList(reviewList);
                            switchReviewLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            switchReviewLoading();
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
                    binding.tvTrailersError.setText(R.string.error_no_videos);
                } else binding.tvTrailersError.setText(R.string.error_no_internet);
            }
        }
    }

    private void switchReviewLoading() {
        if (binding.clpbReviews.getVisibility() == View.GONE) {
            // RecyclerView is visible
            binding.clpbReviews.setVisibility(View.VISIBLE);
            binding.rvReviewsList.setVisibility(View.GONE);
            binding.tvReviewsError.setVisibility(View.GONE);
        } else {
            // RecyclerView is not visible
            if (reviewAdapter.getItemCount() != 0) {
                binding.clpbReviews.setVisibility(View.GONE);
                binding.rvReviewsList.setVisibility(View.VISIBLE);
                binding.tvReviewsError.setVisibility(View.GONE);
            } else {
                binding.clpbReviews.setVisibility(View.GONE);
                binding.rvReviewsList.setVisibility(View.GONE);
                binding.tvReviewsError.setVisibility(View.VISIBLE);
                if (NetworkUtils.isOnline(this)) {
                    binding.tvReviewsError.setText(R.string.error_no_review);
                } else binding.tvReviewsError.setText(R.string.error_no_internet);
            }
        }
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        Uri videoUri = Uri.parse(getString(R.string.builder_youtube_base) + trailer.getKey());
        Intent playVideo = new Intent(Intent.ACTION_VIEW, videoUri);
        startActivity(playVideo);
    }

    @Override
    public void onReviewClick(final Review review) {
        openReviewDialog(review);
    }

    private void openReviewDialog(final Review review) {
        currentReviewReading = review;
        LayoutInflater inflater= LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.dialog_review, null);

        TextView tv_content = (TextView) view.findViewById(R.id.tv_dialog_content);
        tv_content.setText(review.getContent());

        TextView tv_author = (TextView) view.findViewById(R.id.tv_dialog_author);
        tv_author.setText(review.getAuthor());

        ImageView iv_siteicon = (ImageView) view.findViewById(R.id.iv_dialog_site_icon);
        // Check review site to choose icon
        if (review.getUrl().contains(getString(R.string.key_moviedb))) {
            Picasso.with(this).load(R.mipmap.ic_tmdb).into(iv_siteicon);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton(R.string.btn_openinbrowser, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
                startActivity(openBrowser);
            }
        });
        builder.setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currentReviewReading = null;
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
