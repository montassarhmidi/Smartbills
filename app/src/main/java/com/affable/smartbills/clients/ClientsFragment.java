package com.affable.smartbills.clients;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.database.DatabaseOpenHelper;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ClientsFragment extends Fragment {

    private EditText searchTxt;
    private RecyclerView recyclerView;
    private ClientsAdapter adapter;
    private List<HashMap<String, String>> clientsList;
    private ProgressDialog loading;
    private boolean clicked = true;
    private FloatingActionButton fab, export, add;
    private View noDataIndicator, expandFab;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        //instantiating UI components
        searchTxt = view.findViewById(R.id.etxt_search);
        recyclerView = view.findViewById(R.id.recyclerView);

        fab = view.findViewById(R.id.fab);
        add = view.findViewById(R.id.add);
        export = view.findViewById(R.id.export);
        expandFab = view.findViewById(R.id.expand_fab);

        noDataIndicator = view.findViewById(R.id.no_data);

        rotateOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_anim);

        //setting fab
        fab.setOnClickListener(v -> setExpandedFab());
        add.setOnClickListener(v -> {
            setExpandedFab();
            startActivity(new Intent(getContext(), AddClientActivity.class));
        });
        export.setOnClickListener(v -> {
            setExpandedFab();
            folderChooser();
        });

        //set recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //database operation
        getClientDataFromDB();

        //search operation
        setSearchOption();

        return view;
    }


    private void getClientDataFromDB() {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
        databaseAccess.open();

        //get data from local database
        clientsList = databaseAccess.getClients();

        Log.i("data", "" + clientsList.size());

        if (clientsList.isEmpty()) {
            Toasty.info(getContext(), R.string.no_clients_found, Toasty.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            noDataIndicator.setVisibility(View.VISIBLE);
        } else {

            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            if (noDataIndicator.getVisibility() == View.VISIBLE) {
                noDataIndicator.setVisibility(View.GONE);
            }

            adapter = new ClientsAdapter(getContext(), clientsList, recyclerView, noDataIndicator);
            recyclerView.setAdapter(adapter);

        }

    }

    private void setSearchOption() {

        searchTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchClientsList;

                searchClientsList = databaseAccess.searchClients(s.toString());

                if (searchClientsList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    noDataIndicator.setVisibility(View.VISIBLE);

                } else {

                    if (recyclerView.getVisibility() == View.GONE) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    if (noDataIndicator.getVisibility() == View.VISIBLE) {
                        noDataIndicator.setVisibility(View.GONE);
                    }

                    adapter = new ClientsAdapter(getContext(), clientsList, recyclerView, noDataIndicator);
                    recyclerView.setAdapter(adapter);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }

        });
    }

    private void setExpandedFab() {
        if (clicked) {
            expandFab.setVisibility(View.VISIBLE);
            expandFab.startAnimation(fromBottom);
            fab.startAnimation(rotateOpen);
            add.setClickable(true);
            export.setClickable(true);
            clicked = false;
        } else {
            expandFab.startAnimation(toBottom);
            fab.startAnimation(rotateClose);
            expandFab.setVisibility(View.INVISIBLE);
            add.setClickable(false);
            export.setClickable(false);
            clicked = true;
        }
    }

    public void folderChooser() {
        new ChooserDialog(getContext())

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path);
                    Log.d("path", path);

                })
                .build()
                .show();
    }

    public void onExport(String path) {

        String directoryPath = path;
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getContext(), DatabaseOpenHelper.DATABASE_NAME, directoryPath);
        sqliteToExcel.exportSingleTable("customers", "customers.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(getContext());
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(getContext(), R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(getContext(), R.string.data_export_fail, Toast.LENGTH_SHORT).show();

                Log.d("Error", e.toString());
            }
        });
    }

}