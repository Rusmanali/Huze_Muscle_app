package com.example.huzemuscle.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huzemuscle.databinding.ActivityGoalSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class GoalSettingsActivity extends AppCompatActivity {

    private ActivityGoalSettingsBinding binding;
    private DatabaseReference mDatabase;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getUid();

        loadCurrentGoals();

        binding.btnSaveGoals.setOnClickListener(v -> saveGoals());
    }

    private void loadCurrentGoals() {
        if (uid == null) return;
        mDatabase.child("users").child(uid).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Long steps = dataSnapshot.child("dailyStepGoal").getValue(Long.class);
                Long calories = dataSnapshot.child("dailyCalorieGoal").getValue(Long.class);
                Double water = dataSnapshot.child("dailyWaterGoal").getValue(Double.class);

                if (steps != null) binding.etStepGoal.setText(String.valueOf(steps));
                if (calories != null) binding.etCalorieGoal.setText(String.valueOf(calories));
                if (water != null) binding.etWaterGoal.setText(String.valueOf(water));
            }
        });
    }

    private void saveGoals() {
        String stepsStr = binding.etStepGoal.getText() != null ? binding.etStepGoal.getText().toString().trim() : "";
        String caloriesStr = binding.etCalorieGoal.getText() != null ? binding.etCalorieGoal.getText().toString().trim() : "";
        String waterStr = binding.etWaterGoal.getText() != null ? binding.etWaterGoal.getText().toString().trim() : "";

        if (stepsStr.isEmpty() || caloriesStr.isEmpty() || waterStr.isEmpty()) {
            Toast.makeText(this, "Please fill all goals", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("dailyStepGoal", Integer.parseInt(stepsStr));
        updates.put("dailyCalorieGoal", Integer.parseInt(caloriesStr));
        updates.put("dailyWaterGoal", Double.parseDouble(waterStr));

        mDatabase.child("users").child(uid).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Goals updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
