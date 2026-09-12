package com.example.presspilot;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presspilot.adapter.DeliveryAdapter;
import com.example.presspilot.model.Customer;
import com.example.presspilot.model.Delivery;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDelivery;
    private TextView tvDate;
    private TextView tvDeliveryCount;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    private DeliveryAdapter deliveryAdapter;

    private final List<Customer> customerList =
            new ArrayList<>();

    private final Map<String, String> deliveryStatuses =
            new HashMap<>();

    private String currentDate;
    private String currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_delivery
        );

        db = FirebaseFirestore.getInstance();

        recyclerViewDelivery =
                findViewById(
                        R.id.recyclerViewDelivery
                );

        tvDate =
                findViewById(R.id.tvDate);

        tvDeliveryCount =
                findViewById(
                        R.id.tvDeliveryCount
                );

        tvEmpty =
                findViewById(R.id.tvEmpty);

        progressBar =
                findViewById(R.id.progressBar);

        // Current date
        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                );

        // Current day
        SimpleDateFormat dayFormat =
                new SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                );

        Date today = new Date();

        currentDate =
                dateFormat.format(today);

        currentDay =
                dayFormat.format(today);

        tvDate.setText(
                currentDay + ", " + currentDate
        );

        // RecyclerView
        recyclerViewDelivery.setLayoutManager(
                new LinearLayoutManager(this)
        );

        deliveryAdapter =
                new DeliveryAdapter(
                        customerList,
                        new DeliveryAdapter.OnDeliveryClickListener() {

                            @Override
                            public void onDelivered(
                                    Customer customer) {

                                saveDelivery(
                                        customer,
                                        "Delivered"
                                );
                            }

                            @Override
                            public void onNotDelivered(
                                    Customer customer) {

                                saveDelivery(
                                        customer,
                                        "Not Delivered"
                                );
                            }
                        }
                );

        recyclerViewDelivery.setAdapter(
                deliveryAdapter
        );

        // First load customers
        loadCustomers();
    }

    private void loadCustomers() {

        progressBar.setVisibility(
                View.VISIBLE
        );

        db.collection("customers")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            customerList.clear();

                            for (
                                    QueryDocumentSnapshot document
                                    : queryDocumentSnapshots
                            ) {

                                Customer customer =
                                        document.toObject(
                                                Customer.class
                                        );

                                customerList.add(
                                        customer
                                );
                            }

                            deliveryAdapter.notifyDataSetChanged();

                            updateEmptyMessage();

                            // After customers load,
                            // load today's statuses
                            loadTodayDeliveryStatuses();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressBar.setVisibility(
                                    View.GONE
                            );

                            Toast.makeText(
                                    DeliveryActivity.this,
                                    "Failed to load customers: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            updateEmptyMessage();
                        }
                );
    }

    private void loadTodayDeliveryStatuses() {

        db.collection("deliveries")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            deliveryStatuses.clear();

                            for (
                                    QueryDocumentSnapshot document
                                    : queryDocumentSnapshots
                            ) {

                                Delivery delivery =
                                        document.toObject(
                                                Delivery.class
                                        );

                                if (
                                        currentDate.equals(
                                                delivery.getDate()
                                        )
                                ) {

                                    deliveryStatuses.put(
                                            delivery.getCustomerId(),
                                            delivery.getStatus()
                                    );
                                }
                            }

                            deliveryAdapter.setDeliveryStatuses(
                                    deliveryStatuses
                            );

                            progressBar.setVisibility(
                                    View.GONE
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressBar.setVisibility(
                                    View.GONE
                            );

                            Toast.makeText(
                                    DeliveryActivity.this,
                                    "Failed to load delivery status: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    private void saveDelivery(
            Customer customer,
            String status) {

        String customerId =
                customer.getCustomerId();

        if (
                customerId == null
                        || customerId.isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Customer ID is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        progressBar.setVisibility(
                View.VISIBLE
        );

        /*
         * One customer + one date = one document
         */
        String documentId =
                customerId + "_" + currentDate;

        Delivery delivery =
                new Delivery(
                        documentId,
                        customerId,
                        customer.getName(),
                        customer.getMobile(),
                        currentDate,
                        currentDay,
                        status,
                        System.currentTimeMillis()
                );

        db.collection("deliveries")
                .document(documentId)
                .set(delivery)
                .addOnSuccessListener(
                        unused -> {

                            progressBar.setVisibility(
                                    View.GONE
                            );

                            // Update screen immediately
                            deliveryAdapter.updateDeliveryStatus(
                                    customerId,
                                    status
                            );

                            Toast.makeText(
                                    DeliveryActivity.this,
                                    customer.getName()
                                            + " - "
                                            + status,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressBar.setVisibility(
                                    View.GONE
                            );

                            Toast.makeText(
                                    DeliveryActivity.this,
                                    "Failed to save delivery: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    private void updateEmptyMessage() {

        if (customerList.isEmpty()) {

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            recyclerViewDelivery.setVisibility(
                    View.GONE
            );

            tvDeliveryCount.setText(
                    "Customers: 0"
            );

        } else {

            tvEmpty.setVisibility(
                    View.GONE
            );

            recyclerViewDelivery.setVisibility(
                    View.VISIBLE
            );

            tvDeliveryCount.setText(
                    "Customers: "
                            + customerList.size()
            );
        }
    }
}