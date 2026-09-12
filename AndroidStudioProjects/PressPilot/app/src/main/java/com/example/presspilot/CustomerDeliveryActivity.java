package com.example.presspilot;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.presspilot.model.Delivery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerDeliveryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView tvDate;
    private TextView tvStatus;
    private TextView tvCustomerName;
    private TextView tvDeliveryTime;
    private ProgressBar progressBar;

    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_delivery);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvDeliveryTime = findViewById(R.id.tvDeliveryTime);
        progressBar = findViewById(R.id.progressBar);

        setTodayDate();

        loadTodayDelivery();
    }

    private void setTodayDate() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                );

        todayDate = dateFormat.format(new Date());

        tvDate.setText("Date: " + todayDate);
    }

    private void loadTodayDelivery() {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String uid =
                mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                this,
                                "Customer profile not found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String mobile =
                            documentSnapshot.getString("mobile");

                    if (mobile == null ||
                            mobile.trim().isEmpty()) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                this,
                                "Customer mobile number not found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    loadDeliveryUsingMobile(mobile);
                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Failed to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadDeliveryUsingMobile(String mobile) {

        db.collection("deliveries")
                .whereEqualTo("mobile", mobile)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    boolean found = false;

                    for (QueryDocumentSnapshot document
                            : querySnapshot) {

                        Delivery delivery =
                                document.toObject(
                                        Delivery.class
                                );

                        if (todayDate.equals(
                                delivery.getDate())) {

                            found = true;

                            tvCustomerName.setText(
                                    delivery.getCustomerName()
                            );

                            String status =
                                    delivery.getStatus();

                            if ("Delivered".equalsIgnoreCase(
                                    status)) {

                                tvStatus.setText(
                                        "Status: ✅ Delivered"
                                );

                            } else if (
                                    "Not Delivered"
                                            .equalsIgnoreCase(status)) {

                                tvStatus.setText(
                                        "Status: ❌ Not Delivered"
                                );

                            } else {

                                tvStatus.setText(
                                        "Status: ⏳ Pending"
                                );
                            }

                            if (delivery.getDeliveryTime() > 0) {

                                SimpleDateFormat timeFormat =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        );

                                String time =
                                        timeFormat.format(
                                                new Date(
                                                        delivery.getDeliveryTime()
                                                )
                                        );

                                tvDeliveryTime.setText(
                                        "Delivery Time: " + time
                                );

                            } else {

                                tvDeliveryTime.setText(
                                        "Delivery Time: Not available"
                                );
                            }

                            break;
                        }
                    }

                    if (!found) {

                        tvCustomerName.setText(
                                "No delivery record"
                        );

                        tvStatus.setText(
                                "Status: ⏳ Pending"
                        );

                        tvDeliveryTime.setText(
                                "Delivery Time: Not available"
                        );
                    }

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Failed to load delivery: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}