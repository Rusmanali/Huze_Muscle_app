package com.example.huzemuscle.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WaterDao {
    @Insert
    void insert(WaterIntake intake);

    @Query("SELECT SUM(amountLiters) FROM water_intake WHERE date >= :startOfDay AND date <= :endOfDay")
    LiveData<Float> getTotalWaterForDay(long startOfDay, long endOfDay);
}
