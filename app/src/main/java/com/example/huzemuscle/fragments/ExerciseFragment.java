package com.example.huzemuscle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.huzemuscle.adapters.ExerciseAdapter;
import com.example.huzemuscle.databinding.FragmentExerciseBinding;
import com.example.huzemuscle.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFragment extends Fragment {

    private FragmentExerciseBinding binding;
    private ExerciseAdapter adapter;
    private List<Exercise> allExercises;
    private String currentCategory = "All";
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeData();
        setupRecyclerView();
        setupCategorySelection();
        setupSearch();
    }

    private void initializeData() {
        allExercises = new ArrayList<>();
        // Arms
        allExercises.add(new Exercise("Bicep Curls", "Arms", "Build peaked biceps with controlled dumbbell curls.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Tricep Dips", "Arms", "Focus on triceps using your body weight or a bench.", android.R.drawable.ic_menu_directions));
        
        // Legs
        allExercises.add(new Exercise("Squats", "Legs", "The king of leg exercises for overall lower body strength.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Lunges", "Legs", "Improve balance and target glutes and quads.", android.R.drawable.ic_menu_directions));
        
        // Chest
        allExercises.add(new Exercise("Push Ups", "Chest", "Standard push ups for building chest and triceps strength.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Chest Press", "Chest", "Using dumbbells or a barbell to target major chest muscles.", android.R.drawable.ic_menu_directions));
        
        // Back
        allExercises.add(new Exercise("Pull Ups", "Back", "Wide grip pulls to widen your lats and build a V-taper.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Dumbbell Rows", "Back", "Isolate each side of your back for thickness.", android.R.drawable.ic_menu_directions));
        
        // Shoulders
        allExercises.add(new Exercise("Overhead Press", "Shoulders", "Build boulder shoulders with this compound movement.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Lateral Raises", "Shoulders", "Specifically targets the side delts for width.", android.R.drawable.ic_menu_directions));
        
        // Core
        allExercises.add(new Exercise("Plank", "Core", "Isometric hold to strengthen your entire core stability.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Crunches", "Core", "Focus on the upper abs for definition.", android.R.drawable.ic_menu_directions));
        
        // Upper Body / Lower Body can overlap or be separate categories
        allExercises.add(new Exercise("Deadlift", "Lower Body", "Power movement for hamstrings, glutes, and lower back.", android.R.drawable.ic_menu_directions));
        allExercises.add(new Exercise("Burpees", "Upper Body", "Full body explosive movement for cardio and power.", android.R.drawable.ic_menu_directions));
    }

    private void setupRecyclerView() {
        adapter = new ExerciseAdapter(new ArrayList<>(allExercises));
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvExercises.setAdapter(adapter);
    }

    private void setupCategorySelection() {
        binding.cgCategories.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.chipArms.getId()) currentCategory = "Arms";
            else if (checkedId == binding.chipLegs.getId()) currentCategory = "Legs";
            else if (checkedId == binding.chipChest.getId()) currentCategory = "Chest";
            else if (checkedId == binding.chipBack.getId()) currentCategory = "Back";
            else if (checkedId == binding.chipShoulders.getId()) currentCategory = "Shoulders";
            else if (checkedId == binding.chipCore.getId()) currentCategory = "Core";
            else if (checkedId == binding.chipUpperBody.getId()) currentCategory = "Upper Body";
            else if (checkedId == binding.chipLowerBody.getId()) currentCategory = "Lower Body";
            else currentCategory = "All";
            
            applyFilters();
        });
    }

    private void setupSearch() {
        binding.searchExercise.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                applyFilters();
                return true;
            }
        });
    }

    private void applyFilters() {
        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            boolean matchesCategory = currentCategory.equals("All") || exercise.getCategory().equalsIgnoreCase(currentCategory);
            boolean matchesQuery = currentQuery.isEmpty() || exercise.getName().toLowerCase().contains(currentQuery.toLowerCase());
            
            if (matchesCategory && matchesQuery) {
                filteredList.add(exercise);
            }
        }
        adapter.setExercises(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}