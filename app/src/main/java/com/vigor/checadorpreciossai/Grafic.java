package com.vigor.checadorpreciossai;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;


public class Grafic extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        // in this example, a LineChart is initialized from xml
        LineChart mChart = (LineChart) findViewById(R.id.chart);

        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 50000));
        values.add(new Entry(2, 100000));
        values.add(new Entry(3, 80));
        values.add(new Entry(4, 120000));
        values.add(new Entry(5, 110000));
        values.add(new Entry(7, 15030));
        values.add(new Entry(8, 2500));
        values.add(new Entry(9, 190));
        values.add(new Entry(10, 0));
        values.add(new Entry(11, 190));
        values.add(new Entry(12, 34000));

        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Sample Data");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);


            set1.setFillColor(Color.MAGENTA);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);

        }
    }
}
