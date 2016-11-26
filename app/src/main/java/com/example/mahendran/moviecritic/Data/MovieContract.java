/*
 * Copyright (C) 2016. Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mahendran.moviecritic.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.mahendran.moviecritic";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movies";


    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        // column for storing the id of movie
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // column for storing the title of movie
        public static final String COLUMN_MOVIE_TITLE = "title";

        // column for storing the poster path of movie
        public static final String COLUMN_MOVIE_POSTER = "poster_path";

        // column for storing the overview of movie
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        // column for storing the rated of movie
        public static final String COLUMN_MOVIE_RATED = "rated";

        // column for storing the release date of movie
        public static final String COLUMN_MOVIE_RELEASE = "release_date";

        // column for storing backdrop image of the movie
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop_path";

        public static final String SORT_INFORMATION = "sort_information";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Store all column of movies table we will use it for the Detail view.
        // Specify the columns we need.
        public static final String[] MOVIE_COLUMNS={
                MovieEntry._ID,
                COLUMN_MOVIE_ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_MOVIE_POSTER,
                COLUMN_MOVIE_OVERVIEW,
                COLUMN_MOVIE_RATED,
                COLUMN_MOVIE_RELEASE,
                COLUMN_MOVIE_BACKDROP,
                SORT_INFORMATION
        };

        // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
        // must change.
        public static final int COL_MOVIE_TABLE_ID = 0;
        public static final int COL_MOVIE_ID = 1;
        public static final int COL_MOVIE_TITLE = 2;
        public static final int COL_MOVIE_POSTER = 3;
        public static final int COL_MOVIE_OVERVIEW = 4;
        public static final int COL_MOVIE_RATED = 5;
        public static final int COL_MOVIE_RELEASE = 6;
        public static final int COL_MOVIE_BACKDROP = 7;
        public static final int COL_SORT_INFORMATION = 8;
    }
}
