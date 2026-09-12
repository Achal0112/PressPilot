package com.example.presspilot;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.presspilot.model.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerBillActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // XML Views
    private TextView tvBillTitle;
    private TextView tvCustomerName;
    private TextView tvMonth;
    private TextView tvDeliveredDays;
    private TextView tvPricePerDay;
    private TextView tvTotalAmount;
    private TextView tvNoBill;

    private ProgressBar progressBar;

    // Current month
    private String currentMonthName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Activity with XML layout
        setContentView(R.layout.activity_customer_bill);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        tvBillTitle = findViewById(R.id.tvBillTitle);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvMonth = findViewById(R.id.tvMonth);
        tvDeliveredDays = findViewById(R.id.tvDeliveredDays);
        tvPricePerDay = findViewById(R.id.tvPricePerDay);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvNoBill = findViewById(R.id.tvNoBill);

        progressBar = findViewById(R.id.progressBar);

        // Set current month
        setCurrentMonth();

        // Load customer's bill
        loadCustomerBill();
    }

    // ---------------------------------------------------------
    // SET CURRENT MONTH
    // ---------------------------------------------------------

    private void setCurrentMonth() {

        SimpleDateFormat monthFormat =
                new SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.getDefault()
                );

        currentMonthName =
                monthFormat.format(new Date());

        tvBillTitle.setText("Monthly Bill");

        tvMonth.setText(
                "Month: " + currentMonthName
        );
    }

    // ---------------------------------------------------------
    // LOAD CUSTOMER BILL
    // ---------------------------------------------------------

    private void loadCustomerBill() {

        // Check whether customer is logged in
        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Hide "No Bill" message
        tvNoBill.setVisibility(View.GONE);

        // Get logged-in user's UID
        String uid =
                mAuth.getCurrentUser().getUid();

        // -----------------------------------------------------
        // STEP 1:
        // Get customer information from users collection
        // -----------------------------------------------------

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    // Check whether user document exists
                    if (!documentSnapshot.exists()) {

                        showError(
                                "Customer profile not found"
                        );

                        return;
                    }

                    // Get mobile number from user document
                    String mobile =
                            documentSnapshot.getString("mobile");

                    // Check mobile number
                    if (mobile == null ||
                            mobile.trim().isEmpty()) {

                        showError(
                                "Customer mobile number not found"
                        );

                        return;
                    }

                    // Find customer's bill
                    findCustomerBill(mobile);
                })
                .addOnFailureListener(e ->
                        showError(
                                "Failed to load profile: "
                                        + e.getMessage()
                        )
                );
    }

    // ---------------------------------------------------------
    // FIND CUSTOMER AND BILL
    // ---------------------------------------------------------

    private void findCustomerBill(String mobile) {

        // Search customer using mobile number
        db.collection("customers")
                .whereEqualTo("mobile", mobile)
                .get()
                .addOnSuccessListener(customerSnapshot -> {

                    // Check whether customer exists
                    if (customerSnapshot.isEmpty()) {

                        showError(
                                "Customer record not found"
                        );

                        return;
                    }

                    // IMPORTANT:
                    // getDocuments().get(0) returns DocumentSnapshot
                    DocumentSnapshot customerDocument =
                            customerSnapshot
                                    .getDocuments()
                                    .get(0);

                    // Get customer ID
                    String customerId =
                            customerDocument.getId();

                    // -------------------------------------------------
                    // Create current month key
                    // Example: September 2026 -> 2026-09
                    // -------------------------------------------------

                    SimpleDateFormat monthKeyFormat =
                            new SimpleDateFormat(
                                    "yyyy-MM",
                                    Locale.getDefault()
                            );

                    String currentMonthKey =
                            monthKeyFormat.format(new Date());

                    // -------------------------------------------------
                    // Bill document ID
                    // Example:
                    // customerId_2026-09
                    // -------------------------------------------------

                    String billId =
                            customerId
                                    + "_"
                                    + currentMonthKey;

                    // -------------------------------------------------
                    // Get bill from Firestore
                    // -------------------------------------------------

                    db.collection("bills")
                            .document(billId)
                            .get()
                            .addOnSuccessListener(
                                    billDocument -> {

                                        // Hide progress bar
                                        progressBar.setVisibility(
                                                View.GONE
                                        );

                                        // Check whether bill exists
                                        if (billDocument.exists()) {

                                            // Convert Firestore document
                                            // into Bill object
                                            Bill bill =
                                                    billDocument.toObject(
                                                            Bill.class
                                                    );

                                            if (bill != null) {

                                                showBill(bill);

                                            } else {

                                                tvNoBill.setVisibility(
                                                        View.VISIBLE
                                                );

                                                tvNoBill.setText(
                                                        "Unable to read bill data"
                                                );
                                            }

                                        } else {

                                            // Bill does not exist
                                            tvNoBill.setVisibility(
                                                    View.VISIBLE
                                            );

                                            tvNoBill.setText(
                                                    "No bill generated for "
                                                            + currentMonthName
                                            );
                                        }
                                    }
                            )
                            .addOnFailureListener(e ->
                                    showError(
                                            "Failed to load bill: "
                                                    + e.getMessage()
                                    )
                            );
                })
                .addOnFailureListener(e ->
                        showError(
                                "Failed to find customer: "
                                        + e.getMessage()
                        )
                );
    }

    // ---------------------------------------------------------
    // DISPLAY BILL
    // ---------------------------------------------------------

    private void showBill(Bill bill) {

        // Hide progress bar
        progressBar.setVisibility(View.GONE);

        // Hide no-bill message
        tvNoBill.setVisibility(View.GONE);

        // Customer name
        tvCustomerName.setText(
                bill.getCustomerName()
        );

        // Month
        tvMonth.setText(
                "Month: " + bill.getMonth()
        );

        // Delivered days
        tvDeliveredDays.setText(
                "Delivered Days: "
                        + bill.getDeliveredDays()
        );

        // Price per day
        tvPricePerDay.setText(
                String.format(
                        Locale.getDefault(),
                        "Price per Day: ₹%.2f",
                        bill.getPricePerDay()
                )
        );

        // Total amount
        tvTotalAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "Total Amount: ₹%.2f",
                        bill.getTotalAmount()
                )
        );
    }

    // ---------------------------------------------------------
    // SHOW ERROR
    // ---------------------------------------------------------

    private void showError(String message) {

        // Hide progress bar
        progressBar.setVisibility(View.GONE);

        // Show error message
        Toast.makeText(
                CustomerBillActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
