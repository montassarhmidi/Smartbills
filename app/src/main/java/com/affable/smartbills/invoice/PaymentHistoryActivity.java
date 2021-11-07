package com.affable.smartbills.invoice;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.invoice.adapter.PaymentHistoryAdapter;
import com.affable.smartbills.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

public class PaymentHistoryActivity extends BaseActivity {

    private TextView textInvoiceValue, textSubTotalValue, textTaxValue, textDiscValue, textTotalAmValue, textTotalPayedValue, textDueValue;
    private RecyclerView recyclerView;
    private LinearLayout noData;

    private String currency;
    private String invoiceID;
    private DatabaseAccess databaseAccess;
    private List<HashMap<String, String>> paymentList;
    private PaymentHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        getSupportActionBar().setTitle(R.string.payment_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        databaseAccess = DatabaseAccess.getInstance(this);

        //init views
        textInvoiceValue = findViewById(R.id.textInvoiceValue);
        textSubTotalValue = findViewById(R.id.textSubTotalValue);
        textTaxValue = findViewById(R.id.textTaxValue);
        textDiscValue = findViewById(R.id.textDiscValue);
        textTotalAmValue = findViewById(R.id.textTotalAmValue);
        textTotalPayedValue = findViewById(R.id.textTotalPayedValue);
        textDueValue = findViewById(R.id.textDueValue);
        noData = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //get data though intent
        invoiceID = getIntent().getStringExtra("invoice_id");

        getPaymentsList();
        databaseAccess.open();
        currency = databaseAccess.getCurrency();

        //first payment details
        getPaymentInfo();


    }


    private void getPaymentsList() {

        if (invoiceID != null) {

            databaseAccess.open();
            paymentList = databaseAccess.getPaymentList(invoiceID);

            if (paymentList.isEmpty()) {

                //no data visualizer
                recyclerView.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);

            } else {

                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                }

                adapter = new PaymentHistoryAdapter(this, paymentList);
                recyclerView.setAdapter(adapter);

            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void getPaymentInfo() {

        if (invoiceID != null) {

            databaseAccess.open();
            HashMap<String, String> orderMap = databaseAccess.getSingleOrderList(invoiceID);

            if (orderMap != null) {
                textInvoiceValue.setText(invoiceID);
                textSubTotalValue.setText(" + " + orderMap.get("sub_total") + currency);
                textTaxValue.setText(" + " + orderMap.get("tax") + currency);
                textDiscValue.setText(" - " + orderMap.get("discount") + currency);

            }

            databaseAccess.open();
            int paymentCount = databaseAccess.getPaymentCount(invoiceID);

            //get payment info
            databaseAccess.open();
            HashMap<String, String> paymentMap = databaseAccess.getSinglePayment(invoiceID, paymentCount + "");

            if (paymentMap != null) {
                textTotalAmValue.setText(" = " + paymentMap.get("total_money") + currency);
                textTotalPayedValue.setText(" - " + paymentMap.get("total_payed") + currency);
                textDueValue.setText(" = " + paymentMap.get("due_money") + currency);
            }

        }
    }

    //for top back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}