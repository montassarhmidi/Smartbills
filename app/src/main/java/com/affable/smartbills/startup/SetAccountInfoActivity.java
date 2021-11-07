package com.affable.smartbills.startup;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.dmoral.toasty.Toasty;

public class SetAccountInfoActivity extends BaseActivity {

    private EditText etxtAcName, etxtAcEmail, etxtAcPhone,
            etxtAcAddress, etxtTax, etxtAcCurrency;
    private FloatingActionButton fabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account_info);

        etxtAcName = findViewById(R.id.etxt_ac_name);
        etxtAcEmail = findViewById(R.id.etxt_ac_email);
        etxtAcPhone = findViewById(R.id.etxt_ac_phone);
        etxtAcAddress = findViewById(R.id.etxt_ac_address);
        etxtTax = findViewById(R.id.etxt_tax);
        etxtAcCurrency = findViewById(R.id.etxt_ac_currency);

        fabDone = findViewById(R.id.fab_done);

        fabDone.setOnClickListener(v -> {

            String name = etxtAcName.getText().toString().trim();
            String phone = etxtAcPhone.getText().toString().trim();
            String email = etxtAcEmail.getText().toString().trim();
            String address = etxtAcAddress.getText().toString().trim();
            String currency = etxtAcCurrency.getText().toString().trim();
            String tax = etxtTax.getText().toString().trim().replace("%", "");

            if (name.isEmpty()) {
                etxtAcName.setError("fill up this field!");
                etxtAcName.requestFocus();
            } else if (email.isEmpty()) {
                etxtAcEmail.setError("fill up this field!");
                etxtAcEmail.requestFocus();
            }else if (phone.isEmpty()) {
                etxtAcPhone.setError("fill up this field!");
                etxtAcPhone.requestFocus();
            }  else if (address.isEmpty()) {
                etxtAcAddress.setError("fill up this field!");
                etxtAcAddress.requestFocus();
            } else if (tax.isEmpty()) {
                etxtTax.setError("fill up this field!");
                etxtTax.requestFocus();
            }  else if (currency.isEmpty()) {
                etxtAcCurrency.setError("fill up this field!");
                etxtAcCurrency.requestFocus();
            }else {

                setAccountInfo(name, phone, email, address, currency, tax);

            }

        });

    }

    private void setAccountInfo(String name, String phone, String email, String address, String currency, String tax) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        if (databaseAccess.updateUserInformation("1", name, phone, email, address, currency, tax)) {

            Toasty.success(this, getString(R.string.ac_info_updated), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, SetPasswordActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else {
            Toasty.error(this, getString(R.string.ac_info_not_updated), Toast.LENGTH_SHORT).show();
        }

    }
}