package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Test extends View {

    public Test() {
        super("Test");

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("1200px", "500px");

        // Data (common to all charts)
        CategoryData x = new CategoryData("Banana and so many other things to show", "Mango", "Apple", "Juice", "Orange");
        Data y1 = new Data(5, 9, 6, 7, 8);
        Data y2 = new Data(4, 8, 7, 8, 9);

        // Chart I - Independent bars

        BarChart barChart1 = new BarChart(x, y1);
        barChart1.setName("Capacity");

        BarChart barChart2 = new BarChart(x, y2);
        barChart2.setName("Used Capacity");
        barChart2.setColors(new Color("#25B15F"));

        XAxis xAxis1 = new XAxis(DataType.CATEGORY);
        xAxis1.getLabel(true).setRotation(-45);
        YAxis yAxis = new YAxis(DataType.NUMBER);

        RectangularCoordinate rc = new RectangularCoordinate(xAxis1, yAxis);
        Position position = rc.getPosition(true);
        position.setRight(Size.percentage(70));
        position.setBottom(Size.percentage(30));

        barChart1.plotOn(rc);
        barChart2.plotOn(rc);

        // Add to the chart display
        soChart.add(rc);

        // Chart II - Stacked bars

        barChart1 = new BarChart(x, y1);
        barChart1.setName("Capacity (Stacked)");
        barChart1.setStackName("ONE");

        barChart2 = new BarChart(x, y2);
        barChart2.setName("Used Capacity (Stacked)");
        barChart2.setStackName("ONE");
        barChart2.setColors(new Color("#25B15F"));

        xAxis1 = new XAxis(DataType.CATEGORY);
        yAxis = new YAxis(DataType.NUMBER);

        rc = new RectangularCoordinate(xAxis1, yAxis);
        position = rc.getPosition(true);
        position.setLeft(Size.percentage(38));
        position.setRight(Size.percentage(38));

        barChart1.plotOn(rc);
        barChart2.plotOn(rc);

        // Add to the chart display
        soChart.add(rc);

        // Chart III - Overlapped bars

        barChart1 = new BarChart(x, y1);
        barChart1.setName("Capacity (Overlapped)");

        barChart2 = new BarChart(x, y2);
        barChart2.setName("Used Capacity (Overlapped)");
        barChart2.setColors(new Color("#25B15F"));

        xAxis1 = new XAxis(DataType.CATEGORY);
        XAxis xAxis2 = new XAxis(DataType.CATEGORY);
        yAxis = new YAxis(DataType.NUMBER);

        rc = new RectangularCoordinate(xAxis1, xAxis2, yAxis);
        rc.getPosition(true).setLeft(Size.percentage(70));

        barChart1.plotOn(rc, xAxis1, yAxis);
        barChart2.plotOn(rc, xAxis2, yAxis);
        xAxis2.hide(); // We don't want to show the duplicate X-axis

        // Add to the chart display
        soChart.add(rc);

        // Set the component for the view
        setComponent(new HorizontalLayout(soChart));
    }
}
