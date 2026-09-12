package com.example.presspilot;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.presspilot.model.Customer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCustomer extends AppCompatActivity {

    private TextInputEditText etCustomerName;
    private TextInputEditText etCustomerMobile;
    private TextInputEditText etCustomerAddress;
    private TextInputEditText etNewspaperType;
    private TextInputEditText etPricePerDay;

    private MaterialButton btnAddCustomer;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_customer);

        db = FirebaseFirestore.getInstance();

        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerMobile = findViewById(R.id.etCustomerMobile);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etNewspaperType = findViewById(R.id.etNewspaperType);
        etPricePerDay = findViewById(R.id.etPricePerDay);

        btnAddCustomer = findViewById(R.id.btnAddCustomer);
        progressBar = findViewById(R.id.progressBar);

        btnAddCustomer.setOnClickListener(v -> addCustomer());
    }

    private void addCustomer() {

        String name = etCustomerName.getText().toString().trim();
        String mobile = etCustomerMobile.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();
        String newspaperType = etNewspaperType.getText().toString().trim();
        String priceText = etPricePerDay.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etCustomerName.setError("Enter customer name");
            etCustomerName.requestFocus();
            return;
        }

        if (name.length() < 3) {
            etCustomerName.setError("Name must contain at least 3 characters");
            etCustomerName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            etCustomerMobile.setError("Enter mobile number");
            etCustomerMobile.requestFocus();
            return;
        }

        if (!mobile.matches("\\d{10}")) {
            etCustomerMobile.setError("Enter valid 10 digit mobile number");
            etCustomerMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etCustomerAddress.setError("Enter customer address");
            etCustomerAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newspaperType)) {
            etNewspaperType.setError("Enter newspaper type");
            etNewspaperType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceText)) {
            etPricePerDay.setError("Enter price per day");
            etPricePerDay.requestFocus();
            return;
        }

        double pricePerDay;

        try {
            pricePerDay = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            etPricePerDay.setError("Enter valid price");
            etPricePerDay.requestFocus();
            return;
        }

        if (pricePerDay <= 0) {
            etPricePerDay.setError("Price must be greater than 0");
            etPricePerDay.requestFocus();
            return;
        }

        setLoading(true);

        String customerId = db.collection("customers")
                .document()
                .getId();

        Customer customer = new Customer(
                customerId,
                name,
                mobile,
                address,
                newspaperType,
                pricePerDay,
                System.currentTimeMillis()
        );

        db.collection("customers")
                .document(customerId)
                .set(customer)
                .addOnSuccessListener(unused -> {

                    setLoading(false);

                    Toast.makeText(
                            AddCustomer.this,
                            "Customer added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    clearFields();

                })
                .addOnFailureListener(e -> {

                    setLoading(false);

                    Toast.makeText(
                            AddCustomer.this,
                            "Failed to add customer: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void clearFields() {

        etCustomerName.setText("");
        etCustomerMobile.setText("");
        etCustomerAddress.setText("");
        etNewspaperType.setText("");
        etPricePerDay.setText("");

        etCustomerName.requestFocus();
    }

    private void setLoading(boolean loading) {

        if (loading) {

            progressBar.setVisibility(ProgressBar.VISIBLE);
            btnAddCustomer.setEnabled(false);
            btnAddCustomer.setText("Saving...");

        } else {

            progressBar.setVisibility(ProgressBar.GONE);
            btnAddCustomer.setEnabled(true);
            btnAddCustomer.setText("Add Customer");
        }
    }
}