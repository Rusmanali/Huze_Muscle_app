package com.example.huzemuscle.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FitnessDao {
    @Insert
    void insert(FitnessActivity activity);

    @Query("SELECT * FROM fitness_activities ORDER BY date DESC")
    LiveData<List<FitnessActivity>> getAllActivities();

    @Query("SELECT SUM(caloriesBurned) FROM fitness_activities WHERE date >= :since")
    LiveData<Integer> getTotalCaloriesSince(long since);

    @Query("SELECT SUM(durationMinutes) FROM fitness_activities WHERE date >= :since")
    LiveData<Integer> getTotalDurationSince(long since);
}
