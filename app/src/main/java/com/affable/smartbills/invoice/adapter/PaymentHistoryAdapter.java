package com.affable.smartbills.invoice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder> {

    private Context context;
    private List<HashMap<String, String>> paymentList;

    public PaymentHistoryAdapter(Context context, List<HashMap<String, String>> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public PaymentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_payment_history, parent, false);
        return new PaymentHistoryViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryViewHolder holder, int position) {

        HashMap<String, String> map = paymentList.get(holder.getAdapterPosition());

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        holder.textTime.setText(String.format("%s: %s", context.getString(R.string.time), map.get("payment_time")));
        holder.textDate.setText(String.format("%s: %s", context.getString(R.string.date), map.get("payment_date")));
        holder.textTotalPaidValue.setText(map.get("total_payed") + currency);
        holder.textPaidValue.setText(map.get("now_payed") + currency);
        holder.textDueValue.setText(map.get("due_money") + currency);

    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    static class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView textTime, textDate, textDueValue, textPaidValue, textTotalPaidValue;

        public PaymentHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textTime = itemView.findViewById(R.id.textTime);
            textDate = itemView.findViewById(R.id.textDate);
            textTotalPaidValue = itemView.findViewById(R.id.textTotalPaidValue);
            textPaidValue = itemView.findViewById(R.id.textPaidValue);
            textDueValue = itemView.findViewById(R.id.textDueValue);

        }
    }

}
