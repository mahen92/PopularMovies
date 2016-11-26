package com.example.mahendran.moviecritic;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


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
}




