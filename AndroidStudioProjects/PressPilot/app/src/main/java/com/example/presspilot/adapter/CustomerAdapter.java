package com.example.presspilot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presspilot.R;
import com.example.presspilot.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerAdapter
        extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private List<Customer> customerListFull;

    public CustomerAdapter(List<Customer> customerList) {
        this.customerList = customerList;
        this.customerListFull = new ArrayList<>(customerList);
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent, false);

        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CustomerViewHolder holder,
            int position) {

        Customer customer = customerList.get(position);

        holder.tvCustomerName.setText(customer.getName());

        holder.tvCustomerMobile.setText(
                "Mobile: " + customer.getMobile()
        );

        holder.tvCustomerAddress.setText(
                "Address: " + customer.getAddress()
        );

        holder.tvNewspaperType.setText(
                "Newspaper: " + customer.getNewspaperType()
        );

        holder.tvPricePerDay.setText(
                String.format(
                        Locale.getDefault(),
                        "Price/Day: ₹%.2f",
                        customer.getPricePerDay()
                )
        );
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void updateList(List<Customer> filteredList) {

        customerList = filteredList;

        notifyDataSetChanged();
    }

    public void setFullList(List<Customer> list) {

        customerList = list;

        customerListFull = new ArrayList<>(list);

        notifyDataSetChanged();
    }

    public void filter(String text) {

        List<Customer> filteredList = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {

            filteredList.addAll(customerListFull);

        } else {

            String searchText =
                    text.toLowerCase(Locale.getDefault()).trim();

            for (Customer customer : customerListFull) {

                String name = customer.getName() == null
                        ? ""
                        : customer.getName().toLowerCase(Locale.getDefault());

                String mobile = customer.getMobile() == null
                        ? ""
                        : customer.getMobile();

                if (name.contains(searchText)
                        || mobile.contains(searchText)) {

                    filteredList.add(customer);
                }
            }
        }

        customerList = filteredList;

        notifyDataSetChanged();
    }

    public static class CustomerViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvCustomerName;
        TextView tvCustomerMobile;
        TextView tvCustomerAddress;
        TextView tvNewspaperType;
        TextView tvPricePerDay;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName =
                    itemView.findViewById(R.id.tvCustomerName);

            tvCustomerMobile =
                    itemView.findViewById(R.id.tvCustomerMobile);

            tvCustomerAddress =
                    itemView.findViewById(R.id.tvCustomerAddress);

            tvNewspaperType =
                    itemView.findViewById(R.id.tvNewspaperType);

            tvPricePerDay =
                    itemView.findViewById(R.id.tvPricePerDay);
        }
    }
}