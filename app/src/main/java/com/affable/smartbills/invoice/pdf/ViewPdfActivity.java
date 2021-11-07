package com.affable.smartbills.invoice.pdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.utils.BaseActivity;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ViewPdfActivity extends BaseActivity {

    private PDFView pdfView;
    private File file;
    private Context primaryBaseActivity;//THIS WILL KEEP ORIGINAL INSTANCE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = findViewById(R.id.pdfView);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.pdf_invoice);

        String pathValue = getIntent().getStringExtra("path");
        Log.d("location", pathValue);

        if (pathValue != null) {
            file = new File(pathValue);
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                // .nightMode(true)
                .load();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_pdf, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Toast.makeText(this, R.string.share, Toast.LENGTH_SHORT).show();
            sharePdfFile();


            return true;
        } else if (id == R.id.action_open_pdf) {

            Toast.makeText(this, R.string.open_with_external_pdf_reader, Toast.LENGTH_SHORT).show();
            openWithExternalPdfApp();

        } else if (id == R.id.action_print) {

            printPDf();

        } else if (id == android.R.id.home) {
            // app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void sharePdfFile() {

        /*Please note If your targetSdkVersion is 24 or higher,
          we have to use FileProvider class to give access to the particular
         file or folder to make them accessible for other apps.
         */

        Uri uri = FileProvider.getUriForFile(ViewPdfActivity.this, this.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setDataAndType(uri, "application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    public void openWithExternalPdfApp() {

        /*Please note If your targetSdkVersion is 24 or higher,
          we have to use FileProvider class to give access to the particular
         file or folder to make them accessible for other apps.

         */
        Uri uri = FileProvider.getUriForFile(ViewPdfActivity.this, ViewPdfActivity.this.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);


        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    public void printPDf() {

        PrintManager printManager = (PrintManager) primaryBaseActivity.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(R.string.app_name) + "Order Receipt";

        PrintDocumentAdapter pda = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;

                try {

                    File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");
                    if (!folder.exists())
                        folder.mkdir();

                    file = new File(folder, "invoice_receipt.pdf");

                    input = new FileInputStream(file);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                callback.onLayoutFinished(pdi, true);
            }

        };

        if (printManager != null) {

            PrintAttributes.Builder builder = new PrintAttributes.Builder();
            //set default printing page size, you can change printing page size here
            builder.setMediaSize(PrintAttributes.MediaSize.PRC_6);
            printManager.print(jobName, pda, builder.build());

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        primaryBaseActivity = base;
        super.attachBaseContext(base);
    }

}