package com.example.presspilot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etMobile;
    private TextInputEditText etAddress;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private RadioGroup rgRole;
    private RadioButton rbAdmin;
    private RadioButton rbCustomer;

    private Button btnRegister;
    private TextView tvLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        rgRole = findViewById(R.id.rgRole);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbCustomer = findViewById(R.id.rbCustomer);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Default role
        rbCustomer.setChecked(true);

        // Register button
        btnRegister.setOnClickListener(v -> registerUser());

        // Login link
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword =
                etConfirmPassword.getText().toString().trim();

        // Get selected role
        int selectedRoleId = rgRole.getCheckedRadioButtonId();

        if (selectedRoleId == -1) {
            Toast.makeText(
                    this,
                    "Please select a role",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        RadioButton selectedRadioButton =
                findViewById(selectedRoleId);

        String role =
                selectedRadioButton.getText().toString();

        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Enter full name");
            etFullName.requestFocus();
            return;
        }

        if (fullName.length() < 3) {
            etFullName.setError("Name must contain at least 3 characters");
            etFullName.requestFocus();
            return;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Validate mobile
        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Enter mobile number");
            etMobile.requestFocus();
            return;
        }

        if (!mobile.matches("^[0-9]{10}$")) {
            etMobile.setError(
                    "Enter a valid 10-digit mobile number"
            );
            etMobile.requestFocus();
            return;
        }

        // Validate address
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Enter address");
            etAddress.requestFocus();
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError(
                    "Password must contain at least 6 characters"
            );
            etPassword.requestFocus();
            return;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError(
                    "Confirm your password"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Passwords do not match"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // Disable button to prevent multiple clicks
        btnRegister.setEnabled(false);

        // Create Firebase Authentication account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid =
                                mAuth.getCurrentUser().getUid();

                        saveUserToFirestore(
                                uid,
                                fullName,
                                email,
                                mobile,
                                address,
                                role
                        );

                    } else {

                        btnRegister.setEnabled(true);

                        String errorMessage;

                        if (task.getException() != null) {
                            errorMessage =
                                    task.getException().getMessage();
                        } else {
                            errorMessage =
                                    "Registration failed";
                        }

                        Toast.makeText(
                                RegisterActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void saveUserToFirestore(
            String uid,
            String fullName,
            String email,
            String mobile,
            String address,
            String role) {

        Map<String, Object> user = new HashMap<>();

        user.put("uid", uid);
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("mobile", mobile);
        user.put("address", address);
        user.put("role", role);
        user.put("createdAt",
                System.currentTimeMillis());

        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Sign out after registration
                    mAuth.signOut();

                    // Go to Login
                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    );

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {

                    btnRegister.setEnabled(true);

                    Toast.makeText(
                            RegisterActivity.this,
                            "Failed to save user: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}