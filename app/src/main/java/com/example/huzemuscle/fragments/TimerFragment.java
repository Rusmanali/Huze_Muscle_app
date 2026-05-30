package com.example.huzemuscle.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.huzemuscle.databinding.FragmentTimerBinding;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private FragmentTimerBinding binding;

    // Stopwatch variables
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean isStopwatchRunning = false;

    // Timer variables
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 0L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStopwatch();
        setupCountdownTimer();
    }

    private void setupStopwatch() {
        binding.btnStopwatchStart.setOnClickListener(v -> {
            if (!isStopwatchRunning) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                isStopwatchRunning = true;
                binding.btnStopwatchStart.setText("Pause");
            } else {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                isStopwatchRunning = false;
                binding.btnStopwatchStart.setText("Start");
            }
        });

        binding.btnStopwatchReset.setOnClickListener(v -> {
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            isStopwatchRunning = false;
            customHandler.removeCallbacks(updateTimerThread);
            binding.tvStopwatchDisplay.setText("00:00:00");
            binding.btnStopwatchStart.setText("Start");
        });
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            binding.tvStopwatchDisplay.setText("" + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };

    private void setupCountdownTimer() {
        binding.btnTimerAction.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        binding.btnTimerReset.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        if (timeLeftInMillis == 0) {
            String minsStr = binding.etTimerMinutes.getText().toString();
            String secsStr = binding.etTimerSeconds.getText().toString();

            int mins = minsStr.isEmpty() ? 0 : Integer.parseInt(minsStr);
            int secs = secsStr.isEmpty() ? 0 : Integer.parseInt(secsStr);

            timeLeftInMillis = (mins * 60 + secs) * 1000L;
        }

        if (timeLeftInMillis <= 0) {
            Toast.makeText(getContext(), "Please enter a valid time", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.llTimerInput.setVisibility(View.GONE);
        binding.tvTimerDisplay.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                binding.btnTimerAction.setText("Start");
                Toast.makeText(getContext(), "Time's up!", Toast.LENGTH_LONG).show();
                resetTimer();
            }
        }.start();

        isTimerRunning = true;
        binding.btnTimerAction.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        binding.btnTimerAction.setText("Start");
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        timeLeftInMillis = 0;
        binding.llTimerInput.setVisibility(View.VISIBLE);
        binding.tvTimerDisplay.setVisibility(View.GONE);
        binding.btnTimerAction.setText("Start");
        binding.etTimerMinutes.setText("");
        binding.etTimerSeconds.setText("");
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.tvTimerDisplay.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        customHandler.removeCallbacks(updateTimerThread);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
