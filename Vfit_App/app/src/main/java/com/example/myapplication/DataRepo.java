package com.example.myapplication;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class DataRepo {
    public LineGraphSeries<DataPoint> seriesDataRepo = new LineGraphSeries<>(new DataPoint[]{
            // on below line we are adding
            // each point on our x and y axis.
            new DataPoint(0, 0),
            new DataPoint(1, 1),
            new DataPoint(2, 2)
    });
    public LineGraphSeries<DataPoint> getData() {return seriesDataRepo;}
    private static final DataRepo holder = new DataRepo();
    public static DataRepo getInstance() {return holder;}
}
