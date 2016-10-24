package com.example.mahendran.moviecritic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mahendran on 15-08-2016.
 */
public class MovieDetailsfragment extends Fragment  {
    //ArrayList<Movie> resultStrs=new ArrayList<>();
    ArrayList<Movie> resultS=new ArrayList<>();
    CustomAdapter cs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public void onStart()
    {
        super.onStart();
       FetchMovieDetails task=new FetchMovieDetails();

        task.execute("");


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
        //if (id == R.id.action_refresh) {

        //}
        return super.onOptionsItemSelected(item);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)

    {

        cs=new CustomAdapter(getActivity(),resultS);


        View rootView=inflater.inflate(R.layout.fragment_layout,container,false);
        GridView gridView=(GridView) rootView.findViewById(R.id.list_view);


        gridView.setAdapter(cs);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView,View view,int position,long l)
            {

                    Intent intent = new Intent(getActivity(), onClickActivity.class);
                    Movie movie= cs.getItem(position);
                    intent.putExtra("key", movie);
                    startActivity(intent);

            }
        });
       return rootView;
    }




    public class FetchMovieDetails extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMovieDetails.class.getSimpleName();
        //private final String OPEN_WEATHER_MAP_API_KEY = "";

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_keysnew),
                    getString(R.string.pref_units_popular_unitnew));



            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/"+unitType;
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
                Log.v("URLBuilder",builtUri.toString());


                String urlString;

                URL url =new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");

                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                JsonStr = buffer.toString();


            } catch (IOException e) {


                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(JsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;

        }
        protected void onPostExecute(ArrayList<Movie> result) {

            if (result != null) {
                cs.clear();
                for(Movie movieStr : result) {
                    cs.add(movieStr);
                }
            }
            else
            {

                Toast toast = Toast.makeText(getContext(), "An error Occured while displaying the information", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        }


        private ArrayList<Movie> getMovieDataFromJson(String JsonStr, int numDays)
                throws JSONException {
            final String POSTER_PATH = "poster_path";
            final String  OVERVIEW= "overview";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVERAGE = "vote_average";
            final String ORIGINAL_TITLE = "original_title";
            final String OWM_DESCRIPTION = "main";
            final String RESULTS = "results";
            JSONObject movieJson = new JSONObject(JsonStr);
            JSONArray resultsArray=movieJson.getJSONArray(RESULTS);
            ArrayList<Movie> resultStrs=new ArrayList<>();

            for(int i=0;i<resultsArray.length();i++) {
                JSONObject movieObject=resultsArray.getJSONObject(i);
                String poster = movieObject.getString(POSTER_PATH);
                String overView = movieObject.getString(OVERVIEW);
                String releaseDate = movieObject.getString(RELEASE_DATE);
                String voteAverage = movieObject.getString(VOTE_AVERAGE);
                String originalTitle = movieObject.getString(ORIGINAL_TITLE);
                String background = movieObject.getString("backdrop_path");

                resultStrs.add(new Movie(poster, overView, releaseDate, voteAverage, originalTitle, background));
            }
            return resultStrs;
        }
    }

