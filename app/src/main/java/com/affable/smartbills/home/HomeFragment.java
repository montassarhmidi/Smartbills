package com.affable.smartbills.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.Constant;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {

    private TextView txtSaleValue, txtPaidValue, txtDueValue, txtProductValue, txtExpenseValue;
    private PieChart pieChart;
    private DatabaseAccess databaseAccess;
    private DecimalFormat f;
    private List<HashMap<String, String>> paymentMapList;
    private float paid, due;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init views
        txtSaleValue = view.findViewById(R.id.txtSaleValue);
        txtPaidValue = view.findViewById(R.id.txtPaidValue);
        txtDueValue = view.findViewById(R.id.txtDueValue);
        txtProductValue = view.findViewById(R.id.txtProductValue);
        txtExpenseValue = view.findViewById(R.id.txtExpenseValue);
        pieChart = view.findViewById(R.id.pieChart);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        f = new DecimalFormat("#0.00");
        databaseAccess = DatabaseAccess.getInstance(getContext());

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        //collect data from db
        gatherPaidData();

        //set txt values
        databaseAccess.open();
        txtSaleValue.setText(String.format("%s %s", f.format(databaseAccess.getYearlySaleAmount()), currency));

        databaseAccess.open();
        txtProductValue.setText(String.format("%d", databaseAccess.totalItemsCount()));

        databaseAccess.open();
        txtExpenseValue.setText(String.format("%s %s", f.format(databaseAccess.getYearlyExpenseAmount()), currency));

        databaseAccess.open();
        due = databaseAccess.getYearlyTotalDueAmount(paymentMapList);
        txtDueValue.setText(String.format("%s %s", f.format(due), currency));

        databaseAccess.open();
        paid = databaseAccess.getYearlyTotalPaidAmount(paymentMapList);
        txtPaidValue.setText(String.format("%s %s", f.format(paid), currency));

        //set pie chart
        setUpPieChart();
        loadPieChart();

    }

    private void gatherPaidData() {

        paymentMapList = new ArrayList<>();

        databaseAccess.open();
        List<HashMap<String, String>> orderList = databaseAccess.getOrderListByStatus(Constant.COMPLETED);

        for (HashMap<String, String> map : orderList) {

            HashMap<String, String> paymentMap = new HashMap<>();

            String invoiceId = map.get("invoice_id");

            databaseAccess.open();
            int paymentCount = databaseAccess.getPaymentCount(invoiceId);

            paymentMap.put("invoice_id", invoiceId);
            paymentMap.put("payment_id", String.valueOf(paymentCount));

            paymentMapList.add(paymentMap);

        }


    }

    private void setUpPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(35);
        pieChart.setTransparentCircleRadius(40);
        pieChart.setCenterText(getString(R.string.sale_analytics));
        pieChart.setEntryLabelTextSize(16f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);


        //customize legend
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(12);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(12);
        l.setDrawInside(false);
        l.setEnabled(true);

    }

    private void loadPieChart() {

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        pieEntries.add(new PieEntry(paid, "Paid"));
        pieEntries.add(new PieEntry(due, "Due"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(
                ContextCompat.getColor(getContext(), R.color.blue_700),
                ContextCompat.getColor(getContext(), R.color.blue_200));

        PieData data = new PieData(pieDataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

}