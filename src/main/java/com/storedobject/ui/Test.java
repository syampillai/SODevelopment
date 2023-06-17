package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDateTime;

public class Test extends View implements CloseableView {

    public Test() {
        super("Activity Chart Example");

        // Define a chart component
        SOChart soChart = new SOChart();
        soChart.setWidth("100%");

        // Activity list
        ActivityList activityList = new ActivityList();
        activityList.setGroupFontSize(18);
        activityList.setExtraFontSize(12);
        activityList.setActivityFontSize(14);
        activityList.setStart(LocalDateTime.now()); // Set the start date
        // Create some activities
        ActivityList.ActivityGroup tg1 = activityList.createActivityGroup("Group 1"); // Group 1
        tg1.setExtraInfo("Cleaning activities"); // Some extra info
        ActivityList.Activity tg1T1 = // An activity that belongs to group 1
                tg1.createActivity("Activity 1/1", LocalDateTime.now().minusDays(10), 6);
        // Add another one just after the first onw
        ActivityList.Activity tg1T2 = tg1T1.createNext("Activity 1/2", 5);
        tg1T2.setCompleted(100); // This activity is 100% done
        ActivityList.Activity tg1T3 = tg1T2.createNext("Activity 1/3", 11); // Next
        tg1T3.createNext("Activity 1/4", 10)
                .setExtraInfo("Some extra info"); // Extra info: Will be shown as part of the tooltip
        ActivityList.ActivityGroup tg2 = activityList.createActivityGroup("Group 2"); // Group 2
        tg2.setExtraInfo("Other tasks"); // Some extra info
        // Add some activities under group 2 too
        ActivityList.Activity tg2T1 = tg2.createActivity("Activity 2/1", LocalDateTime.now(), 3);
        ActivityList.Activity tg2T2 = tg2T1.createNext("Activity 2/2", 7);
        ActivityList.Activity tg2T3 = tg2T2.createNext("Activity 2/3", 13);
        tg2T3.createNext("Activity 2/4", 9);
        tg2T3.setColor(new Color("green")); // Specific color for this task
        tg2T1.setCompleted(35); // This activity is 35% complete

        // Plot the activities on an Activity Chart
        ActivityChart ac = new ActivityChart(activityList);
        ac.getTimeAxisZoom().hide(); // Hiding the time-axis zoom
        ac.getTimeAxis().getLabel(true).setFontStyle(new Font(Font.Family.sans_serif(), Font.Size.pixels(16)));

        // Add the chart to the chart component
        soChart.add(ac);

        // Set the component for the view
        setComponent(new VerticalLayout(soChart));
    }
}