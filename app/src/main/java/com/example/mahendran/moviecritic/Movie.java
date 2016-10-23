package com.example.mahendran.moviecritic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mahendran on 23-10-2016.
 */

public class Movie implements Parcelable{

    private String poster;
    private String overView;
    private String releaseDate;
    private String voteAverage;
    private String originalTitle;
    private String background;


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param out  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(poster);
        out.writeString(overView);
        out.writeString(releaseDate);
        out.writeString(voteAverage);
        out.writeString(originalTitle);
        out.writeString(background);
    }

    public static final Creator<Movie> CREATOR
            = new Creator<Movie>(){
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param in The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in){
        poster = in.readString();
        overView = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
        originalTitle = in.readString();
        background = in.readString();

    }

    public Movie(String id, String originalTitle, String posterPath, String backdropPath, String synapsis, String userRating){
        this.poster = id;
        this.overView = originalTitle;
        this.releaseDate = posterPath;
        this.voteAverage = backdropPath;
        this.originalTitle = synapsis;
        this.background = userRating;

    }

    public String getPoster() {
        return poster;
    }

    public String getOverview() {
        return overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getBackground() {
        return background;
    }

    public String getVoteAverage() {
        return voteAverage;
    }


}
