package com.example.huzemuscle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.databinding.FragmentAddActivityBinding;
import com.example.huzemuscle.database.FitnessActivity;
import com.example.huzemuscle.viewmodels.FitnessViewModel;

public class AddActivityFragment extends Fragment {

    private FragmentAddActivityBinding binding;
    private FitnessViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupSpinner();
        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);

        binding.btnSaveActivity.setOnClickListener(v -> saveActivity());
    }

    private void setupSpinner() {
        String[] activityTypes = {"Running", "Walking", "Cycling", "Gym Workout", "Yoga"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, activityTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerActivityType.setAdapter(adapter);
    }

    private void saveActivity() {
        if (binding.spinnerActivityType.getSelectedItem() == null) return;
        String type = binding.spinnerActivityType.getSelectedItem().toString();
        String durationStr = binding.etDuration.getText().toString().trim();
        String caloriesStr = binding.etCalories.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();

        if (durationStr.isEmpty() || caloriesStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter duration and calories", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationStr);
        int calories = Integer.parseInt(caloriesStr);

        FitnessActivity activity = new FitnessActivity(type, duration, calories, System.currentTimeMillis(), notes);
        viewModel.insert(activity);

        Toast.makeText(getContext(), "Activity saved!", Toast.LENGTH_SHORT).show();
        
        // Reset fields
        binding.etDuration.setText("");
        binding.etCalories.setText("");
        binding.etNotes.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
