package com.manzo.popularmovies.data;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Manolo on 23/02/2017.
 */

public class ApiServiceGenerator {

    public interface TMDBClient {
        @GET("/3/movie/{sort_order}?language=en-US")
        Call<MovieContainer> getMovies(
                @Path("sort_order") String sort_order,
                @Query("api_key") String api_key,
                @Query("page") String page
        );

        @GET("/3/movie/{movie_id}/reviews?language=en-US&page=1")
        Call<ReviewContainer> getReviews(
                @Path("movie_id") String movie_id,
                @Query("api_key") String api_key
        );

        @GET("/3/movie/{movie_id}/videos?language=en-US&page=1")
        Call<VideoContainer> getVideos(
                @Path("movie_id") String movie_id,
                @Query("api_key") String api_key
        );

        @GET("/3/movie/{movie_id}/videos?language=en-US&page=1")
        Call<ImageContainer> getImages(
                @Path("movie_id") String movie_id,
                @Query("api_key") String api_key
        );
    }


    private static final String API_BASE_URL = "https://api.themoviedb.org/";

    private static Retrofit.Builder builder = new Retrofit.Builder().
            baseUrl(API_BASE_URL).
            addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();



    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();



    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
