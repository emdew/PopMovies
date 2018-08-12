package com.example.android.popmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favorites")
public class Movies {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    @SerializedName("id")
    private int movieId;

    @SerializedName("poster_path")
    private String poster;

    @SerializedName("backdrop_path")
    private String backdrop;

    @SerializedName("original_title")
    private String title;

    @SerializedName("vote_average")
    private String rating;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("overview")
    private String synopsis;

    @Ignore
    public Movies(String poster, String backdrop, String title, String rating, String releaseDate, String synopsis) {
        this.poster = poster;
        this.backdrop = backdrop;
        this.title = title;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
    }

    public Movies(int movieId, String poster, String backdrop, String title, String rating, String releaseDate, String synopsis) {
        this.movieId = movieId;
        this.poster = poster;
        this.backdrop = backdrop;
        this.title = title;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
