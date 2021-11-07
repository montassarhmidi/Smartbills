package com.affable.smartbills.expenses;

import android.annotation.SuppressLint;
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

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private Context context;
    private List<HashMap<String, String>> expensesList;

    private RecyclerView recyclerView;
    private View noData;

    public ExpensesAdapter(Context c, List<HashMap<String, String>> expensesList, RecyclerView recyclerView, View noData) {
        context = c;
        this.expensesList = expensesList;
        this.recyclerView = recyclerView;
        this.noData = noData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_expenses, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String expense_id = expensesList.get(position).get("expense_id");
        String expense_name = expensesList.get(position).get("expense_name");
        String expense_note = expensesList.get(position).get("expense_note");
        String expense_amount = expensesList.get(position).get("expense_amount");
        String date = expensesList.get(position).get("expense_date");
        String time = expensesList.get(position).get("expense_time");

        if (expense_note.isEmpty())
            expense_note = "(none)";

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        holder.txtExpenseName.setText(expense_name);
        holder.txtExpenseAmount.setText(expense_amount + currency);
        holder.txtDateTime.setText(date + " " + time);
        holder.txtExpenseNote.setText(context.getString(R.string.note) + expense_note);

        //delete expense
        holder.imgDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.want_to_delete_expense)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {

                        databaseAccess.open();
                        boolean deleteProduct = databaseAccess.deleteExpense(expense_id);

                        if (deleteProduct) {
                            Toasty.error(context, R.string.expense_deleted, Toast.LENGTH_SHORT).show();

                            expensesList.remove(holder.getAdapterPosition());

                            // Notify that item at position has been removed
                            notifyItemRemoved(holder.getAdapterPosition());

                            //show no data indicator when list in empty
                            if (expensesList.size() <= 0) {
                                recyclerView.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();

                    })
                    .setNegativeButton(R.string.no, (dialog, which) ->
                            dialog.cancel()).show();

        });

    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtExpenseName, txtExpenseAmount, txtDateTime, txtExpenseNote;
        ImageView productImage, imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtExpenseName = itemView.findViewById(R.id.txt_expense_name);
            txtExpenseAmount = itemView.findViewById(R.id.txt_expense_amount);
            txtDateTime = itemView.findViewById(R.id.txt_date_time);
            txtExpenseNote = itemView.findViewById(R.id.txt_expense_note);
            imgDelete = itemView.findViewById(R.id.img_delete);

            productImage = itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditExpenseActivity.class);
            i.putExtra("expense_id", expensesList.get(getAdapterPosition()).get("expense_id"));
            i.putExtra("expense_name", expensesList.get(getAdapterPosition()).get("expense_name"));
            i.putExtra("expense_note", expensesList.get(getAdapterPosition()).get("expense_note"));
            i.putExtra("expense_amount", expensesList.get(getAdapterPosition()).get("expense_amount"));
            i.putExtra("expense_date", expensesList.get(getAdapterPosition()).get("expense_date"));
            i.putExtra("expense_time", expensesList.get(getAdapterPosition()).get("expense_time"));
            context.startActivity(i);

        }
    }

}
