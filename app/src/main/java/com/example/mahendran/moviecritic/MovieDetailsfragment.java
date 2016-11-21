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
import com.example.mahendran.moviecritic.NetworkData.Review;
import com.example.mahendran.moviecritic.NetworkData.Trailer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahendran on 15-08-2016.
 */
public class MovieDetailsfragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        FetchMoviesTask.Listener,CustomAdapter.Callbacks,FetchReviewTask.Listener,FetchTrailersTask.Listener {
    Cursor c;
    private final String MOVIE_DATA = "MOVIE_DATA";
    private final String MOVIE_SORT = "SORT_BY";
    private String sortType;
    ArrayList<Movie> resultS = new ArrayList<>();
    private CustomAdapter cs;
    private static final int CURSOR_LOADER_ID = 0;
    private ActionBar actionBar;

    @BindView(R.id.movie_recycler)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReviewFetchFinished(List<Review> reviews) {

    }

    @Override
    public void onFetchFinished(List<Trailer> trailers) {

    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Movie movie);
    }

    public void onStart() {
        super.onStart();
        String s = getMovieSort();
        fetchMovies(s);
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
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)

    {


        cs = new CustomAdapter(resultS,this);
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        ButterKnife.bind(this, rootView);


        sortType = getMovieSort();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        mRecyclerView.setAdapter(cs);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("The Rockers");

        return rootView;
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
            fetchMovies(sortType);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = cs.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(MOVIE_DATA, movies);
        }
        outState.putString(MOVIE_SORT, sortType);

        // Needed to avoid confusion, when we back from detail screen (i. e. top rated selected but
        // favorite movies are shown and onCreate was not called in this case).
        if (!sortType.equals(getString(R.string.pref_units_favorites))) {
            getLoaderManager().destroyLoader(CURSOR_LOADER_ID);
        }
    }

    private void fetchMovies(String sort) {

        if (!sort.equals(getString(R.string.pref_units_favorites))) {
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
            new FetchMoviesTask(this,c).execute(sort);


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

        c = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry._ID},
                        null,
                        null,
                        null);

        if (c.getCount() != 0) {
            Log.v("insert data", "call");

        }
        int versionIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);

        Log.v("value please", "" + versionIndex);
        super.onActivityCreated(savedInstanceState);
    }

    public void insertData() {
        ContentValues[] flavorValuesArr = new ContentValues[resultS.size()];
        Log.v("posterSIZE", "" + resultS.size());

        // Loop through static array of Flavors, add each to an instance of ContentValues
        // in the array of ContentValues
        for (int i = 0; i < resultS.size(); i++) {
            flavorValuesArr[i] = new ContentValues();
            Log.v("poster", resultS.get(i).getPoster());
            flavorValuesArr[i].put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, resultS.get(i).getPoster());

        }


        getActivity().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                flavorValuesArr);


        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("oncreateloader", "bust");
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        c = cursor;
        if (getMovieSort().equals(getString(R.string.pref_units_favorites))) {
         List<Movie> moviesList=new ArrayList<>();
            if (getMovieSort().equals(getString(R.string.pref_units_favorites))) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);

                        String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                        String sorting = cursor.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);
                        Log.v("Favorite List",rating+"="+(getMovieSort()));
                        if (sorting.equals("Favorite")) {

                            long id1 = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                            String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                            String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER);
                            String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                            String rating1 = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                            String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE);
                            String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP);
                            Movie movie = new Movie(id1, backdropPath, title, posterPath, overview, rating1, releaseDate,getMovieSort());
                            moviesList.add(movie);
                        }

                    } while (cursor.moveToNext());
                    cs.add(moviesList);

                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onMovieFetchFinished(List<Movie> movies) {
        Cursor cursor=c;

        if(movies.size()==0)
        {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);

                    String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                    String sortInfo=cursor.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);
                   if(sortInfo.equals(getMovieSort()))
                    {

                        long id1 = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                        String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                        String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER);
                        String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                        String rating1 = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                        String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE);
                        String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP);
                        Movie movie = new Movie(id1, backdropPath, title, posterPath, overview, rating1, releaseDate,getMovieSort());
                        movies.add(movie);
                    }

                } while (cursor.moveToNext());
            }
            cs.add(movies);
            Toast.makeText(getActivity(), "Use It!!!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.v("else","atleast enters");
            if (cursor != null && cursor.moveToFirst()) {
                Log.v("for delete","atleast enters");
                do {
                    long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                    String s = cursor.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);
                    String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                    String title=cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);


                        if(s.equals(getMovieSort())) {

                            getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                                    MovieContract.MovieEntry.SORT_INFORMATION+ "="+ "'"+s+"'", null);
                        }


                } while (cursor.moveToNext());
            }
            cs.add(movies);
            for(Movie mMovie:movies)
            {
                Log.v("for Loop","atleast enters");
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
                        "");
                movieValues.put(MovieContract.MovieEntry.SORT_INFORMATION,
                        getMovieSort());
                getContext().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieValues
                );
            }
        }

    }


    public void open(Movie movie, int position) {

        ((Callback) getActivity()).onItemSelected(movie);
//        Log.i("Movie", "open: "+ movie.mTitle);
       // Intent intent = new Intent(getActivity(), onClickActivity.class);
       // intent.putExtra("key", movie);
        //startActivity(intent);
    }
}




