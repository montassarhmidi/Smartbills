package com.affable.smartbills.invoice.order_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.invoice.adapter.InvoiceAdapter;
import com.affable.smartbills.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PendingOrderFragment extends Fragment {

    private EditText etxtSearch;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout noData;

    private DatabaseAccess databaseAccess;
    List<HashMap<String, String>> orderList;
    private InvoiceAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_order, container, false);

        etxtSearch = view.findViewById(R.id.etxt_search);
        noData = view.findViewById(R.id.no_data);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);

        databaseAccess = DatabaseAccess.getInstance(getContext());

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getOrderList();

        //add search feature
        searchFeature();


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {

                if (!orderList.isEmpty()) {

                    databaseAccess.open();
                    ArrayList<HashMap<String, String>> newOrderList = databaseAccess.getOrderListByStatus(Constant.PENDING);

                    if (newOrderList.size() != orderList.size()) {
                        orderList.clear();
                        orderList.addAll(newOrderList);
                        adapter.notifyDataSetChanged();
                    }
                } else getOrderList();

                swipeRefresh.setRefreshing(false);

            }
        });

    }



    private void getOrderList() {

        databaseAccess.open();
        orderList = databaseAccess.getOrderListByStatus(Constant.PENDING);

        if (orderList.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);

        } else {

            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);

            adapter = new InvoiceAdapter(getContext(), orderList, recyclerView, noData);
            recyclerView.setAdapter(adapter);

        }

    }

    private void searchFeature() {

        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                databaseAccess.open();
                List<HashMap<String, String>> orderList = databaseAccess.searchOrder(s.toString(), Constant.PENDING);

                if (orderList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);

                } else {

                    recyclerView.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);

                    adapter = new InvoiceAdapter(getContext(), orderList, recyclerView, noData);
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