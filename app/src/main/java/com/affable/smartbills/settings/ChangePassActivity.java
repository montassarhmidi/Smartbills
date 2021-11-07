package com.affable.smartbills.settings;

import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class ChangePassActivity extends BaseActivity {

    private EditText etxtOldPass, etxtNewPass, etxtConfirmPass;
    private Button btnChangePass, btnConfirm, btnCanclePass;
    private CardView cardNewPass;

    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        databaseAccess = DatabaseAccess.getInstance(this);

        //init views
        etxtOldPass = findViewById(R.id.etxt_old_pass);
        etxtNewPass = findViewById(R.id.etxt_new_pass);
        etxtConfirmPass = findViewById(R.id.etxt_confirm_pass);

        btnChangePass = findViewById(R.id.btn_change_pass);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCanclePass = findViewById(R.id.btn_cancle_pass);

        cardNewPass = findViewById(R.id.card_new_pass);

        btnConfirm.setOnClickListener(v -> {

            String pass = etxtOldPass.getText().toString();

            if (pass.isEmpty()) {
                etxtOldPass.setError("type the correct password first.");
                etxtOldPass.requestFocus();
            } else {

                databaseAccess.open();
                if (databaseAccess.checkPassword(pass)) {

                    etxtOldPass.setEnabled(false);
                    btnConfirm.setEnabled(false);

                    cardNewPass.setVisibility(View.VISIBLE);

                } else {
                    etxtOldPass.setError("typed password didn't match");
                    etxtOldPass.requestFocus();
                }

            }

        });

        btnCanclePass.setOnClickListener(v -> {
            etxtOldPass.setEnabled(true);
            btnConfirm.setEnabled(true);

            etxtOldPass.setText("");
            cardNewPass.setVisibility(View.GONE);
        });

        btnChangePass.setOnClickListener(v -> {

            String pass = etxtNewPass.getText().toString();
            String conf_pass = etxtConfirmPass.getText().toString();

            if (pass.isEmpty()) {
                etxtNewPass.setError("type the password first!");
                etxtNewPass.requestFocus();
            } else if (pass.length() < 4) {
                etxtNewPass.setError("password must be at least 4 characters!");
                etxtNewPass.requestFocus();
            } else if (conf_pass.isEmpty()) {
                etxtConfirmPass.setError("type the password first!");
                etxtConfirmPass.requestFocus();
            } else if (conf_pass.length() < 4) {
                etxtConfirmPass.setError("password must be at least 4 characters!");
                etxtConfirmPass.requestFocus();
            } else {

                if (pass.equals(conf_pass)) {
                    databaseAccess.open();
                    String userId = databaseAccess.getUserId();

                    databaseAccess.open();

                    if (databaseAccess.changePassword(userId, pass)) {

                        Toasty.success(ChangePassActivity.this, "Password updated", Toast.LENGTH_SHORT).show();

                        etxtOldPass.setEnabled(true);
                        btnConfirm.setEnabled(true);

                        etxtOldPass.setText("");
                        cardNewPass.setVisibility(View.GONE);
                    }

                } else {
                    etxtConfirmPass.setError("password didn't matched, try again!");
                    etxtConfirmPass.requestFocus();
                }
            }

        });

    }


}