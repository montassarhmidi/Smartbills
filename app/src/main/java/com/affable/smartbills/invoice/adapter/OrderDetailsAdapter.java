package com.affable.smartbills.invoice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ItemViewHolder> {


    private Context context;
    private List<HashMap<String, String>> itemList;

    private DatabaseAccess databaseAccess;
    private DecimalFormat f;

    public OrderDetailsAdapter(Context context, List<HashMap<String, String>> itemList) {
        this.context = context;
        this.itemList = itemList;
        databaseAccess = DatabaseAccess.getInstance(context);
        f = new DecimalFormat("#0.00");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_order_details, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        HashMap<String, String> map = itemList.get(position);

        String itemID = map.get("item_id");


        databaseAccess.open();
        String itemName = databaseAccess.getItemName(itemID);

        databaseAccess.open();
        String base64Image = databaseAccess.getItemImage(itemID);

        String itemWeight = map.get("item_weight");
        int qty = Integer.parseInt(map.get("item_qty"));
        double price = Double.parseDouble(map.get("item_price"));

        //set item info
        holder.itemNameValue.setText(itemName);
        holder.itemWeightValue.setText(itemWeight);
        holder.itemPriceValue.setText(price+" x "+qty+" = "+f.format(qty * price));

        //item image set
        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64Base", base64Image);
                holder.imgItem.setImageResource(R.drawable.image_placeholder);
            } else {

                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgItem.setImageBitmap(bitmap);

            }
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameValue, itemWeightValue, itemPriceValue;
        ImageView imgItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameValue = itemView.findViewById(R.id.itemNameValue);
            itemWeightValue = itemView.findViewById(R.id.itemWeightValue);
            itemPriceValue = itemView.findViewById(R.id.itemPriceValue);
            imgItem = itemView.findViewById(R.id.img_item);

        }
    }

}
