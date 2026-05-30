package com.example.huzemuscle.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FitnessActivity.class, WaterIntake.class, WeightEntry.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FitnessDao fitnessDao();
    public abstract WaterDao waterDao();
    public abstract WeightDao weightDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "fitness_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
