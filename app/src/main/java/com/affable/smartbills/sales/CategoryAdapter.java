package com.affable.smartbills.sales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> categoryList;
    private final RecyclerView itemsRecyclerView;
    private final LinearLayout noDataIndicator;
    private final TextView itemsTxt;

    public CategoryAdapter(Context context, List<HashMap<String, String>> categoryList,
                           RecyclerView itemsRecyclerView, LinearLayout noDataIndicator,
                           TextView itemsTxt
    ) {
        this.context = context;
        this.categoryList = categoryList;
        this.itemsRecyclerView = itemsRecyclerView;
        this.noDataIndicator = noDataIndicator;
        this.itemsTxt = itemsTxt;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_categories, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {

        final String categoryId = categoryList.get(position).get("category_id");
        String categoryName = categoryList.get(position).get("category_name");

        holder.categoryText.setText(categoryName);

        //change items as categories
        holder.categoryItem.setOnClickListener(v -> {

            itemsTxt.setText(categoryName);

            final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            ArrayList<HashMap<String, String>> items = databaseAccess.getItemsByCategory(categoryId);

            if (items.isEmpty()) {
                itemsRecyclerView.setVisibility(View.GONE);
                noDataIndicator.setVisibility(View.VISIBLE);
            } else {
                noDataIndicator.setVisibility(View.GONE);
                itemsRecyclerView.setVisibility(View.VISIBLE);

                SalesItemsAdapter itemsAdapter = new SalesItemsAdapter(context, items);
                itemsRecyclerView.setAdapter(itemsAdapter);
            }

        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout categoryItem;
        TextView categoryText;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryItem = itemView.findViewById(R.id.category_item);
            categoryText = itemView.findViewById(R.id.category_text);

        }


    }

}
