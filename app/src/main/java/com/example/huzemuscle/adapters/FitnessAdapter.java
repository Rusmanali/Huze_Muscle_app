package com.example.huzemuscle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huzemuscle.database.FitnessActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FitnessAdapter extends ListAdapter<FitnessActivity, FitnessAdapter.FitnessViewHolder> {

    public FitnessAdapter() {
        super(new DiffUtil.ItemCallback<FitnessActivity>() {
            @Override
            public boolean areItemsTheSame(@NonNull FitnessActivity oldItem, @NonNull FitnessActivity newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull FitnessActivity oldItem, @NonNull FitnessActivity newItem) {
                return oldItem.getType().equals(newItem.getType()) &&
                        oldItem.getCaloriesBurned() == newItem.getCaloriesBurned() &&
                        oldItem.getDurationMinutes() == newItem.getDurationMinutes();
            }
        });
    }

    @NonNull
    @Override
    public FitnessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new FitnessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessViewHolder holder, int position) {
        FitnessActivity activity = getItem(position);
        holder.tvType.setText(activity.getType() + " - " + activity.getDurationMinutes() + " mins");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        holder.tvDetails.setText(activity.getCaloriesBurned() + " kcal | " + sdf.format(activity.getDate()));
    }

    static class FitnessViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDetails;

        public FitnessViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(android.R.id.text1);
            tvDetails = itemView.findViewById(android.R.id.text2);
        }
    }
}
