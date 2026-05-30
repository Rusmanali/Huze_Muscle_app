package com.example.huzemuscle.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private double heightCm;
    private double weightKg;
    private String gender;
    private String goal; // "Bulk", "Lose Weight", "Maintain"
    private int dailyStepGoal;
    private int dailyCalorieGoal;

    public User() {}

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.dailyStepGoal = 10000;
        this.dailyCalorieGoal = 2000;
        this.weightKg = 0;
        this.gender = "";
        this.goal = "";
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public int getDailyStepGoal() { return dailyStepGoal; }
    public void setDailyStepGoal(int dailyStepGoal) { this.dailyStepGoal = dailyStepGoal; }
    public int getDailyCalorieGoal() { return dailyCalorieGoal; }
    public void setDailyCalorieGoal(int dailyCalorieGoal) { this.dailyCalorieGoal = dailyCalorieGoal; }
}
