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

    public CustomAdapter(ArrayList<Movie> movies, Callbacks callbacks) {
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

        @BindView(R.id.image_view)
        ImageView mThumbnailView;
        @BindView(R.id.movie_name)
        TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }
}
