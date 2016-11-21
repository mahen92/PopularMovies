package com.example.mahendran.moviecritic;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahendran.moviecritic.Data.MovieContract;
import com.example.mahendran.moviecritic.NetworkData.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahendran on 16-08-2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {


    private final ArrayList<Movie> mMovies;
    private final Callbacks mCallbacks;

    public CustomAdapter(ArrayList<Movie> movies,Callbacks callbacks) {
        mMovies = movies;
        this.mCallbacks = callbacks;
    }

    public interface Callbacks {
        void open(Movie movie, int position);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Movie movie = mMovies.get(position);
        final Context context = holder.mView.getContext();
        holder.mTitleView.setText(movie.getTitle());
        String posterUrl = movie.getPoster();


        if (!posterUrl.equals("")) {
            Picasso.with(context)
                    .load(posterUrl)
                    .placeholder(R.drawable.temp_poster) // before load an image
                    .error(R.drawable.temp_poster)// at error of loading image
                    .into(holder.mThumbnailView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.open(movie, holder.getAdapterPosition());
            }
        });
    }

    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        @BindView(R.id.iv_thumbnail)
        ImageView mThumbnailView;
        @BindView(R.id.tv_title)
        TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;

        }

    }

    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP);
                String sortInformation = cursor.getString(MovieContract.MovieEntry.COL_SORT_INFORMATION);
                Movie movie = new Movie(id, backdropPath, title, posterPath, overview, rating, releaseDate,"");
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }
}
