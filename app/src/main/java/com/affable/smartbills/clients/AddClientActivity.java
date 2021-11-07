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

public class AddClientActivity extends BaseActivity {

    private TextInputEditText client_name, client_phone, client_email, client_address;
    private Button add_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        getSupportActionBar().setTitle(R.string.add_clients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        client_name = findViewById(R.id.client_name);
        client_phone = findViewById(R.id.client_phone);
        client_email = findViewById(R.id.client_email);
        client_address = findViewById(R.id.client_address);
        add_client = findViewById(R.id.add_client);

        add_client.setOnClickListener(v -> {

            String clientNameStr = client_name.getText().toString().trim();
            String clientPhoneStr = client_phone.getText().toString().trim();
            String clientEmailStr = client_email.getText().toString().trim();
            String clientAddressStr = client_address.getText().toString().trim();


            if (clientNameStr.isEmpty()) {
                client_name.setError(getString(R.string.client_name_error));
                client_name.requestFocus();
            } else if (clientPhoneStr.isEmpty()) {
                client_phone.setError(getString(R.string.client_phone_error));
                client_phone.requestFocus();
            } else if (clientEmailStr.isEmpty() || !clientEmailStr.contains("@") || !clientEmailStr.contains(".")) {
                client_email.setError(getString(R.string.client_email));
                client_email.requestFocus();
            } else if (clientAddressStr.isEmpty()) {
                client_address.setError(getString(R.string.enter_client_address));
                client_address.requestFocus();
            } else {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddClientActivity.this);
                databaseAccess.open();

                boolean check = databaseAccess.addClients(clientNameStr, clientPhoneStr, clientEmailStr, clientAddressStr);

                if (check) {
                    Toasty.success(AddClientActivity.this, R.string.client_successfully_added, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddClientActivity.this, MainActivity.class);
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_CLIENTS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {

                    Toasty.error(AddClientActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                }
            }

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