package com.example.huzemuscle.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeightDao {
    @Insert
    void insert(WeightEntry entry);

    @Query("SELECT * FROM weight_history ORDER BY date ASC")
    LiveData<List<WeightEntry>> getAllWeightEntries();
}
