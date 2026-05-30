package com.example.huzemuscle.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.fragments.ExerciseFragment;
import com.example.huzemuscle.R;
import com.example.huzemuscle.activities.MainActivity;
import com.example.huzemuscle.activities.WorkoutTimerActivity;
import com.example.huzemuscle.databinding.FragmentHomeBinding;
import com.example.huzemuscle.viewmodels.FitnessViewModel;
import com.example.huzemuscle.utils.BmiCalculator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FitnessViewModel viewModel;
    private int stepGoal = 10000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        
        loadUserData();
        observeFitnessData();
    }

    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && binding != null) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                if (name != null) {
                                    binding.tvWelcomeName.setText("Hi, " + name + "!");
                                }
                                
                                Long goal = dataSnapshot.child("dailyStepGoal").getValue(Long.class);
                                if (goal != null) stepGoal = goal.intValue();

                                Double weight = dataSnapshot.child("weightKg").getValue(Double.class);
                                Double height = dataSnapshot.child("heightCm").getValue(Double.class);
                                if (weight != null && height != null) {
                                    double bmi = BmiCalculator.calculateBmi(weight, height);
                                    String category = BmiCalculator.getBmiCategory(bmi);
                                    binding.tvBmiValue.setText(String.format(Locale.getDefault(), "BMI: %.1f (%s)", bmi, category));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    }

    private void observeFitnessData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfToday = calendar.getTimeInMillis();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endOfToday = calendar.getTimeInMillis();

        viewModel.getTotalCaloriesSince(startOfToday).observe(getViewLifecycleOwner(), total -> {
            int cal = (total != null) ? total : 0;
            binding.tvCalories.setText(cal + " kcal");
        });

        viewModel.getTotalDurationSince(startOfToday).observe(getViewLifecycleOwner(), total -> {
            int mins = (total != null) ? total : 0;
            binding.tvDuration.setText(mins + " min");
        });

        viewModel.getDailyWater(startOfToday, endOfToday).observe(getViewLifecycleOwner(), total -> {
            float water = (total != null) ? total : 0.0f;
            updateWaterUI(water);
        });

        binding.tvTrackWater.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setSelectedTab(R.id.nav_progress);
            }
        });

        binding.tvSeeAllExercises.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setSelectedTab(R.id.nav_exercise);
            }
        });
        
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getStepCountData().observe(getViewLifecycleOwner(), steps -> {
                binding.tvStepsValue.setText(String.valueOf(steps));
                binding.pbStepsCircular.setMax(stepGoal);
                binding.pbStepsCircular.setProgress(steps);
            });
        }
    }

    private void updateWaterUI(float waterLiters) {
        int glasses = (int) (waterLiters / 0.25f);
        binding.tvWaterStats.setText(glasses + "/10 Glasses");
        
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_red);
        int greyColor = ContextCompat.getColor(requireContext(), R.color.grey_text);
        
        for (int i = 0; i < binding.glGlassesProgress.getChildCount(); i++) {
            if (binding.glGlassesProgress.getChildAt(i) instanceof ImageView) {
                ImageView glass = (ImageView) binding.glGlassesProgress.getChildAt(i);
                if (i < glasses) {
                    glass.setColorFilter(primaryColor);
                } else {
                    glass.setColorFilter(greyColor);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
