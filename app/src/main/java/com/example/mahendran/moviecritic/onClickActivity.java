package com.example.mahendran.moviecritic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahendran.moviecritic.NetworkData.Movie;
import com.squareup.picasso.Picasso;

public class onClickActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("key",
                    getIntent().getParcelableExtra("key"));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }





    /*Movie movie=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click);
        ImageView img = (ImageView) findViewById(R.id.clickImage);
        TextView desc = (TextView) findViewById(R.id.description);
        TextView date = (TextView) findViewById(R.id.date);
        TextView orig = (TextView) findViewById(R.id.original);
        TextView vote = (TextView) findViewById(R.id.vote);
        Intent intent=this.getIntent();

            movie = (Movie) intent.getParcelableExtra("key");




                Picasso.with(this).load(movie.getPoster()).into(img);
                desc.setText(movie.getOverview());
                date.setText(movie.getReleaseDate());
                vote.setText(movie.getRating());
                orig.setText(movie.getTitle());*/
}




