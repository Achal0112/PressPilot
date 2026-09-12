package com.example.presspilot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Login
        btnLogin.setOnClickListener(v -> loginUser());

        // Register
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );
            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Email validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            etEmail.requestFocus();
            return;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (mAuth.getCurrentUser() == null) {
                            btnLogin.setEnabled(true);

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login successful, but user not found",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        String uid = mAuth.getCurrentUser().getUid();

                        getUserRole(uid);

                    } else {

                        btnLogin.setEnabled(true);

                        String errorMessage = "Login failed";

                        if (task.getException() != null) {
                            errorMessage =
                                    "Login failed: "
                                            + task.getException().getMessage();
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void getUserRole(String uid) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    btnLogin.setEnabled(true);

                    // Check document
                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "User data not found in Firestore.\nUID: " + uid,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Get role
                    String role = documentSnapshot.getString("role");

                    if (role == null || role.trim().isEmpty()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Role is missing in Firestore.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Remove extra spaces
                    role = role.trim();

                    // Admin
                    if (role.equalsIgnoreCase("admin")) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Admin Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                DashboardActivity.class
                        );

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();

                    }

                    // Customer
                    else if (role.equalsIgnoreCase("customer")) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Customer Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                CustomerDashboardActivity.class
                        );

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();

                    }

                    // Invalid role
                    else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Invalid role: " + role,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                })
                .addOnFailureListener(e -> {

                    btnLogin.setEnabled(true);

                    Toast.makeText(
                            LoginActivity.this,
                            "Firestore error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}