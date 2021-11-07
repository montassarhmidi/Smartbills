package com.affable.smartbills;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Tools;

public class AboutUsActivity extends BaseActivity {

    private TextView txtVersion, txtUpdateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initToolbar();

        txtVersion = findViewById(R.id.txt_version);
        txtUpdateDate = findViewById(R.id.txt_update_date);

        txtVersion.setText(String.format("%s", BuildConfig.VERSION_NAME));
        txtUpdateDate.setText(getString(R.string.update_date));

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    //for top back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}