package com.affable.smartbills.settings.payment_metthod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;


import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private Context context;
    private List<HashMap<String, String>> list;

    public PaymentAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_setting_payment_methods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String id = list.get(position).get("payment_method_id");
        String name = list.get(position).get("payment_method_name");

        holder.txtPaymentName.setText(name);

        holder.imgDelete.setOnClickListener(v -> deleteOption(holder, id));

        holder.itemView.setOnClickListener(v -> {

            Intent i = new Intent(context, EditPaymentActivity.class);
            i.putExtra("PAYMENT_ID", id);
            i.putExtra("PAYMENT_NAME", name);
            context.startActivity(i);

        });

    }

    private void deleteOption(ViewHolder holder, String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.want_to_delete_payment)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> {


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    boolean chk = databaseAccess.deletePaymentMethod(id);

                    if (chk) {

                        Toasty.error(context, R.string.payment_method_deleted, Toast.LENGTH_SHORT).show();
                        list.remove(holder.getAdapterPosition());

                        // Notify that item at position has been removed
                        notifyItemRemoved(holder.getAdapterPosition());

                    } else {
                        Toasty.warning(context, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();

                })
                .setNegativeButton(R.string.no, (dialog, which) ->
                    dialog.cancel()).show();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPaymentName;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPaymentName = itemView.findViewById(R.id.txt_payment_name);
            imgDelete = itemView.findViewById(R.id.img_delete);

        }

    }

}
