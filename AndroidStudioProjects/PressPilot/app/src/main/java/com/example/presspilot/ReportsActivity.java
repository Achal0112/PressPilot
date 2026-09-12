package com.example.presspilot;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.presspilot.model.Bill;
import com.example.presspilot.model.Delivery;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private TextView tvReportMonth;
    private TextView tvTotalCustomers;
    private TextView tvDelivered;
    private TextView tvNotDelivered;
    private TextView tvMonthlyRevenue;

    private ProgressBar progressBar;

    private String currentMonthKey;
    private String currentMonthName;

    private int totalCustomers = 0;
    private int deliveredCount = 0;
    private int notDeliveredCount = 0;
    private double monthlyRevenue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reports);

        db = FirebaseFirestore.getInstance();

        tvReportMonth = findViewById(R.id.tvReportMonth);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        tvDelivered = findViewById(R.id.tvDelivered);
        tvNotDelivered = findViewById(R.id.tvNotDelivered);
        tvMonthlyRevenue = findViewById(R.id.tvMonthlyRevenue);

        progressBar = findViewById(R.id.progressBar);

        setCurrentMonth();

        loadReport();
    }

    private void setCurrentMonth() {

        Date currentDate = new Date();

        SimpleDateFormat monthKeyFormat =
                new SimpleDateFormat(
                        "yyyy-MM",
                        Locale.getDefault()
                );

        SimpleDateFormat monthNameFormat =
                new SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.getDefault()
                );

        currentMonthKey =
                monthKeyFormat.format(currentDate);

        currentMonthName =
                monthNameFormat.format(currentDate);

        tvReportMonth.setText(
                "Report Month: " + currentMonthName
        );
    }

    private void loadReport() {

        progressBar.setVisibility(View.VISIBLE);

        loadCustomers();
    }

    private void loadCustomers() {

        db.collection("customers")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    totalCustomers =
                            querySnapshot.size();

                    tvTotalCustomers.setText(
                            String.valueOf(totalCustomers)
                    );

                    loadDeliveries();

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            ReportsActivity.this,
                            "Failed to load customers: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadDeliveries() {

        deliveredCount = 0;
        notDeliveredCount = 0;

        db.collection("deliveries")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (QueryDocumentSnapshot document
                            : querySnapshot) {

                        Delivery delivery =
                                document.toObject(
                                        Delivery.class
                                );

                        if (delivery.getDate() == null) {
                            continue;
                        }

                        if (!isDeliveryInCurrentMonth(
                                delivery.getDate())) {
                            continue;
                        }

                        String status =
                                delivery.getStatus();

                        if ("Delivered".equalsIgnoreCase(status)) {

                            deliveredCount++;

                        } else if (
                                "Not Delivered"
                                        .equalsIgnoreCase(status)) {

                            notDeliveredCount++;
                        }
                    }

                    tvDelivered.setText(
                            String.valueOf(deliveredCount)
                    );

                    tvNotDelivered.setText(
                            String.valueOf(notDeliveredCount)
                    );

                    loadRevenue();

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            ReportsActivity.this,
                            "Failed to load deliveries: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadRevenue() {

        monthlyRevenue = 0.0;

        db.collection("bills")
                .whereEqualTo(
                        "month",
                        currentMonthName
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (QueryDocumentSnapshot document
                            : querySnapshot) {

                        Bill bill =
                                document.toObject(
                                        Bill.class
                                );

                        monthlyRevenue +=
                                bill.getTotalAmount();
                    }

                    tvMonthlyRevenue.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "₹%.2f",
                                    monthlyRevenue
                            )
                    );

                    progressBar.setVisibility(
                            View.GONE
                    );

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            ReportsActivity.this,
                            "Failed to load revenue: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private boolean isDeliveryInCurrentMonth(
            String dateString) {

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                    );

            Date deliveryDate =
                    dateFormat.parse(dateString);

            if (deliveryDate == null) {
                return false;
            }

            SimpleDateFormat monthFormat =
                    new SimpleDateFormat(
                            "yyyy-MM",
                            Locale.getDefault()
                    );

            String deliveryMonth =
                    monthFormat.format(deliveryDate);

            return currentMonthKey.equals(
                    deliveryMonth
            );

        } catch (Exception e) {

            return false;
        }
    }
}