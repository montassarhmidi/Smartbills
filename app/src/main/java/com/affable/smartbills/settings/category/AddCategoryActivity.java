package com.affable.smartbills.settings.category;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class AddCategoryActivity extends BaseActivity {


    private EditText etxtCategoryName;
    private Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        etxtCategoryName = findViewById(R.id.etxt_category_name);
        btnAddCategory = findViewById(R.id.btn_add_category);

        btnAddCategory.setOnClickListener(v -> {
            String category = etxtCategoryName.getText().toString().trim();
            if (category.isEmpty()) {
                etxtCategoryName.setError("enter the category name first!");
                etxtCategoryName.requestFocus();
            } else {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCategoryActivity.this);
                databaseAccess.open();

                if (databaseAccess.addCategory(category, ""))
                    Toasty.success(AddCategoryActivity.this, getString(R.string.category_added), Toast.LENGTH_SHORT).show();
                else
                    Toasty.error(AddCategoryActivity.this, getString(R.string.category_add_failed), Toast.LENGTH_SHORT).show();

                AddCategoryActivity.this.finish();
                startActivity(new Intent(AddCategoryActivity.this, CategoriesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


            }
        });

    }

}