package com.affable.smartbills.database;

import android.content.Context;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class DatabaseOpenHelper extends SQLiteAssetHelper {


    public static final String DATABASE_NAME = "smart_bills.db";
    public static final int DATABASE_VERSION = 1;
    Context mContext;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    public void backup(String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toasty.success(mContext, "Data backup successful.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toasty.error(mContext, "Unable to backup database. Please, try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void importDB(String inFileName) {

        final String outFileName = mContext.getDatabasePath("smart_bills.db").toString();


        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toasty.success(mContext, "Data import successful.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toasty.error(mContext, "Unable to import database. Please, try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
