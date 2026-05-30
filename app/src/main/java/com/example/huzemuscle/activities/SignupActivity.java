package com.example.huzemuscle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.R;
import com.example.huzemuscle.databinding.ActivitySignupBinding;
import com.example.huzemuscle.fragments.registration.AccountStepFragment;
import com.example.huzemuscle.fragments.registration.NameStepFragment;
import com.example.huzemuscle.fragments.registration.PhysicalInfoStepFragment;
import com.example.huzemuscle.models.User;
import com.example.huzemuscle.viewmodels.RegistrationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private RegistrationViewModel viewModel;
    private int currentStep = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {
            if (viewModel.name.getValue() == null || viewModel.name.getValue().isEmpty()) {
                viewModel.name.setValue(mAuth.getCurrentUser().getDisplayName());
            }
            if (viewModel.email.getValue() == null || viewModel.email.getValue().isEmpty()) {
                viewModel.email.setValue(mAuth.getCurrentUser().getEmail());
            }
        }

        if (savedInstanceState == null) {
            showStep(new NameStepFragment(), false);
            updateProgressBar();
        }
    }

    public void nextStep() {
        switch (currentStep) {
            case 1:
                currentStep = 2;
                showStep(new PhysicalInfoStepFragment(), true);
                break;
            case 2:
                currentStep = 3;
                showStep(new AccountStepFragment(), true);
                break;
        }
        updateProgressBar();
    }

    private void showStep(Fragment fragment, boolean animate) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (animate) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        }
        ft.replace(R.id.fragment_container, fragment);
        if (animate) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void updateProgressBar() {
        int colorPrimary = ContextCompat.getColor(this, R.color.primary_red);
        int colorDivider = ContextCompat.getColor(this, R.color.divider);

        binding.viewProgress1.setBackgroundColor(currentStep >= 1 ? colorPrimary : colorDivider);
        binding.viewProgress2.setBackgroundColor(currentStep >= 2 ? colorPrimary : colorDivider);
        binding.viewProgress3.setBackgroundColor(currentStep >= 3 ? colorPrimary : colorDivider);

        // Update header text
        switch (currentStep) {
            case 1:
                binding.tvHeader.setText(R.string.signup);
                break;
            case 2:
                binding.tvHeader.setText(R.string.physical_info_header);
                break;
            case 3:
                binding.tvHeader.setText(R.string.create_account_header);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            currentStep--;
            updateProgressBar();
        } else {
            super.onBackPressed();
        }
    }

    public void completeRegistration() {
        if (mAuth.getCurrentUser() != null) {
            binding.progressBar.setVisibility(android.view.View.VISIBLE);
            binding.fragmentContainer.setVisibility(android.view.View.GONE);
            saveUserData();
            return;
        }

        String email = viewModel.email.getValue();
        String password = viewModel.password.getValue();

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(android.view.View.VISIBLE);
        binding.fragmentContainer.setVisibility(android.view.View.GONE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        saveUserData();
                    } else {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.fragmentContainer.setVisibility(android.view.View.VISIBLE);
                        Toast.makeText(this, "Registration Failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData() {
        if (mAuth.getCurrentUser() == null) {
            binding.progressBar.setVisibility(android.view.View.GONE);
            binding.fragmentContainer.setVisibility(android.view.View.VISIBLE);
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        String name = viewModel.name.getValue();
        String weight = viewModel.weight.getValue();
        String height = viewModel.height.getValue();
        String gender = viewModel.gender.getValue();
        String goal = viewModel.goal.getValue();

        User user = new User(uid, name, mAuth.getCurrentUser().getEmail());
        if (weight != null && !weight.isEmpty()) {
            try {
                user.setWeightKg(Double.parseDouble(weight));
            } catch (NumberFormatException e) {
                user.setWeightKg(0);
            }
        }
        if (height != null && !height.isEmpty()) {
            try {
                user.setHeightCm(Double.parseDouble(height));
            } catch (NumberFormatException e) {
                user.setHeightCm(0);
            }
        }
        user.setGender(gender != null ? gender : "");
        user.setGoal(goal != null ? goal : "");

        mDatabase.child("users").child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(android.view.View.GONE);
                    binding.fragmentContainer.setVisibility(android.view.View.VISIBLE);
                    Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
