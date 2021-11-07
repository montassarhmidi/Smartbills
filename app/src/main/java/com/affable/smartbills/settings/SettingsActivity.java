package com.affable.smartbills.settings;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.affable.smartbills.R;
import com.affable.smartbills.settings.category.CategoriesActivity;
import com.affable.smartbills.settings.backup.BackupActivity;
import com.affable.smartbills.settings.payment_metthod.PaymentMethodActivity;
import com.affable.smartbills.utils.BaseActivity;

public class SettingsActivity extends BaseActivity {

    private CardView cardAccountInfo, cardPassChange, cardBackup,cardCategory,cardPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cardAccountInfo = findViewById(R.id.card_ac_info);
        cardPassChange = findViewById(R.id.card_pass_change);
        cardBackup = findViewById(R.id.card_backup);
        cardCategory=findViewById(R.id.card_category);
        cardPaymentMethod=findViewById(R.id.card_payment_method);


        cardAccountInfo.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, UserInfoActivity.class);
            startActivity(intent);
        });

        cardPassChange.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, ChangePassActivity.class);
            startActivity(intent);
        });

        cardCategory.setOnClickListener(v -> {
            Intent intent=new Intent(SettingsActivity.this, CategoriesActivity.class);
            startActivity(intent);
        });

        cardPaymentMethod.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
            startActivity(intent);
        });

        cardBackup.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, BackupActivity.class);
            startActivity(intent);
        });


    }

}