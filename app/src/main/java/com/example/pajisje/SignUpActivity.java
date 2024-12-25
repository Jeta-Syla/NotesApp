package com.example.pajisje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout emailLayout, passwordLayout, confirmPasswordLayout;
    private DB database;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText); // Corrected ID
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout); // Corrected ID
        loginButton = findViewById(R.id.loginButton);

        //Butoni Login
        loginButton.setOnClickListener(v -> {
            // Navigate to MainActivity
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Close SignUpActivity if you don't want it in the back stack
        });

        // Initialize database
        database = new DB(this);

        // Set sign-up button listener
        findViewById(R.id.signUpButton).setOnClickListener(v -> validateAndSignUp());
    }

    private void validateAndSignUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Reset errors
        resetErrors();

        // Validate input fields
        if (!validateEmail(email) || !validatePassword(password) || !validateConfirmPassword(password, confirmPassword)) {
            return;
        }

        // Kontrollo nese email funksionon
        if (database.checkEmailExists(email)) {
            emailLayout.setError("Email is already registered");
            return;
        }

        // Insert user into database
        long result = database.addUser(email, password);
        if (result != -1) {
            Toast.makeText(this, "Sign up successful! Welcome " + email, Toast.LENGTH_LONG).show();
            navigateToLogin();
        } else {
            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void resetErrors() {
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    // Validate email
    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            return false;
        }
        return true;
    }

    // Validate password
    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password cannot be empty");
            return false;
        } else  if (TextUtils.isEmpty(password) ||
                !Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{6,20}$").matcher(password).matches()) {
            passwordLayout.setError("Password must contain 1 lowercase, 1 uppercase, 1 digit, 1 special character, and be 6-20 characters long.");
            return false;
        }
        return true;
    }

    // Validate confirm password
    private boolean validateConfirmPassword(String password, String confirmPassword) {
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm your password");
            return false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    //Per t'u kthyer ne Login perseri
    private void navigateToLogin() {
        // Create an Intent to navigate to MainActivity (login page)
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        // Start MainActivity
        startActivity(intent);
        // Optionally, you can call finish() to close the current activity (SignUpActivity)
        finish();
    }

}
