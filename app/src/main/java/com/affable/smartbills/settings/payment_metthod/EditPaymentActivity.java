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

public class EditPaymentActivity extends BaseActivity {


    private EditText etxtName;
    private Button btnUpdate, btnEditCategory, btnCancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment);

        etxtName = findViewById(R.id.etxt_name);
        btnUpdate = findViewById(R.id.btn_update);
        btnEditCategory = findViewById(R.id.btn_edit_category);
        btnCancle = findViewById(R.id.btn_cancle);

        etxtName.setEnabled(false);

        String paymentId = getIntent().getStringExtra("PAYMENT_ID");
        String paymentName = getIntent().getStringExtra("PAYMENT_NAME");

        if (paymentId != null && paymentName != null)
            etxtName.setText(paymentName);

        btnEditCategory.setOnClickListener(v -> {
            etxtName.setEnabled(true);
            btnEditCategory.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnCancle.setVisibility(View.VISIBLE);
        });


        btnCancle.setOnClickListener(v -> {

            if (paymentId != null && paymentName != null)
                etxtName.setText(paymentName);

            etxtName.setEnabled(false);
            btnUpdate.setVisibility(View.GONE);
            btnCancle.setVisibility(View.GONE);
            btnEditCategory.setVisibility(View.VISIBLE);
        });

        btnUpdate.setOnClickListener(v -> {
            String category = etxtName.getText().toString().trim();
            if (category.isEmpty()) {
                etxtName.setError("enter the category name first!");
                etxtName.requestFocus();
            } else {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditPaymentActivity.this);
                databaseAccess.open();

                if (databaseAccess.updatePaymentMethod(paymentId, category))
                    Toasty.success(EditPaymentActivity.this, getString(R.string.payment_method_updated), Toast.LENGTH_SHORT).show();
                else
                    Toasty.error(EditPaymentActivity.this, getString(R.string.payment_method_update_failed), Toast.LENGTH_SHORT).show();

                EditPaymentActivity.this.finish();
                startActivity(new Intent(EditPaymentActivity.this, PaymentMethodActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


    }
}