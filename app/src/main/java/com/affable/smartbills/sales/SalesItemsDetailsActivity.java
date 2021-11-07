package com.affable.smartbills.sales;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Tools;
import com.affable.smartbills.utils.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SalesItemsDetailsActivity extends BaseActivity {

    private ImageView imageView;
    private TextView itemName, itemCode, itemCategory, itemStock, itemWeight, itemDescription, sellPrice, buyPrice;
    private ImageButton btToggleDescription;
    private View lytExpandDescription;
    private String itemId;
    private FloatingActionButton fab;
    private NestedScrollView nestedScrollView;
    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_items_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init views
        imageView = findViewById(R.id.image);
        itemName = findViewById(R.id.itemName);
        itemCode = findViewById(R.id.itemCode);
        itemCategory = findViewById(R.id.itemCategory);
        itemStock = findViewById(R.id.itemStock);
        itemWeight = findViewById(R.id.itemWeight);
        sellPrice = findViewById(R.id.sellPrice);
        buyPrice = findViewById(R.id.buyPrice);
        itemDescription = findViewById(R.id.item_description);
        fab = findViewById(R.id.fab);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        btToggleDescription = (ImageButton) findViewById(R.id.bt_toggle_description);
        lytExpandDescription = (View) findViewById(R.id.lyt_expand_description);


        itemId = getIntent().getStringExtra("itemId");

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        HashMap<String, String> itemInfo = databaseAccess.getItemInfo(itemId);

        String nameString = itemInfo.get("item_name");
        String codeString = itemInfo.get("item_code");
        String stockString = itemInfo.get("item_stock");
        String sellPriceString = itemInfo.get("item_sell_price");
        String buyPriceString = itemInfo.get("item_buy_price");
        String weightString = itemInfo.get("item_weight");
        String base64Image = itemInfo.get("item_image");
        String descriptionString = itemInfo.get("item_description");

        //get Category using ID
        String category_id = itemInfo.get("item_category");
        databaseAccess.open();
        String categoryString = databaseAccess.getCategory(category_id);

        //get Currency user selected
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        //get exact WeightUnit of Item using weight_unit_id
        String weight_unit_id = itemInfo.get("item_weight_unit_id");
        databaseAccess.open();
        String item_weight_unit_id = databaseAccess.getWeightUnit(weight_unit_id);

        //set values in UI
        itemName.setText(nameString);
        itemCategory.setText(categoryString);
        itemStock.setText(String.format("%s %s", stockString, "Left"));
        itemCode.setText(String.format("%s", codeString));
        buyPrice.setText(String.format("%s %s", buyPriceString, currency));
        sellPrice.setText(String.format("%s %s", sellPriceString, currency));
        itemWeight.setText(String.format("%s %s", weightString, item_weight_unit_id));

        if (descriptionString != null && descriptionString.isEmpty())
            itemDescription.setText(R.string.no_description_available);
        else
            itemDescription.setText(descriptionString);

        //item image set
        if (base64Image != null) {
            if (base64Image.length() < 6) {

                imageView.setImageResource(R.drawable.img_product_placeholder);

            } else {

                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);

            }
        }

        //add item to cart
        fab.setOnClickListener(v -> {

            if (stockString != null && Integer.parseInt(stockString) <= 0) {
                Toasty.error(this, getString(R.string.item_out_of_stock)).show();
            } else {

                databaseAccess.open();
                int check = databaseAccess.addToCart(
                        itemId,
                        weightString,
                        weight_unit_id,
                        sellPriceString,
                        1,
                        stockString
                );

                //let user know if item added in cart successfully
                if (check == 1) {
                    Toasty.success(this, R.string.item_added_to_cart, Toast.LENGTH_SHORT).show();
                } else if (check == 2) {
                    Toasty.info(this, R.string.already_added_to_cart, Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.error(this, R.string.item_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();
                }
                onBackPressed();

            }

        });

        //toggle description details
        btToggleDescription.setOnClickListener(view -> toggleSection(view, lytExpandDescription));

    }


    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimation.expand(lyt, () -> Tools.nestedScrollTo(nestedScrollView, lyt));
        } else {
            ViewAnimation.collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }


}