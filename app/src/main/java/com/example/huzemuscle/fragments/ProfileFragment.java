package com.example.huzemuscle.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huzemuscle.activities.GoalSettingsActivity;
import com.example.huzemuscle.activities.LoginActivity;
import com.example.huzemuscle.databinding.FragmentProfileBinding;
import com.example.huzemuscle.models.User;
import com.example.huzemuscle.utils.BmiCalculator;
import com.example.huzemuscle.viewmodels.FitnessViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FitnessViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);

        if (mAuth.getCurrentUser() != null) {
            loadUserProfile(mAuth.getCurrentUser().getUid());
        }

        binding.btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GoalSettingsActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        binding.btnUpdateWeight.setOnClickListener(v -> {
            if (binding.etNewWeight.getText() == null) return;
            String wStr = binding.etNewWeight.getText().toString().trim();
            if (!wStr.isEmpty()) {
                float weight = Float.parseFloat(wStr);
                viewModel.addWeight(weight);
                binding.etNewWeight.setText("");
                Toast.makeText(getContext(), "Weight logged", Toast.LENGTH_SHORT).show();
                
                // Update Database too
                if (mAuth.getUid() != null) {
                    mDatabase.child("users").child(mAuth.getUid()).child("weightKg").setValue((double)weight);
                }
            } else {
                Toast.makeText(getContext(), "Enter weight", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile(String uid) {
        mDatabase.child("users").child(uid).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && binding != null) {
                    binding.tvProfileName.setText("Name: " + user.getName());
                    binding.tvProfileEmail.setText("Email: " + user.getEmail());
                    binding.tvProfileStats.setText(String.format(Locale.getDefault(), "Weight: %.1f kg | Height: %.1f cm", 
                            user.getWeightKg(), user.getHeightCm()));
                    
                    double bmi = BmiCalculator.calculateBmi(user.getWeightKg(), user.getHeightCm());
                    String category = BmiCalculator.getBmiCategory(bmi);
                    binding.tvProfileBmi.setText(String.format(Locale.getDefault(), "BMI: %.1f (%s)", bmi, category));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
