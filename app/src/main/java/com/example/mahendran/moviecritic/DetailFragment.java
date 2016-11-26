/*
 * Copyright 2016 Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mahendran.moviecritic;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mahendran.moviecritic.Data.MovieContract;
import com.example.mahendran.moviecritic.NetworkData.Movie;
import com.example.mahendran.moviecritic.NetworkData.Review;
import com.example.mahendran.moviecritic.NetworkData.Reviews;
import com.example.mahendran.moviecritic.NetworkData.Trailer;
import com.example.mahendran.moviecritic.NetworkData.Trailers;
import com.example.mahendran.moviecritic.NetworkData.movieResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DetailFragment extends Fragment implements TrailerAdapter.Callbacks,
        ReviewAdapter.Callback, LoaderManager.LoaderCallbacks<Cursor> {
    Cursor checkCursor;
    private Movie mMovie;
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";
    private static final int CURSOR_LOADER_ID = 0;
    private Intent sharingIntent = null;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @BindView(R.id.trailer_list)
    RecyclerView mRecyclerTrailers;
    @BindView(R.id.review_recycler)
    RecyclerView mRecyclerReviews;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.release_textview)
    TextView mMovieRelease;
    @BindView(R.id.rated_textview)
    TextView mMovieRated;
    @BindView(R.id.overview_textview)
    TextView mMovieOverview;
    @BindView(R.id.poster_imageview)
    ImageView mMoviePoster;
    @BindView(R.id.backdrop)
    ImageView mMovieBackdrop;
    @BindView(R.id.favoritetext)
    TextView favoritetext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey("key")) {
            mMovie = getArguments().getParcelable("key");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        ImageView login;
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        if (!isFavorite()) {
            favoritetext.setText("   Click To Favorite");
        } else {
            favoritetext.setText("   Click To Un-Favorite");
        }
        login = (ImageView) rootView.findViewById(R.id.poster_imageview);

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                makeAsFavorite();
            }
        });
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerTrailers.setLayoutManager(layoutManager);
        mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
        mRecyclerTrailers.setAdapter(mTrailerAdapter);
        mRecyclerTrailers.setNestedScrollingEnabled(false);

        LinearLayoutManager reviewLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerReviews.setLayoutManager(reviewLayoutManager);
        mReviewAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
        mRecyclerReviews.setAdapter(mReviewAdapter);

        mCollapsingToolbar.setTitle(mMovie.getTitle());

        mMovieRelease.setText("Release Date: " + mMovie.getReleaseDate());
        mMovieRated.setText("Rating: " + mMovie.getRating());
        mMovieOverview.setText(mMovie.getOverview());

        Picasso.with(getContext())
                .load(mMovie.getPoster())
                .placeholder(R.drawable.temp_poster) // before load an image
                .error(R.drawable.temp_poster)
                .into(mMoviePoster);
        Picasso.with(getContext())
                .load(mMovie.getBackdrop())
                .placeholder(R.drawable.temp_poster) // before load an image
                .error(R.drawable.temp_poster)
                .into(mMovieBackdrop);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerAdapter.add(trailers);
        } else {
            fetchTrailers();
        }


        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            fetchReviews();
        }


        return rootView;
    }

    public void makeAsFavorite() {

        if (isFavorite()) {
            removeFavorite();
            favoritetext.setText("   Click To Favorite");

        } else {
            makeFavorite();
            favoritetext.setText("   Click To Un-Favorite");
        }
    }

    public void ShareVideo() {
        if (sharingIntent != null) {
            Intent intent = Intent.createChooser(sharingIntent, "Share trailer via");


            if (sharingIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }


    private void fetchTrailers() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        MovieTaskService service = retrofit.create(MovieTaskService.class);
        service.findTrailers(mMovie.getId(),
                BuildConfig.OPEN_WEATHER_MAP_API_KEY).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Trailers>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Trailers trailers) {
                mTrailerAdapter.add(trailers.getTrailers());
            }
        });

    }

    private void fetchReviews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        MovieTaskService service = retrofit.create(MovieTaskService.class);
        service.findReviews(mMovie.getId(),
                BuildConfig.OPEN_WEATHER_MAP_API_KEY).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Reviews>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Reviews reviews) {
                mReviewAdapter.add(reviews.getReviews());
            }
        });

    }

    public void updateShareIntent(Trailer trailer) {
        sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mMovie.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, trailer.getName() + ": "
                + trailer.getTrailerUrl());
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
    }


    private boolean isFavorite() {

        boolean isFav = false;
        checkCursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (checkCursor != null && checkCursor.moveToFirst()) {
            do {
                long dbId = checkCursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                long movieId = mMovie.getId();
                String sortInfo = checkCursor.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);
                if (sortInfo.equals("Favorites") && (dbId == movieId)) {
                    isFav = true;
                }
            } while (checkCursor.moveToNext());
        }
        return isFav;
    }

    private void makeFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {


                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                            mMovie.getPoster());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATED,
                            mMovie.getRating());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE,
                            mMovie.getReleaseDate());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                            mMovie.getBackdrop());
                    movieValues.put(MovieContract.MovieEntry.SORT_INFORMATION,
                            "Favorites");
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues

                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), mMovie.getTitle() + " Added to Favorite", Toast.LENGTH_LONG).show();

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), mMovie.getTitle() + " Removed From Favorite", Toast.LENGTH_LONG).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        checkCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
