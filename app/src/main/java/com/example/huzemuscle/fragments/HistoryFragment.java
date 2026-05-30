package com.example.huzemuscle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.huzemuscle.adapters.FitnessAdapter;
import com.example.huzemuscle.databinding.FragmentHistoryBinding;
import com.example.huzemuscle.viewmodels.FitnessViewModel;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private FitnessViewModel viewModel;
    private FitnessAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new FitnessAdapter();
        binding.rvHistory.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activities -> {
            adapter.submitList(activities);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
