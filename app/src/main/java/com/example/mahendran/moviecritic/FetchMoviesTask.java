/*
 * Copyright 2016 Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.mahendran.moviecritic;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mahendran.moviecritic.Data.MovieContract;
import com.example.mahendran.moviecritic.NetworkData.Movie;
import com.example.mahendran.moviecritic.NetworkData.movieResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kosrat on 6/10/16.
 *
 * Getting movie data from themoviedb API by creating a new thread to work in background.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Cursor cursor;

    public Listener mListener;

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    public interface Listener{
        void onMovieFetchFinished(List<Movie> movies);
    }

    public FetchMoviesTask(Listener listener,Cursor cursor){
        mListener = listener;
        this.cursor=cursor;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        if(params.length == 0){
            return null;
        }

        String sortType=params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Movie m= new Movie();
       
        MovieTaskService movieService = retrofit.create(MovieTaskService.class);

        Call<movieResponse> call = movieService.discoverMovies(sortType, BuildConfig.OPEN_WEATHER_MAP_API_KEY);

        try {
            Response<movieResponse> response = call.execute();
            movieResponse movies = response.body();

            return movies.getMovies();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        // This will only happen if there was an error getting or parsing the movie.
        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if(movies != null){

            mListener.onMovieFetchFinished(movies);


        }else{

            mListener.onMovieFetchFinished(new ArrayList<Movie>());
        }
    }
}