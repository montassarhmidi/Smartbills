package com.affable.smartbills.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.Constant;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;

public class SetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etxtPass, etxtConfirmPass;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        //init views
        etxtPass = findViewById(R.id.etxt_pass);
        etxtConfirmPass = findViewById(R.id.etxt_confirm_pass);

        btnConfirm = findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(v -> {

            String pass = etxtPass.getText().toString();
            String cPass = etxtConfirmPass.getText().toString();

            if (pass.isEmpty()) {
                etxtPass.setError("fill up this field!");
                etxtPass.requestFocus();
            } else if (cPass.isEmpty()) {
                etxtConfirmPass.setError("fill up this field!");
                etxtConfirmPass.requestFocus();
            } else if (pass.length() < 6) {
                etxtPass.setError("password length must minimum of 6");
                etxtPass.requestFocus();
            } else if (cPass.length() < 6) {
                etxtConfirmPass.setError("password length must minimum of 6");
                etxtConfirmPass.requestFocus();
            } else if (!pass.equals(cPass)) {
                etxtConfirmPass.setError("doesn't match the password!");
                etxtConfirmPass.requestFocus();
            } else {
                setPassword(pass);
            }

        });

    }

    private void setPassword(String pass) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        if (databaseAccess.changePassword("1", pass)) {

            Toasty.success(SetPasswordActivity.this, "Password set", Toast.LENGTH_SHORT).show();
            changeFirstLaunchStatus();

            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else {
            Toasty.error(SetPasswordActivity.this, "something wrong! try again.", Toast.LENGTH_SHORT).show();
        }

    }


    private void changeFirstLaunchStatus() {
        SharedPreferences.Editor editor = getSharedPreferences(Constant.PREF_REMEMBER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constant.PREF_REMEMBER_FIRST_LAUNCH, false);
        editor.apply();
    }

}