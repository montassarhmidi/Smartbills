package com.affable.smartbills.settings.category;

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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<HashMap<String, String>> list;

    public CategoryAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_setting_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String category_id = list.get(position).get("category_id");
        String category_name = list.get(position).get("category_name");

        holder.txtCategoryName.setText(category_name);

        holder.imgDelete.setOnClickListener(v -> deleteOption(holder, category_id));

        holder.itemView.setOnClickListener(v -> {

            Intent i = new Intent(context, EditCategoryActivity.class);
            i.putExtra("CATEGORY_ID", category_id);
            i.putExtra("CATEGORY_NAME", category_name);
            context.startActivity(i);

        });

    }

    private void deleteOption(ViewHolder holder, String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.want_to_delete_category)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> {


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    boolean chk = databaseAccess.deleteCategory(id);

                    if (chk) {

                        Toasty.success(context, R.string.category_deleted, Toast.LENGTH_SHORT).show();
                        list.remove(holder.getAdapterPosition());

                        // Notify that item at position has been removed
                        notifyItemRemoved(holder.getAdapterPosition());

                    } else {
                        Toasty.error(context, R.string.failed, Toast.LENGTH_SHORT).show();
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

        ImageView img, imgDelete;
        TextView txtCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txt_category_name);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

}
