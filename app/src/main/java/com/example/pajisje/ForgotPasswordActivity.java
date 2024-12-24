package com.example.pajisje;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnRecoverPassword;
    private DB dbHelper; // Reference to DB helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword); // XML layout for ForgotPasswordActivity

        // Initialize elements
        edtEmail = findViewById(R.id.edt_email);  // EditText for email
        btnRecoverPassword = findViewById(R.id.btn_recover_password); // Button to recover password
        TextView tvGoBack = findViewById(R.id.tv_go_back); // Initialize the Go Back TextView

        // Initialize DB helper
        dbHelper = new DB(this);

        // Handle the "Go Back" click event
        tvGoBack.setOnClickListener(v -> {
            // Navigate back to MainActivity (Login Screen)
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Optional: to close ForgotPasswordActivity
        });

        // Button to recover password
        btnRecoverPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            // Validate email input
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Ju lutem shkruani adresën tuaj të email-it", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Ju lutem shkruani një adresë email-i të vlefshme", Toast.LENGTH_SHORT).show();
            } else {
                // Check if email exists in the database
                if (isEmailExist(email)) {
                    // If exists, simulate password recovery process
                    recoverPassword(email);
                } else {
                    // If email doesn't exist, show error message
                    Toast.makeText(ForgotPasswordActivity.this, "Ky email nuk është i regjistruar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to check if email exists in the database
    private boolean isEmailExist(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DB.TABLE_USER + " WHERE " + DB.COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = cursor.moveToFirst(); // If rows exist, email exists
        cursor.close();
        return exists;
    }

    // Method to simulate password recovery (for now just shows a toast)
    private void recoverPassword(String email) {
        // Simulate password recovery process
        Toast.makeText(ForgotPasswordActivity.this, "Udhëzime për rikuperimin e fjalëkalimit janë dërguar në email-in: " + email, Toast.LENGTH_LONG).show();
        // You can add logic here to send an email with a password reset link
    }
}
