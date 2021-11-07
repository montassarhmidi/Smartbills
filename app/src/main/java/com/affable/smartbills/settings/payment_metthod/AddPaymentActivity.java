package com.affable.smartbills.settings.payment_metthod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class AddPaymentActivity extends BaseActivity {

    private EditText etxtPaymentName;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        etxtPaymentName = findViewById(R.id.etxt_payment_name);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(v -> {
            String paymentMethodName = etxtPaymentName.getText().toString().trim();
            if (paymentMethodName.isEmpty()) {
                etxtPaymentName.setError("fill up the field first!");
                etxtPaymentName.requestFocus();
            } else {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddPaymentActivity.this);
                databaseAccess.open();

                if (databaseAccess.addPaymentMethod(paymentMethodName))
                    Toasty.success(AddPaymentActivity.this, getString(R.string.payment_method_added), Toast.LENGTH_SHORT).show();
                else
                    Toasty.error(AddPaymentActivity.this, getString(R.string.payment_method_add_failed), Toast.LENGTH_SHORT).show();

                AddPaymentActivity.this.finish();
                startActivity(new Intent(AddPaymentActivity.this, PaymentMethodActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


            }
        });


    }
}