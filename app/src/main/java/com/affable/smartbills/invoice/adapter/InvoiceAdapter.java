package com.affable.smartbills.invoice.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.invoice.OrderDetailsActivity;
import com.affable.smartbills.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private Context context;
    private List<HashMap<String, String>> orderList;

    private RecyclerView recyclerView;
    private LinearLayout noData;
    private DatabaseAccess databaseAccess;

    public InvoiceAdapter(Context context, List<HashMap<String, String>> orderList, RecyclerView recyclerView, LinearLayout noData) {
        this.context = context;
        this.orderList = orderList;
        this.recyclerView = recyclerView;
        this.noData = noData;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_orderlist, parent, false);

        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {

        databaseAccess = DatabaseAccess.getInstance(context);

        HashMap<String, String> order = orderList.get(position);

        final String invoice_id = order.get("invoice_id");
        String order_date = order.get("order_date");
        String order_type = order.get("order_type");
        String payment_method = order.get("payment_method");
        String customer_name = order.get("customer_name");
        String order_status = order.get("order_status");


        holder.txtCustomerName.setText(customer_name);
        holder.txtOrderId.setText(String.format("Invoice ID: %s", invoice_id));
        holder.txtPaymentMethod.setText(String.format("Payment Method: %s", payment_method));
        holder.txtOrderType.setText(String.format("Order Type: %s", order_type));
        holder.txtDate.setText(String.format("Date: %s", order_date));
        holder.txtOrderStatus.setText(order_status);

        if (order_status != null) {

            if (order_status.equals(Constant.CANCELED)) {
                holder.txtOrderStatus.setBackgroundResource(R.color.red);
            } else if (order_status.equals(Constant.COMPLETED)) {
                holder.txtOrderStatus.setBackgroundResource(R.color.green);
            }


            if (order_status.equals(Constant.PENDING)) {
                holder.txtOrderStatus.setOnClickListener(v -> {

                    //create custom dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = li.inflate(R.layout.dialog_order_status, null);
                    dialog.setView(dialogView);

                    final TextView txtComplete = dialogView.findViewById(R.id.txtComplete);
                    final TextView txtCancel = dialogView.findViewById(R.id.txtCancel);

                    final AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                    txtComplete.setOnClickListener(v1 -> {
                        changeStatus(holder, invoice_id, Constant.COMPLETED);
                        alertDialog.dismiss();

                    });

                    txtCancel.setOnClickListener(v12 -> {
                        changeStatus(holder, invoice_id, Constant.CANCELED);

                        //on cancel restore item stock
                        restoreItemStock(invoice_id);

                        alertDialog.dismiss();
                    });


                });
            }
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("invoice_id", invoice_id);
            context.startActivity(intent);
        });


    }

    private void restoreItemStock(String invoice_id) {

        /*
         * 1.get order details
         * 2.get stock & item_id
         * 3. loop though the updateStock() method
         */

        databaseAccess.open();
        ArrayList<HashMap<String, String>> orderDetailsList = databaseAccess.getOrderDetailsList(invoice_id);

        if (orderDetailsList.isEmpty()) {

            for (HashMap<String, String> map : orderDetailsList) {

                String item_id = map.get("item_id");
                String stock = map.get("stock");
                String item_qty = map.get("item_qty");

                int reset_stock = 0;
                if (item_qty != null && stock != null)
                    reset_stock = Integer.parseInt(stock) + Integer.parseInt(item_qty);

                databaseAccess.open();
                databaseAccess.updateItemStock(item_id, String.valueOf(reset_stock));

            }
            databaseAccess.close();

        }

    }

    private void changeStatus(InvoiceViewHolder holder, String invoice_id, String status) {
        databaseAccess.open();
        if (databaseAccess.updateOrderStatus(invoice_id, status)) {

            orderList.remove(holder.getAdapterPosition());
            notifyDataSetChanged();
            if (orderList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            }

        }
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomerName, txtOrderId, txtOrderStatus, txtOrderType, txtPaymentMethod, txtDate;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderType = itemView.findViewById(R.id.txt_order_type);
            txtPaymentMethod = itemView.findViewById(R.id.txt_payment_method);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);

        }
    }


}
