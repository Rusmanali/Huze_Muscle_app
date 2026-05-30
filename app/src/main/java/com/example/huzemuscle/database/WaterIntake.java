package com.example.huzemuscle.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_intake")
public class WaterIntake {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private float amountLiters;
    private long date; // timestamp (start of day recommended for daily grouping)

    public WaterIntake() {} // Required for Firebase

    public WaterIntake(float amountLiters, long date) {
        this.amountLiters = amountLiters;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getAmountLiters() { return amountLiters; }
    public void setAmountLiters(float amountLiters) { this.amountLiters = amountLiters; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
