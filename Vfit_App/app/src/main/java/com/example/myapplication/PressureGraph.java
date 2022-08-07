package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;

public class PressureGraph extends AppCompatActivity {

    GraphView graphView;
    LineGraphSeries<DataPoint> mySeries = DataRepo.getInstance().seriesDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure_graph);
        graphView = findViewById(R.id.idGraphView);

        initGraph();
    }

    private void initGraph() {
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0.0);
        graphView.getViewport().setMaxX(50.0);
        graphView.getViewport().setYAxisBoundsManual(true);

        graphView.getViewport().setMinY(0.0);
        graphView.getViewport().setMaxY(100.0);
        graphView.getGridLabelRenderer().setLabelVerticalWidth(100);

        // first mSeries is a line
        mySeries.setDrawDataPoints(false);
        mySeries.setDrawBackground(false);
        graphView.addSeries(mySeries);
        setLabelsFormat(graphView);


//        graphView.setTitle("My Graph View");
//
//        // on below line we are setting
//        // text color to our graph view.
//        graphView.setTitleColor(R.color.purple_200);
//
//        // on below line we are setting
//        // our title text size.
//        graphView.setTitleTextSize(18);
//
//        // on below line we are adding
//        // data series to our graph view.
//        graphView.addSeries(mySeries);
    }
//mGraph:GraphView,maxInt:Int,maxFraction:Int
    private void setLabelsFormat(@NonNull GraphView mGraph){
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMaximumIntegerDigits(1);

        mGraph.getGridLabelRenderer().setVerticalAxisTitle("Pressure");
        mGraph.getGridLabelRenderer().setHorizontalAxisTitle("Time");

//        mGraph.getGridLabelRenderer().setLabelFormatter(object : DefaultLabelFormatter(nf,nf) {
//            override fun formatLabel(value: Double, isValueX: Boolean): String {
//                return if (isValueX) {
//                    super.formatLabel(value, isValueX)+ "s"
//                } else {
//                    super.formatLabel(value, isValueX)
//                }
//            }
//        })

//        mGraph.getGridLabelRenderer().setLabelFormatter(new
//                DefaultLabelFormatter(nf,nf)  {
//                    @Override
//                    public String formatLabel(double value, boolean isValueX) {
//                        if(isValueX) {
////                            return formatLabel(value, true) + "s";
//                            return super.formatLabel(value, true)+ "s";
//                        } else {
//                            return super.formatLabel(value, false);
//                        }
//                    }
//                });

    }
}