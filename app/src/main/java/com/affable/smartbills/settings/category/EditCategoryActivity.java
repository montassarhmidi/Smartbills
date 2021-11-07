package com.affable.smartbills.settings.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditCategoryActivity extends BaseActivity {

    private EditText etxtCategoryName;
    private Button btnUpdate, btnEditCategory, btnCancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        etxtCategoryName = findViewById(R.id.etxt_category_name);
        btnUpdate = findViewById(R.id.btn_update);
        btnEditCategory = findViewById(R.id.btn_edit_category);
        btnCancle = findViewById(R.id.btn_cancle);

        etxtCategoryName.setEnabled(false);

        String categoryId = getIntent().getStringExtra("CATEGORY_ID");
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryId != null && categoryName != null)
            etxtCategoryName.setText(categoryName);

        btnEditCategory.setOnClickListener(v -> {
            etxtCategoryName.setEnabled(true);
            btnEditCategory.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnCancle.setVisibility(View.VISIBLE);
        });


        btnUpdate.setOnClickListener(v -> {
            String category = etxtCategoryName.getText().toString().trim();
            if (category.isEmpty()) {
                etxtCategoryName.setError("enter the category name first!");
                etxtCategoryName.requestFocus();
            } else {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCategoryActivity.this);
                databaseAccess.open();

                if (databaseAccess.updateCategory(categoryId, category))
                    Toasty.success(EditCategoryActivity.this, getString(R.string.category_updated), Toast.LENGTH_SHORT).show();
                else
                    Toasty.error(EditCategoryActivity.this, getString(R.string.category_update_failed), Toast.LENGTH_SHORT).show();

                EditCategoryActivity.this.finish();
                startActivity(new Intent(EditCategoryActivity.this, CategoriesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        btnCancle.setOnClickListener(v -> {

            if (categoryId != null && categoryName != null)
                etxtCategoryName.setText(categoryName);

            etxtCategoryName.setEnabled(false);
            btnUpdate.setVisibility(View.GONE);
            btnCancle.setVisibility(View.GONE);
            btnEditCategory.setVisibility(View.VISIBLE);
        });


    }
}