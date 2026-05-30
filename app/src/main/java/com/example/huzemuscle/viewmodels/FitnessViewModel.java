package com.example.huzemuscle.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.huzemuscle.database.AppDatabase;
import com.example.huzemuscle.database.FitnessActivity;
import com.example.huzemuscle.database.FitnessDao;
import com.example.huzemuscle.database.WaterDao;
import com.example.huzemuscle.database.WaterIntake;
import com.example.huzemuscle.database.WeightDao;
import com.example.huzemuscle.database.WeightEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FitnessViewModel extends AndroidViewModel {
    private final FitnessDao fitnessDao;
    private final WaterDao waterDao;
    private final WeightDao weightDao;
    private final LiveData<List<FitnessActivity>> allActivities;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DatabaseReference mDatabase;
    private final String userId;

    public FitnessViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        fitnessDao = db.fitnessDao();
        waterDao = db.waterDao();
        weightDao = db.weightDao();
        allActivities = fitnessDao.getAllActivities();
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getUid();
        
        if (userId != null) {
            syncDataFromFirebase();
        }
    }

    private void syncDataFromFirebase() {
        // Real-time sync for Water
        mDatabase.child("users").child(userId).child("water_intake").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                WaterIntake intake = snapshot.getValue(WaterIntake.class);
                if (intake != null) {
                    executorService.execute(() -> {
                        // Minimal check for duplicates by date
                        if (waterDao.getTotalWaterForDay(intake.getDate(), intake.getDate() + 1).getValue() == null) {
                             waterDao.insert(intake);
                        }
                    });
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Real-time sync for Activities
        mDatabase.child("users").child(userId).child("activities").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                FitnessActivity activity = snapshot.getValue(FitnessActivity.class);
                if (activity != null) {
                    executorService.execute(() -> fitnessDao.insert(activity));
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public LiveData<List<FitnessActivity>> getAllActivities() {
        return allActivities;
    }

    public void insert(FitnessActivity activity) {
        executorService.execute(() -> fitnessDao.insert(activity));
        if (userId != null) {
            mDatabase.child("users").child(userId).child("activities").push().setValue(activity);
        }
    }

    public LiveData<Integer> getTotalCaloriesSince(long since) {
        return fitnessDao.getTotalCaloriesSince(since);
    }

    public LiveData<Integer> getTotalDurationSince(long since) {
        return fitnessDao.getTotalDurationSince(since);
    }

    public void addWater(float amount) {
        WaterIntake intake = new WaterIntake(amount, System.currentTimeMillis());
        executorService.execute(() -> waterDao.insert(intake));
        if (userId != null) {
            mDatabase.child("users").child(userId).child("water_intake").push().setValue(intake);
        }
    }

    public LiveData<Float> getDailyWater(long startOfDay, long endOfDay) {
        return waterDao.getTotalWaterForDay(startOfDay, endOfDay);
    }

    public void addWeight(float weight) {
        WeightEntry entry = new WeightEntry(weight, System.currentTimeMillis());
        executorService.execute(() -> weightDao.insert(entry));
        if (userId != null) {
            mDatabase.child("users").child(userId).child("weight_history").push().setValue(entry);
            mDatabase.child("users").child(userId).child("weightKg").setValue((double) weight);
        }
    }

    public void syncSteps(int steps) {
        if (userId != null) {
            mDatabase.child("users").child(userId).child("daily_steps").setValue(steps);
        }
    }

    public LiveData<List<WeightEntry>> getAllWeights() {
        return weightDao.getAllWeightEntries();
    }
}
