package com.affable.smartbills.sales;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

public class SalesItemsAdapter extends RecyclerView.Adapter<SalesItemsAdapter.SalesItemsViewHolder> {

    private final Context context;
    private List<HashMap<String, String>> itemsList;
    private TextView textCount;

    public SalesItemsAdapter(Context context, List<HashMap<String, String>> itemsList, TextView textCount) {
        this.context = context;
        this.itemsList = itemsList;
        this.textCount = textCount;
    }

    public SalesItemsAdapter(Context context, List<HashMap<String, String>> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public SalesItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_sales_items, parent, false);
        return new SalesItemsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SalesItemsViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String item_id = itemsList.get(position).get("item_id");
        String item_name = itemsList.get(position).get("item_name");
        String item_sell_price = itemsList.get(position).get("item_sell_price");
        String item_weight = itemsList.get(position).get("item_weight");
        String base64Image = itemsList.get(position).get("item_image");
        String item_stock = itemsList.get(position).get("item_stock");

        //get Currency user selected
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        //get exact WeightUnit of Item using weight_unit_id
        String weight_unit_id = itemsList.get(position).get("item_weight_unit_id");
        databaseAccess.open();
        String weight_unit_name = databaseAccess.getWeightUnit(weight_unit_id);

        //set the values in recyclerItem
        holder.itemName.setText(item_name);
        holder.itemPrice.setText(String.format("Price: %s%s", item_sell_price, currency));
        holder.itemWeight.setText(String.format("Weight: %s%s", item_weight, weight_unit_name));

        //item image set
        if (base64Image != null) {
            if (base64Image.length() < 6) {

                holder.itemImage.setImageResource(R.drawable.image_placeholder);

            } else {

                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.itemImage.setImageBitmap(bitmap);

            }
        }

        //open item details
        holder.itemView.setOnClickListener(v ->
                context.startActivity(new Intent(context, SalesItemsDetailsActivity.class)
                        .putExtra("itemId", item_id))
        );

        //add item to cart
        holder.addToCart.setOnClickListener(v -> {

            if (item_stock != null && Integer.parseInt(item_stock) <= 0) {
                Toasty.error(context, "Item Out of stock!").show();
            } else {

                databaseAccess.open();
                int check = databaseAccess.addToCart(
                        item_id,
                        item_weight,
                        weight_unit_id,
                        item_sell_price,
                        1,
                        item_stock
                );

                //let user know if item added in cart successfully
                if (check == 1) {

                    Toasty.success(context, R.string.item_added_to_cart, Toast.LENGTH_SHORT).show();

                } else if (check == 2) {

                    Toasty.info(context, R.string.already_added_to_cart, Toast.LENGTH_SHORT).show();

                } else {

                    Toasty.error(context, R.string.item_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                }

                databaseAccess.open();
                int count = databaseAccess.getCartItemCount();

                if (textCount != null) {
                    if (count == 0) {
                        textCount.setVisibility(View.INVISIBLE);
                    } else {
                        textCount.setVisibility(View.VISIBLE);
                        textCount.setText(String.valueOf(count));
                    }
                }


            }

        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    static class SalesItemsViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemWeight, itemPrice, addToCart;

        public SalesItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.itemName);
            itemWeight = itemView.findViewById(R.id.item_weight);
            itemPrice = itemView.findViewById(R.id.item_price);
            addToCart = itemView.findViewById(R.id.addToCart);

        }
    }

}
