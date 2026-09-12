package com.example.presspilot;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerProfileActivity extends AppCompatActivity {

    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfileMobile;
    private TextView tvProfileAddress;
    private TextView tvProfileRole;

    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileMobile = findViewById(R.id.tvProfileMobile);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        tvProfileRole = findViewById(R.id.tvProfileRole);

        progressBar = findViewById(R.id.progressBar);

        loadCustomerProfile();
    }

    private void loadCustomerProfile() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        progressBar.setVisibility(ProgressBar.VISIBLE);

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    progressBar.setVisibility(ProgressBar.GONE);

                    if (documentSnapshot.exists()) {

                        showProfile(documentSnapshot);

                    } else {

                        Toast.makeText(
                                CustomerProfileActivity.this,
                                "Profile data not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(ProgressBar.GONE);

                    Toast.makeText(
                            CustomerProfileActivity.this,
                            "Failed to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showProfile(DocumentSnapshot document) {

        String fullName = document.getString("fullName");
        String email = document.getString("email");
        String mobile = document.getString("mobile");
        String address = document.getString("address");
        String role = document.getString("role");

        tvProfileName.setText(
                fullName != null && !fullName.isEmpty()
                        ? fullName
                        : "Not available"
        );

        tvProfileEmail.setText(
                email != null && !email.isEmpty()
                        ? email
                        : "Not available"
        );

        tvProfileMobile.setText(
                mobile != null && !mobile.isEmpty()
                        ? mobile
                        : "Not available"
        );

        tvProfileAddress.setText(
                address != null && !address.isEmpty()
                        ? address
                        : "Not available"
        );

        tvProfileRole.setText(
                role != null && !role.isEmpty()
                        ? role
                        : "Customer"
        );
    }
}