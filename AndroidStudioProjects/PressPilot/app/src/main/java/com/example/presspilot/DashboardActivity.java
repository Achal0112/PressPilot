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

public class DashboardActivity extends AppCompatActivity {

    private TextView tvDate;

    private MaterialCardView btnAddCustomer;
    private MaterialCardView btnCustomerList;
    private MaterialCardView btnDailyDelivery;
    private MaterialCardView btnMonthlyBilling;
    private MaterialCardView btnReports;
    private MaterialCardView btnLogout;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Connect XML views
        tvDate = findViewById(R.id.tvDate);

        btnAddCustomer = findViewById(R.id.btnAddCustomer);
        btnCustomerList = findViewById(R.id.btnCustomerList);
        btnDailyDelivery = findViewById(R.id.btnDailyDelivery);
        btnMonthlyBilling = findViewById(R.id.btnMonthlyBilling);
        btnReports = findViewById(R.id.btnReports);
        btnLogout = findViewById(R.id.btnLogout);

        // Show current date
        showCurrentDate();

        // Add Customer
        btnAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    AddCustomer.class
            );
            startActivity(intent);
        });

        // Customer List
        btnCustomerList.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    CustomerListActivity.class
            );
            startActivity(intent);
        });

        // Daily Delivery
        btnDailyDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    DeliveryActivity.class
            );
            startActivity(intent);
        });

        // Monthly Billing
        btnMonthlyBilling.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    BillingActivity.class
            );
            startActivity(intent);
        });

        // Reports
        btnReports.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    ReportsActivity.class
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

        String currentDate =
                dateFormat.format(new Date());

        tvDate.setText(currentDate);
    }

    private void logout() {

        mAuth.signOut();

        Intent intent = new Intent(
                DashboardActivity.this,
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