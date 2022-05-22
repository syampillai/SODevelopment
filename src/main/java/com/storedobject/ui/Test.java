package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

import java.util.ArrayList;
import java.util.List;

public class Test extends View implements CloseableView {

    // To hold multiple charts
    private final List<Chart> charts = new ArrayList<>();

    public Test() {
        super("Chart Example 6");

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Create multiple charts
        createCharts();

        // Add the chart component(s) to the chart display area
        charts.forEach(soChart::add);

        // Set the component for the view
        setComponent(soChart);
    }

    private void createCharts() {
        // Define a data matrix to hold production data.
        DataMatrix dataMatrix = new DataMatrix("Production in Million Tons");
        // Columns contain products
        dataMatrix.setColumnNames("Matcha Latte", "Milk Tea", "Cheese Cocoa");
        dataMatrix.setColumnDataName("Products");
        // Rows contain years of production
        dataMatrix.setRowNames("2012", "2013", "2014", "2015");
        dataMatrix.setRowDataName("Years");
        // Add row values
        dataMatrix.addRow(41.1, 86.5, 24.1);
        dataMatrix.addRow(30.4, 92.1, 24.1);
        dataMatrix.addRow(31.9, 85.7, 67.2);
        dataMatrix.addRow(53.3, 85.1, 86.4);

        // Bar chart variable
        BarChart bc;

        // Define the fist rectangular coordinate.
        XAxis xAxis = new XAxis(DataType.CATEGORY);
        xAxis.setName(dataMatrix.getColumnDataName());
        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName(dataMatrix.getName());
        RectangularCoordinate rc = new RectangularCoordinate();
        rc.addAxis(xAxis, yAxis);

        // Create a bar chart for each row
        for (int i = 0; i < dataMatrix.getRowCount(); i++) {
            // Bar chart for the row
            bc = new BarChart(dataMatrix.getColumnNames(), dataMatrix.getRow(i));
            bc.setName(dataMatrix.getRowName(i));
            // Plot that to the coordinate system defined
            bc.plotOn(rc);
            // Add that to the chart list
            charts.add(bc);
        }
        rc.getPosition(true).setBottom(Size.percentage(55));

        // Define the 2nd rectangular coordinate
        xAxis = new XAxis(DataType.CATEGORY);
        xAxis.setName(dataMatrix.getRowDataName());
        rc = new RectangularCoordinate();
        rc.addAxis(xAxis, yAxis);
        rc.getPosition(true).setTop(Size.percentage(55));

        // Create a bar chart for each column
        for (int i = 0; i < dataMatrix.getColumnCount(); i++) {
            // Bar chart for the row
            bc = new BarChart(dataMatrix.getRowNames(), dataMatrix.getColumn(i));
            bc.setName(dataMatrix.getColumnName(i));
            // Plot that to the coordinate system defined
            bc.plotOn(rc);
            // Add that to the chart list
            charts.add(bc);
        }
    }
}
