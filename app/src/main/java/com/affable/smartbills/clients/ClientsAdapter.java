package com.affable.smartbills.clients;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> {

    private Context context;
    private List<HashMap<String, String>> clientDataList;
    private RecyclerView recyclerView;
    private View noData;

    public ClientsAdapter(Context context, List<HashMap<String, String>> clientDataList, RecyclerView recyclerView, View noData) {
        this.context = context;
        this.clientDataList = clientDataList;
        this.recyclerView = recyclerView;
        this.noData = noData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_clients, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String customer_id = clientDataList.get(position).get("client_id");
        String name = clientDataList.get(position).get("client_name");
        String cell = clientDataList.get(position).get("client_phone");
        String email = clientDataList.get(position).get("client_email");
        String address = clientDataList.get(position).get("client_address");

        holder.txtCustomerName.setText(name);
        holder.txtCell.setText(cell);
        holder.txtEmail.setText(email);
        holder.txtAddress.setText(address);


        holder.imgCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            String phone = "tel:" + cell;
            callIntent.setData(Uri.parse(phone));
            context.startActivity(callIntent);
        });

        holder.imgDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.want_to_delete_client)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {

                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        boolean deleteClient = databaseAccess.deleteClient(customer_id);

                        if (deleteClient) {

                            Toasty.error(context, R.string.client_deleted, Toast.LENGTH_SHORT).show();

                            clientDataList.remove(holder.getAdapterPosition());
                            // Notify that item at position has been removed
                            notifyItemRemoved(holder.getAdapterPosition());

                            //show no data indicator when list in empty
                            if (clientDataList.size()<=0){
                                recyclerView.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();

                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> {
                        // Perform Your Task Here--When No is pressed
                        dialog.cancel();
                    }).show();

        });

    }

    @Override
    public int getItemCount() {
        return clientDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCustomerName, txtCell, txtEmail, txtAddress;
        ImageView imgDelete, imgCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtCell = itemView.findViewById(R.id.txt_cell);
            txtEmail = itemView.findViewById(R.id.txt_email);
            txtAddress = itemView.findViewById(R.id.txt_address);

            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditClientActivity.class);
            i.putExtra("client_id", clientDataList.get(getAdapterPosition()).get("client_id"));
            i.putExtra("client_name", clientDataList.get(getAdapterPosition()).get("client_name"));
            i.putExtra("client_phone", clientDataList.get(getAdapterPosition()).get("client_phone"));
            i.putExtra("client_email", clientDataList.get(getAdapterPosition()).get("client_email"));
            i.putExtra("client_address", clientDataList.get(getAdapterPosition()).get("client_address"));
            context.startActivity(i);

        }

    }

}
