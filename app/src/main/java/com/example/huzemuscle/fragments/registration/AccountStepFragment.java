package com.example.huzemuscle.fragments.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.activities.SignupActivity;
import com.example.huzemuscle.databinding.FragmentRegistrationAccountBinding;
import com.example.huzemuscle.viewmodels.RegistrationViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class AccountStepFragment extends Fragment {

    private FragmentRegistrationAccountBinding binding;
    private RegistrationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            binding.tilEmail.setVisibility(View.GONE);
            binding.tilPassword.setVisibility(View.GONE);
            binding.tilConfirmPassword.setVisibility(View.GONE);
            binding.tvPasswordHint.setVisibility(View.GONE);
            binding.tvStepTitle.setText("Finish Profile");
            binding.tvFooter.setText("You are signed in as " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + ". Complete your profile to get started!");
            binding.btnFinalNext.setText("Complete");
        }

        binding.btnFinalNext.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
                String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
                String confirmPassword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString().trim() : "";

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || !binding.cbTerms.isChecked()) {
                    Toast.makeText(getContext(), "Please fill all fields and agree to terms", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 10) {
                    Toast.makeText(getContext(), "Password must be at least 10 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.email.setValue(email);
                viewModel.password.setValue(password);
            } else {
                if (!binding.cbTerms.isChecked()) {
                    Toast.makeText(getContext(), "Please agree to terms", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            ((SignupActivity) requireActivity()).completeRegistration();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
