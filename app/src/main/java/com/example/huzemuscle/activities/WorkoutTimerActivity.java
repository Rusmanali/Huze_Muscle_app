package com.example.huzemuscle.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.database.FitnessActivity;
import com.example.huzemuscle.databinding.ActivityWorkoutTimerBinding;
import com.example.huzemuscle.viewmodels.FitnessViewModel;

import java.util.Locale;

public class WorkoutTimerActivity extends AppCompatActivity {

    private ActivityWorkoutTimerBinding binding;
    private final Handler handler = new Handler();
    private boolean isRunning = false;
    private int seconds = 0;
    private FitnessViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkoutTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FitnessViewModel.class);

        binding.btnStartStop.setOnClickListener(v -> {
            if (isRunning) {
                stopTimer();
            } else {
                startTimer();
            }
        });

        binding.btnFinishWorkout.setOnClickListener(v -> finishWorkout());
    }

    private void startTimer() {
        isRunning = true;
        binding.btnStartStop.setText("Stop");
        binding.btnFinishWorkout.setVisibility(View.GONE);
        handler.post(runnable);
    }

    private void stopTimer() {
        isRunning = false;
        binding.btnStartStop.setText("Resume");
        binding.btnFinishWorkout.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int hrs = seconds / 3600;
            int mins = (seconds % 3600) / 60;
            int secs = seconds % 60;

            String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs);
            binding.tvTimer.setText(time);

            // Rough calorie calculation (e.g., 5 kcal per minute)
            int cals = (seconds / 60) * 5;
            binding.tvCaloriesBurnedLive.setText("Calories: " + cals + " kcal");

            if (isRunning) {
                seconds++;
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void finishWorkout() {
        int durationMins = seconds / 60;
        int calories = durationMins * 5;
        
        FitnessActivity activity = new FitnessActivity("Manual Workout", durationMins, calories, System.currentTimeMillis(), "Live tracked");
        viewModel.insert(activity);
        finish();
    }
}
