package com.affable.smartbills.invoice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.invoice.adapter.OrderDetailsAdapter;
import com.affable.smartbills.invoice.pdf.InvoicePdf;
import com.affable.smartbills.utils.BaseActivity;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class OrderDetailsActivity extends BaseActivity {

    private TextView invoiceValue, ordertypValue, paymentMethodValue, customerNameValue, orderStatusValue,
            orderTimeValue, orderDateValue, txtSubTotal, txtTax, txtDiscount, txtTotalCost, txtDueAmount;
    private LinearLayout generatePdf, printReceipt, addNewPayment, paymentHistory;
    private RecyclerView recycler;
    private ArrayList<HashMap<String, String>> itemList;
    private DecimalFormat f;
    private DatabaseAccess databaseAccess;
    private OrderDetailsAdapter adapter;
    private String invoiceId, currency;
    private double subTotal, tax, discount, totalPrice, dueMoney, totalPayed;
    private int paymentCount;

    private String paymentMethod, customerName, orderTime, orderDate;
    private HashMap<String, String> customerInfo, userInfo;
    private ArrayList<String[]> stringItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setTitle(R.string.order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        invoiceValue = findViewById(R.id.invoiceValue);
        ordertypValue = findViewById(R.id.ordertypValue);
        paymentMethodValue = findViewById(R.id.paymentMethodValue);
        customerNameValue = findViewById(R.id.customerNameValue);
        orderStatusValue = findViewById(R.id.orderStatusValue);
        orderTimeValue = findViewById(R.id.orderTimeValue);
        orderDateValue = findViewById(R.id.orderDateValue);

        txtSubTotal = findViewById(R.id.txt_sub_total);
        txtTax = findViewById(R.id.txt_tax);
        txtDiscount = findViewById(R.id.txt_discount);
        txtTotalCost = findViewById(R.id.txt_total_cost);
        txtDueAmount = findViewById(R.id.txt_due_amount);

        generatePdf = findViewById(R.id.generatePdf);
        printReceipt = findViewById(R.id.printReceipt);
        addNewPayment = findViewById(R.id.add_new_payment);
        paymentHistory = findViewById(R.id.payment_history);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);

        f = new DecimalFormat("#0.00");
        invoiceId = getIntent().getStringExtra("invoice_id");

        databaseAccess.open();
        currency = databaseAccess.getCurrency();

        getOrderListInfo();
        getOrderDetailsInfo();
        getPaymentInfo();
        collectDataForInvoicePdf();

        //additional feature buttons
        addNewPaymentFeature();
        paymentHistoryFeature();
        generatePdfFeature();
        printReceiptFeature();

    }


    private void addNewPaymentFeature() {

        addNewPayment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                DecimalFormat dFormat = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
                dFormat.applyPattern("#0.00");

                //create custom dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(OrderDetailsActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_payment, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                final ImageButton btn_close = dialogView.findViewById(R.id.btn_close);
                final TextView textTotalValue = dialogView.findViewById(R.id.textTotalValue);
                final TextView textDueValue = dialogView.findViewById(R.id.textDueValue);
                final EditText extPayAmount = dialogView.findViewById(R.id.extPayAmount);
                final TextView textStillDueValue = dialogView.findViewById(R.id.textStillDueValue);
                final Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);

                //set values of dialog
                textTotalValue.setText(dFormat.format(totalPrice) + currency);
                textStillDueValue.setText(dFormat.format(dueMoney) + currency);
                textDueValue.setText(dFormat.format(dueMoney) + currency);

                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                //update still due value by adding textWatcher in extPayAmount
                extPayAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        //type your code here

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        String payedValue = s.toString();

                        if (payedValue.isEmpty()) {

                            textStillDueValue.setText(dFormat.format(dueMoney) + currency);

                        } else {

                            if (payedValue.equals("."))
                                payedValue = "0.";

                            double dPayedValue = Double.parseDouble(payedValue);

                            if (dPayedValue > dueMoney) {
                                extPayAmount.setError(getString(R.string.cant_pay_more_then_due));
                                extPayAmount.requestFocus();
                                textStillDueValue.setText(dFormat.format(dueMoney) + currency);
                            } else if (dPayedValue <= 0) {
                                extPayAmount.setError(getString(R.string.cant_pay_negative_or_empty_amount));
                                extPayAmount.requestFocus();
                                textStillDueValue.setText(dFormat.format(dueMoney) + currency);
                            } else {
                                double stillDue = dueMoney - dPayedValue;
                                textStillDueValue.setText(dFormat.format(stillDue) + currency);
                            }

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //type your code here
                    }
                });

                //on confirm button pressed
                buttonConfirm.setOnClickListener(v1 -> {

                    String payedValue = extPayAmount.getText().toString();

                    if (payedValue.isEmpty()) {
                        extPayAmount.setError("enter the pay amountFirst");
                        extPayAmount.requestFocus();
                    } else if (payedValue.equals(".")) {
                        extPayAmount.setError("Invalid input");
                        extPayAmount.requestFocus();
                    } else if (Double.parseDouble(payedValue) <= 0) {
                        extPayAmount.setError("Can't pay 0 or negative amount");
                        extPayAmount.requestFocus();
                    } else if (Double.parseDouble(payedValue) > dueMoney) {
                        extPayAmount.setError("Can't pay more then the due");
                        extPayAmount.requestFocus();
                    } else {

                        double payed = Double.parseDouble(payedValue) + totalPayed;
                        double due = totalPrice - payed;

                        databaseAccess.open();
                        if (databaseAccess.addPaymentIntoList(
                                invoiceId,
                                String.valueOf(paymentCount + 1),
                                dFormat.format(totalPrice),
                                dFormat.format(payed),
                                payedValue,
                                dFormat.format(due))
                        ) {
                            Toasty.success(OrderDetailsActivity.this, getString(R.string.new_payment_added), Toast.LENGTH_SHORT).show();

                            //update due amount
                            dueMoney = due;
                            txtDueAmount.setText("Due Money: " + dFormat.format(dueMoney) + currency);

                        } else
                            Toasty.error(OrderDetailsActivity.this, getString(R.string.payment_add_failed), Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();

                    }


                });

                //on cross button pressed
                btn_close.setOnClickListener(v12 -> alertDialog.dismiss());

            }
        });

    }

    private void paymentHistoryFeature() {
        paymentHistory.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, PaymentHistoryActivity.class);
            intent.putExtra("invoice_id", invoiceId);
            startActivity(intent);
        });
    }

    private void generatePdfFeature() {

        generatePdf.setOnClickListener(v -> {

            String issueTime = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date()) + " " + new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date());

            InvoicePdf invoicePdf = new InvoicePdf(
                    getApplicationContext(),
                    invoiceId,
                    paymentMethod,
                    orderTime,
                    issueTime,
                    currency,
                    customerInfo,
                    userInfo,
                    stringItemList,
                    subTotal,
                    tax,
                    discount,
                    totalPrice,
                    dueMoney
            );

            try {
                if (invoicePdf.createPdf())
                    invoicePdf.viewPDF();
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
                Toasty.error(OrderDetailsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void printReceiptFeature() {
        printReceipt.setOnClickListener(v -> {

            //TODO: add thermal print feature
            Toast.makeText(OrderDetailsActivity.this, "this feature will be available soon", Toast.LENGTH_SHORT).show();

        });
    }

    @SuppressLint("SetTextI18n")
    private void getOrderListInfo() {

        databaseAccess.open();
        HashMap<String, String> map = databaseAccess.getSingleOrderList(invoiceId);

        String order_type = map.get("order_type");
        paymentMethod = map.get("payment_method");
        customerName = map.get("customer_name");
        String order_status = map.get("order_status");
        orderTime = map.get("order_time");
        orderDate = map.get("order_date");
        subTotal = Double.parseDouble(Objects.requireNonNull(map.get("sub_total")));
        tax = Double.parseDouble(Objects.requireNonNull(map.get("tax")));
        discount = Double.parseDouble(Objects.requireNonNull(map.get("discount")));

        invoiceValue.setText(invoiceId);
        ordertypValue.setText(order_type);
        paymentMethodValue.setText(paymentMethod);
        customerNameValue.setText(customerName);
        orderStatusValue.setText(order_status);
        orderTimeValue.setText(orderTime);
        orderDateValue.setText(orderDate);

    }


    private void getOrderDetailsInfo() {

        databaseAccess.open();
        itemList = databaseAccess.getOrderDetailsList(invoiceId);

        if (!itemList.isEmpty()) {
            adapter = new OrderDetailsAdapter(OrderDetailsActivity.this, itemList);
            recycler.setAdapter(adapter);
        }


    }


    private void getPaymentInfo() {


        databaseAccess.open();
        paymentCount = databaseAccess.getPaymentCount(invoiceId);

        //determine the dueAmount
        databaseAccess.open();
        HashMap<String, String> map = databaseAccess.getSinglePayment(invoiceId, String.valueOf(paymentCount));

        dueMoney = Double.parseDouble(Objects.requireNonNull(map.get("due_money")));
        totalPrice = Double.parseDouble(Objects.requireNonNull(map.get("total_money")));
        totalPayed = Double.parseDouble(Objects.requireNonNull(map.get("total_payed")));

        txtSubTotal.setText(String.format("%s %s%s", getString(R.string.sub_total), f.format(subTotal), currency));
        txtTax.setText(String.format("%s %s%s", getString(R.string.total_tax), f.format(tax), currency));
        txtDiscount.setText(String.format("%s %s%s", getString(R.string.discount), f.format(discount), currency));
        txtTotalCost.setText(String.format("%s %s%s", getString(R.string.total_money), totalPrice, currency));
        txtDueAmount.setText(String.format("%s: %s%s", getString(R.string.due_amount), f.format(dueMoney), currency));

    }


    private void collectDataForInvoicePdf() {

        //get data from db for invoice pdf
        databaseAccess.open();
        customerInfo = databaseAccess.getClientByName(customerName);
        Log.d("collectDataForInvoice", "getClientByName" + customerInfo.size());

        databaseAccess.open();
        userInfo = databaseAccess.getUserInformation();
        Log.d("collectDataForInvoice", "getUserInformation" + userInfo.size());

        stringItemList = new ArrayList<>();

        for (int i = 0; i < itemList.size(); i++) {

            String[] strings = new String[4];
            HashMap<String, String> map = itemList.get(i);

            databaseAccess.open();
            strings[0] = databaseAccess.getItemName(map.get("item_id"));
            strings[1] = map.get("item_price");
            strings[2] = map.get("item_qty");
            if (strings[1] != null && strings[2] != null)
                strings[3] = String.valueOf(Double.parseDouble(strings[1]) * Double.parseDouble(strings[2]));

            stringItemList.add(strings);

        }
        Log.d("collectDataForInvoice", "stringItemList" + stringItemList.size());

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