package com.affable.smartbills.items;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerViewActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private static final int CURRENT_API_VERSION = Build.VERSION.SDK_INT;
    private String returnActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_view);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.qr_barcode_scanner);

        returnActivity = getIntent().getStringExtra(Constant.SCANNER_VIEW_RETURN_ACTIVITY);

        if (CURRENT_API_VERSION >= Build.VERSION_CODES.M) {
            requestCameraPermission();
        }

        scannerView = new ZXingScannerView(ScannerViewActivity.this);
        setContentView(scannerView);
        scannerView.startCamera();
        scannerView.setResultHandler(ScannerViewActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        final String myResult = rawResult.getText();

        //set result in caller activity
        if (returnActivity.equals("EditItemsActivity")) {
            EditItemsActivity.itemCode.setText(myResult);
        } else if (returnActivity.equals("AddItemsActivity")) {
            AddItemsActivity.itemCode.setText(myResult);
        }

        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());

        onBackPressed();

    }


    //Runtime permission
    private void requestCameraPermission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        scannerView = new ZXingScannerView(ScannerViewActivity.this);
                        setContentView(scannerView);
                        scannerView.startCamera();
                        scannerView.setResultHandler(ScannerViewActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            Toasty.info(ScannerViewActivity.this, R.string.camera_permission, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}