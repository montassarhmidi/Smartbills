package com.affable.smartbills.settings.backup;

import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseOpenHelper;
import com.affable.smartbills.utils.BaseActivity;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BackupActivity extends BaseActivity {

    private CardView cardDatabaseBackup, cardDatabaseImport, cardExclBackup;
    private LocalBackupHelper backupHelper;
    private DatabaseOpenHelper databaseOpenHelper;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        cardDatabaseBackup = findViewById(R.id.card_database_backup);
        cardDatabaseImport = findViewById(R.id.card_database_import);
        cardExclBackup = findViewById(R.id.card_excl_backup);

        backupHelper = new LocalBackupHelper(this);
        databaseOpenHelper = new DatabaseOpenHelper(getApplicationContext());

        if (Build.VERSION.SDK_INT >= 23)
            requestPermission();


        cardDatabaseBackup.setOnClickListener(v -> {
            String outFileName = Environment.getExternalStorageDirectory() + File.separator + "SmartBills/";
            backupHelper.performBackup(databaseOpenHelper, outFileName);
        });

        cardDatabaseImport.setOnClickListener(v -> backupHelper.performRestore(databaseOpenHelper));

        cardExclBackup.setOnClickListener(v -> folderChooser());
    }


    private void requestPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //type your code here
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }


    public void folderChooser() {
        new ChooserDialog(BackupActivity.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path);
                    Log.d("path",path);

                })
                .build()
                .show();
    }


    public void onExport(String path) {

        String directory_path = path;
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directory_path);
        sqliteToExcel.exportAllTables( "SmartBills_AllData.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(BackupActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(BackupActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();

                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(BackupActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

}