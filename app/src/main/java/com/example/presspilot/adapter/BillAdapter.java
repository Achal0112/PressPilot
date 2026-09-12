package com.example.presspilot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presspilot.R;
import com.example.presspilot.model.Bill;

import java.util.List;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private final List<Bill> billList;

    public BillAdapter(List<Bill> billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_item, parent, false);

        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BillViewHolder holder,
            int position) {

        Bill bill = billList.get(position);

        holder.tvBillCustomerName.setText(
                bill.getCustomerName()
        );

        holder.tvBillMonth.setText(
                "Month: " + bill.getMonth()
        );

        holder.tvDeliveredDays.setText(
                "Delivered Days: " + bill.getDeliveredDays()
        );

        holder.tvPricePerDay.setText(
                String.format(
                        Locale.getDefault(),
                        "Price/Day: ₹%.2f",
                        bill.getPricePerDay()
                )
        );

        holder.tvTotalAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "Total: ₹%.2f",
                        bill.getTotalAmount()
                )
        );
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {

        TextView tvBillCustomerName;
        TextView tvBillMonth;
        TextView tvDeliveredDays;
        TextView tvPricePerDay;
        TextView tvTotalAmount;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBillCustomerName =
                    itemView.findViewById(R.id.tvBillCustomerName);

            tvBillMonth =
                    itemView.findViewById(R.id.tvBillMonth);

            tvDeliveredDays =
                    itemView.findViewById(R.id.tvDeliveredDays);

            tvPricePerDay =
                    itemView.findViewById(R.id.tvPricePerDay);

            tvTotalAmount =
                    itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}