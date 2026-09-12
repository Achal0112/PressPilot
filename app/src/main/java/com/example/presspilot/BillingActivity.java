package com.example.presspilot;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presspilot.adapter.BillAdapter;
import com.example.presspilot.model.Bill;
import com.example.presspilot.model.Customer;
import com.example.presspilot.model.Delivery;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private TextView tvBillingMonth;
    private TextView tvBillCount;
    private TextView tvEmpty;

    private ProgressBar progressBar;

    private MaterialButton btnGenerateBills;

    private RecyclerView recyclerViewBills;

    private ArrayList<Bill> billList;

    private BillAdapter billAdapter;

    private String currentMonthKey;
    private String currentMonthName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_billing);

        db = FirebaseFirestore.getInstance();

        tvBillingMonth = findViewById(R.id.tvBillingMonth);
        tvBillCount = findViewById(R.id.tvBillCount);
        tvEmpty = findViewById(R.id.tvEmpty);

        progressBar = findViewById(R.id.progressBar);

        btnGenerateBills = findViewById(R.id.btnGenerateBills);

        recyclerViewBills = findViewById(R.id.recyclerViewBills);

        recyclerViewBills.setLayoutManager(
                new LinearLayoutManager(this)
        );

        billList = new ArrayList<>();

        billAdapter = new BillAdapter(billList);

        recyclerViewBills.setAdapter(billAdapter);

        setCurrentMonth();

        loadExistingBills();

        btnGenerateBills.setOnClickListener(v -> generateMonthlyBills());
    }

    private void setCurrentMonth() {

        Date currentDate = new Date();

        SimpleDateFormat monthKeyFormat =
                new SimpleDateFormat("yyyy-MM", Locale.getDefault());

        SimpleDateFormat monthNameFormat =
                new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        currentMonthKey =
                monthKeyFormat.format(currentDate);

        currentMonthName =
                monthNameFormat.format(currentDate);

        tvBillingMonth.setText(
                "Billing Month: " + currentMonthName
        );
    }

    private void loadExistingBills() {

        showLoading(true);

        db.collection("bills")
                .whereEqualTo("month", currentMonthName)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    billList.clear();

                    for (QueryDocumentSnapshot document
                            : querySnapshot) {

                        Bill bill =
                                document.toObject(Bill.class);

                        billList.add(bill);
                    }

                    billAdapter.notifyDataSetChanged();

                    updateBillCount();

                    showEmptyState();

                    showLoading(false);
                })
                .addOnFailureListener(e -> {

                    showLoading(false);

                    Toast.makeText(
                            BillingActivity.this,
                            "Failed to load bills: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void generateMonthlyBills() {

        showLoading(true);

        btnGenerateBills.setEnabled(false);

        db.collection("customers")
                .get()
                .addOnSuccessListener(customerSnapshot -> {

                    if (customerSnapshot.isEmpty()) {

                        showLoading(false);

                        btnGenerateBills.setEnabled(true);

                        Toast.makeText(
                                BillingActivity.this,
                                "No customers found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    db.collection("deliveries")
                            .get()
                            .addOnSuccessListener(deliverySnapshot -> {

                                Map<String, Integer>
                                        deliveredDaysMap =
                                        new HashMap<>();

                                for (QueryDocumentSnapshot document
                                        : deliverySnapshot) {

                                    Delivery delivery =
                                            document.toObject(
                                                    Delivery.class
                                            );

                                    if (delivery.getDate() == null) {
                                        continue;
                                    }

                                    if (!"Delivered".equalsIgnoreCase(
                                            delivery.getStatus())) {
                                        continue;
                                    }

                                    if (isDeliveryInCurrentMonth(
                                            delivery.getDate())) {

                                        String customerId =
                                                delivery.getCustomerId();

                                        int count =
                                                deliveredDaysMap.containsKey(
                                                        customerId
                                                )
                                                        ? deliveredDaysMap.get(
                                                        customerId
                                                )
                                                        : 0;

                                        deliveredDaysMap.put(
                                                customerId,
                                                count + 1
                                        );
                                    }
                                }

                                saveBills(
                                        customerSnapshot,
                                        deliveredDaysMap
                                );

                            })
                            .addOnFailureListener(e -> {

                                showLoading(false);

                                btnGenerateBills.setEnabled(true);

                                Toast.makeText(
                                        BillingActivity.this,
                                        "Failed to read deliveries: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });

                })
                .addOnFailureListener(e -> {

                    showLoading(false);

                    btnGenerateBills.setEnabled(true);

                    Toast.makeText(
                            BillingActivity.this,
                            "Failed to read customers: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveBills(
            com.google.firebase.firestore.QuerySnapshot customerSnapshot,
            Map<String, Integer> deliveredDaysMap) {

        com.google.firebase.firestore.WriteBatch batch =
                db.batch();

        for (QueryDocumentSnapshot document
                : customerSnapshot) {

            Customer customer =
                    document.toObject(Customer.class);

            String customerId =
                    customer.getCustomerId();

            if (customerId == null ||
                    customerId.trim().isEmpty()) {

                customerId = document.getId();
            }

            int deliveredDays =
                    deliveredDaysMap.containsKey(customerId)
                            ? deliveredDaysMap.get(customerId)
                            : 0;

            double pricePerDay =
                    customer.getPricePerDay();

            double totalAmount =
                    deliveredDays * pricePerDay;

            String billId =
                    customerId + "_" + currentMonthKey;

            Bill bill = new Bill(
                    billId,
                    customerId,
                    customer.getName(),
                    currentMonthName,
                    deliveredDays,
                    pricePerDay,
                    totalAmount,
                    System.currentTimeMillis()
            );

            batch.set(
                    db.collection("bills")
                            .document(billId),
                    bill
            );
        }

        batch.commit()
                .addOnSuccessListener(unused -> {

                    showLoading(false);

                    btnGenerateBills.setEnabled(true);

                    Toast.makeText(
                            BillingActivity.this,
                            "Monthly bills generated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadExistingBills();
                })
                .addOnFailureListener(e -> {

                    showLoading(false);

                    btnGenerateBills.setEnabled(true);

                    Toast.makeText(
                            BillingActivity.this,
                            "Failed to generate bills: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private boolean isDeliveryInCurrentMonth(String dateString) {

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

            return currentMonthKey.equals(deliveryMonth);

        } catch (Exception e) {

            return false;
        }
    }

    private void updateBillCount() {

        tvBillCount.setText(
                "Bills: " + billList.size()
        );
    }

    private void showEmptyState() {

        if (billList.isEmpty()) {

            tvEmpty.setVisibility(View.VISIBLE);

        } else {

            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean loading) {

        if (loading) {

            progressBar.setVisibility(View.VISIBLE);

        } else {

            progressBar.setVisibility(View.GONE);
        }
    }
}