package com.affable.smartbills.expenses;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AddExpenseActivity extends BaseActivity {


    private String dateTime = "", minStr = "";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String expenseNameStr, expenseNoteStr, expenseAmountStr, expenseDateStr, expenseTimeStr;

    private TextInputEditText expenseName, expenseNote, expenseAmount, expenseDate, expenseTime;
    private MaterialButton addExpense;
    String cDate, cMonth, cYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setTitle(R.string.add_expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        expenseName = findViewById(R.id.expense_name);
        expenseNote = findViewById(R.id.expense_note);
        expenseAmount = findViewById(R.id.expense_amount);
        expenseDate = findViewById(R.id.expense_date);
        expenseTime = findViewById(R.id.expense_time);
        addExpense = findViewById(R.id.add_expense);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        //H denote 24 hours and h denote 12 hour hour format
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a

        expenseDate.setText(currentDate);
        expenseTime.setText(currentTime);

        expenseDate.setOnClickListener(v -> datePicker());

        expenseTime.setOnClickListener(v -> timePicker());

        addExpense.setOnClickListener(v -> {

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

    }


    private void addExpenseDBOperation() {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddExpenseActivity.this);
        databaseAccess.open();

        boolean check = databaseAccess.addExpense(expenseNameStr, expenseNoteStr, expenseAmountStr,expenseTimeStr, cDate, cMonth, cYear);

        if (check) {
            Toasty.success(AddExpenseActivity.this, R.string.expense_successfully_added, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
            intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_EXPENSES);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {

            Toasty.error(AddExpenseActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

        }

    }


    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddExpenseActivity.this,
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