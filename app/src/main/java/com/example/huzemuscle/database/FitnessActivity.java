package com.example.huzemuscle.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fitness_activities")
public class FitnessActivity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;
    private int durationMinutes;
    private int caloriesBurned;
    private long date;
    private String notes;

    public FitnessActivity() {} // Required for Firebase

    public FitnessActivity(String type, int durationMinutes, int caloriesBurned, long date, String notes) {
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.date = date;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
