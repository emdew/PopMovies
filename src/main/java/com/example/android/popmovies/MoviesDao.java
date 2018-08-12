package com.example.android.popmovies;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.popmovies.models.Movies;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM favorites ORDER BY movie_id")
    LiveData<List<Movies>> loadFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movies movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movies movie);

    @Delete
    void deleteMovie(Movies movies);

    @Query("SELECT * FROM favorites WHERE movie_id = :id")
    LiveData<Movies> loadMovieById(int id);
}
