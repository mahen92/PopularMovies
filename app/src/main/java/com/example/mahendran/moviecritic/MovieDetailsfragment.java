package com.example.mahendran.moviecritic;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mahendran.moviecritic.Data.MovieContract;
import com.example.mahendran.moviecritic.NetworkData.Movie;
import com.example.mahendran.moviecritic.NetworkData.movieResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mahendran on 15-08-2016.
 */
public class MovieDetailsfragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomAdapter.Callbacks {
    Cursor cursor;
    int position1=-1;
    private static final String SELECTED_KEY = "selected_position";
    private final String MOVIE_DATA = "MOVIE_DATA";
    private final String MOVIE_SORT = "SORT_BY";
    private String sortType;
    ArrayList<Movie> resultS = new ArrayList<>();
    private CustomAdapter cs;
    private static final int CURSOR_LOADER_ID = 0;


    @BindView(R.id.movie_recycler)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public interface Callback {

        void onItemSelected(Movie movie);
    }

    public void onStart() {
        super.onStart();
        fetchMovies();
        cs.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)

    {
        cs = new CustomAdapter(resultS,this);
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        ButterKnife.bind(this, rootView);


        sortType = getMovieSort();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns()));


        mRecyclerView.setAdapter(cs);
        Thread t = Thread.currentThread();
        t.setDefaultUncaughtExceptionHandler(new MyThreadUncaughtExceptionHandler());
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            position1 = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    public int numberOfColumns() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int numOfColumns;
        if (dpWidth < dpHeight) {

            numOfColumns = 2;
            if (dpWidth >= 600) {
                numOfColumns = 3;
            }
        } else {

            numOfColumns = 3;
        }
        return numOfColumns;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            sortType = savedInstanceState.getString(MOVIE_SORT);
            if (savedInstanceState.containsKey(MOVIE_DATA)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIE_DATA);
                cs.add(movies);
                getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
                // For listening content updates for tow pane mode
                if (sortType.equals(getString(R.string.pref_units_favorites))) {
                    getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
                }
            }
        } else {
            // Fetch Movies only if savedInstanceState == null
            fetchMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = cs.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(MOVIE_DATA, movies);
            outState.putInt(SELECTED_KEY, position1);
        }
        outState.putString(MOVIE_SORT, sortType);


    }

    private void fetchMovies() {


        if (!getMovieSort().equals(getString(R.string.pref_units_favorites))) {
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
            getMovies();


        } else {
            Toast toast = Toast.makeText(getContext(), "Favorites", Toast.LENGTH_SHORT);
            toast.show();
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        }

    }

    private String getMovieSort() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(
                getString(R.string.pref_units_keysnew),
                getString(R.string.pref_units_popular_unitnew));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                null,
                null,
                null);

        super.onActivityCreated(savedInstanceState);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor1) {
        Cursor cursor=cursor1;
        List<Movie> moviesList=new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String sorting = cursor.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);
                        if (sorting.contains(getMovieSort())) {

                            long id1 = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                            String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                            String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER);
                            String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                            String rating1 = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                            String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE);
                            String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP);
                            String[] pathPosterArr = posterPath.split("/");
                            String[] backdropArr = backdropPath.split("/");
                            String pathPoster = pathPosterArr[pathPosterArr.length - 1];
                            String backdropStr1 = backdropArr[backdropArr.length - 1];
                            Movie movie = new Movie(id1, backdropStr1, title, pathPoster, overview, rating1, releaseDate,getMovieSort());
                            moviesList.add(movie);
                        }

                    } while (cursor.moveToNext());
            if (position1 != -1) {
                mRecyclerView.smoothScrollToPosition(position1);
            }
                    cs.add(moviesList);
                }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    protected List<Movie> getMovies() {
        String sortType=getMovieSort();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        MovieTaskService movieService = retrofit.create(MovieTaskService.class);
        movieService.discoverMovies(sortType, BuildConfig.OPEN_WEATHER_MAP_API_KEY).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<movieResponse>() {
            List<Movie> movies=null;

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
               message();
            }

            @Override
            public void onNext(movieResponse response) {
                Cursor cursor1= getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                movies = response.getMovies();
                if (cursor1 != null && cursor1.moveToFirst()) {
                        do {
                            String s = cursor1.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);

                           if ((s.equals(getMovieSort())) && !(s.contains("Favorite"))) {

                               getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                                        MovieContract.MovieEntry.SORT_INFORMATION + "=" + "'" + s + "'", null);
                            }
                        } while (cursor1.moveToNext());
                    }
                    cs.add(movies);
                   for (Movie mMovie : movies) {
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
                                getMovieSort());
                        getContext().getContentResolver().insert(
                                MovieContract.MovieEntry.CONTENT_URI,
                                movieValues
                        );

                    }
                }
            public void message()
            {
                if(movies==null)
                {
                    Toast.makeText(getContext(), "An error occured. Showing the last successful update.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return null;
    }

    public void open(Movie movie, int position) {
        position1=position;
        ((Callback) getActivity()).onItemSelected(movie);
    }
}




