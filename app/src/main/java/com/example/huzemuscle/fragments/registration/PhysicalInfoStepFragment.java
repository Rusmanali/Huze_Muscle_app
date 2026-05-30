package com.example.huzemuscle.fragments.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.activities.SignupActivity;
import com.example.huzemuscle.databinding.FragmentRegistrationPhysicalBinding;
import com.example.huzemuscle.viewmodels.RegistrationViewModel;

public class PhysicalInfoStepFragment extends Fragment {

    private FragmentRegistrationPhysicalBinding binding;
    private RegistrationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationPhysicalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        // Pre-fill if already set
        if (viewModel.weight.getValue() != null && !viewModel.weight.getValue().isEmpty()) {
            binding.etWeight.setText(viewModel.weight.getValue());
        }

        binding.btnNextPhysical.setOnClickListener(v -> {
            String weight = binding.etWeight.getText() != null ? binding.etWeight.getText().toString().trim() : "";
            int genderId = binding.rgGender.getCheckedRadioButtonId();
            int goalId = binding.rgGoal.getCheckedRadioButtonId();

            if (!weight.isEmpty() && genderId != -1 && goalId != -1) {
                String gender = ((RadioButton) view.findViewById(genderId)).getText().toString();
                String goal = ((RadioButton) view.findViewById(goalId)).getText().toString();

                viewModel.weight.setValue(weight);
                viewModel.gender.setValue(gender);
                viewModel.goal.setValue(goal);

                ((SignupActivity) requireActivity()).nextStep();
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
