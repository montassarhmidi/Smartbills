package com.affable.smartbills.items;

import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class EditItemsActivity extends BaseActivity {

    public static TextInputEditText itemCode;
    private TextInputEditText itemName, itemCategory, itemBuyPrice, itemSellPrice, itemStock,
            itemWeight, itemWeightUnit, itemDescription;
    private ImageView btnScanner, addItemImage;
    private MaterialButton editItem, updateItem, cancelItemUpdate;

    private String mediaPath, stringImage = "N/A";

    private List<HashMap<String, String>> itemCategoryList, itemWeightUnitList;
    private List<String> categoryNames, weightUnits;
    private ArrayAdapter<String> categoryAdapter, weightUnitAdapter;
    private String selectedCategoryNameID = "1", selectedWeightUnitID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);

        getSupportActionBar().setTitle(R.string.update_items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        itemName = findViewById(R.id.itemName);
        itemCode = findViewById(R.id.itemCode);
        itemCategory = findViewById(R.id.itemCategory);
        itemBuyPrice = findViewById(R.id.item_buy_price);
        itemSellPrice = findViewById(R.id.item_sell_price);
        itemStock = findViewById(R.id.item_stock);
        itemWeight = findViewById(R.id.item_weight);
        itemWeightUnit = findViewById(R.id.item_weight_unit);
        itemDescription = findViewById(R.id.item_description);
        btnScanner = findViewById(R.id.btn_scanner);
        addItemImage = findViewById(R.id.add_item_image);
        editItem = findViewById(R.id.edit_item);
        updateItem = findViewById(R.id.update_item);
        cancelItemUpdate = findViewById(R.id.cancel_item_update);

        //disable until edit btn clicked
        itemName.setEnabled(false);
        itemCode.setEnabled(false);
        btnScanner.setEnabled(false);
        itemCategory.setEnabled(false);
        itemBuyPrice.setEnabled(false);
        itemSellPrice.setEnabled(false);
        itemStock.setEnabled(false);
        itemWeight.setEnabled(false);
        itemWeightUnit.setEnabled(false);
        itemDescription.setEnabled(false);
        addItemImage.setEnabled(false);

        //get data from intent/Bundle
        final String item_id_str = getIntent().getStringExtra("item_id");

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditItemsActivity.this);
        databaseAccess.open();
        HashMap<String, String> itemData = databaseAccess.getItemInfo(item_id_str);

        String item_name_str = itemData.get("item_name");
        String item_code_str = itemData.get("item_code");
        String item_description_str = itemData.get("item_description");
        String item_buy_price_str = itemData.get("item_buy_price");
        String item_sell_price_str = itemData.get("item_sell_price");
        String item_stock_str = itemData.get("item_stock");
        String item_weight_str = itemData.get("item_weight");
        stringImage = itemData.get("item_image");

        //get exact category of Item using category_id
        selectedCategoryNameID = itemData.get("item_category");
        databaseAccess.open();
        String item_category_str = databaseAccess.getCategory(selectedCategoryNameID);

        //get exact WeightUnit of Item using weight_unit_id
        selectedWeightUnitID = itemData.get("item_weight_unit_id");
        databaseAccess.open();
        String item_weight_unit_id_str = databaseAccess.getWeightUnit(selectedWeightUnitID);


        //set data in UI components
        itemName.setText(item_name_str);
        itemCode.setText(item_code_str);
        itemCategory.setText(item_category_str);
        itemBuyPrice.setText(item_buy_price_str);
        itemSellPrice.setText(item_sell_price_str);
        itemStock.setText(item_stock_str);
        itemWeight.setText(item_weight_str);
        itemWeightUnit.setText(item_weight_unit_id_str);
        itemDescription.setText(item_description_str);

        //item image set
        if (stringImage != null) {
            if (stringImage.length() < 6) {
                addItemImage.setImageResource(R.drawable.image_placeholder);
            } else {

                byte[] bytes = Base64.decode(stringImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                addItemImage.setImageBitmap(bitmap);

            }
        }


        //scanner button
        btnScanner.setOnClickListener(v -> startActivity(new Intent(EditItemsActivity.this, ScannerViewActivity.class)
                .putExtra(Constant.SCANNER_VIEW_RETURN_ACTIVITY, "EditItemsActivity")));

        //update with new image
        addItemImage.setOnClickListener(v -> {

            Intent intent = new Intent(EditItemsActivity.this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
            startActivityForResult(intent, 1213);

        });

        //add data form DB to categoryNames & weightUnits ArrayList
        createCategoryAndWeightUnitList();

        //category chooser
        itemCategory.setOnClickListener(v -> categoryChooserDialog());

        //weight unit chooser
        itemWeightUnit.setOnClickListener(v -> weightUnitChooserDialog());


        //on edit btn clicked
        editItem.setOnClickListener(v -> {

            //enable when edit btn clicked
            itemName.setEnabled(true);
            itemCode.setEnabled(true);
            btnScanner.setEnabled(true);
            itemCategory.setEnabled(true);
            itemBuyPrice.setEnabled(true);
            itemSellPrice.setEnabled(true);
            itemStock.setEnabled(true);
            itemWeight.setEnabled(true);
            itemWeightUnit.setEnabled(true);
            itemDescription.setEnabled(true);
            addItemImage.setEnabled(true);

            editItem.setVisibility(View.GONE);
            updateItem.setVisibility(View.VISIBLE);
            cancelItemUpdate.setVisibility(View.VISIBLE);

        });

        //on cancel_item_update btn clicked
        cancelItemUpdate.setOnClickListener(v -> {

            updateItem.setVisibility(View.GONE);
            cancelItemUpdate.setVisibility(View.GONE);
            editItem.setVisibility(View.VISIBLE);

            itemName.setEnabled(false);
            itemCode.setEnabled(false);
            btnScanner.setEnabled(false);
            itemCategory.setEnabled(false);
            itemBuyPrice.setEnabled(false);
            itemSellPrice.setEnabled(false);
            itemStock.setEnabled(false);
            itemWeight.setEnabled(false);
            itemWeightUnit.setEnabled(false);
            itemDescription.setEnabled(false);
            addItemImage.setEnabled(false);

        });


        updateItem.setOnClickListener(v -> {

            String item_nameSt = itemName.getText().toString();
            String item_codeSt = itemCode.getText().toString();
            String item_categoryNameSt = itemCategory.getText().toString();
            String item_buy_priceSt = itemBuyPrice.getText().toString();
            String item_sell_priceSt = itemSellPrice.getText().toString();
            String item_stockSt = itemStock.getText().toString();
            String item_weightSt = itemWeight.getText().toString();
            String item_weight_unitSt = itemWeightUnit.getText().toString();
            String item_descriptionSt = itemDescription.getText().toString();

            if (item_nameSt.isEmpty()) {
                itemName.setError(getString(R.string.item_name_cannot_be_empty));
                itemName.requestFocus();
            } else if (item_codeSt.isEmpty()) {
                itemCode.setError(getString(R.string.item_code_cannot_be_empty));
                itemCode.requestFocus();
            } else if (item_categoryNameSt.isEmpty()) {
                itemCategory.setError(getString(R.string.item_category_cannot_be_empty));
                itemCategory.requestFocus();
            } else if (item_buy_priceSt.isEmpty()) {
                itemBuyPrice.setError(getString(R.string.item_buy_price_cannot_be_empty));
                itemBuyPrice.requestFocus();
            } else if (item_sell_priceSt.isEmpty()) {
                itemSellPrice.setError(getString(R.string.item_sell_price_cannot_be_empty));
                itemSellPrice.requestFocus();
            } else if (item_weightSt.isEmpty()) {
                itemWeight.setError(getString(R.string.item_name_cannot_be_empty));
                itemWeight.requestFocus();
            } else if (item_weight_unitSt.isEmpty()) {
                itemWeightUnit.setError(getString(R.string.item_name_cannot_be_empty));
                itemWeightUnit.requestFocus();
            } else {

                DatabaseAccess databaseAccess1 = DatabaseAccess.getInstance(EditItemsActivity.this);
                databaseAccess1.open();

                boolean check = databaseAccess1.updateItem(
                        item_id_str,
                        item_nameSt, item_codeSt, selectedCategoryNameID, item_buy_priceSt,
                        item_sell_priceSt, item_stockSt, stringImage,
                        selectedWeightUnitID, item_weightSt, item_descriptionSt
                );

                if (check) {
                    Toasty.success(EditItemsActivity.this, R.string.item_sucessfully_updated, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditItemsActivity.this, MainActivity.class);
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_ITEMS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toasty.error(EditItemsActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                }

            }


        });


    }


    private void createCategoryAndWeightUnitList() {

        categoryNames = new ArrayList<>();
        weightUnits = new ArrayList<>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditItemsActivity.this);

        databaseAccess.open();
        itemCategoryList = databaseAccess.getItemCategories();
        Log.i("ItemCategories", itemCategoryList.size() + "");

        for (int i = 0; i < itemCategoryList.size(); i++) {

            categoryNames.add(itemCategoryList.get(i).get("category_name"));

        }
        Log.d("categoryNames", categoryNames.size() + "");


        databaseAccess.open();
        itemWeightUnitList = databaseAccess.getItemWeightUnit();
        for (int i = 0; i < itemWeightUnitList.size(); i++) {

            weightUnits.add(itemWeightUnitList.get(i).get("unit_name"));

        }

    }


    private void categoryChooserDialog() {

        categoryAdapter = new ArrayAdapter<>(EditItemsActivity.this, android.R.layout.simple_list_item_1);
        categoryAdapter.addAll(categoryNames);

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditItemsActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        dialog_title.setText(R.string.product_category);
        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(categoryAdapter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());


        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();

            final String selectedItem = categoryAdapter.getItem(position);
            itemCategory.setText(selectedItem);

            //get category ID
            for (int i = 0; i < categoryNames.size(); i++) {

                if (categoryNames.get(i).equalsIgnoreCase(selectedItem)) {
                    selectedCategoryNameID = itemCategoryList.get(i).get("category_id");
                }

            }


        });


    }


    private void weightUnitChooserDialog() {

        weightUnitAdapter = new ArrayAdapter<>(EditItemsActivity.this, android.R.layout.simple_list_item_1);
        weightUnitAdapter.addAll(weightUnits);

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditItemsActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        MaterialButton dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        dialog_title.setText(R.string.product_weight_unit);
        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(weightUnitAdapter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weightUnitAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());

        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();

            final String selectedItem = weightUnitAdapter.getItem(position);
            itemWeightUnit.setText(selectedItem);

            //get weight unit ID
            for (int i = 0; i < weightUnits.size(); i++) {

                if (weightUnits.get(i).equalsIgnoreCase(selectedItem)) {
                    selectedWeightUnitID = itemWeightUnitList.get(i).get("unit_id");
                }

            }


        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == 1213 && resultCode == RESULT_OK && data != null) {

                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);
                addItemImage.setImageBitmap(selectedImage);

                stringImage = encodeImage(selectedImage);

            }

        } catch (Exception e) {
            Toasty.error(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}