package com.example.presspilot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presspilot.R;
import com.example.presspilot.model.Customer;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAdapter
        extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    public interface OnDeliveryClickListener {

        void onDelivered(Customer customer);

        void onNotDelivered(Customer customer);
    }

    private final List<Customer> customerList;

    private final OnDeliveryClickListener listener;

    private final Map<String, String> deliveryStatusMap =
            new HashMap<>();

    public DeliveryAdapter(
            List<Customer> customerList,
            OnDeliveryClickListener listener) {

        this.customerList = customerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.delivery_item,
                        parent,
                        false
                );

        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DeliveryViewHolder holder,
            int position) {

        Customer customer = customerList.get(position);

        holder.tvCustomerName.setText(
                customer.getName()
        );

        holder.tvCustomerMobile.setText(
                "Mobile: " + customer.getMobile()
        );

        String customerId = customer.getCustomerId();

        String status = deliveryStatusMap.get(customerId);

        if (status == null || status.isEmpty()) {

            holder.tvDeliveryStatus.setText(
                    "Status: Pending"
            );

        } else if (status.equals("Delivered")) {

            holder.tvDeliveryStatus.setText(
                    "Status: ✅ Delivered"
            );

        } else if (status.equals("Not Delivered")) {

            holder.tvDeliveryStatus.setText(
                    "Status: ❌ Not Delivered"
            );

        } else {

            holder.tvDeliveryStatus.setText(
                    "Status: " + status
            );
        }

        holder.btnDelivered.setOnClickListener(v ->
                listener.onDelivered(customer)
        );

        holder.btnNotDelivered.setOnClickListener(v ->
                listener.onNotDelivered(customer)
        );
    }

    @Override
    public int getItemCount() {

        return customerList.size();
    }

    public void updateDeliveryStatus(
            String customerId,
            String status) {

        deliveryStatusMap.put(
                customerId,
                status
        );

        notifyDataSetChanged();
    }

    public void setDeliveryStatuses(
            Map<String, String> statuses) {

        deliveryStatusMap.clear();

        deliveryStatusMap.putAll(statuses);

        notifyDataSetChanged();
    }

    static class DeliveryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvCustomerName;
        TextView tvCustomerMobile;
        TextView tvDeliveryStatus;

        MaterialButton btnDelivered;
        MaterialButton btnNotDelivered;

        public DeliveryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvCustomerName =
                    itemView.findViewById(
                            R.id.tvCustomerName
                    );

            tvCustomerMobile =
                    itemView.findViewById(
                            R.id.tvCustomerMobile
                    );

            tvDeliveryStatus =
                    itemView.findViewById(
                            R.id.tvDeliveryStatus
                    );

            btnDelivered =
                    itemView.findViewById(
                            R.id.btnDelivered
                    );

            btnNotDelivered =
                    itemView.findViewById(
                            R.id.btnNotDelivered
                    );
        }
    }
}