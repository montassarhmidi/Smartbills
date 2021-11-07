package com.affable.smartbills.sales;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.cart.CartActivity;
import com.affable.smartbills.database.DatabaseAccess;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SalesFragment extends Fragment {

    private EditText etxtSearch;
    private TextView resetTxt, textCount, itemsTxt;
    private FloatingActionButton fab;
    private LinearLayout noDataIndicator;

    private CategoryAdapter categoryAdapter;
    private SalesItemsAdapter itemsAdapter;
    private RecyclerView categoriesRecycler, itemsRecyclerview;

    private DatabaseAccess databaseAccess;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

        //instantiating UI components
        etxtSearch = view.findViewById(R.id.etxt_search);
        resetTxt = view.findViewById(R.id.reset_txt);
        textCount = view.findViewById(R.id.text_count);
        itemsTxt = view.findViewById(R.id.items_txt);
        fab = view.findViewById(R.id.fab);
        noDataIndicator = view.findViewById(R.id.no_data);

        categoriesRecycler = view.findViewById(R.id.categories_recycler);
        itemsRecyclerview = view.findViewById(R.id.product_recyclerView);

        databaseAccess = DatabaseAccess.getInstance(getContext());


        //set search option
        setSearchOption();

        //show category list
        showCategoriesRecycler();

        //set item list
        showItemListRecycler();

        //reset Item list
        resetTxt.setOnClickListener(v -> {

            itemsTxt.setText(R.string.all_items);
            showItemsList();
        });

        //go to cart
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), CartActivity.class)));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //show cart items count
        databaseAccess.open();
        int count = databaseAccess.getCartItemCount();
        if (count == 0) {
            textCount.setVisibility(View.INVISIBLE);
        } else {
            textCount.setVisibility(View.VISIBLE);
            textCount.setText(String.valueOf(count));
        }
    }

    private void setSearchOption() {

        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                databaseAccess.open();

                //get data from local database
                List<HashMap<String, String>> searchItemsList;
                searchItemsList = databaseAccess.searchItems(s.toString());

                if (searchItemsList.isEmpty()) {

                    itemsRecyclerview.setVisibility(View.GONE);
                    noDataIndicator.setVisibility(View.VISIBLE);

                } else {

                    if (itemsRecyclerview.getVisibility() == View.GONE) {
                        itemsRecyclerview.setVisibility(View.VISIBLE);
                    }

                    if (noDataIndicator.getVisibility() == View.VISIBLE) {
                        noDataIndicator.setVisibility(View.GONE);
                    }

                    itemsAdapter = new SalesItemsAdapter(getContext(), searchItemsList, textCount);
                    itemsRecyclerview.setAdapter(itemsAdapter);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }

        });
    }

    private void showCategoriesRecycler() {

        //set horizontal layout for category Adapter
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecycler.setLayoutManager(categoryLayoutManager);
        categoriesRecycler.setHasFixedSize(true);

        //get categories data from DB
        databaseAccess.open();
        List<HashMap<String, String>> categoriesList = databaseAccess.getItemCategories();

        //check data is empty or not then set adapter
        if (categoriesList.size() <= 0) {

            Toasty.info(getContext(), R.string.no_data_found, Toast.LENGTH_SHORT).show();

        } else {

            categoryAdapter = new CategoryAdapter(
                    getContext(), categoriesList, itemsRecyclerview, noDataIndicator, itemsTxt
            );
            categoriesRecycler.setAdapter(categoryAdapter);

        }

    }

    private void showItemListRecycler() {

        GridLayoutManager itemsLayoutManager = new GridLayoutManager(getContext(), 3);
        itemsRecyclerview.setLayoutManager(itemsLayoutManager);
        itemsRecyclerview.setHasFixedSize(true);

        showItemsList();

    }

    private void showItemsList() {

        databaseAccess.open();
        List<HashMap<String, String>> itemsList = databaseAccess.getItems();

        if (itemsList.size() <= 0) {

            itemsRecyclerview.setVisibility(View.GONE);
            noDataIndicator.setVisibility(View.VISIBLE);

        } else {

            noDataIndicator.setVisibility(View.GONE);
            itemsRecyclerview.setVisibility(View.VISIBLE);

            itemsAdapter = new SalesItemsAdapter(getContext(), itemsList, textCount);
            itemsRecyclerview.setAdapter(itemsAdapter);

        }

    }


}