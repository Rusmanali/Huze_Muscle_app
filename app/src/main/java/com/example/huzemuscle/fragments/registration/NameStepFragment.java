package com.example.huzemuscle.fragments.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.activities.SignupActivity;
import com.example.huzemuscle.databinding.FragmentRegistrationNameBinding;
import com.example.huzemuscle.viewmodels.RegistrationViewModel;

public class NameStepFragment extends Fragment {

    private FragmentRegistrationNameBinding binding;
    private RegistrationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationNameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        binding.etFirstName.setText(viewModel.name.getValue());

        binding.btnNext.setOnClickListener(v -> {
            String name = binding.etFirstName.getText() != null ? binding.etFirstName.getText().toString().trim() : "";
            if (!name.isEmpty()) {
                viewModel.name.setValue(name);
                ((SignupActivity) requireActivity()).nextStep();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
