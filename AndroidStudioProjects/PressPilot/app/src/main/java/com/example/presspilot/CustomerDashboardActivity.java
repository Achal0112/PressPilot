package com.example.presspilot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvDate;

    private MaterialCardView btnTodayDelivery;
    private MaterialCardView btnMonthlyBill;
    private MaterialCardView btnProfile;
    private MaterialCardView btnLogout;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Connect XML views
        tvDate = findViewById(R.id.tvDate);

        btnTodayDelivery = findViewById(R.id.btnTodayDelivery);
        btnMonthlyBill = findViewById(R.id.btnMonthlyBill);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Display current date
        showCurrentDate();

        // Today's Delivery
        btnTodayDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    CustomerDeliveryActivity.class
            );
            startActivity(intent);
        });

        // Monthly Bill
        btnMonthlyBill.setOnClickListener(v -> {
            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    CustomerBillActivity.class
            );
            startActivity(intent);
        });

        // Profile
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    CustomerProfileActivity.class
            );
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> logout());
    }

    private void showCurrentDate() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale.getDefault()
                );

        String currentDate = dateFormat.format(new Date());

        tvDate.setText(currentDate);
    }

    private void logout() {

        mAuth.signOut();

        Intent intent = new Intent(
                CustomerDashboardActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}