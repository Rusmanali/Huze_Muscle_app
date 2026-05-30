package com.example.huzemuscle.models;

public class Exercise {
    private String name;
    private String category;
    private String description;
    private int imageResId; // Using resource IDs for now

    public Exercise(String name, String category, String description, int imageResId) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}
