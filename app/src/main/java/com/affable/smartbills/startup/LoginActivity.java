package com.affable.smartbills.startup;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends BaseActivity {

    private EditText emailId, passId;
    private Button loginButton;
    private CheckBox rememberBox;

    private ProgressDialog progressDialog;
    private String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailId = findViewById(R.id.emailId);
        passId = findViewById(R.id.passId);
        loginButton = findViewById(R.id.loginButton);
        rememberBox = findViewById(R.id.rememberBox);
        progressDialog = new ProgressDialog(this);

        loginButton.setOnClickListener(v -> {

            email = emailId.getText().toString();
            pass = passId.getText().toString();

            if (email.isEmpty()) {
                emailId.setError("This field can't be Empty!");
            } else if (pass.isEmpty()) {
                passId.setError("enter a valid password first!");
                passId.requestFocus();
            } else {
                progressDialog.setTitle("Logging in to the Account...");
                progressDialog.setMessage("please wait, while we are checking the credentials...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                loginAccount();
            }

        });

    }

    //storing data for checked remember_me box
    private void checkRemember() {
        if (rememberBox.isChecked()) {

            SharedPreferences.Editor editor = getSharedPreferences(Constant.PREF_REMEMBER, Context.MODE_PRIVATE).edit();
            editor.putBoolean(Constant.PREF_KEY_REMEMBER_LOGIN, true);
            editor.apply();

        }
    }

    @SuppressLint("CheckResult")
    private void loginAccount() {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(LoginActivity.this);
        databaseAccess.open();

        if (databaseAccess.loginUser(email, pass)) {

            Toasty.success(LoginActivity.this, "Login Successful").show();

            //remember user for next login
            checkRemember();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            progressDialog.dismiss();

        } else {

            Toasty.error(LoginActivity.this, "Login Unsuccessful, try again").show();
            progressDialog.dismiss();

        }

        //closing database
        databaseAccess.close();

    }

}