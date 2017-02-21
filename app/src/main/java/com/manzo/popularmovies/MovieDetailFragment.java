package com.manzo.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manzo.popularmovies.data.DbContract;
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

import java.text.DecimalFormat;
import java.util.List;


public class MovieDetailFragment extends Fragment
        implements TrailerAdapter.TrailerClickListener, ReviewAdapter.ReviewClickListener {

    private static final int REQUEST_WRITE_STORAGE = 111;
    final LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    final TrailerAdapter trailerAdapter = new TrailerAdapter(this);
    final LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext());
    final ReviewAdapter reviewAdapter = new ReviewAdapter(this);
    private ActivityMovieDetailBinding binding;

    private Review currentReviewReading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Data binding sets content view and binds data to views in just few steps!
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_movie_detail, container, false);
        final Movie movieData = getArguments().getParcelable(getString(R.string.intent_key_moviedata));
        binding.setMovie(movieData);


        // Poster
        Picasso.with(getContext())
                .load(movieData.getImageLink())
                .into(binding.ivDetailPoster);

        // Rating circle
        String rating = movieData.getRating();
        if (rating.contains(".")) {
            rating = rating.replace(".","");}
        else rating += "0";
        float percent = Float.parseFloat(rating);
        binding.cdRatingCircle.setAnimDuration(1500);
        binding.cdRatingCircle.setTextSize(16f);
        int ratingColor;
        if (percent < 45) {ratingColor = ContextCompat.getColor(getContext(), R.color.colorAccent);}
        else if (percent < 70) {ratingColor = ContextCompat.getColor(getContext(), R.color.colorAccentYellow);}
        else {ratingColor = ContextCompat.getColor(getContext(), R.color.colorAccentGreen);}
        binding.cdRatingCircle.setColor(ratingColor);
        binding.cdRatingCircle.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_text));
        binding.cdRatingCircle.setInnerCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        binding.cdRatingCircle.setValueWidthPercent(25f);
        binding.cdRatingCircle.setDecimalFormat(new DecimalFormat("##0"));
        binding.cdRatingCircle.showValue(percent, 100f, true);

        // Start Volley
        final RequestQueue queue = Volley.newRequestQueue(getContext());
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

        // Favs Buttons
        setupFavsButton();

        return binding.getRoot();
    }

    private void setupFavsButton() {
        Cursor cursor = getContext().getContentResolver().query(DbContract.UserFavourites.URI_CONTENT,
                new String[]{DbContract.UserFavourites._ID},
                DbContract.UserFavourites.COLUMN_TMDB_ID + "=?",
                new String[]{
                        ((Movie) getArguments().getParcelable(getString(R.string.intent_key_moviedata)))
                                .getId()}
                , null);
        if (cursor != null && cursor.moveToNext()){
            switchFavsButton();
        }
        binding.btnFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnFavourites.getText().equals(getString(R.string.add_to_favourites))) {
                    addToFavs();
                } else removeFromFavs();
            }
        });
        cursor.close();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
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
                                    .jsonStringToTrailersList(getContext(), response);
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
                                    .jsonStringToReviewsList(getContext(), response);
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
                if (NetworkUtils.isOnline(getContext())) {
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
                if (NetworkUtils.isOnline(getContext())) {
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
        LayoutInflater inflater= LayoutInflater.from(getContext());
        View view=inflater.inflate(R.layout.dialog_review, null);

        TextView tv_content = (TextView) view.findViewById(R.id.tv_dialog_content);
        tv_content.setText(review.getContent());

        TextView tv_author = (TextView) view.findViewById(R.id.tv_dialog_author);
        tv_author.setText(review.getAuthor());

        ImageView iv_siteicon = (ImageView) view.findViewById(R.id.iv_dialog_site_icon);
        // Check review site to choose icon
        if (review.getUrl().contains(getString(R.string.key_moviedb))) {
            Picasso.with(getContext()).load(R.mipmap.ic_tmdb).into(iv_siteicon);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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


    private void addToFavs() {
//        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
//        if (!hasPermission) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_WRITE_STORAGE);
//        } else {
            final Movie movieData = getArguments().getParcelable(getString(R.string.intent_key_moviedata));
            Uri imageUri = MovieDbUtilities.imageDownload(movieData.getImageLink(), getContext());
            Movie tempMovie = movieData;
            tempMovie.setImageLink(String.valueOf(imageUri));
            Uri insertedUri = getContext().getContentResolver().insert(
                    DbContract.UserFavourites.URI_CONTENT,
                    MovieDbUtilities.movieToContentValues(tempMovie));
            Log.d("Inserted uri: ", String.valueOf(insertedUri));

            switchFavsButton();
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode)
//        {
//            case REQUEST_WRITE_STORAGE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    //reload my activity with permission granted or use the features what required the permission
//                } else
//                {
//                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//
//    }


    private void removeFromFavs() {
        final Movie movieData = getArguments().getParcelable(getString(R.string.intent_key_moviedata));
        int deleted = getContext().getContentResolver().delete(
                DbContract.UserFavourites.URI_CONTENT,
                DbContract.UserFavourites.COLUMN_TMDB_ID + "=?",
                new String[]{movieData.getId()});
        Log.d("deleted rows", String.valueOf(deleted));

        switchFavsButton();
    }

    private void switchFavsButton() {
        if (binding.btnFavourites.getText().equals(getString(R.string.add_to_favourites))) {
            binding.btnFavourites.setText(R.string.one_of_my_favs);
            binding.btnFavourites.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_text));
            binding.btnFavourites.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.amber_background));
            binding.btnFavourites.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), android.R.drawable.star_big_on), null,null,null);
        } else {
            binding.btnFavourites.setText(R.string.add_to_favourites);
            binding.btnFavourites.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentYellow));
            binding.btnFavourites.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_transparent));
            binding.btnFavourites.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_off), null,null,null);
        }
    }
}
