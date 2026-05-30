package com.example.huzemuscle.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.R;
import com.example.huzemuscle.databinding.ActivityMainBinding;
import com.example.huzemuscle.fragments.AddActivityFragment;
import com.example.huzemuscle.fragments.ExerciseFragment;
import com.example.huzemuscle.fragments.HomeFragment;
import com.example.huzemuscle.fragments.ProfileFragment;
import com.example.huzemuscle.fragments.ProgressFragment;
import com.example.huzemuscle.fragments.TimerFragment;
import com.example.huzemuscle.utils.ReminderReceiver;
import com.example.huzemuscle.viewmodels.FitnessViewModel;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private final MutableLiveData<Integer> stepCountData = new MutableLiveData<>(0);
    private FitnessViewModel fitnessViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fitnessViewModel = new ViewModelProvider(this).get(FitnessViewModel.class);

        setupNavigation();
        setupStepCounter();
        setupReminders();
        preFillData();

        binding.fabAddActivity.setOnClickListener(v -> loadFragment(new AddActivityFragment()));

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void setupReminders() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        long interval = 4 * 60 * 60 * 1000; // Every 4 hours
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
        }
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_exercise) {
                selectedFragment = new ExerciseFragment();
            } else if (itemId == R.id.nav_timer) {
                selectedFragment = new TimerFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_progress) {
                selectedFragment = new ProgressFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setupStepCounter() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            Toast.makeText(this, "Step counter sensor not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];
            stepCountData.setValue(totalSteps);
            fitnessViewModel.syncSteps(totalSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void preFillData() {
        com.example.huzemuscle.database.AppDatabase db = com.example.huzemuscle.database.AppDatabase.getDatabase(this);
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            if (db.weightDao().getAllWeightEntries().getValue() == null || db.weightDao().getAllWeightEntries().getValue().isEmpty()) {
                db.weightDao().insert(new com.example.huzemuscle.database.WeightEntry(75.5f, System.currentTimeMillis() - 86400000L * 3));
                db.weightDao().insert(new com.example.huzemuscle.database.WeightEntry(74.8f, System.currentTimeMillis() - 86400000L * 2));
                db.weightDao().insert(new com.example.huzemuscle.database.WeightEntry(74.2f, System.currentTimeMillis()));
            }
        });
    }

    public void setSelectedTab(int itemId) {
        binding.bottomNavigation.setSelectedItemId(itemId);
    }

    public MutableLiveData<Integer> getStepCountData() {
        return stepCountData;
    }
}
