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
        super("Chart Example 7");

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

        // Define axes
        XAxis xAxisProduct = new XAxis(DataType.CATEGORY);
        xAxisProduct.setName(dataMatrix.getColumnDataName());
        XAxis xAxisYear = new XAxis(DataType.CATEGORY);
        xAxisYear.setName(dataMatrix.getRowDataName());
        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName(dataMatrix.getName());

        // First rectangular coordinate
        RectangularCoordinate rc1 = new RectangularCoordinate();
        rc1.addAxis(xAxisProduct, yAxis);
        rc1.getPosition(true)
                .setBottom(Size.percentage(55)); // Position it leaving 55% space at the bottom
        // Second rectangular coordinate
        RectangularCoordinate rc2 = new RectangularCoordinate();
        rc2.addAxis(xAxisYear, yAxis); // Same Y-axis is re-used here
        rc2.getPosition(true).setTop(Size.percentage(55)); // Position it leaving 55% space at the top

        // Bar chart variable
        BarChart bc;

        // Crate a bar chart for each data row
        for (int i = 0; i < dataMatrix.getRowCount(); i++) {
            bc = new BarChart(dataMatrix.getColumnNames(), dataMatrix.getRow(i));
            bc.setName(dataMatrix.getRowName(i));
            bc.plotOn(rc1);
            charts.add(bc);
        }
        // Crate a bar chart for each data column
        for (int i = 0; i < dataMatrix.getColumnCount(); i++) {
            bc = new BarChart(dataMatrix.getRowNames(), dataMatrix.getColumn(i));
            bc.setName(dataMatrix.getColumnName(i));
            bc.plotOn(rc2);
            charts.add(bc);
        }
    }
}
