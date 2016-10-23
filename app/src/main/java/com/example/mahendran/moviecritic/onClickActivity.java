package com.example.mahendran.moviecritic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class onClickActivity extends AppCompatActivity {

    Movie movie=null;
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




                Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.getPoster()).into(img);
                desc.setText(movie.getOverview());
                date.setText(movie.getReleaseDate());
                vote.setText(movie.getVoteAverage());
                orig.setText(movie.getOriginalTitle());
            }
        }



