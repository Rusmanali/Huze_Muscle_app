package com.example.huzemuscle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.R;
import com.example.huzemuscle.activities.MainActivity;
import com.example.huzemuscle.utils.BmiCalculator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.example.huzemuscle.databinding.FragmentProgressBinding;
import com.example.huzemuscle.viewmodels.FitnessViewModel;

import java.util.Calendar;
import java.util.Locale;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;
    private FitnessViewModel viewModel;
    private int stepGoal = 10000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);

        setupStepsTracker();
        setupHydrationTracker();
        setupWeightTracker();
        setupBmiTool();
    }

    private void setupStepsTracker() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getStepCountData().observe(getViewLifecycleOwner(), steps -> {
                binding.tvStepsValue.setText(String.valueOf(steps));
                binding.pbStepsCircular.setMax(stepGoal);
                binding.pbStepsCircular.setProgress(steps);
            });
        }
    }

    private void setupHydrationTracker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfToday = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endOfToday = calendar.getTimeInMillis();

        viewModel.getDailyWater(startOfToday, endOfToday).observe(getViewLifecycleOwner(), this::updateWaterUI);

        binding.btnAddWaterProgress.setOnClickListener(v -> {
            viewModel.addWater(0.25f);
            Toast.makeText(getContext(), "Glass added!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateWaterUI(Float waterLiters) {
        float liters = (waterLiters != null) ? waterLiters : 0.0f;
        int glasses = (int) (liters / 0.25f);
        binding.tvWaterStatsProgress.setText(glasses + "/10 Glasses");

        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_red);
        int dividerColor = ContextCompat.getColor(requireContext(), R.color.divider);

        for (int i = 0; i < binding.glGlassesProgress.getChildCount(); i++) {
            if (binding.glGlassesProgress.getChildAt(i) instanceof ImageView) {
                ImageView glass = (ImageView) binding.glGlassesProgress.getChildAt(i);
                if (i < glasses) {
                    glass.setColorFilter(primaryColor);
                } else {
                    glass.setColorFilter(dividerColor);
                }
            }
        }
    }

    private void setupWeightTracker() {
        viewModel.getAllWeights().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                float lastWeight = entries.get(entries.size() - 1).getWeight();
                binding.etCurrentWeight.setText(String.valueOf(lastWeight));
            }
        });

        binding.btnUpdateWeight.setOnClickListener(v -> {
            String weightStr = binding.etCurrentWeight.getText().toString().trim();
            if (!weightStr.isEmpty()) {
                try {
                    float weight = Float.parseFloat(weightStr);
                    viewModel.addWeight(weight);
                    Toast.makeText(getContext(), "Weight updated!", Toast.LENGTH_SHORT).show();
                    binding.etCurrentWeight.clearFocus();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid weight", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBmiTool() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists() && binding != null) {
                            Double weight = dataSnapshot.child("weightKg").getValue(Double.class);
                            Double heightCm = dataSnapshot.child("heightCm").getValue(Double.class);
                            if (weight != null) binding.etBmiWeight.setText(String.valueOf(weight));
                            if (heightCm != null) {
                                double totalInches = heightCm / 2.54;
                                int feet = (int) (totalInches / 12);
                                int inches = (int) Math.round(totalInches % 12);
                                binding.etBmiHeightFt.setText(String.valueOf(feet));
                                binding.etBmiHeightIn.setText(String.valueOf(inches));
                            }
                        }
                    });
        }

        binding.btnCalculateBmi.setOnClickListener(v -> {
            String weightStr = binding.etBmiWeight.getText().toString().trim();
            String heightFtStr = binding.etBmiHeightFt.getText().toString().trim();
            String heightInStr = binding.etBmiHeightIn.getText().toString().trim();

            if (!weightStr.isEmpty() && !heightFtStr.isEmpty()) {
                try {
                    double weight = Double.parseDouble(weightStr);
                    double ft = Double.parseDouble(heightFtStr);
                    double in = heightInStr.isEmpty() ? 0 : Double.parseDouble(heightInStr);
                    double heightCm = (ft * 12 + in) * 2.54;
                    
                    double bmi = BmiCalculator.calculateBmi(weight, heightCm);
                    String category = BmiCalculator.getBmiCategory(bmi);
                    
                    binding.tvBmiResult.setText(String.format(Locale.getDefault(), "BMI: %.1f (%s)", bmi, category));
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter both height and weight", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
