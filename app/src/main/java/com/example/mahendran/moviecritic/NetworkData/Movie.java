package com.example.mahendran.moviecritic.NetworkData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mahendran on 23-10-2016.
 */

public class Movie implements Parcelable{


    @SerializedName("id")
    private long mId;
    @SerializedName("original_title")
    private String mTitle;
    @SerializedName("poster_path")
    private String mPoster;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("vote_average")
    private String mRating;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("backdrop_path")
    private String mBackdrop;
    private String sortInformation;
    public Movie(){

    }

    public Movie(long id, String backdropPath, String title, String posterPath, String overview, String rating, String releaseDate,String sortInfo) {
        mId = id;
        mBackdrop = backdropPath;
        mTitle = title;
        mPoster = posterPath;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
        sortInformation=sortInfo;
    }



    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPoster() {
        if (mPoster != null && !mPoster.isEmpty()) {
            return "http://image.tmdb.org/t/p/w185/" + mPoster;
        }
        return null;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getBackdrop() {
        if (mBackdrop != null && !mBackdrop.isEmpty()) {
            return "http://image.tmdb.org/t/p/w500/" + mBackdrop;
        }
        return null;
    }

    public String getSortInformation()
    {
        return sortInformation;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    // This is where you write the values you want to save to the `Parcel`.
    // The `Parcel` class has methods defined to help you save all of your values.
    // Note that there are only methods defined for simple values, lists, and other Parcelable objects.
    // You may need to make several classes Parcelable to send the data you want.
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(mId);
        parcel.writeString(mBackdrop);
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeString(mRating);
        parcel.writeString(mReleaseDate);

    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel parcel) {
            Movie mMovie = new Movie();
            mMovie.mId = parcel.readLong();
            mMovie.mBackdrop = parcel.readString();
            mMovie.mTitle = parcel.readString();
            mMovie.mPoster = parcel.readString();
            mMovie.mOverview = parcel.readString();
            mMovie.mRating = parcel.readString();
            mMovie.mReleaseDate = parcel.readString();

            return mMovie;

        }

        // We just need to copy this and change the type to match our class.

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
