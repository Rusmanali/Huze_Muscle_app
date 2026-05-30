package com.example.huzemuscle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huzemuscle.R;
import com.example.huzemuscle.databinding.ActivityPhoneAuthBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private ActivityPhoneAuthBinding binding;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private boolean isOtpSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        binding.btnAction.setOnClickListener(v -> {
            if (!isOtpSent) {
                sendOtp();
            } else {
                verifyOtp();
            }
        });

        binding.tvResend.setOnClickListener(v -> {
            if (isOtpSent) {
                sendOtp();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isOtpSent) {
            isOtpSent = false;
            binding.viewSwitcher.setInAnimation(this, android.R.anim.fade_in);
            binding.viewSwitcher.setOutAnimation(this, android.R.anim.fade_out);
            binding.viewSwitcher.showPrevious();
            binding.btnAction.setText(R.string.send_otp);
        } else {
            super.onBackPressed();
        }
    }

    private void sendOtp() {
        String phoneNumber = binding.etPhone.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phoneNumber.startsWith("+")) {
            Toast.makeText(this, "Please include country code starting with +", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnAction.setEnabled(false);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp() {
        String code = binding.etOtp.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnAction.setEnabled(false);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnAction.setEnabled(true);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnAction.setEnabled(true);
            
            String errorMessage = e.getMessage();
            if (e.getMessage() != null && e.getMessage().contains("App validation failed")) {
                errorMessage = "SafetyNet/App Check failed. Ensure SHA-256 is in Firebase Console.";
            } else if (e.getMessage() != null && e.getMessage().contains("format is invalid")) {
                errorMessage = "Invalid phone format. Please include country code (e.g., +1).";
            }
            
            android.util.Log.e("PhoneAuth", "Verification Failed: ", e);
            Toast.makeText(PhoneAuthActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnAction.setEnabled(true);
            verificationId = s;
            resendToken = token;
            
            if (!isOtpSent) {
                isOtpSent = true;
                binding.viewSwitcher.setInAnimation(PhoneAuthActivity.this, android.R.anim.fade_in);
                binding.viewSwitcher.setOutAnimation(PhoneAuthActivity.this, android.R.anim.fade_out);
                binding.viewSwitcher.showNext();
                binding.btnAction.setText(R.string.verify_otp);
            }

            Toast.makeText(PhoneAuthActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserInDatabase();
                    } else {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAction.setEnabled(true);
                        Toast.makeText(PhoneAuthActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInDatabase() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(PhoneAuthActivity.this, SignupActivity.class));
            finish();
            return;
        }
        
        // After successful Phone sign-in, take the user directly to Home
        startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
        finishAffinity();
    }
}
