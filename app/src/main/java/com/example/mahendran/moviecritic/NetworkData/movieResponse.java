package com.example.mahendran.moviecritic.NetworkData;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahendran on 15-11-2016.
 */

public class movieResponse {

    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

    public List<Movie> getMovies(){
        return movies;
    }
}
