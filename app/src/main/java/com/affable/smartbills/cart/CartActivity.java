package com.affable.smartbills.cart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class CartActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayout no_data;
    private Button btn_submit_order;
    private TextView txt_total_price;
    private CartItemAdapter adapter;
    private DatabaseAccess databaseAccess;
    private ArrayList<String> customerNameList, orderTypesList, paymentMethodList;
    private double calculated_total_cost, due_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle(R.string.cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        databaseAccess = DatabaseAccess.getInstance(CartActivity.this);

        recyclerView = findViewById(R.id.recyclerView);
        no_data = findViewById(R.id.no_data);
        btn_submit_order = findViewById(R.id.btn_submit_order);
        txt_total_price = findViewById(R.id.txt_sub_total);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //get data from local database
        databaseAccess.open();
        List<HashMap<String, String>> cartItemsList = databaseAccess.getCartItems();

        if (cartItemsList.size() <= 0) {
            no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_submit_order.setVisibility(View.VISIBLE);
            txt_total_price.setVisibility(View.VISIBLE);

            adapter = new CartItemAdapter(CartActivity.this, cartItemsList,
                    txt_total_price, btn_submit_order, no_data, recyclerView);
            recyclerView.setAdapter(adapter);

        }

        btn_submit_order.setOnClickListener(v -> {

            //show confirmation dialog
            showDialog();

        });


    }

    @SuppressLint("SetTextI18n")
    private void showDialog() {

        DecimalFormat f = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        f.applyPattern("#0.00");

        //create custom dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        //init dialog views
        final Button dialog_btn_submit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialog_btn_close = dialogView.findViewById(R.id.btn_close);

        final TextView dialog_customer = dialogView.findViewById(R.id.dialog_customer);
        final TextView dialog_order_type = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialog_order_payment_method = dialogView.findViewById(R.id.dialog_order_status);

        final TextView dialog_txt_total = dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialog_txt_total_tax = dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialog_txt_level_tax = dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialog_txt_total_cost = dialogView.findViewById(R.id.dialog_txt_total_cost);
        final EditText dialog_etxt_discount = dialogView.findViewById(R.id.etxt_dialog_discount);

        final EditText etxt_paid_amount = dialogView.findViewById(R.id.etxt_paid_amount);
        final TextView txt_due_amount = dialogView.findViewById(R.id.txt_due_amount);


        final ImageButton dialog_img_customer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialog_img_order_payment_method = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialog_img_order_type = dialogView.findViewById(R.id.img_order_type);


        //submit won't available until paid money set
        dialog_btn_submit.setVisibility(View.INVISIBLE);


        //get data from local database
        //TODO: if multiple users added get user info with user id
        databaseAccess.open();
        HashMap<String, String> userInfo = databaseAccess.getUserInformation();

        String currency = userInfo.get("currency");

        String tax = userInfo.get("tax");
        assert tax != null;
        double getTax = Double.parseDouble(tax);

        double total_cost = CartItemAdapter.totalPrice;
        double calculated_tax = (total_cost * getTax) / 100.0;
        calculated_total_cost = (total_cost + calculated_tax);


        //set data
        dialog_txt_level_tax.setText(getString(R.string.total_tax) + "( " + tax + "%) : ");
        dialog_txt_total.setText(f.format(total_cost) + currency);
        dialog_txt_total_tax.setText(f.format(calculated_tax) + currency);
        dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);
        txt_due_amount.setText(f.format(calculated_total_cost) + currency);

        //discount edit text
        dialog_etxt_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //your code here

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                calculated_total_cost = (total_cost + calculated_tax);

                String get_discount = s.toString();
                if (get_discount.isEmpty()) {
                    get_discount = "0";
                } else if (get_discount.equals(".")) {
                    get_discount = "0.";
                }
                double calculate_discount = Double.parseDouble(get_discount);
                if (calculate_discount > calculated_total_cost) {
                    dialog_etxt_discount.setError(getString(R.string.discount_cant_be_greater_than_total_price));
                    dialog_etxt_discount.requestFocus();
                } else {
                    //change total cost on discount change
                    calculated_total_cost = calculated_total_cost - calculate_discount;
                    dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);
                }

                String get_paid = etxt_paid_amount.getText().toString();
                if (get_paid.isEmpty()) {
                    get_paid = "0";
                } else if (get_paid.equals(".")) {
                    get_paid = "0.";
                }
                double paid = Double.parseDouble(get_paid);

                //change due on discount change
                due_money = calculated_total_cost - paid;
                txt_due_amount.setText(currency + f.format(due_money));

            }

            @Override
            public void afterTextChanged(Editable s) {
                //your code here
            }
        });


        //paid money edit text
        etxt_paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //add your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                calculated_total_cost = (total_cost + calculated_tax);

                String get_paid = s.toString();
                if (get_paid.isEmpty()) {
                    get_paid = "0";
                } else if (get_paid.equals(".")) {
                    get_paid = "0.";
                }
                double paid_amount = Double.parseDouble(get_paid);

                String get_discount = dialog_etxt_discount.getText().toString();
                if (get_discount.isEmpty()) {
                    get_discount = "0";
                } else if (get_discount.equals(".")) {
                    get_discount = "0.";
                }
                double calculate_discount = Double.parseDouble(get_discount);
                calculated_total_cost = calculated_total_cost - calculate_discount;

                if (paid_amount > calculated_total_cost) {
                    etxt_paid_amount.setError(getString(R.string.due_cant_be_greater_than_total_price));
                    etxt_paid_amount.requestFocus();
                } else {

                    //change due on discount change

                    due_money = calculated_total_cost - paid_amount;
                    txt_due_amount.setText(f.format(due_money) + currency);

                }

                //change total cost
                dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);
                dialog_btn_submit.setVisibility(View.VISIBLE);


            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });


        //get customer name list
        databaseAccess.open();
        customerNameList = databaseAccess.getClientsName();
        //show selectCustomer Dialog
        dialog_img_customer.setOnClickListener(v -> selectCustomerDialog(dialog_customer));


        //get payment method list
        databaseAccess.open();
        paymentMethodList = databaseAccess.getPaymentMethodNames();
        //show payment method selection dialog
        dialog_img_order_payment_method.setOnClickListener(v -> selectPaymentMethodDialog(dialog_order_payment_method));


        //get orderTypes
        databaseAccess.open();
        orderTypesList = databaseAccess.getOrderTypeNames();
        //show order type dialog
        dialog_img_order_type.setOnClickListener(v -> selectOrderTypesDialog(dialog_order_type));


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        //submit button
        dialog_btn_submit.setOnClickListener(v -> {

            String order_type = dialog_order_type.getText().toString().trim();
            String order_payment_method = dialog_order_payment_method.getText().toString().trim();
            String customer_name = dialog_customer.getText().toString().trim();

            String discount = dialog_etxt_discount.getText().toString().trim();
            if (discount.isEmpty()||discount.equals(".")) {
                discount = "0.00";
            }

            String paid_amount = etxt_paid_amount.getText().toString().trim();
            if (paid_amount.isEmpty() || paid_amount.equals(".")) {
                paid_amount = "0.00";
            }

            proceedOrder(order_type, order_payment_method, customer_name, f.format(calculated_total_cost),
                    total_cost, calculated_tax, discount, paid_amount, f.format(due_money));


            alertDialog.dismiss();
        });

        dialog_btn_close.setOnClickListener(v -> alertDialog.dismiss());


    }


    private void selectCustomerDialog(TextView dialog_customer) {

        ArrayAdapter<String> customerAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1);
        customerAdapter.addAll(customerNameList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
        EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
        ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);

        dialogTitle.setText(R.string.select_customer);
        dialogList.setVerticalScrollBarEnabled(true);
        dialogList.setAdapter(customerAdapter);

        dialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                customerAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();

        dialogButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();


        dialogList.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();
            String selectedItem = customerAdapter.getItem(position);

            dialog_customer.setText(selectedItem);

        });

    }


    private void selectPaymentMethodDialog(TextView dialog_order_payment_method) {

        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1, paymentMethodList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
        EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
        ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


        dialogTitle.setText(R.string.select_payment_method);
        dialogList.setVerticalScrollBarEnabled(true);
        dialogList.setAdapter(paymentMethodAdapter);

        dialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                paymentMethodAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });


        final AlertDialog alertDialog = dialog.create();

        dialogButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();


        dialogList.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();
            String selectedItem = paymentMethodAdapter.getItem(position);
            dialog_order_payment_method.setText(selectedItem);

        });
    }


    private void selectOrderTypesDialog(TextView dialog_order_type) {

        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1, orderTypesList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);


        dialog_title.setText(R.string.select_order_type);
        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(orderTypeAdapter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                orderTypeAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });


        final AlertDialog alertDialog = dialog.create();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();

        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();
            String selectedItem = orderTypeAdapter.getItem(position);

            dialog_order_type.setText(selectedItem);

        });

    }


    private void proceedOrder(String orderType, String orderPaymentMethod, String customerName, String totalPrice, double subTotal,
                              double calculatedTax, String discount, String paidAmount, String dueAmount) {

        databaseAccess.open();
        int itemCount = databaseAccess.getCartItemCount();

        if (itemCount > 0) {

            databaseAccess.open();
            List<HashMap<String, String>> cartItemsList = databaseAccess.getCartItems();

            if (cartItemsList.isEmpty()) {
                Toasty.error(this, R.string.no_items_found, Toast.LENGTH_SHORT).show();
            } else {

                //ge all essential order details values

                String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date());

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int cMonth = calendar.get(Calendar.MONTH) + 1;
                int cDay = calendar.get(Calendar.DATE);

                String day = String.valueOf(cDay);
                String month = String.valueOf(cMonth);

                if (cDay < 10)
                    day = "0" + day;

                if (cMonth < 10)
                    month = "0" + month;


                final JSONObject jObject = new JSONObject();

                try {

                    jObject.put("order_type", orderType);
                    jObject.put("payment_method", orderPaymentMethod);
                    jObject.put("customer_name", customerName);
                    jObject.put("tax", calculatedTax);
                    jObject.put("discount", discount);
                    jObject.put("sub_total", subTotal);
                    jObject.put("total_price", totalPrice);
                    jObject.put("paid_money", paidAmount);
                    jObject.put("due_money", dueAmount);
                    jObject.put("order_time", currentTime);
                    jObject.put("day", day);
                    jObject.put("month", month);
                    jObject.put("year", year);

                    /*
                     * every order can hold multiple number of items
                     * so store all items in a json array to easily send to the db file
                     * & then retrieve it from json array and store it in order_details table
                     *
                     */
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < cartItemsList.size(); i++) {

                        databaseAccess.open();
                        String itemId = cartItemsList.get(i).get("item_id");

                        databaseAccess.open();
                        String stock = databaseAccess.getItemStock(itemId);

                        databaseAccess.open();
                        String itemWeightUnit = cartItemsList.get(i).get("item_weight_unit");
                        String weightUnit = databaseAccess.getWeightUnit(itemWeightUnit);


                        //for Order details
                        JSONObject obj = new JSONObject();
                        obj.put("item_id", itemId);
                        obj.put("item_weight", cartItemsList.get(i).get("item_weight") + " " + weightUnit);
                        obj.put("item_qty", cartItemsList.get(i).get("item_qty"));
                        obj.put("item_price", cartItemsList.get(i).get("item_price"));
                        obj.put("stock", stock);

                        jsonArray.put(obj);

                    }

                    jObject.put("orderDetails", jsonArray);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                /************-------saveOrderInDB--------------***********/
                saveOrderInDB(jObject);


            }

        } else {
            Toasty.error(this, R.string.no_items_in_cart, Toast.LENGTH_SHORT).show();
        }

    }


    private void saveOrderInDB(JSONObject object) {

        //generate current timestamp for unique invoice id
        long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = Long.toString(tsLong);

        databaseAccess.open();

        //timestamp used for un sync order and make it unique id
        if (databaseAccess.insertOrder(timeStamp, object)) {

            Toasty.success(this, R.string.order_done_successful, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_INVOICES);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else {
            Toasty.error(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }


    }


    //for top back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}