package com.affable.smartbills.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class UserInfoActivity extends BaseActivity {

    private EditText etxtAcName, etxtAcPhone, etxtAcEmail, etxtAcAddress, etxtAcCurrency, etxtTax;
    private Button editButton, updateButton, cancelButton;
    private DatabaseAccess databaseAccess;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        etxtAcName = findViewById(R.id.etxt_ac_name);
        etxtAcPhone = findViewById(R.id.etxt_ac_phone);
        etxtAcEmail = findViewById(R.id.etxt_ac_email);
        etxtAcAddress = findViewById(R.id.etxt_ac_address);
        etxtAcCurrency = findViewById(R.id.etxt_ac_currency);
        etxtTax = findViewById(R.id.etxt_tax);
        editButton = findViewById(R.id.editButton);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.cancel_button);

        databaseAccess = DatabaseAccess.getInstance(this);

        //disable until btn clicked
        disableEtxt();

        //get & set data in etxt
        setDataInEtxt();

        editButton.setOnClickListener(v -> {

            enableEtxt();

            editButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

        });

        cancelButton.setOnClickListener(v -> {

            disableEtxt();
            setDataInEtxt();

            updateButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);

        });

        updateButton.setOnClickListener(v -> validateAndUpdateData());


    }


    private void setDataInEtxt() {

        databaseAccess.open();
        HashMap<String, String> infoMap = databaseAccess.getUserInformation();

        if (infoMap != null) {

            id = infoMap.get("user_id");

            etxtAcName.setText(infoMap.get("user_name"));
            etxtAcPhone.setText(infoMap.get("user_phone"));
            etxtAcEmail.setText(infoMap.get("user_email"));
            etxtAcAddress.setText(infoMap.get("user_address"));
            etxtAcCurrency.setText(infoMap.get("currency"));
            etxtTax.setText(infoMap.get("tax"));

        }


    }

    private void validateAndUpdateData() {

        String name = etxtAcName.getText().toString().trim();
        String phone = etxtAcPhone.getText().toString().trim();
        String email = etxtAcEmail.getText().toString().trim();
        String address = etxtAcAddress.getText().toString().trim();
        String currency = etxtAcCurrency.getText().toString().trim();
        String tax = etxtTax.getText().toString().trim().replace("%", "");

        if (name.isEmpty()) {
            etxtAcName.setError("fill up this field!");
            etxtAcName.requestFocus();
        } else if (phone.isEmpty()) {
            etxtAcPhone.setError("fill up this field!");
            etxtAcPhone.requestFocus();
        } else if (email.isEmpty()) {
            etxtAcEmail.setError("fill up this field!");
            etxtAcEmail.requestFocus();
        } else if (address.isEmpty()) {
            etxtAcAddress.setError("fill up this field!");
            etxtAcAddress.requestFocus();
        } else if (currency.isEmpty()) {
            etxtAcCurrency.setError("fill up this field!");
            etxtAcCurrency.requestFocus();
        } else if (tax.isEmpty()) {
            etxtTax.setError("fill up this field!");
            etxtTax.requestFocus();
        } else {

            databaseAccess.open();
            if (databaseAccess.updateUserInformation(id, name, phone, email, address, currency, tax)) {
                Toasty.success(UserInfoActivity.this, getString(R.string.ac_info_updated), Toast.LENGTH_SHORT).show();
            } else {
                Toasty.error(UserInfoActivity.this, getString(R.string.ac_info_not_updated), Toast.LENGTH_SHORT).show();
            }

            disableEtxt();
            updateButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);

        }

    }

    private void enableEtxt() {
        etxtAcName.setEnabled(true);
        etxtAcPhone.setEnabled(true);
        etxtAcEmail.setEnabled(true);
        etxtAcAddress.setEnabled(true);
        etxtAcCurrency.setEnabled(true);
        etxtTax.setEnabled(true);
    }

    private void disableEtxt() {
        etxtAcName.setEnabled(false);
        etxtAcPhone.setEnabled(false);
        etxtAcEmail.setEnabled(false);
        etxtAcAddress.setEnabled(false);
        etxtAcCurrency.setEnabled(false);
        etxtTax.setEnabled(false);
    }


}