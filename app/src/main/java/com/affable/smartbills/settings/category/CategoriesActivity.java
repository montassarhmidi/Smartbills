package com.affable.smartbills.settings.category;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

public class CategoriesActivity extends BaseActivity {

    private EditText searchBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private View noData;
    private DatabaseAccess databaseAccess;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        noData = findViewById(R.id.no_data);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(CategoriesActivity.this);

        databaseAccess.open();
        //set categories in recycle
        List<HashMap<String, String>> categories = databaseAccess.getItemCategories();
        if (categories.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new CategoryAdapter(this, categories);
            recyclerView.setAdapter(adapter);
        }

        //fab
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
            startActivity(intent);
        });

        //search
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                databaseAccess.open();
                List<HashMap<String, String>> searchList = databaseAccess.searchCategories(s.toString());

                if (searchList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);

                } else {

                    noData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    adapter = new CategoryAdapter(CategoriesActivity.this, searchList);
                    recyclerView.setAdapter(adapter);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

    }


}