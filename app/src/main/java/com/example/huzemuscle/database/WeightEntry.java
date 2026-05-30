package com.example.huzemuscle.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weight_history")
public class WeightEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private float weight;
    private long date;

    public WeightEntry() {} // Required for Firebase

    public WeightEntry(float weight, long date) {
        this.weight = weight;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
