package com.affable.smartbills.clients;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;

public class EditClientActivity extends BaseActivity {

    private TextInputEditText clientName, clientPhone, clientEmail, clientAddress;
    private Button updateClient, editClient, cancelClientUpdate;

    private String clientIdStr, clientNameStr, clientPhoneStr, clientEmailStr, clientAddressStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        getSupportActionBar().setTitle(R.string.update_clients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        clientName = findViewById(R.id.client_name);
        clientPhone = findViewById(R.id.client_phone);
        clientEmail = findViewById(R.id.client_email);
        clientAddress = findViewById(R.id.client_address);
        updateClient = findViewById(R.id.update_client);
        editClient = findViewById(R.id.edit_client);
        cancelClientUpdate = findViewById(R.id.cancel_client_update);

        //disable until edit
        clientName.setEnabled(false);
        clientPhone.setEnabled(false);
        clientEmail.setEnabled(false);
        clientAddress.setEnabled(false);

        //get data from intent
        Intent intent = getIntent();
        clientIdStr = intent.getStringExtra("client_id");
        clientNameStr = intent.getStringExtra("client_name");
        clientPhoneStr = intent.getStringExtra("client_phone");
        clientEmailStr = intent.getStringExtra("client_email");
        clientAddressStr = intent.getStringExtra("client_address");

        //set data on EditText
        clientName.setText(clientNameStr);
        clientPhone.setText(clientPhoneStr);
        clientEmail.setText(clientEmailStr);
        clientAddress.setText(clientAddressStr);

        //edit client info
        editClient.setOnClickListener(v -> {

            clientName.setEnabled(true);
            clientPhone.setEnabled(true);
            clientEmail.setEnabled(true);
            clientAddress.setEnabled(true);

            editClient.setVisibility(View.GONE);
            updateClient.setVisibility(View.VISIBLE);
            cancelClientUpdate.setVisibility(View.VISIBLE);

        });


        //update client info
        updateClient.setOnClickListener(v -> {

            clientNameStr = clientName.getText().toString().trim();
            clientPhoneStr = clientPhone.getText().toString().trim();
            clientEmailStr = clientEmail.getText().toString().trim();
            clientAddressStr = clientAddress.getText().toString().trim();


            if (clientNameStr.isEmpty()) {
                clientName.setError(getString(R.string.client_name_error));
                clientName.requestFocus();
            } else if (clientPhoneStr.isEmpty()) {
                clientPhone.setError(getString(R.string.client_phone_error));
                clientPhone.requestFocus();
            } else if (clientEmailStr.isEmpty() || !clientEmailStr.contains("@") || !clientEmailStr.contains(".")) {
                clientEmail.setError(getString(R.string.client_email));
                clientEmail.requestFocus();
            } else if (clientAddressStr.isEmpty()) {
                clientAddress.setError(getString(R.string.enter_client_address));
                clientAddress.requestFocus();
            } else {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditClientActivity.this);
                databaseAccess.open();

                boolean check = databaseAccess.updateClient(clientIdStr, clientNameStr, clientPhoneStr, clientEmailStr, clientAddressStr);

                if (check) {
                    Toasty.success(EditClientActivity.this, getString(R.string.client_updated), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(EditClientActivity.this, MainActivity.class);
                    intent1.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_CLIENTS);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                } else {

                    Toasty.error(EditClientActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                }
            }

        });

        //cancel client update
        cancelClientUpdate.setOnClickListener(v -> {

            updateClient.setVisibility(View.GONE);
            cancelClientUpdate.setVisibility(View.GONE);
            editClient.setVisibility(View.VISIBLE);

            clientName.setEnabled(false);
            clientPhone.setEnabled(false);
            clientEmail.setEnabled(false);
            clientAddress.setEnabled(false);

        });


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}