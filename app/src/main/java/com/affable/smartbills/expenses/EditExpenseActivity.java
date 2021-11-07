package com.affable.smartbills.expenses;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class EditExpenseActivity extends BaseActivity {

    private String dateTime = "", minStr = "";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String expenseIdStr, expenseNameStr, expenseNoteStr, expenseAmountStr, expenseDateStr, expenseTimeStr;

    private TextInputEditText expenseName, expenseNote, expenseAmount, expenseDate, expenseTime;
    private MaterialButton editExpense, updateExpense, cancelExpenseUpdate;

    private String cDate, cMonth, cYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        getSupportActionBar().setTitle(R.string.update_expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        expenseName = findViewById(R.id.expense_name);
        expenseNote = findViewById(R.id.expense_note);
        expenseAmount = findViewById(R.id.expense_amount);
        expenseDate = findViewById(R.id.expense_date);
        expenseTime = findViewById(R.id.expense_time);
        editExpense = findViewById(R.id.edit_expense);
        updateExpense = findViewById(R.id.update_expense);
        cancelExpenseUpdate = findViewById(R.id.cancel_expense_update);

        //get data from intent
        Intent intent = getIntent();
        expenseIdStr = intent.getStringExtra("expense_id");
        expenseNameStr = intent.getStringExtra("expense_name");
        expenseNoteStr = intent.getStringExtra("expense_note");
        expenseAmountStr = intent.getStringExtra("expense_amount");
        expenseDateStr = intent.getStringExtra("expense_date");
        expenseTimeStr = intent.getStringExtra("expense_time");

        //set data on EditText
        expenseName.setText(expenseNameStr);
        expenseNote.setText(expenseNoteStr);
        expenseAmount.setText(expenseAmountStr);
        expenseDate.setText(expenseDateStr);
        expenseTime.setText(expenseTimeStr);


        expenseDate.setOnClickListener(v -> datePicker());

        expenseTime.setOnClickListener(v -> timePicker());

        //edit expense
        editExpense.setOnClickListener(v -> {

            expenseName.setEnabled(true);
            expenseNote.setEnabled(true);
            expenseAmount.setEnabled(true);
            expenseDate.setEnabled(true);
            expenseTime.setEnabled(true);

            editExpense.setVisibility(View.GONE);
            updateExpense.setVisibility(View.VISIBLE);
            cancelExpenseUpdate.setVisibility(View.VISIBLE);

        });


        //update expense info
        updateExpense.setOnClickListener(v -> {

            expenseNameStr = expenseName.getText().toString().trim();
            expenseNoteStr = expenseNote.getText().toString().trim();
            expenseAmountStr = expenseAmount.getText().toString().trim();
            expenseDateStr = expenseDate.getText().toString();
            expenseTimeStr = expenseTime.getText().toString();

            if (expenseNameStr.isEmpty()) {
                expenseName.setError(getString(R.string.expense_name_error));
                expenseName.requestFocus();
            } else if (expenseAmountStr.isEmpty()) {
                expenseAmount.setError(getString(R.string.expense_amount_error));
                expenseAmount.requestFocus();
            } else if (expenseDateStr.isEmpty()) {
                expenseDate.setError(getString(R.string.expense_date_error));
                expenseDate.requestFocus();
            } else if (expenseTimeStr.isEmpty()) {
                expenseTime.setError(getString(R.string.expense_time_error));
                expenseTime.requestFocus();
            } else {

                String[] dateArr = expenseDateStr.split("-");
                cYear = dateArr[0];
                cMonth = dateArr[1];
                cDate = dateArr[2];

                addExpenseDBOperation();

            }

        });


        //cancel expense update
        cancelExpenseUpdate.setOnClickListener(v -> {

            updateExpense.setVisibility(View.GONE);
            cancelExpenseUpdate.setVisibility(View.GONE);
            editExpense.setVisibility(View.VISIBLE);

            expenseName.setEnabled(false);
            expenseNote.setEnabled(false);
            expenseAmount.setEnabled(false);
            expenseDate.setEnabled(false);
            expenseTime.setEnabled(false);

        });


    }

    private void addExpenseDBOperation() {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditExpenseActivity.this);
        databaseAccess.open();

        boolean check = databaseAccess.updateExpense(expenseIdStr, expenseNameStr, expenseNoteStr, expenseAmountStr, expenseTimeStr, cDate, cMonth, cYear);

        if (check) {
            Toasty.success(EditExpenseActivity.this, getString(R.string.expense_updated), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditExpenseActivity.this, MainActivity.class);
            intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_EXPENSES);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {

            Toasty.error(EditExpenseActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

        }

    }

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpenseActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    int month = monthOfYear + 1;
                    String fm = "" + month;
                    String fd = "" + dayOfMonth;

                    if (month < 10) {
                        fm = "0" + month;
                    }
                    if (dayOfMonth < 10) {
                        fd = "0" + dayOfMonth;
                    }
                    dateTime = year + "-" + fm + "-" + fd;


                    expenseDate.setText(dateTime);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void timePicker() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(EditExpenseActivity.this,
                (view, hourOfDay, minute) -> {
                    String am_pm;
                    mHour = hourOfDay;
                    mMinute = minute;

                    //configure AM or PM
                    if (mHour == 0) {
                        am_pm = "AM";
                        mHour = 12;
                    } else if (mHour == 12) {
                        am_pm = "PM";
                    } else if (mHour > 12) {
                        am_pm = "PM";
                        mHour = mHour - 12;
                    } else {
                        am_pm = "AM";
                    }

                    //format minute
                    minStr = String.valueOf(mMinute);
                    if (minStr.length() <= 1) {
                        minStr = "0" + minStr;
                    }

                    String timeStr = mHour + ":" + minStr + " " + am_pm;
                    expenseTime.setText(timeStr);

                }, mHour, mMinute, false);
        timePickerDialog.show();
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