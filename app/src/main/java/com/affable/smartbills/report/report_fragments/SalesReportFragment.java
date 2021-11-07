package com.affable.smartbills.report.report_fragments;

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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SalesReportFragment extends Fragment {


    private int mYear = 2021;
    private BarChart barChart;
    private TextView txtTotalSales, txtSelectYear;
    private DatabaseAccess databaseAccess;

    private float totalSales;

    DecimalFormat f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sales_report, container, false);

        f = new DecimalFormat("#0.00");

        barChart = view.findViewById(R.id.barchart);
        txtTotalSales = view.findViewById(R.id.txt_total_sales);
        txtSelectYear = view.findViewById(R.id.txt_select_year);

        databaseAccess = DatabaseAccess.getInstance(getContext());

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpChart();

        txtSelectYear.setText(String.format("%s %d", getString(R.string.monthly_sales_report), mYear));

        databaseAccess.open();
        txtTotalSales.setText(String.format("%s %s%s", getString(R.string.total_sales_amount), totalSales, databaseAccess.getCurrency()));


    }

    private void setUpChart() {

        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        //customize xAxis
        String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthList));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(12);

        //customize yAxis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0);

        barChart.getAxisRight().setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(getBarData(), "Month");
        barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.blue_500));

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setBarWidth(0.8f);

        barChart.setData(barData);
        barChart.setScaleEnabled(false);
        barChart.invalidate();
    }

    private List<BarEntry> getBarData() {

        totalSales = 0;
        String[] monthNumber = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < monthNumber.length; i++) {

            databaseAccess.open();
            float sales = databaseAccess.getMonthlySalesAmount(monthNumber[i], String.valueOf(mYear));

            barEntries.add(new BarEntry(i, sales));

            //calculate yearly total expense
            totalSales = sales + totalSales;

        }

        return barEntries;

    }

}