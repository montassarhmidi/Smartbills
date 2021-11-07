package com.affable.smartbills.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private Context context;
    private List<HashMap<String, String>> cart_item_list;

    private TextView txt_total_price;
    private Button btnSubmitOrder;
    private LinearLayout no_data;
    private RecyclerView recyclerView;

    public static double totalPrice;
    int quantity, stock;


    public CartItemAdapter(Context context, List<HashMap<String, String>> cart_item_list,
                           TextView txt_total_price, Button btnSubmitOrder, LinearLayout no_data, RecyclerView recyclerView) {
        this.context = context;
        this.cart_item_list = cart_item_list;
        this.txt_total_price = txt_total_price;
        this.btnSubmitOrder = btnSubmitOrder;
        this.no_data = no_data;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_cart_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String cart_id = cart_item_list.get(position).get("cart_id");
        final String item_id = cart_item_list.get(position).get("item_id");

        databaseAccess.open();
        String item_name = databaseAccess.getItemName(item_id);

        String item_price = cart_item_list.get(position).get("item_price");
        String item_weight = cart_item_list.get(position).get("item_weight");
        String item_stock = cart_item_list.get(position).get("stock");
        String item_qty = cart_item_list.get(position).get("item_qty");

        //get Currency user selected
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        //get exact WeightUnit of Item using weight_unit_id
        String weight_unit_id = cart_item_list.get(position).get("item_weight_unit");
        databaseAccess.open();
        String weight_unit_name = databaseAccess.getWeightUnit(weight_unit_id);

        //set the values in recyclerItem
        holder.txt_item_name.setText(item_name);
        holder.txt_price.setText(String.format("Price: %s%s", item_price, currency));
        holder.txt_weight.setText(String.format("Weight: %s%s", item_weight, weight_unit_name));
        holder.txt_number.setText(item_qty);


        //item image set
        databaseAccess.open();
        String base64Image = databaseAccess.getItemImage(item_id);
        Log.d("ItemImage", base64Image);

        if (base64Image.isEmpty() || base64Image.length() < 6) {

            holder.cart_product_image.setImageResource(R.drawable.image_placeholder);

        } else {

            byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.cart_product_image.setImageBitmap(bitmap);

        }

        //set total price
        setTotalPrice(databaseAccess, currency);

        holder.img_delete.setOnClickListener(v -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getString(R.string.delete_item))
                    .setIcon(R.drawable.ic_delete)
                    .setNegativeButton("Cancel", (dialoginterface, i) -> dialoginterface.cancel())
                    .setPositiveButton("Ok", (dialoginterface, i) -> {

                        databaseAccess.open();
                        boolean deleteProduct = databaseAccess.deleteItemFromCart(cart_id);

                        if (deleteProduct) {

                            Toasty.error(context, context.getString(R.string.product_removed_from_cart), Toast.LENGTH_SHORT).show();

                            //for delete cart item dynamically
                            // Remove CartItem from Cart List
                            cart_item_list.remove(holder.getAdapterPosition());

                            // Notify that item at position has been removed
                            notifyItemRemoved(holder.getAdapterPosition());

                            // Calculate Cart's Total Price Again
                            setTotalPrice(databaseAccess, currency);

                        } else {
                            Toasty.error(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }

                        //if all items are deleted then show no data indicator & invisible other views
                        databaseAccess.open();
                        int itemCount = databaseAccess.getCartItemCount();

                        if (itemCount <= 0) {

                            txt_total_price.setVisibility(View.INVISIBLE);
                            btnSubmitOrder.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                            no_data.setVisibility(View.VISIBLE);

                        } else {

                            txt_total_price.setVisibility(View.VISIBLE);
                            btnSubmitOrder.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            no_data.setVisibility(View.GONE);

                        }

                    }).show();


        });

        holder.txt_plus.setOnClickListener(v -> {

            quantity = Integer.parseInt(holder.txt_number.getText().toString());
            stock = Integer.parseInt(item_stock);

            if (quantity < stock) {

                quantity = quantity + 1;

                databaseAccess.open();

                if (databaseAccess.updateProductQty(cart_id, String.valueOf(quantity))) {
                    setTotalPrice(databaseAccess, currency);
                    holder.txt_number.setText(quantity + "");
                }

            } else {

                Toasty.error(context, context.getString(R.string.stock_limit_crossed), Toast.LENGTH_SHORT).show();

            }
        });

        holder.txt_minus.setOnClickListener(v -> {
            quantity = Integer.parseInt(holder.txt_number.getText().toString());

            if (quantity > 0) {
                quantity--;


            } else {
                Toasty.error(context, context.getString(R.string.stock_limit_crossed), Toast.LENGTH_SHORT).show();
            }
            if (quantity == 0) {
                holder.img_delete.callOnClick();
            } else {
                databaseAccess.open();
                if (databaseAccess.updateProductQty(cart_id, String.valueOf(quantity))) {
                    setTotalPrice(databaseAccess, currency);
                    holder.txt_number.setText(quantity + "");
                }
            }

        });

    }

    private void setTotalPrice(DatabaseAccess databaseAccess, String currency) {

        DecimalFormat f = new DecimalFormat("#0.00");
        databaseAccess.open();
        totalPrice = databaseAccess.getTotalPrice();
        txt_total_price.setText(
                String.format("%s: %s%s",
                        context.getString(R.string.total_price),
                        f.format(totalPrice),
                        currency
                )
        );
    }

    @Override
    public int getItemCount() {
        return cart_item_list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cart_product_image, img_delete;
        TextView txt_item_name, txt_weight, txt_price, txt_number;
        ImageButton txt_minus, txt_plus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_product_image = itemView.findViewById(R.id.cart_product_image);
            txt_item_name = itemView.findViewById(R.id.txt_item_name);
            txt_weight = itemView.findViewById(R.id.txt_weight);
            txt_price = itemView.findViewById(R.id.txt_price);

            img_delete = itemView.findViewById(R.id.img_delete);

            txt_minus = itemView.findViewById(R.id.txt_minus);
            txt_plus = itemView.findViewById(R.id.txt_plus);
            txt_number = itemView.findViewById(R.id.txt_number);

        }
    }

}
