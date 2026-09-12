package com.example.presspilot;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presspilot.adapter.CustomerAdapter;
import com.example.presspilot.model.Customer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity {

    private TextInputEditText etSearchCustomer;
    private RecyclerView recyclerViewCustomers;
    private TextView tvCustomerCount;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private CustomerAdapter customerAdapter;

    private FirebaseFirestore db;

    private final List<Customer> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_list);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        etSearchCustomer =
                findViewById(R.id.etSearchCustomer);

        recyclerViewCustomers =
                findViewById(R.id.recyclerViewCustomers);

        tvCustomerCount =
                findViewById(R.id.tvCustomerCount);

        tvEmpty =
                findViewById(R.id.tvEmpty);

        progressBar =
                findViewById(R.id.progressBar);

        // RecyclerView setup
        recyclerViewCustomers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        customerAdapter =
                new CustomerAdapter(customerList);

        recyclerViewCustomers.setAdapter(customerAdapter);

        // Search
        etSearchCustomer.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        customerAdapter.filter(
                                s.toString()
                        );

                        updateEmptyMessage();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // Load customers
        loadCustomers();
    }

    private void loadCustomers() {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("customers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    progressBar.setVisibility(View.GONE);

                    customerList.clear();

                    for (var document : queryDocumentSnapshots) {

                        Customer customer =
                                document.toObject(Customer.class);

                        customerList.add(customer);
                    }

                    customerAdapter.setFullList(
                            customerList
                    );

                    updateEmptyMessage();

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            CustomerListActivity.this,
                            "Failed to load customers: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    updateEmptyMessage();
                });
    }

    private void updateEmptyMessage() {

        int count = customerAdapter.getItemCount();

        tvCustomerCount.setText(
                "Customers: " + count
        );

        if (count == 0) {

            tvEmpty.setVisibility(View.VISIBLE);
            recyclerViewCustomers.setVisibility(View.GONE);

        } else {

            tvEmpty.setVisibility(View.GONE);
            recyclerViewCustomers.setVisibility(View.VISIBLE);
        }
    }
}