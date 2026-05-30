package com.example.huzemuscle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.huzemuscle.R;
import com.example.huzemuscle.databinding.ActivityWelcomeBinding;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private ActivityWelcomeBinding binding;
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        binding.btnSignInWelcome.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.stay);
        });

        binding.btnSignUpWelcome.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, SignupActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.stay);
        });

        binding.btnGoogleWelcome.setOnClickListener(v -> signInWithGoogle());

        binding.btnPhoneWelcome.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, PhoneAuthActivity.class));
        });

        binding.btnFacebookWelcome.setOnClickListener(v -> {
            Toast.makeText(this, "Facebook login coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle: Clicked");
        try {
            String serverClientId = getString(R.string.default_web_client_id);
            Log.d(TAG, "Using Server Client ID: " + serverClientId);

            binding.progressBar.setVisibility(android.view.View.VISIBLE);

            // Using a nonce can help trigger the picker on some devices
            String nonce = java.util.UUID.randomUUID().toString();

            GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setNonce(nonce)
                    .setAutoSelectEnabled(false)
                    .build();

            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build();

            credentialManager.getCredentialAsync(this, request, null, Runnable::run, new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                @Override
                public void onResult(GetCredentialResponse result) {
                    runOnUiThread(() -> binding.progressBar.setVisibility(android.view.View.GONE));
                    Credential credential = result.getCredential();
                    
                    if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                        try {
                            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                            String idToken = googleIdTokenCredential.getIdToken();
                            firebaseAuthWithGoogle(idToken);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing Google ID Token: " + e.getMessage());
                        }
                    } else if (credential instanceof GoogleIdTokenCredential) {
                        GoogleIdTokenCredential googleIdTokenCredential = (GoogleIdTokenCredential) credential;
                        String idToken = googleIdTokenCredential.getIdToken();
                        firebaseAuthWithGoogle(idToken);
                    } else {
                        Log.e(TAG, "Unexpected credential type: " + credential.getType());
                    }
                }

                @Override
                public void onError(@NonNull GetCredentialException e) {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        Log.e(TAG, "Credential Manager Error: " + e.getMessage(), e);
                        if (e instanceof androidx.credentials.exceptions.GetCredentialCancellationException) {
                            Toast.makeText(WelcomeActivity.this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof androidx.credentials.exceptions.NoCredentialException) {
                            Toast.makeText(WelcomeActivity.this, "No accounts found", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Google Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            binding.progressBar.setVisibility(android.view.View.GONE);
            Log.e(TAG, "Exception in signInWithGoogle", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        runOnUiThread(() -> binding.progressBar.setVisibility(android.view.View.VISIBLE));
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserInDatabase();
                    } else {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        Toast.makeText(WelcomeActivity.this, "Firebase Auth Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInDatabase() {
        if (mAuth.getCurrentUser() == null) {
            binding.progressBar.setVisibility(android.view.View.GONE);
            startActivity(new Intent(WelcomeActivity.this, SignupActivity.class));
            finish();
            return;
        }
        
        // After successful Google sign-in, we prefer taking the user to Home.
        // If they are brand new, Home will show default values, but they won't be stuck in setup.
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finishAffinity();
    }
}
