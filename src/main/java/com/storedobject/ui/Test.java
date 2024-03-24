package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

public class Test extends View implements CloseableView {

    public Test() {
        setCaption("Test");
        SOChart soChart = new SOChart() {
            @Override
            protected void addCustomEncoding(ComponentPart componentPart, StringBuilder buffer) {
                if(componentPart == null) {
                    buffer.append("""
                            "aria": {
                                "show": true
                              },
                            """);
                }
            }
        };

        // Heatmap chart requires 2 category axes and then, values to be added for each data-point
        CategoryData days = new CategoryData("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        CategoryData slots = new CategoryData("Morning", "Noon", "Afternoon", "Evening", "Night");

        // Create the chart.
        HeatmapChart chart = new HeatmapChart(days, slots);
        chart.getLabel(true).show(); // Want to display the value as labels

        // Add some data-points
        chart.addData(0, 0, 27); // Sunday morning
        chart.addData(0, 3, 28); // Sunday evening
        chart.addData(1, 3, 31); // Monday evening
        chart.addData(1, 4, 25); // Monday night
        chart.addData("Wed", "Noon", 37); // Values can be added by directly addressing X/Y values too.

        chart.getTooltip(true).append("My Tooltip").newline().append("Day: ").append(days).newline()
                .append("Slot: ").append(slots).newline().append("Temperature: ").append(chart);

        // Heatmap charts should be plotted on a rectangular coordinate system
        chart.plotOn(new RectangularCoordinate(new XAxis(days), new YAxis(slots)));

        // Add to the chart display area
        soChart.add(chart);
        setComponent(soChart);
    }
}